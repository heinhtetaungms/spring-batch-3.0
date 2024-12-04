package org.wavemoney.middleware.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
public record CustomerRecord(
        String msisdn,
        double balance,
        String category,
        int status
) {
}
