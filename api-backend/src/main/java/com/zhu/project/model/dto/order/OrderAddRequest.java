package com.zhu.project.model.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderAddRequest implements Serializable {

    private String orderId;

    private String orderName;

    /**
     * 订单类型：z币充值，开通会员等
     */
    private Integer orderType;

    private BigDecimal orderPrice;

    private String description;

    private Integer coinNum;

    /**
     * 支付类型 1.支付宝 2.微信
     */
    private Integer payType;

    private Long productId;

    private Long userId;

}
