package com.zhu.project.model.dto.interfaceInfo;

import com.zhu.project.model.dto.interfaceParams.InterfaceParamsInvokeRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接口调用请求
 *
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户请求参数
     */
    private String userRequestParams;

    private List<InterfaceParamsInvokeRequest> requestParams;

    private static final long serialVersionUID = 1L;
}

