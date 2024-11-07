package com.zhu.project.model.dto.coin;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoinUpdateRequest implements Serializable {

    private Long id;

    private Integer coinNum;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private Integer status;

    private String description;

}
