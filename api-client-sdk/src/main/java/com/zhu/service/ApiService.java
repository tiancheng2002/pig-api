package com.zhu.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.zhu.client.ApiClient;
import com.zhu.exception.ApiException;
import com.zhu.exception.ErrorCode;
import com.zhu.exception.ErrorResponse;
import com.zhu.project.model.request.BaseRequest;
import com.zhu.project.model.response.BaseResponse;
import com.zhu.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class ApiService {

    private ApiClient apiClient;

    //网关地址(后面可以配置到yml文件当中)
    private final String GATEWAY_HOST = "http://localhost:8090/api";

//    private final String GATEWAY_HOST = "http://116.62.178.128:8090/api";

    public <T extends BaseResponse> T request(BaseRequest<T> request){
        try {
            return res(request);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    public <T extends BaseResponse> T res(BaseRequest<T> request) throws ApiException {
        if (apiClient == null || StringUtils.isAnyBlank(apiClient.getAccessKey(), apiClient.getSecretKey())) {
            throw new ApiException(ErrorCode.NO_AUTH_ERROR, "请先配置密钥AccessKey/SecretKey");
        }
        T rsp;
        try {
            Class<T> clazz = request.getResponseClass();
            rsp = clazz.newInstance();
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
        HttpResponse httpResponse = doRequest(request);
        String body = httpResponse.body();
        Map<String, Object> data = new HashMap<>();
        if (httpResponse.getStatus() != 200) {
            //todo 可能返回的不一定是含有错误信息的，也有可能是403
            ErrorResponse errorResponse = JSONUtil.toBean(body, ErrorResponse.class);
            data.put("errorMessage", errorResponse.getErrorMessage());
            data.put("code", errorResponse.getCode());
        } else {
            try {
                // 尝试解析为JSON对象
                // 将对象转换成Map<String,Object>形式，表示接口调用参数
                data = new Gson().fromJson(body, new TypeToken<Map<String, Object>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                System.out.println("解析出错啦");
                // 解析失败，将body作为普通字符串处理
                data.put("value", body);
            }
        }
        rsp.setData(data);
        return rsp;
    }

    private <T extends BaseResponse> HttpResponse doRequest(BaseRequest<T> request) throws ApiException {
        try (HttpResponse httpResponse = getHttpRequestByRequestMethod(request).execute()) {
            return httpResponse;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, e.getMessage());
        }
    }

    private <T extends BaseResponse> HttpRequest getHttpRequestByRequestMethod(BaseRequest<T> request) throws ApiException {
        if (ObjectUtils.isEmpty(request)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求参数错误");
        }
        String path = request.getApiRequestPath().trim();
        String method = request.getApiMethodName().trim().toUpperCase();

        if (ObjectUtils.isEmpty(method)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求方法不存在");
        }
        if (StringUtils.isBlank(path)) {
            throw new ApiException(ErrorCode.OPERATION_ERROR, "请求路径不存在");
        }
        path = GATEWAY_HOST + path;
        Map<String,Object> params = new Gson().fromJson(JSONUtil.toJsonStr(request), new TypeToken<Map<String, Object>>() {
        }.getType());
        log.info("请求方法：{}，请求路径：{}，请求参数：{}", method, path, params);
        HttpRequest httpRequest;
        switch (method) {
            case "GET": {
                httpRequest = HttpRequest.get(splicingParams(path, params));
                break;
            }
            case "POST": {
                httpRequest = HttpRequest.post(path);
                break;
            }
            default: {
                throw new ApiException(ErrorCode.OPERATION_ERROR, "不支持该请求");
            }
        }
        return httpRequest.addHeaders(getHeaders(path)).body(JSONUtil.toJsonStr(params));
    }

    public String splicingParams(String url,Map<String,Object> params){
        StringBuilder urlBuilder = new StringBuilder();
        // urlBuilder最后是/结尾且path以/开头的情况下，去掉urlBuilder结尾的/
        if (urlBuilder.toString().endsWith("/") && url.startsWith("/")) {
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        urlBuilder.append(url);
        if (!params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                urlBuilder.append(key).append("=").append(value).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        log.info("GET请求路径：{}", urlBuilder);
        return urlBuilder.toString();
    }

    private Map<String, String> getHeaders(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", apiClient.getAccessKey());
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        String encodeBody = SecureUtil.md5(body);
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(encodeBody, apiClient.getSecretKey()));
        return hashMap;
    }

}