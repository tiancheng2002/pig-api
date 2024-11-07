package com.zhu.project.model.dto.coin;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoinAddRequest implements Serializable {

    private Integer coinNum;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String description;

}
