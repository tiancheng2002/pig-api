package com.zhu.project.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.client.ApiClient;
import com.zhu.project.annotation.AuthCheck;
import com.zhu.project.common.*;
import com.zhu.project.constant.CommonConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.zhu.project.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.zhu.project.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.zhu.project.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.zhu.project.model.dto.interfaceParams.InterfaceParamsAddRequest;
import com.zhu.project.model.dto.interfaceParams.InterfaceParamsInvokeRequest;
import com.zhu.project.model.dto.interfaceRes.InterfaceResAddRequest;
import com.zhu.project.model.dto.interfaceRoute.InterfaceRouteQueryRequest;
import com.zhu.project.model.entity.InterfaceInfo;
import com.zhu.project.model.entity.InterfaceInfoParams;
import com.zhu.project.model.entity.InterfaceInfoRes;
import com.zhu.project.model.entity.User;
import com.zhu.project.model.enums.InterfaceInfoStatusEnum;
import com.zhu.project.model.vo.InterfaceInfoVo;
import com.zhu.project.model.vo.InterfaceRouteVo;
import com.zhu.project.service.*;
import com.zhu.project.utils.BeanCopeUtils;
import com.zhu.project.utils.RedisUtils;
import com.zhu.project.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 接口管理
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/interfaceInfo")
@Slf4j
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoParamsService interfaceInfoParamsService;

    @Resource
    private InterfaceInfoResService interfaceInfoResService;

    @Resource
    private InterfaceRouteService interfaceRouteService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private ApiClient apiClient;

    // region 增删改查

    /**
     * 创建
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<InterfaceParamsAddRequest> paramsList = interfaceInfoAddRequest.getRequestParams();
        List<InterfaceResAddRequest> resList = interfaceInfoAddRequest.getResponseParams();
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        // 校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //获取到接口的id值
        long newInterfaceInfoId = interfaceInfo.getId();
        //这里需要判断请求参数以及响应参数是否有值，如果没有的话就不需要转换实体类
        if(!CollectionUtils.isEmpty(paramsList)){
            List<InterfaceInfoParams> interfaceInfoParams = BeanCopeUtils.copyBeanList(paramsList,InterfaceInfoParams.class);
            //将请求参数添加到数据库中；如果传入的请求参数的id值不为空的时候就代表插入，否则就更新
            interfaceInfoParams.stream().forEach(params -> params.setInfoId(newInterfaceInfoId));
            result = interfaceInfoParamsService.saveBatch(interfaceInfoParams);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }
        if(!CollectionUtils.isEmpty(resList)){
            List<InterfaceInfoRes> interfaceInfoRes = BeanCopeUtils.copyBeanList(resList,InterfaceInfoRes.class);
            interfaceInfoRes.stream().forEach(res -> res.setInfoId(newInterfaceInfoId));
            result = interfaceInfoResService.saveBatch(interfaceInfoRes);
            if (!result) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }
        //将响应参数添加到数据库中；如果传入的响应参数的id值不为空的时候就代表插入，否则就更新
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        //删除该接口关联的请求参数和返回参数
        interfaceInfoParamsService.remove(new QueryWrapper<InterfaceInfoParams>().eq("infoId",id));
        interfaceInfoResService.remove(new QueryWrapper<InterfaceInfoRes>().eq("infoId",id));
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                     HttpServletRequest request) {
        log.info("更新参数：{}",interfaceInfoUpdateRequest);
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(interfaceInfo, false);
        User user = userService.getLoginUser(request);
        long id = interfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //更新请求参数以及返回参数，首先会判断主键id是否有值，有的话就更新没有就添加，并且会过滤掉没有的参数，然后删除掉
        //1.先查询该接口的所有请求参数ids以及返回参数ids
        List<InterfaceInfoParams> oldReqParams = interfaceInfoParamsService.list(new QueryWrapper<InterfaceInfoParams>().eq("infoId", id));
        List<InterfaceInfoParams> newReqParams = interfaceInfoUpdateRequest.getRequestParams();
        List<Long> delReqParamIds = oldReqParams.stream().filter(params ->
                !newReqParams.contains(params)
        ).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(newReqParams))
            result = interfaceInfoParamsService.saveOrUpdateBatch(newReqParams);
        if(!CollectionUtils.isEmpty(delReqParamIds))
            interfaceInfoParamsService.removeBatchByIds(delReqParamIds);
        if(!result)
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        //更新接口的响应参数信息
        List<InterfaceInfoRes> oldResParams = interfaceInfoResService.list(new QueryWrapper<InterfaceInfoRes>().eq("infoId", id));
        List<InterfaceInfoRes> newResParams = interfaceInfoUpdateRequest.getResponseParams();
        List<Long> delResParamIds = oldResParams.stream().filter(params ->
                !newResParams.contains(params)
        ).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(newResParams))
            result = interfaceInfoResService.saveOrUpdateBatch(newResParams);
        if(!CollectionUtils.isEmpty(delResParamIds))
            interfaceInfoResService.removeBatchByIds(delResParamIds);
        if(!result)
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceInfoVo> getInterfaceInfoById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfoVo interfaceInfoByIdVo = interfaceInfoService.getInterfaceInfoByIdVo(id);
//        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        return ResultUtils.success(interfaceInfoByIdVo);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceInfo>> listInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        if (interfaceInfoQueryRequest != null) {
            BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(queryWrapper);
        return ResultUtils.success(interfaceInfoList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfoByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest, HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String description = interfaceInfoQuery.getDescription();
        // description 需支持模糊搜索
        interfaceInfoQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>(interfaceInfoQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceInfoPage);
    }

    // endregion

    /**
     * 发布
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                     HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断该接口是否可以调用（直接修改接口状态）
//        com.zhu.project.model.User user = new com.zhu.project.model.User();
//        user.setUsername("test");
//        System.out.println(apiClient);
//        String username = apiClient.getUsernameByPost(user);
//        if (StringUtils.isBlank(username)) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "接口验证失败");
//        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线
     *
     * @param idRequest
     * @param request
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest,
                                                      HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 测试调用
     *
     * @param interfaceInfoInvokeRequest
     * @param request
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest,
                                                    HttpServletRequest request) {
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已关闭");
        }
        String method = oldInterfaceInfo.getMethod();
        String url = oldInterfaceInfo.getUrl();
        List<InterfaceParamsInvokeRequest> requestParams = interfaceInfoInvokeRequest.getRequestParams();
        //根据请求接口信息判断是get还是post请求，并添加请求头以及请求参数信息进行调用，调用完成之后返回调用结果
        //调用网关层
        User loginUser = userService.getLoginUser(request);
        RequestUtils requestUtils = new RequestUtils();
            requestUtils.setApiClient(new ApiClient(loginUser.getAccessKey(),loginUser.getSecretKey()));
        HttpResponse httpResponse = null;
        try {
            httpResponse = requestUtils.request(url, method, requestParams);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(httpResponse==null){
            throw new BusinessException(ErrorCode.REQUEST_ERROR);
        }
        //获取请求接口的返回值
        String resResult = httpResponse.body();
        //如果返回的响应值中有code，那就说明调用过程中出现了错误，直接返回错误信息即可
        Map<String,Object> map = (Map<String, Object>) JSONUtil.parse(resResult);
        if(map.get("code")!=null){
            return ResultUtils.success(map);
//            return ResultUtils.error((int)map.get("code"),(String) map.get("errorMessage"));
        }
//        Gson gson = new Gson();
//        com.zhu.project.model.User user = gson.fromJson(userRequestParams, com.zhu.project.model.User.class);
//        String usernameByPost = tempClient.getUsernameByPost(user);
        return ResultUtils.success(map.get("data"));
    }

    @GetMapping("/route")
    public BaseResponse<List<InterfaceRouteVo>> listInterfaceRoute(){
        //获取接口路由信息:先从缓存当中查询，如果缓存当中没有的话就去数据库中查询，查询完毕之后存储到缓存当中
        List<InterfaceRouteVo> interfaceRouteVos = (List<InterfaceRouteVo>) JSON.parse(String.valueOf(redisUtils.get("interface_route")));
        if(interfaceRouteVos==null){
            interfaceRouteVos = interfaceRouteService.listInterfaceRoute();
            redisUtils.set("interface_route",JSON.toJSONString(interfaceRouteVos));
        }
        return ResultUtils.success(interfaceRouteVos);
    }

}

