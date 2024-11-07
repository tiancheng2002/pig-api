package com.zhu.project.model.request;

import com.zhu.project.model.enums.RequestMethodEnum;
import com.zhu.project.model.response.WallpaperResponse;
import lombok.Data;

@Data
public class WallpaperRequest implements BaseRequest<WallpaperResponse> {

    @Override
    public String getApiMethodName() {
        return RequestMethodEnum.GET.getValue();
    }

    @Override
    public String getApiRequestPath() {
        return "/wallpaper/";
    }

    @Override
    public Class<WallpaperResponse> getResponseClass() {
        return WallpaperResponse.class;
    }

}
