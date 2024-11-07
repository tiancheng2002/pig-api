package com.zhu.modal.dto.region;

import lombok.Data;

import java.io.Serializable;

@Data
public class CityQueryRequest implements Serializable {

    private String name;

    private Long provinceCode;

}