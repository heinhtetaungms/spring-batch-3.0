package org.wavemoney.middleware.api.dto.request.kyc;

import lombok.Builder;

@Builder
public record SearchKycQueryParam(
        String msisdn,
        String dbSchema
) {
}
