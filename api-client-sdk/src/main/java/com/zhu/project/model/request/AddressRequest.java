package com.zhu.project.model.request;

import com.zhu.project.model.enums.RequestMethodEnum;
import com.zhu.project.model.response.AddressResponse;
import lombok.Data;

@Data
public class AddressRequest implements BaseRequest<AddressResponse> {

    private String ip;

    @Override
    public String getApiMethodName() {
        return RequestMethodEnum.GET.getValue();
    }

    @Override
    public String getApiRequestPath() {
        return "/address/get";
    }

    @Override
    public Class<AddressResponse> getResponseClass() {
        return AddressResponse.class;
    }

}
