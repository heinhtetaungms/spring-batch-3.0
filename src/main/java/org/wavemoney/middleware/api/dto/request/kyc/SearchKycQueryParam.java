package org.wavemoney.middleware.api.dto.request.kyc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchKycQueryParam {
    private String msisdn;
    private String dbSchema;
}
