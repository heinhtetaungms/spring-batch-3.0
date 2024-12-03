package org.wavemoney.middleware.api.dto.response.mfs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mfs {
    private int id;
    private String msisdn;
    private double balance;
    private String category;
    private int isDelete;
}
