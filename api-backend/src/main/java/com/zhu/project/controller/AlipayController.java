package com.zhu.project.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.zhu.project.common.BaseResponse;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.common.ResultUtils;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.order.OrderAddRequest;
import com.zhu.project.model.entity.User;
import com.zhu.project.model.vo.OrderPayVo;
import com.zhu.project.service.UserService;
import com.zhu.project.service.impl.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
@Slf4j
public class AlipayController {

    @Resource
    private AlipayService alipayService;

    @Resource
    private UserService userService;

    //跳转支付宝网页进行付款
    @GetMapping("/pay")
    public void payOrder(OrderAddRequest orderAddRequest, HttpServletResponse httpResponse, HttpServletRequest request) throws IOException {
        if(orderAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String form = alipayService.pay(orderAddRequest,loginUser.getId());
        httpResponse.setContentType("text/html;charset=UTF-8");
        // 直接将完整的表单html输出到页面
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    //创建支付二维码返回前端
    /**
     * 创建支付宝支付订单
     * @param orderId 订单ID
     * @param amount 支付金额
     * @return 支付宝支付二维码链接
     */
    @GetMapping("/createOrder")
    public BaseResponse<OrderPayVo> createAlipayOrder(@RequestParam String orderId, @RequestParam BigDecimal amount) {
        try {
            String qrCodeUrl = alipayService.createPayOrder(orderId, amount);
            OrderPayVo orderPayVo = new OrderPayVo();
            orderPayVo.setQrCodeUrl(qrCodeUrl);
            return ResultUtils.success(orderPayVo);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"创建支付宝支付订单失败");
        }
    }

    /**
     * 创建支付宝支付订单
     * @return 支付宝支付二维码链接
     */
    //正在使用
    @PostMapping("/createOrder")
    public BaseResponse<String> createAlipayOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request) {
        //先提交订单，获取到对应的订单号之后就创建支付宝订单，根据订单id以及订单金额生成二维码，并且订单只在十五分钟内有效，超过时间就无法进行支付
        //取消订单方法：1、定时任务每一段时间刷新订单状态 2、RabbitMQ延迟队列更新订单状态 3、Redis过期键更新订单状态
        if(orderAddRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        try {
            String oid = alipayService.createAlipayOrder(orderAddRequest,loginUser.getId());
//            OrderPayVo orderPayVo = new OrderPayVo();
//            orderPayVo.setQrCodeUrl(qrCodeUrl);
            return ResultUtils.success(oid);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 前端轮询查询订单交易状态
     * @param oid
     * @return
     */
    @PostMapping("/tradeQuery")
    public Map<String,Object> handleTradeQuery(String oid){
        Map<String,Object> resultMap=new HashMap<>();
        try {
            System.out.println("订单id:"+oid);
            String json = alipayService.tradeQuery(oid);
            System.out.println(json);
            JSONObject jsonObject=JSONObject.parseObject(json);
            JSONObject jsonobj_two=(JSONObject)jsonObject.get("alipay_trade_query_response");
            //网关返回码,详见文档 https://opendocs.alipay.com/open/common/105806
            String ZFBCode=(String)jsonobj_two.get("code");
            //业务返回码
            String ZFBSubCode=(String)jsonobj_two.get("sub_code");
            //业务返回码描述
            String sub_msg=(String)jsonobj_two.get("sub_msg");
            //交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
            String trade_status=(String)jsonobj_two.get("trade_status");
            if (ZFBCode.equals("40004") && ZFBSubCode.equals("ACQ.TRADE_NOT_EXIST")) {
                //订单未创建（用户未扫码）
                resultMap.put("code", ZFBCode);
                resultMap.put("data", "用户未扫码");
            } else if (ZFBCode.equals("10000") && trade_status.equals("WAIT_BUYER_PAY")) {
                //订单已经创建但未支付（用户扫码后但是未支付）
                resultMap.put("code", ZFBCode);
                resultMap.put("data", "non-payment");
            } else if (ZFBCode.equals("10000") && (trade_status.equals("TRADE_SUCCESS") || trade_status.equals("TRADE_FINISHED"))) {
                //判断ZFBCode是否等于”10000“ 并且 trade_status等于TRADE_SUCCESS（交易支付成功）或者 trade_status等于TRADE_FINISHED（交易结束，不可退款）
                //订单已支付（用户扫码完成并且支付成功之后）
                resultMap.put("code", ZFBCode);
                resultMap.put("data", "yes-payment");
            } else {
                resultMap.put("code", ZFBCode);
                resultMap.put("data", sub_msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 支付宝支付结果通知处理
     * @param request 请求对象
     * @return 支付宝支付结果通知响应
     */
    @PostMapping("/notify")
    public String handleAlipayNotify(HttpServletRequest request) {
        // 获取支付宝支付结果通知参数
        log.info("订单通知");
        Map<String, String> requestParams = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String key : parameterMap.keySet()) {
            requestParams.put(key, StringUtils.join(parameterMap.get(key), ","));
        }
        // 处理支付宝支付结果通知
        boolean success = alipayService.handleAlipayNotify(requestParams);
        log.info("订单支付"+(success?"成功":"失败"));
        if (success) {
            return "success";
        } else {
            return "failure";
        }
    }
}
