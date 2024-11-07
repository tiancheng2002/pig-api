package com.zhu.modal.dto.region;

import lombok.Data;

@Data
public class RegionQueryRequest {

    private Long code;

    private String name;

    private Long provinceCode;

    private Long cityCode;

    private Long areaCode;

}
