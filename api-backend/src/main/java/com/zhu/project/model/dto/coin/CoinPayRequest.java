package com.zhu.project.model.dto.coin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CoinPayRequest implements Serializable {

    private Long id;

    private Integer coinNum;

}
