package org.wavemoney.middleware.api.dto.response.kyc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kyc {
    private int id;
    private String msisdn;
    private String name;
    private int status;
}
