package com.zhu.project.model.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public abstract class BaseResponse implements Serializable {

    private int code;

    private String msg;

    private Map<String,Object> data;

}
