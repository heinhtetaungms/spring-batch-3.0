package org.wavemoney.middleware.api.dto.response.mfs;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class Mfs{
    private int id;
    private String msisdn;
    private double balance;
    private String category;
    private int isDelete;
}
