package com.zhu.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.project.annotation.AuthCheck;
import com.zhu.project.common.BaseResponse;
import com.zhu.project.common.DeleteRequest;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.common.ResultUtils;
import com.zhu.project.constant.CommonConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.interfaceRoute.InterfaceRouteAddRequest;
import com.zhu.project.model.dto.interfaceRoute.InterfaceRouteQueryRequest;
import com.zhu.project.model.dto.interfaceRoute.InterfaceRouteUpdateRequest;
import com.zhu.project.model.entity.InterfaceRoute;
import com.zhu.project.service.InterfaceRouteService;
import com.zhu.project.service.UserService;
import com.zhu.project.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/interfaceRoute")
public class InterfaceRouteController {

    @Resource
    private InterfaceRouteService interfaceRouteService;
    
    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 创建
     *
     * @param interfaceRouteAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Long> addInterfaceRoute(@RequestBody InterfaceRouteAddRequest interfaceRouteAddRequest, HttpServletRequest request) {
        if (interfaceRouteAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceRoute interfaceRoute = new InterfaceRoute();
        BeanUtils.copyProperties(interfaceRouteAddRequest, interfaceRoute);
        // 校验
        interfaceRouteService.validInterfaceRoute(interfaceRoute, true);
        boolean result = interfaceRouteService.save(interfaceRoute);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 删除缓存当中的接口路由信息
        redisUtils.remove("interface_route");
        return ResultUtils.success(interfaceRoute.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceRoute(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceRoute oldInterfaceRoute = interfaceRouteService.getById(id);
        if (oldInterfaceRoute == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<InterfaceRoute> interfaceRouteDelQueryWrapper = new QueryWrapper<>();
        interfaceRouteDelQueryWrapper.eq("id",id).or().eq("parentId",id);
        boolean b = interfaceRouteService.remove(interfaceRouteDelQueryWrapper);
        redisUtils.remove("interface_route");
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param interfaceRouteUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateInterfaceRoute(@RequestBody InterfaceRouteUpdateRequest interfaceRouteUpdateRequest,
                                            HttpServletRequest request) {
        if (interfaceRouteUpdateRequest == null || interfaceRouteUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceRoute interfaceRoute = new InterfaceRoute();
        BeanUtils.copyProperties(interfaceRouteUpdateRequest, interfaceRoute);
        // 参数校验
        interfaceRouteService.validInterfaceRoute(interfaceRoute, false);
        long id = interfaceRouteUpdateRequest.getId();
        // 判断是否存在
        InterfaceRoute oldInterfaceRoute = interfaceRouteService.getById(id);
        if (oldInterfaceRoute == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = interfaceRouteService.updateById(interfaceRoute);
        redisUtils.remove("interface_route");
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<InterfaceRoute> getInterfaceRouteById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceRoute interfaceRoute = interfaceRouteService.getById(id);
        return ResultUtils.success(interfaceRoute);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param interfaceRouteQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<InterfaceRoute>> listInterfaceRoute(InterfaceRouteQueryRequest interfaceRouteQueryRequest) {
        InterfaceRoute interfaceRouteQuery = new InterfaceRoute();
        if (interfaceRouteQueryRequest != null) {
            BeanUtils.copyProperties(interfaceRouteQueryRequest, interfaceRouteQuery);
        }
        QueryWrapper<InterfaceRoute> queryWrapper = new QueryWrapper<>(interfaceRouteQuery);
        List<InterfaceRoute> interfaceRouteList = interfaceRouteService.list(queryWrapper);
        return ResultUtils.success(interfaceRouteList);
    }

    /**
     * 分页获取列表
     *
     * @param interfaceRouteQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceRoute>> listInterfaceRouteByPage(InterfaceRouteQueryRequest interfaceRouteQueryRequest, HttpServletRequest request) {
        if (interfaceRouteQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceRoute interfaceRouteQuery = new InterfaceRoute();
        BeanUtils.copyProperties(interfaceRouteQueryRequest, interfaceRouteQuery);
        long current = interfaceRouteQueryRequest.getCurrent();
        long size = interfaceRouteQueryRequest.getPageSize();
        String sortField = interfaceRouteQueryRequest.getSortField();
        String sortOrder = interfaceRouteQueryRequest.getSortOrder();
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceRoute> queryWrapper = new QueryWrapper<>(interfaceRouteQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceRoute> interfaceRoutePage = interfaceRouteService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(interfaceRoutePage);
    }

}
