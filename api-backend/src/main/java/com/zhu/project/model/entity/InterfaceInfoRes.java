package com.zhu.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息
 *
 */
@TableName(value ="interface_res")
@Data
public class InterfaceInfoRes implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
