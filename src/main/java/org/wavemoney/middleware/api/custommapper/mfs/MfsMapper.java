package org.wavemoney.middleware.api.custommapper.mfs;

import org.wavemoney.middleware.api.dto.request.mfs.SearchMfsQueryParam;
import org.wavemoney.middleware.api.dto.request.mfs.UpdateMfsQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.wavemoney.middleware.api.dto.response.mfs.Mfs;

@Mapper
public interface MfsMapper {
    int updateMfs(UpdateMfsQueryParam queryParam);
    Mfs getMfs(SearchMfsQueryParam queryParam);
}