package org.wavemoney.middleware.api.custommapper.kyc;

import org.wavemoney.middleware.api.custommapper.dto.CustomerQueryParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {
    void saveCustomer(CustomerQueryParam queryParam);
}