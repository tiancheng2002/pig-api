package com.zhu.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.project.model.entity.InterfaceInfo;
import com.zhu.project.model.vo.InterfaceInfoVo;

/**
 * 接口信息服务
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    InterfaceInfoVo getInterfaceInfoByIdVo(Long id);

}
