package com.zhu.project.service.impl;

import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.constant.OrderConstant;
import com.zhu.project.constant.RedisConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.dto.coin.CoinPayRequest;
import com.zhu.project.model.dto.order.OrderAddRequest;
import com.zhu.project.model.entity.Order;
import com.zhu.project.model.entity.User;
import com.zhu.project.service.OrderService;
import com.zhu.project.service.UserService;
import com.zhu.project.utils.BeanCopeUtils;
import com.zhu.project.utils.OrderUtils;
import com.zhu.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlipayService {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.server-url}")
    private String serverUrl;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.sign-type}")
    private String signType;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Resource
    private OrderService orderService;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    // 扫描二维码方式付款

    /**
     * 创建支付宝支付订单
     * @return 支付宝支付二维码链接
     * @throws AlipayApiException
     */
    public String createPayOrder(String orderId, BigDecimal amount) throws AlipayApiException {
        // 实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", charset, alipayPublicKey, signType);

        // 创建API对应的request
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        // 设置支付宝支付回调地址
        request.setNotifyUrl(notifyUrl);

        // 设置请求参数
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(orderId);
        model.setTotalAmount(amount.toString());
        model.setSubject("订单支付");
        request.setBizModel(model);

        // 发起请求，获取支付宝支付二维码链接
        AlipayTradePrecreateResponse response = alipayClient.execute(request);

        // 判断请求是否成功
        if (response.isSuccess()) {
            System.out.println(response.getShareCode());
            System.out.println(response.getCode());
            System.out.println(response.getBody());
            System.out.println(response.getQrCode());
            return response.getQrCode();
        } else {
            throw new AlipayApiException(response.getSubMsg());
        }
    }

    /**
     * 创建支付宝支付订单
     * @return 支付宝支付二维码链接
     * @throws AlipayApiException
     */
    public String createAlipayOrder(OrderAddRequest orderAddRequest, Long userId) throws AlipayApiException {
        //创建订单记录
        Order order = BeanCopeUtils.copyBean(orderAddRequest, Order.class);
        order.setUserId(userId);
        String oid = OrderUtils.genOrderId();
        order.setOrderId(oid);
        boolean result = orderService.save(order);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", charset, alipayPublicKey, signType);

        // 创建API对应的request
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();

        // 设置支付宝支付回调地址
        request.setNotifyUrl(notifyUrl);

        // 设置请求参数
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(oid);
        model.setTotalAmount(order.getOrderPrice().toString());
        //将金币数量以及产品id存储到body当中，然后再异步通知当中就能够获取到
        CoinPayRequest coinPayRequest = new CoinPayRequest(orderAddRequest.getProductId(), orderAddRequest.getCoinNum());
        model.setBody(JSONUtil.toJsonStr(coinPayRequest));
        model.setSubject("订单支付");
        request.setBizModel(model);

        // 发起请求，获取支付宝支付二维码链接
        AlipayTradePrecreateResponse response = alipayClient.execute(request);
        // 判断请求是否成功
        if (response.isSuccess()) {
            System.out.println(response.getShareCode());
            System.out.println(response.getCode());
            System.out.println(response.getBody());
            System.out.println(response.getQrCode());
            redisUtils.set(RedisConstant.CREATE_USER_ORDER+":"+oid,oid,900);
            redisUtils.hset(RedisConstant.ORDER_QR_CODE,oid,response.getQrCode());
            return oid;
        } else {
            throw new AlipayApiException(response.getSubMsg());
        }
    }

    public String tradeQuery(String oid) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", charset, alipayPublicKey, signType);
        /** 实例化具体API对应的request类，类名称和接口名称对应,当前调用接口名称：alipay.trade.query（统一收单线下交易查询） **/
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        /** 设置业务参数 **/
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        /** 注：交易号（TradeNo）与订单号（OutTradeNo）二选一传入即可，如果2个同时传入，则以交易号为准 **/
        /** 支付接口传入的商户订单号。如：2020061601290011200000140004 **/
        model.setOutTradeNo(oid);
        /** 异步通知/查询接口返回的支付宝交易号，如：2020061622001473951448314322 **/
        //model.setTradeNo("2020061622001473951448314322");
        /** 将业务参数传至request中 **/
        request.setBizModel(model);
        /** 第三方调用（服务商模式），必须传值与支付接口相同的app_auth_token **/
        //request.putOtherTextParam("app_auth_token", "传入获取到的app_auth_token值");
        /** 通过alipayClient调用API，获得对应的response类  **/
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        /** 获取接口调用结果，如果调用失败，可根据返回错误信息到该文档寻找排查方案：https://opensupport.alipay.com/support/helpcenter/101 **/
        return response.getBody();
    }

    /**
     * 处理支付宝支付结果通知
     * @param requestParams 支付宝支付结果通知参数
     * @return 是否处理成功
     */
    public boolean handleAlipayNotify(Map<String, String> requestParams) {
        try {
            // 验签
            boolean signVerified = AlipaySignature.rsaCheckV1(requestParams, alipayPublicKey, charset, signType);

            if (signVerified) {
                // 获取支付宝支付结果通知数据
                String oid = requestParams.get("out_trade_no"); // 商户订单号
                String tradeNo = requestParams.get("trade_no"); // 支付宝交易号
                String tradeStatus = requestParams.get("trade_status"); // 交易状态
                String body = requestParams.get("body");
                log.info("订单支付回调:{}",oid);
                // 根据支付结果更新订单状态
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    // 更新订单状态为已支付
                    Order order = orderService.getById(oid);
                    order.setOrderStatus(OrderConstant.ORDER_HAVE_PAY);
                    order.setPayTime(new Date());
                    orderService.updateById(order);

                    CoinPayRequest coinPayRequest = JSONUtil.toBean(body, CoinPayRequest.class);
                    //todo 并且需要将用户的z币数量进行增加(更新操作需要搭配日志，如果日志中有用户对应订单的完成记录，那么就不会进行更新，还可以考虑锁)
                    UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
                    userUpdateWrapper.setSql("zCoinNum = zCoinNum" + coinPayRequest.getCoinNum());
                    userUpdateWrapper.eq("id",order.getUserId());
                    userService.update(userUpdateWrapper);
                    //更新用户的z币数量
                    //删除redis当中的订单缓存
                    redisUtils.remove(RedisConstant.CREATE_USER_ORDER+":"+oid);
                    // updateOrderStatus(outTradeNo, tradeNo, "已支付");
                    return true;
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    //跳转支付宝网页进行付款

    public String pay(OrderAddRequest orderAddRequest, Long userId) {
        //创建订单记录
        Order order = BeanCopeUtils.copyBean(orderAddRequest, Order.class);
        order.setUserId(userId);
        String oid = OrderUtils.genOrderId();
        order.setOrderId(oid);
        boolean result = orderService.save(order);
        if(!result){
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //将用户id与订单id进行绑定存储到redis当中并设置十五分钟的过期时间，当对应的键过期的时候，表示用户没有在对应的时间内支付，就将订单状态更改为取消
        redisUtils.set(RedisConstant.CREATE_USER_ORDER+":"+oid,oid,15);
        // 实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", charset, alipayPublicKey, signType);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(returnUrl);
        request.setNotifyUrl(notifyUrl);

        Map<String, Object> params = new HashMap<>();
        params.put("out_trade_no", order.getOrderId());
        params.put("total_amount", order.getOrderPrice());
        params.put("subject", order.getOrderName());
        params.put("body", order.getDescription());
        params.put("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(JSONUtil.toJsonStr(params));
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            return response.getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付宝支付失败", e);
        }
    }

}