package org.wavemoney.middleware.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CustomerRecord {
    private String msisdn;
    private double balance;
    private String category;
    private int status;
}
