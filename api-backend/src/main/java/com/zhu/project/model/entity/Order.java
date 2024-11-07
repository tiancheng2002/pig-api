package com.zhu.project.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author xiaozhu
 * @since 2024-02-25
 */
@TableName("order_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String orderId;

    private String orderName;

    private Integer orderType;

    private BigDecimal orderPrice;

    private String description;

    private Integer payType;

    /**
     * 订单状态 -1.已取消 0.待支付 1.已支付
     */
    private Integer orderStatus;

    private Long productId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private Date payTime;

}
