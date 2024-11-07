package com.zhu.project.model.vo;

import com.zhu.project.model.entity.InterfaceInfo;
import com.zhu.project.model.entity.InterfaceInfoParams;
import com.zhu.project.model.entity.InterfaceInfoRes;
import lombok.Data;

import java.util.List;

@Data
public class InterfaceInfoVo extends InterfaceInfo {

    private List<InterfaceInfoParams> requestParams;

    private List<InterfaceInfoRes> responseParams;

}
