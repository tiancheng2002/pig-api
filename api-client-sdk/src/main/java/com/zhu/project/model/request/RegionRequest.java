package com.zhu.project.model.request;

import com.zhu.project.model.enums.RegionEnums;
import com.zhu.project.model.enums.RequestMethodEnum;
import com.zhu.project.model.response.RegionResponse;
import lombok.Data;

@Data
public class RegionRequest implements BaseRequest<RegionResponse> {

    private String name;

    private RegionEnums regionEnums;

    public RegionRequest() {
        regionEnums = RegionEnums.REGION_PROVINCE;
    }

    @Override
    public String getApiMethodName() {
        return RequestMethodEnum.GET.getValue();
    }

    @Override
    public String getApiRequestPath() {
        return "/region/"+regionEnums.getValue();
    }

    @Override
    public Class<RegionResponse> getResponseClass() {
        return RegionResponse.class;
    }

}
