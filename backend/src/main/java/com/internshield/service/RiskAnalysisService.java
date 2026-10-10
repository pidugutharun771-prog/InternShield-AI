package com.internshield.service;

import com.internshield.dto.*;
import com.internshield.model.*;
import com.internshield.repository.AnalysisRepository;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.regex.*;

@Service
public class RiskAnalysisService {

    private final AnalysisRepository repo;

    private record Signal(String label, int points, Pattern pattern) {}

    private static Signal signal(
            String label, int points, String regex) {

        return new Signal(
            label,
            points,
            Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        );
    }

    private static final List<Signal> SIGNALS = List.of(

        signal("Recruitment or placement fee mentioned", 25,
            "\\b(?:registration|application|training|processing|placement)\\s+(?:fees?|charges?)\\b"),

        signal("Security deposit requested", 30,
            "\\bsecurity\\s+deposit\\b|\\brefundable\\s+deposit\\b|\\bdeposit\\s+to\\s+(?:secure|confirm|unlock)\\s+(?:the\\s+)?(?:job|internship|offer)\\b"),

        signal("Payment required to obtain or start a job", 30,
            "\\bpay\\s+to\\s+(?:join|start|get|secure)\\b|\\bpayment\\s+required\\s+to\\s+(?:join|start)\\b"),

        signal("Urgent hiring language", 12,
            "\\burgent(?:ly)?\\s+hiring\\b"),

        signal("Limited-seat pressure", 10,
            "\\blimited\\s+seats?\\b"),

        signal("Instant earnings claim", 15,
            "\\bearn\\s+(?:money\\s+)?instantly\\b"),

        signal("Interview bypass or guaranteed selection", 20,
            "\\bno\\s+interviews?\\b|\\binterview\\s+not\\s+required\\b|\\bwithout\\s+an?\\s+interview\\b"),

        signal("Guaranteed job promise", 18,
            "\\bguaranteed\\s+jobs?\\b"),

        signal("Work-from-home claim", 3,
            "\\bwork\\s+from\\s+home\\b"),

        signal("Recruitment directed to Telegram", 12,
            "\\btelegram\\b"),

        signal("Recruitment directed to WhatsApp", 8,
            "\\bwhatsapp\\b"),

        signal("Cryptocurrency payment or promotion", 20,
            "\\bcrypto(?:currency)?\\b"),

        signal("Gift-card payment request", 25,
            "\\bgift\\s+cards?\\b"),

        signal("Sensitive financial or authentication data mentioned", 25,
            "\\b(?:otps?|one[- ]time password|cvv|cvc|upi\\s+pin|bank details|banking credentials|account number|ifsc|passwords?)\\b"),

        signal("Immediate-action pressure", 15,
            "\\bact\\s+immediately\\b"),

        signal("Guaranteed placement claim", 18,
            "\\b100\\s*%\\s*placement\\b")
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "\\b[A-Z0-9._%+-]+@([A-Z0-9.-]+\\.[A-Z]{2,})\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
        "https?://[^\\s<>\"']+",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SHORT_URL_PATTERN = Pattern.compile(
        "https?://(?:www\\.)?(?:bit\\.ly|tinyurl\\.com|t\\.co|goo\\.gl|cutt\\.ly|is\\.gd|ow\\.ly)/\\S+",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern NEGATION_BEFORE = Pattern.compile(
        "\\b(?:no|not|without|never|do\\s+not|does\\s+not|did\\s+not|will\\s+not|don't|doesn't|isn't|aren't|won't)\\b(?:\\W+\\w+){0,8}\\W*$",
        Pattern.CASE_INSENSITIVE
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

    public RiskAnalysisService(AnalysisRepository repo) {
        this.repo = repo;
    }

    public AnalysisResponse analyze(AnalyzeRequest req) {

        String text = req.content().trim();
        int score = 0;

        List<String> indicators = new ArrayList<>();

        // Apply each configured risk signal at most once.
        for (Signal s : SIGNALS) {

            if (hasNonNegatedMatch(s.pattern(), text)) {

                score += s.points();

                indicators.add(
                    s.label() + " (+" + s.points() + ")"
                );
            }
        }

        // Identify the recruiter email domain.
        String email = extractEmail(req.email(), text);
        String domain = extractDomain(email);
        boolean freeEmail = isFreeEmail(domain);

        // Public/free email provider check.
        if (freeEmail) {

            score += 10;

            indicators.add(
                "Recruiter address uses a public email provider (+10)"
            );
        }

        // An external link by itself is not proof of fraud.
        if (URL_PATTERN.matcher(text).find()) {

            indicators.add(
                "External link included; verify its destination independently"
            );
        }

        /*
         * Compare recruiter email domain with link hostnames.
         *
         * IMPORTANT:
         * This check also runs for public email providers such as Gmail.
         *
         * Example:
         * recruiter@gmail.com
         * https://www.microsoft.com/careers
         *
         * Result:
         * - Public email provider (+10)
         * - Link host differs from recruiter email domain
         *
         * This is an advisory only and does NOT increase the risk score.
         */
        if (!domain.isBlank()
                && hasUnmatchedLinkDomain(text, domain)) {

            indicators.add(
                "Link host differs from recruiter email domain; " +
                "this may be a third-party platform, so verify independently"
            );
        }

        // Check URLs from commonly used URL-shortening services.
        if (SHORT_URL_PATTERN.matcher(text).find()) {

            score += 12;

            indicators.add(
                "URL shortener detected; verify the final destination (+12)"
            );
        }

        // Short messages provide limited information for assessment.
        if (text.length() < 80) {

            score += 5;

            indicators.add(
                "Very little offer information was provided (+5)"
            );
        }

        score = Math.min(100, score);

        RiskLevel level =
            score >= 70 ? RiskLevel.CRITICAL :
            score >= 45 ? RiskLevel.HIGH :
            score >= 25 ? RiskLevel.MEDIUM :
                          RiskLevel.LOW;

        List<String> recommendations = new ArrayList<>();

        if (level == RiskLevel.LOW) {

            recommendations.add(
                "No major configured scam signals were detected; " +
                "this does not prove the offer is legitimate."
            );

        } else {

            recommendations.add(
                "Pause before proceeding and independently verify the internship."
            );

            recommendations.add(
                "Do not pay registration, training, security or placement fees."
            );
        }

        recommendations.add(
            "Verify the company through its official website and careers page."
        );

        recommendations.add(
            "Never share OTPs, passwords, CVV or banking credentials."
        );

        if (freeEmail) {

            recommendations.add(
                "Verify the recruiter using an independently located company contact; " +
                "a public email address alone does not prove fraud."
            );
        }

        // Save the assessment to PostgreSQL.
        Analysis a = new Analysis();

        a.setContent(text);
        a.setScore(score);
        a.setRiskLevel(level);
        a.setEmailDomain(domain);
        a.setFreeEmail(freeEmail);
        a.setIndicators(indicators);
        a.setRecommendations(recommendations);

        repo.save(a);

        return toResponse(a);
    }

    private boolean hasNonNegatedMatch(
            Pattern pattern, String text) {

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            if (!isNegated(text, matcher.start())) {
                return true;
            }
        }

        return false;
    }

    /*
     * Basic heuristic for negation, such as:
     * "No registration fee is required."
     *
     * This does not provide full natural-language understanding.
     */
    private boolean isNegated(String text, int matchStart) {

        int from = Math.max(0, matchStart - 100);

        String prefix = text.substring(from, matchStart);

        int boundary = Math.max(
            Math.max(prefix.lastIndexOf('.'), prefix.lastIndexOf('!')),
            Math.max(prefix.lastIndexOf('?'), prefix.lastIndexOf('\n'))
        );

        prefix = prefix.substring(boundary + 1);

        return NEGATION_BEFORE.matcher(prefix).find();
    }

    /*
     * Returns true if an HTTP/HTTPS URL has a hostname that
     * does not match the recruiter email domain.
     *
     * This is an advisory heuristic, not a company verification
     * service. Legitimate third-party recruitment platforms can
     * also use a different domain.
     */
    private boolean hasUnmatchedLinkDomain(
            String text, String emailDomain) {

        Matcher matcher = URL_PATTERN.matcher(text);

        while (matcher.find()) {

            String candidate = matcher.group()
                .replaceAll("[),.;!?]+$", "");

            try {

                String host = URI.create(candidate).getHost();

                if (host == null) {
                    continue;
                }

                host = normalizeHostname(host);

                if (host.equals(emailDomain)
                        || host.endsWith("." + emailDomain)) {

                    continue;
                }

                return true;

            } catch (IllegalArgumentException ignored) {

                // Ignore malformed URL values.
            }
        }

        return false;
    }

    private String normalizeHostname(String hostname) {

        String host = hostname.toLowerCase(Locale.ROOT);

        if (host.startsWith("www.")) {
            host = host.substring(4);
        }

        return host;
    }

    public List<AnalysisResponse> recent() {

        return repo.findTop20ByOrderByCreatedAtDesc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AnalysisResponse toResponse(Analysis a) {

        return new AnalysisResponse(
            a.getId(),
            a.getScore(),
            a.getRiskLevel(),
            a.getEmailDomain(),
            a.isFreeEmail(),
            a.getIndicators(),
            a.getRecommendations(),
            a.getCreatedAt()
        );
    }

    private String extractEmail(String supplied, String text) {

        if (supplied != null && !supplied.isBlank()) {

            Matcher suppliedMatcher =
                EMAIL_PATTERN.matcher(supplied.trim());

            if (suppliedMatcher.find()) {

                return suppliedMatcher.group()
                    .toLowerCase(Locale.ROOT);
            }
        }

        Matcher textMatcher = EMAIL_PATTERN.matcher(text);

        if (textMatcher.find()) {

            return textMatcher.group()
                .toLowerCase(Locale.ROOT);
        }

        return "";
    }

    private String extractDomain(String email) {

        if (email == null || email.isBlank()) {
            return "";
        }

        int at = email.lastIndexOf('@');

        String domain = at >= 0
            ? email.substring(at + 1)
            : email;

        return domain.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isFreeEmail(String domain) {

        return domain != null
            && FREE_EMAIL_DOMAINS.contains(
                domain.trim().toLowerCase(Locale.ROOT)
            );
    }
}