package com.zhu.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zhu.project.model.request.AddressRequest;
import com.zhu.project.model.request.RegionRequest;
import com.zhu.project.model.request.WallpaperRequest;
import com.zhu.project.model.response.AddressResponse;
import com.zhu.project.model.response.RegionResponse;
import com.zhu.project.model.response.WallpaperResponse;
import com.zhu.project.model.User;
import com.zhu.service.ApiService;
import lombok.Data;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.zhu.utils.SignUtils.genSign;

/**
 * 调用第三方接口客户端
 * 里面有三个调用对应接口的方法
 */
@Data
public class ApiClient {

    private final String url = "http://localhost:8123";

    private String accessKey;

    private String secretKey;

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Resource
    private ApiService apiService;

    public WallpaperResponse randomWallpaper(WallpaperRequest wallpaperRequest){
        return apiService.request(wallpaperRequest);
    }

    public AddressResponse IpAddressDetail(AddressRequest addressRequest){
        return apiService.request(addressRequest);
    }

    public RegionResponse regionData(RegionRequest regionRequest){
        return apiService.request(regionRequest);
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(url + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(url + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        // 一定不能直接发送
//        hashMap.put("secretKey", secretKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", genSign(body, secretKey));
        return hashMap;
    }

    public String getUsernameByPost(User user) {
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(url + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String result = httpResponse.body();
        System.out.println(result);
        return result;
    }


}
