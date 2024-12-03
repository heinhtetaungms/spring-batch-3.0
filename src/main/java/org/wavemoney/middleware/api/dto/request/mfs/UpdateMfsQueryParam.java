package org.wavemoney.middleware.api.dto.request.mfs;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMfsQueryParam {
    private String msisdn;
    private int isDelete;
    private String dbSchema;
}
