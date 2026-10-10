package com.internshield.service;

import com.internshield.dto.UrlAnalysisRequest;
import com.internshield.dto.UrlAnalysisResponse;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UrlAnalysisService {

    private static final Set<String> URL_SHORTENERS = Set.of(
            "bit.ly",
            "tinyurl.com",
            "t.co",
            "goo.gl",
            "cutt.ly",
            "is.gd",
            "ow.ly",
            "shorturl.at",
            "rb.gy"
    );

    private static final Set<String> SUSPICIOUS_KEYWORDS = Set.of(
            "login",
            "verify",
            "verification",
            "secure",
            "account",
            "claim",
            "reward",
            "winner",
            "free",
            "urgent",
            "payment",
            "fee",
            "registration",
            "deposit",
            "internship",
            "job-offer",
            "offer-letter"
    );

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

    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
            "^(?:\\d{1,3}\\.){3}\\d{1,3}$"
    );

    public UrlAnalysisResponse analyze(UrlAnalysisRequest request) {

        String rawUrl = request.url().trim();

        List<String> indicators = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        int score = 0;

        URI uri;

        try {
            uri = normalizeAndParse(rawUrl);
        } catch (IllegalArgumentException e) {

            return new UrlAnalysisResponse(
                    100,
                    "CRITICAL",
                    rawUrl,
                    "",
                    List.of("Invalid or malformed URL"),
                    List.of(
                            "Do not open the URL.",
                            "Verify the internship through the company's official website."
                    )
            );
        }

        String scheme = uri.getScheme() == null
                ? ""
                : uri.getScheme().toLowerCase(Locale.ROOT);

        String host = uri.getHost() == null
                ? ""
                : uri.getHost().toLowerCase(Locale.ROOT);

        host = normalizeHostname(host);

        String path = uri.getPath() == null
                ? ""
                : uri.getPath().toLowerCase(Locale.ROOT);

        String query = uri.getQuery() == null
                ? ""
                : uri.getQuery().toLowerCase(Locale.ROOT);

        

        // ---------------------------------------------------------
        // 1. HTTPS CHECK
        // ---------------------------------------------------------

        if ("http".equals(scheme)) {

            score += 15;

            indicators.add(
                    "URL does not use HTTPS (+15)"
            );

            recommendations.add(
                    "Prefer a secure HTTPS website before submitting information."
            );

        } else if ("https".equals(scheme)) {

            indicators.add(
                    "HTTPS connection detected"
            );
        }

        // ---------------------------------------------------------
        // 2. URL SHORTENER
        // ---------------------------------------------------------

        if (URL_SHORTENERS.contains(host)) {

            score += 25;

            indicators.add(
                    "URL shortener detected (+25)"
            );

            recommendations.add(
                    "Do not trust shortened links blindly; verify the final destination."
            );
        }

        // ---------------------------------------------------------
        // 3. IP ADDRESS INSTEAD OF DOMAIN
        // ---------------------------------------------------------

        if (IP_ADDRESS_PATTERN.matcher(host).matches()) {

            score += 30;

            indicators.add(
                    "URL uses an IP address instead of a normal domain (+30)"
            );

            recommendations.add(
                    "Avoid entering personal or financial information on IP-based websites."
            );
        }

        // ---------------------------------------------------------
        // 4. @ SYMBOL
        // ---------------------------------------------------------

        if (rawUrl.contains("@")) {

            score += 25;

            indicators.add(
                    "URL contains an '@' symbol that may hide the actual destination (+25)"
            );

            recommendations.add(
                    "Inspect the actual hostname carefully before opening the link."
            );
        }

        // ---------------------------------------------------------
        // 5. SUSPICIOUS KEYWORDS
        // ---------------------------------------------------------

        List<String> matchedKeywords = new ArrayList<>();

        String searchableText = host + path + "?" + query;

        for (String keyword : SUSPICIOUS_KEYWORDS) {

            if (searchableText.contains(keyword)) {
                matchedKeywords.add(keyword);
            }
        }

        if (!matchedKeywords.isEmpty()) {

            int points = Math.min(20, matchedKeywords.size() * 5);

            score += points;

            indicators.add(
                    "Suspicious URL keywords detected: "
                            + String.join(", ", matchedKeywords)
                            + " (+"
                            + points
                            + ")"
            );

            recommendations.add(
                    "Verify why the website is requesting these actions before proceeding."
            );
        }

        // ---------------------------------------------------------
        // 6. TOO MANY SUBDOMAINS
        // ---------------------------------------------------------

        int subdomainCount = countSubdomains(host);

        if (subdomainCount >= 3) {

            score += 15;

            indicators.add(
                    "Unusually deep subdomain structure detected (+15)"
            );

            recommendations.add(
                    "Check that the main domain belongs to the genuine organization."
            );
        }

        // ---------------------------------------------------------
        // 7. LONG URL
        // ---------------------------------------------------------

        if (rawUrl.length() > 150) {

            score += 10;

            indicators.add(
                    "Unusually long URL detected (+10)"
            );
        }

        // ---------------------------------------------------------
        // 8. SUSPICIOUS PORT
        // ---------------------------------------------------------

        int port = uri.getPort();

        if (port != -1
                && port != 80
                && port != 443) {

            score += 15;

            indicators.add(
                    "Non-standard web port detected (+15)"
            );

            recommendations.add(
                    "Be cautious when an internship link uses an unusual port."
            );
        }

        // ---------------------------------------------------------
        // 9. DOMAIN WITH MANY HYPHENS
        // ---------------------------------------------------------

        long hyphenCount = host.chars()
                .filter(c -> c == '-')
                .count();

        if (hyphenCount >= 3) {

            score += 10;

            indicators.add(
                    "Domain contains an unusually high number of hyphens (+10)"
            );
        }

        // ---------------------------------------------------------
        // 10. DOMAIN + EMAIL COMPARISON
        // ---------------------------------------------------------

        String emailDomain = extractEmailDomain(request.email());

        if (!emailDomain.isBlank()) {

            if (FREE_EMAIL_DOMAINS.contains(emailDomain)) {

                score += 10;

                indicators.add(
                        "Recruiter uses a public email provider (+10)"
                );

                recommendations.add(
                        "Verify the recruiter using contact information from the company's official website."
                );

            } else if (!domainsMatch(host, emailDomain)) {

                indicators.add(
                        "Website domain differs from recruiter email domain; "
                                + "this may be a third-party platform"
                );
            }
        }

        // ---------------------------------------------------------
        // 11. SUSPICIOUS FILE EXTENSIONS
        // ---------------------------------------------------------

        if (path.endsWith(".exe")
                || path.endsWith(".scr")
                || path.endsWith(".bat")
                || path.endsWith(".cmd")
                || path.endsWith(".msi")) {

            score += 30;

            indicators.add(
                    "URL points to a potentially executable file (+30)"
            );

            recommendations.add(
                    "Do not download or execute unknown files from internship links."
            );
        }

        // ---------------------------------------------------------
        // FINAL SCORE
        // ---------------------------------------------------------

        score = Math.min(100, score);

        String riskLevel;

        if (score >= 70) {
            riskLevel = "CRITICAL";
        } else if (score >= 45) {
            riskLevel = "HIGH";
        } else if (score >= 25) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        // ---------------------------------------------------------
        // GENERAL RECOMMENDATIONS
        // ---------------------------------------------------------

        if (score < 25) {

            recommendations.add(
                    "No major URL risk signals were detected."
            );

            recommendations.add(
                    "A low score does not guarantee that the internship is legitimate."
            );

        } else {

            recommendations.add(
                    "Verify the company through its independently located official website."
            );

            recommendations.add(
                    "Do not pay registration, training, security or placement fees."
            );

            recommendations.add(
                    "Never share OTPs, passwords, CVV or banking credentials."
            );
        }

        return new UrlAnalysisResponse(
                score,
                riskLevel,
                rawUrl,
                host,
                indicators,
                removeDuplicates(recommendations)
        );
    }

    // ============================================================
    // URL PARSING
    // ============================================================

    private URI normalizeAndParse(String rawUrl) {

        String url = rawUrl.trim();

        if (!url.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
            url = "https://" + url;
        }

        URI uri = URI.create(url);

        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new IllegalArgumentException("Invalid URL");
        }

        return uri;
    }

    // ============================================================
    // DOMAIN NORMALIZATION
    // ============================================================

    private String normalizeHostname(String hostname) {

        String host = hostname.toLowerCase(Locale.ROOT);

        if (host.startsWith("www.")) {
            host = host.substring(4);
        }

        return host;
    }

    // ============================================================
    // SUBDOMAIN COUNT
    // ============================================================

    private int countSubdomains(String host) {

        if (host.isBlank()) {
            return 0;
        }

        String[] parts = host.split("\\.");

        if (parts.length <= 2) {
            return 0;
        }

        return parts.length - 2;
    }

    // ============================================================
    // EMAIL DOMAIN
    // ============================================================

    private String extractEmailDomain(String email) {

        if (email == null || email.isBlank()) {
            return "";
        }

        String value = email.trim().toLowerCase(Locale.ROOT);

        int atIndex = value.lastIndexOf('@');

        if (atIndex < 0 || atIndex == value.length() - 1) {
            return "";
        }

        return value.substring(atIndex + 1);
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

        return websiteDomain.endsWith("." + emailDomain)
                || emailDomain.endsWith("." + websiteDomain);
    }

    // ============================================================
    // REMOVE DUPLICATE RECOMMENDATIONS
    // ============================================================

    private List<String> removeDuplicates(
            List<String> recommendations) {

        return recommendations.stream()
                .distinct()
                .toList();
    }
}