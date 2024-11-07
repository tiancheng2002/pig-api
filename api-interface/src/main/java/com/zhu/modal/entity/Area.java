package com.zhu.modal.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2023-12-29
 */
@TableName("t_area")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long code;

    private String name;

    private Long cityCode;

    private Long provinceCode;

}
