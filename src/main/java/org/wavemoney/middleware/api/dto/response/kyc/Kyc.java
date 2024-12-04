package org.wavemoney.middleware.api.dto.response.kyc;

import lombok.Builder;

@Builder
public record Kyc(
        int id,
        String msisdn,
        String name,
        int status
) {
}
