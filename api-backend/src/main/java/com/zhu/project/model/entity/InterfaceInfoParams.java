package com.zhu.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口信息
 *
 */
@TableName(value ="interface_params")
@Data
public class InterfaceInfoParams implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
