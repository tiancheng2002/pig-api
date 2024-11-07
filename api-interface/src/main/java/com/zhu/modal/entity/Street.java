package com.zhu.modal.entity;

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
@TableName("t_street")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Street implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long code;

    private String name;

    private Long areaCode;

    private Long cityCode;

    private Long provinceCode;

}
