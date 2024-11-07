package com.zhu.project.model.dto.coin;

import com.zhu.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CoinQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private Integer coinNum;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String description;

}
