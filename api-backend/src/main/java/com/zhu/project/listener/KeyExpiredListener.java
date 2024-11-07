package com.zhu.project.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.constant.OrderConstant;
import com.zhu.project.constant.RedisConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.model.entity.Order;
import com.zhu.project.service.OrderService;
import com.zhu.project.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

//对Redis过期的key进行监听
@Slf4j
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private OrderService orderService;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] keySplit = message.toString().split(":");
        if(keySplit[0].equals(RedisConstant.CREATE_USER_ORDER)){
            String oid = keySplit[1];
            log.info("订单超时未支付:{}",oid);
            //如果订单支付了，redis当中不会有对应的记录，对应的键也不会过期
            Order order = orderService.getById(oid);
            if(order==null||order.getOrderStatus()== OrderConstant.ORDER_HAVE_PAY){
                return;
            }
            order.setOrderStatus(OrderConstant.ORDER_CANCEL);
            boolean result = orderService.updateById(order);
            if(!result){
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        }
        //将对应接口的访问数据的key进行删除
//        redisUtils.hdel(RedisConstant.CREATE_USER_ORDER+":"+oid);
    }
}