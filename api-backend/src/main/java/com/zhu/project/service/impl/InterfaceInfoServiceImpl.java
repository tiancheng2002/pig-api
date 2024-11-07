package com.zhu.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.mapper.InterfaceInfoMapper;
import com.zhu.project.model.entity.InterfaceInfo;
import com.zhu.project.model.entity.InterfaceInfoParams;
import com.zhu.project.model.entity.InterfaceInfoRes;
import com.zhu.project.model.vo.InterfaceInfoVo;
import com.zhu.project.service.InterfaceInfoParamsService;
import com.zhu.project.service.InterfaceInfoResService;
import com.zhu.project.service.InterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 接口信息服务实现类
 *
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private InterfaceInfoParamsService interfaceInfoParamsService;

    @Resource
    private InterfaceInfoResService interfaceInfoResService;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public InterfaceInfoVo getInterfaceInfoByIdVo(Long id) {
        InterfaceInfo interfaceInfo = getById(id);
        if(interfaceInfo==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfoVo interfaceInfoVo = new InterfaceInfoVo();
        BeanUtils.copyProperties(interfaceInfo,interfaceInfoVo);
        QueryWrapper<InterfaceInfoParams> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("infoId",id);
        interfaceInfoVo.setRequestParams(interfaceInfoParamsService.list(queryWrapper));
        QueryWrapper<InterfaceInfoRes> resQueryWrapper = new QueryWrapper<>();
        resQueryWrapper.eq("infoId",id);
        interfaceInfoVo.setResponseParams(interfaceInfoResService.list(resQueryWrapper));
        return interfaceInfoVo;
    }

}
