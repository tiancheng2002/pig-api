package com.zhu.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.mapper.InterfaceRouteMapper;
import com.zhu.project.model.entity.InterfaceRoute;
import com.zhu.project.model.vo.InterfaceRouteVo;
import com.zhu.project.service.InterfaceRouteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2024-03-08
 */
@Service
public class InterfaceRouteServiceImpl extends ServiceImpl<InterfaceRouteMapper, InterfaceRoute> implements InterfaceRouteService {

    @Override
    public List<InterfaceRouteVo> listInterfaceRoute() {
        return baseMapper.listInterfaceRoute();
    }

    @Override
    public void validInterfaceRoute(InterfaceRoute interfaceRoute, boolean add) {
        if (interfaceRoute == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = interfaceRoute.getTitle();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isEmpty(title)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }

}
