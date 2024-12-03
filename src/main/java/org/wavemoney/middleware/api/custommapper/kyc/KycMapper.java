package org.wavemoney.middleware.api.custommapper.kyc;

import org.apache.ibatis.annotations.Mapper;
import org.wavemoney.middleware.api.dto.request.kyc.SearchKycQueryParam;
import org.wavemoney.middleware.api.dto.request.kyc.UpdateKycQueryParam;
import org.wavemoney.middleware.api.dto.response.kyc.Kyc;

@Mapper
public interface KycMapper {
    int updateKyc(UpdateKycQueryParam queryParam);
    Kyc getKyc(SearchKycQueryParam queryParam);
}