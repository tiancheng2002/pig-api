package com.zhu.project.model.vo;

import com.zhu.project.model.entity.InterfaceRoute;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InterfaceRouteVo extends InterfaceRoute implements Serializable {

    private List<InterfaceRouteVo> children;

}
