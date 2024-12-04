package org.wavemoney.middleware.api.dto.request.mfs;

import lombok.Builder;

@Builder
public record SearchMfsQueryParam(
        String msisdn,
        String dbSchema
) {
}
