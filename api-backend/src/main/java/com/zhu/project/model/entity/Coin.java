package com.zhu.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2024-02-23
 */
@TableName(value = "coin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coin implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer coinNum;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String description;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
