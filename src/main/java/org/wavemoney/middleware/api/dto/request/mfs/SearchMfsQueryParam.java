package org.wavemoney.middleware.api.dto.request.mfs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchMfsQueryParam {
    private String msisdn;
    private String dbSchema;
}
