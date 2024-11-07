package com.zhu.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhu.project.annotation.AuthCheck;
import com.zhu.project.common.BaseResponse;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.common.ResultUtils;
import com.zhu.project.constant.CommonConstant;
import com.zhu.project.constant.RedisConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.order.OrderAddRequest;
import com.zhu.project.model.dto.order.OrderQueryRequest;
import com.zhu.project.model.entity.Order;
import com.zhu.project.model.entity.User;
import com.zhu.project.model.vo.OrderPayVo;
import com.zhu.project.service.OrderService;
import com.zhu.project.service.UserService;
import com.zhu.project.utils.BeanCopeUtils;
import com.zhu.project.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xiaozhu
 * @since 2024-02-25
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    @PostMapping("/add")
    public BaseResponse<Long> createOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request){
        if(orderAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Order order = BeanCopeUtils.copyBean(orderAddRequest, Order.class);
        order.setUserId(loginUser.getId());
        return null;
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Order> getOrderById(String orderId){
        if(StringUtils.isEmpty(orderId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order order = orderService.getById(orderId);
        if(order==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(order);
    }

    @GetMapping("/get/vo")
    public BaseResponse<OrderPayVo> getOrderVo(String orderId, HttpServletRequest request){
        if(StringUtils.isEmpty(orderId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Order order = orderService.getById(orderId);
        //如果订单中的用户id跟实际操作的用户不一致的话，直接返回错误
        if(order==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(!order.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //获取订单的详情数据，包括订单的支付链接
        OrderPayVo orderPayVo = BeanCopeUtils.copyBean(order, OrderPayVo.class);
        //从redis当中获取当前订单的支付链接
        String QRCode = (String) redisUtils.hget(RedisConstant.ORDER_QR_CODE, orderId);
        orderPayVo.setQrCodeUrl(QRCode);
        return ResultUtils.success(orderPayVo);
    }

    /**
     * 分页获取用户订单列表
     *
     * @param request
     * @return
     */
    @GetMapping("/user/list")
    public BaseResponse<Page<Order>> listOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        if(orderQueryRequest==null || request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Order orderQuery = new Order();
        BeanUtils.copyProperties(orderQueryRequest, orderQuery);
        long current = orderQueryRequest.getCurrent();
        long size = orderQueryRequest.getPageSize();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();
        String description = orderQuery.getDescription();
        // description 需支持模糊搜索
        orderQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>(orderQuery);
        queryWrapper.eq("userId",loginUser.getId());
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderByDesc("createTime");
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Order> orderPage = orderService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(orderPage);
    }

    /**
     * 获取订单列表（仅管理员可使用）
     *
     * @param orderQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Order>> listOrder(OrderQueryRequest orderQueryRequest) {
        Order orderQuery = new Order();
        if (orderQueryRequest != null) {
            BeanUtils.copyProperties(orderQueryRequest, orderQuery);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>(orderQuery);
        List<Order> orderList = orderService.list(queryWrapper);
        return ResultUtils.success(orderList);
    }

    /**
     * 分页获取列表
     *
     * @param orderQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<Order>> listOrderByPage(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        if (orderQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order orderQuery = new Order();
        BeanUtils.copyProperties(orderQueryRequest, orderQuery);
        long current = orderQueryRequest.getCurrent();
        long size = orderQueryRequest.getPageSize();
        String sortField = orderQueryRequest.getSortField();
        String sortOrder = orderQueryRequest.getSortOrder();
        String description = orderQuery.getDescription();
        // description 需支持模糊搜索
        orderQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>(orderQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Order> orderPage = orderService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(orderPage);
    }

    /**
     * 删除订单数据
     * @param orderId
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOrder(String orderId, HttpServletRequest request){
        if(StringUtils.isEmpty(orderId)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Order order = orderService.getById(orderId);
        if(order==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser.getId()!=order.getUserId()){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(orderService.removeById(orderId));
    }
    
}
