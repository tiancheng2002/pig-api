package com.zhu.project.model.dto.order;

import com.zhu.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderQueryRequest extends PageRequest implements Serializable {

    private String orderId;

    private String orderName;

    private Integer orderType;

    private BigDecimal orderPrice;

    private String description;

    private Integer payType;

    /**
     * 订单状态 1.未支付 2.已支付 3.已取消
     */
    private Integer orderStatus;

    private Long productId;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private Date failureTime;

}
