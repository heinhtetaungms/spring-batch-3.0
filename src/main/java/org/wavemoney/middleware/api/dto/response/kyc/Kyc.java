package org.wavemoney.middleware.api.dto.response.kyc;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Builder
@Data
public class Kyc {
    private int id;
    private String msisdn;
    private String name;
    private int status;
}
