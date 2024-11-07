package com.zhu.project.model.dto.interfaceRoute;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceRouteUpdateRequest implements Serializable {

    private Long id;

    private Long parentId;

    private String title;

    private String route;

    private Integer type;

    private Integer orderNum;

    private Integer interfaceId;

    private Integer status;

}
