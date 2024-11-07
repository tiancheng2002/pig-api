package com.zhu.modal.dto.region;

import lombok.Data;

import java.io.Serializable;

@Data
public class StreetQueryRequest implements Serializable {

    private String name;

    private Long areaCode;

    private Long cityCode;

    private Long provinceCode;

}
