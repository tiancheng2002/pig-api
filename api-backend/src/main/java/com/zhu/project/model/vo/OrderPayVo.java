package com.zhu.project.model.vo;

import com.zhu.project.model.entity.Order;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPayVo extends Order implements Serializable {

//    private String appId;

    private String qrCodeUrl;

}
