package org.wavemoney.middleware.api.dto.request.kyc;

import lombok.Builder;

@Builder
public record UpdateKycQueryParam(
        String msisdn,
        int status,
        String dbSchema
) {
}
