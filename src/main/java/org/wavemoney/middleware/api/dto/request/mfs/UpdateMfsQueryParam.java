package org.wavemoney.middleware.api.dto.request.mfs;

import lombok.Builder;

@Builder
public record UpdateMfsQueryParam(
        String msisdn,
        int isDelete,
        String dbSchema
) {
}
