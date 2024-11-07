package com.zhu.project.model.dto.interfaceInfo;

import com.zhu.project.model.dto.interfaceParams.InterfaceParamsAddRequest;
import com.zhu.project.model.dto.interfaceRes.InterfaceResAddRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接口创建请求
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * Z币数量
     */
    private Integer zCoin;

//    /**
//     * 请求参数
//     */
//    private String requestParams;

    private List<InterfaceParamsAddRequest> requestParams;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    private List<InterfaceResAddRequest> responseParams;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 返回示例
     */
    private String resExample;

}
