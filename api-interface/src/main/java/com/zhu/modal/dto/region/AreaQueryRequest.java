package com.zhu.modal.dto.region;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaQueryRequest implements Serializable {

    private String name;

    private Long cityCode;

    private Long provinceCode;

}
