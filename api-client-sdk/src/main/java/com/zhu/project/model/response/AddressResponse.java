package com.zhu.project.model.response;

import lombok.Data;

@Data
public class AddressResponse extends BaseResponse {

    private String pro;

    private Long proCode;

    private String city;

    private Long cityCode;

    private String region;

    private Long regionCode;

    private String addr;

    private String regionNames;

    private String err;

}
