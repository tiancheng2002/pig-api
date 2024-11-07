package com.zhu.project.model.dto.interfaceParams;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceParamsAddRequest implements Serializable {

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 参数默认值
     */
    private String defaultValue;

    /**
     * 是否必填
     */
    private int isRequired;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 接口id
     */
    private Long infoId;

}
