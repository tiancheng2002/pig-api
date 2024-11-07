package com.zhu.project.model.dto.interfaceRoute;

import com.zhu.project.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceRouteQueryRequest extends PageRequest implements Serializable {

    private String title;

    private String route;

    private Integer status;

}
