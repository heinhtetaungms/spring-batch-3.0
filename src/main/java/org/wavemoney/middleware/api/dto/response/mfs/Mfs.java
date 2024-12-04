package org.wavemoney.middleware.api.dto.response.mfs;

import lombok.Builder;


@Builder
public record Mfs(
        int id,
        String msisdn,
        double balance,
        String category,
        int isDelete
) {
}
