package com.internshield.dto;

import java.util.List;

public record CompanyVerificationResponse(

        int score,

        String verificationStatus,

        String companyName,

        String website,

        String websiteDomain,

        String email,

        String emailDomain,

        boolean emailDomainMatches,

        boolean publicEmailProvider,

        List<String> findings,

        List<String> recommendations
) {
}