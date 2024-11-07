package com.zhu.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.project.model.entity.InterfaceRoute;
import com.zhu.project.model.vo.InterfaceRouteVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2024-03-08
 */
public interface InterfaceRouteService extends IService<InterfaceRoute> {

    List<InterfaceRouteVo> listInterfaceRoute();

    void validInterfaceRoute(InterfaceRoute interfaceRoute, boolean b);

}
