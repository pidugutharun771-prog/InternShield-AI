package com.internshield.service;

import com.internshield.dto.CompanyVerificationRequest;
import com.internshield.dto.CompanyVerificationResponse;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CompanyVerificationService {

    private static final Set<String> FREE_EMAIL_DOMAINS = Set.of(
            "gmail.com",
            "googlemail.com",
            "yahoo.com",
            "outlook.com",
            "hotmail.com",
            "live.com",
            "icloud.com",
            "proton.me",
            "protonmail.com",
            "aol.com"
    );

    public CompanyVerificationResponse verify(
            CompanyVerificationRequest request) {

        String companyName = request.companyName().trim();
        String website = request.website().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        List<String> findings = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        int score = 0;

        // ---------------------------------------------------------
        // 1. PARSE WEBSITE
        // ---------------------------------------------------------

        URI uri;

        try {
            uri = normalizeAndParse(website);
        } catch (IllegalArgumentException e) {

            return new CompanyVerificationResponse(
                    100,
                    "HIGH_RISK",
                    companyName,
                    website,
                    "",
                    email,
                    extractEmailDomain(email),
                    false,
                    false,
                    List.of("Invalid or malformed company website"),
                    List.of(
                            "Do not submit personal information through this website.",
                            "Find the company's official website independently.",
                            "Verify the internship through the company's official careers page."
                    )
            );
        }

        String scheme = uri.getScheme()
                .toLowerCase(Locale.ROOT);

        String websiteDomain = normalizeHostname(
                uri.getHost()
        );

        // ---------------------------------------------------------
        // 2. HTTPS CHECK
        // ---------------------------------------------------------

        if ("https".equals(scheme)) {

            findings.add("Company website uses HTTPS");

        } else {

            score += 20;

            findings.add(
                    "Company website does not use HTTPS (+20)"
            );

            recommendations.add(
                    "Prefer the company's secure HTTPS website before submitting information."
            );
        }

        // ---------------------------------------------------------
        // 3. EMAIL DOMAIN
        // ---------------------------------------------------------

        String emailDomain = extractEmailDomain(email);

        boolean publicEmailProvider =
                FREE_EMAIL_DOMAINS.contains(emailDomain);

        boolean emailDomainMatches =
                domainsMatch(websiteDomain, emailDomain);

        // ---------------------------------------------------------
        // 4. PUBLIC EMAIL CHECK
        // ---------------------------------------------------------

        if (publicEmailProvider) {

            score += 25;

            findings.add(
                    "Recruiter uses a public email provider (+25)"
            );

            recommendations.add(
                    "Verify the recruiter through contact information published on the company's official website."
            );

        } else if (!emailDomain.isBlank()) {

            findings.add(
                    "Recruiter uses a custom email domain"
            );
        }

        // ---------------------------------------------------------
        // 5. WEBSITE ↔ EMAIL DOMAIN
        // ---------------------------------------------------------

        if (!emailDomain.isBlank() && !publicEmailProvider) {

            if (emailDomainMatches) {

                score -= 10;

                findings.add(
                        "Recruiter email domain matches the company website"
                );

            } else {

                score += 30;

                findings.add(
                        "Recruiter email domain does not match the company website (+30)"
                );

                recommendations.add(
                        "Verify whether the recruiter is actually associated with the company."
                );
            }
        }

        // ---------------------------------------------------------
        // 6. COMPANY NAME ↔ DOMAIN
        // ---------------------------------------------------------

        String companySlug = normalizeCompanyName(companyName);

        String domainWithoutTld =
                getMainDomainName(websiteDomain);

        if (!companySlug.isBlank()
                && !domainWithoutTld.isBlank()) {

            if (domainLooksRelated(companySlug, domainWithoutTld)) {

                findings.add(
                        "Company name appears consistent with the website domain"
                );

            } else {

                score += 20;

                findings.add(
                        "Company name does not clearly match the website domain (+20)"
                );

                recommendations.add(
                        "Confirm that the website is the company's genuine official domain."
                );
            }
        }

        // ---------------------------------------------------------
        // 7. SUSPICIOUS DOMAIN PATTERNS
        // ---------------------------------------------------------

        long hyphenCount = websiteDomain
                .chars()
                .filter(c -> c == '-')
                .count();

        if (hyphenCount >= 3) {

            score += 10;

            findings.add(
                    "Website domain contains many hyphens (+10)"
            );

            recommendations.add(
                    "Check the company's official website independently before trusting this domain."
            );
        }

        // ---------------------------------------------------------
        // 8. SUBDOMAIN CHECK
        // ---------------------------------------------------------

        int subdomainCount =
                countSubdomains(websiteDomain);

        if (subdomainCount >= 3) {

            score += 10;

            findings.add(
                    "Website uses an unusually deep subdomain structure (+10)"
            );
        }

        // ---------------------------------------------------------
        // 9. SCORE NORMALIZATION
        // ---------------------------------------------------------

        score = Math.max(0, Math.min(100, score));

        String status;

        if (score >= 60) {

            status = "HIGH_RISK";

        } else if (score >= 20) {

            status = "NEEDS_VERIFICATION";

        } else {

            status = "VERIFIED";
        }

        // ---------------------------------------------------------
        // 10. GENERAL RECOMMENDATIONS
        // ---------------------------------------------------------

        if (status.equals("VERIFIED")) {

            recommendations.add(
                    "The provided company, website and recruiter details appear consistent."
            );

            recommendations.add(
                    "Still verify that the internship exists on the company's official careers page."
            );

        } else {

            recommendations.add(
                    "Do not rely only on the recruiter-provided information."
            );

            recommendations.add(
                    "Verify the company using an independently located official website."
            );

            recommendations.add(
                    "Never pay registration, training, security or placement fees."
            );

            recommendations.add(
                    "Never share OTPs, passwords, CVV or banking credentials."
            );
        }

        return new CompanyVerificationResponse(
                score,
                status,
                companyName,
                website,
                websiteDomain,
                email,
                emailDomain,
                emailDomainMatches,
                publicEmailProvider,
                removeDuplicates(findings),
                removeDuplicates(recommendations)
        );
    }

    // ============================================================
    // URL PARSING
    // ============================================================

    private URI normalizeAndParse(String website) {

        String url = website.trim();

        if (!url.matches(
                "^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {

            url = "https://" + url;
        }

        URI uri = URI.create(url);

        if (uri.getHost() == null
                || uri.getHost().isBlank()) {

            throw new IllegalArgumentException(
                    "Invalid website"
            );
        }

        return uri;
    }

    // ============================================================
    // DOMAIN NORMALIZATION
    // ============================================================

    private String normalizeHostname(String hostname) {

        String host =
                hostname.toLowerCase(Locale.ROOT);

        if (host.startsWith("www.")) {

            host = host.substring(4);
        }

        return host;
    }

    // ============================================================
    // EMAIL DOMAIN
    // ============================================================

    private String extractEmailDomain(String email) {

        if (email == null || email.isBlank()) {
            return "";
        }

        int atIndex =
                email.lastIndexOf('@');

        if (atIndex < 0
                || atIndex == email.length() - 1) {

            return "";
        }

        return email
                .substring(atIndex + 1)
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    // ============================================================
    // DOMAIN MATCH
    // ============================================================

    private boolean domainsMatch(
            String websiteDomain,
            String emailDomain) {

        if (websiteDomain.equals(emailDomain)) {
            return true;
        }

        return websiteDomain.endsWith(
                "." + emailDomain
        ) || emailDomain.endsWith(
                "." + websiteDomain
        );
    }

    // ============================================================
    // COMPANY NAME NORMALIZATION
    // ============================================================

    private String normalizeCompanyName(
            String companyName) {

        return companyName
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "[^a-z0-9]",
                        ""
                );
    }

    // ============================================================
    // MAIN DOMAIN NAME
    // ============================================================

    private String getMainDomainName(
            String domain) {

        String[] parts =
                domain.split("\\.");

        if (parts.length < 2) {
            return domain;
        }

        return parts[parts.length - 2]
                .toLowerCase(Locale.ROOT);
    }

    // ============================================================
    // COMPANY ↔ DOMAIN CHECK
    // ============================================================

    private boolean domainLooksRelated(
            String companyName,
            String domainName) {

        if (companyName.equals(domainName)) {
            return true;
        }

        return companyName.contains(domainName)
                || domainName.contains(companyName);
    }

    // ============================================================
    // SUBDOMAIN COUNT
    // ============================================================

    private int countSubdomains(
            String host) {

        if (host == null || host.isBlank()) {
            return 0;
        }

        String[] parts =
                host.split("\\.");

        if (parts.length <= 2) {
            return 0;
        }

        return parts.length - 2;
    }

    // ============================================================
    // REMOVE DUPLICATES
    // ============================================================

    private List<String> removeDuplicates(
            List<String> values) {

        return values.stream()
                .distinct()
                .toList();
    }
}