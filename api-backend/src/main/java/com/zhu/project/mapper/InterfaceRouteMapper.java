package com.zhu.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhu.project.model.entity.InterfaceRoute;
import com.zhu.project.model.vo.InterfaceRouteVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xiaozhu
 * @since 2024-03-08
 */
public interface InterfaceRouteMapper extends BaseMapper<InterfaceRoute> {

    List<InterfaceRouteVo> listInterfaceRoute();

}
