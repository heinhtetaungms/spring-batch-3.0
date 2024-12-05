package org.wavemoney.middleware.api.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRecord{
    private String msisdn;
    private double balance;
    private String category;
    private int status;
}
