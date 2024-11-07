package com.zhu.project.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.zhu.client.ApiClient;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.interfaceParams.InterfaceParamsInvokeRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zhu.utils.SignUtils.genSign;

@Data
public class RequestUtils {

    private ApiClient apiClient;

    public HttpResponse request(String url, String method, List<InterfaceParamsInvokeRequest> params){
        HttpRequest httpRequest;
        switch (method){
            case "GET":
                httpRequest = HttpRequest.get(splicingParams(url,params));
                break;
            case "POST":
                httpRequest = HttpRequest.post(url);
                break;
            default:
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"请求方法错误");
        }
        return httpRequest.addHeaders(getHeaderMap(url)).body(JSONUtil.toJsonStr(convertParams(params))).execute();
    }

    private Map<String,Object> convertParams(List<InterfaceParamsInvokeRequest> params){
        //请求参数为空的话会报错
        Map<String,Object> paramsMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(params)){
            paramsMap = params.stream()
                    .collect(Collectors.toMap(p -> p.getParamName(), p -> p.getParamValue()));
        }
        System.out.println(JSONUtil.toJsonStr(paramsMap));
        return paramsMap;
    }

    public String splicingParams(String url,List<InterfaceParamsInvokeRequest> params){
        StringBuilder urlBuilder = new StringBuilder();
        // urlBuilder最后是/结尾且path以/开头的情况下，去掉urlBuilder结尾的/
        if (urlBuilder.toString().endsWith("/") && url.startsWith("/")) {
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        urlBuilder.append(url);
        if (!CollectionUtils.isEmpty(params)) {
            urlBuilder.append("?");
            params.stream().forEach(p->{
                String key = p.getParamName();
                String value = p.getParamValue();
                urlBuilder.append(key).append("=").append(value).append("&");
            });
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
//        log.info("GET请求路径：{}", urlBuilder);
        return urlBuilder.toString();
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", apiClient.getAccessKey());
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        String encodeBody = SecureUtil.md5(body);
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(encodeBody, apiClient.getSecretKey()));
        return hashMap;
    }

    public static void main(String[] args) {
        System.out.println(SecureUtil.md5("1"));
//        String jsonText = "{\"errorMessage\":\"z币数量不足\",\"code\":40400}";
//        Map<String,Object> map = (Map<String, Object>) JSONUtil.parse(jsonText);
//        System.out.println(map.get("code"));
//        System.out.println(OrderUtils.oid());
    }

}
