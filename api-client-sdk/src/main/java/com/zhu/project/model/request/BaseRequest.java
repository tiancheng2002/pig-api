package com.zhu.project.model.request;

import com.zhu.project.model.response.BaseResponse;

public interface BaseRequest<T extends BaseResponse> {

    String getApiMethodName();

    String getApiRequestPath();

    Class<T> getResponseClass();

}
