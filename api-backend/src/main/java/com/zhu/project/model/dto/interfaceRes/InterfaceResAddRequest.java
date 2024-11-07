package com.zhu.project.model.dto.interfaceRes;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceResAddRequest implements Serializable {

    /**
     * 返回值名称
     */
    private String resName;

    /**
     * 返回值类型
     */
    private String resType;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 接口id
     */
    private Long infoId;

}
