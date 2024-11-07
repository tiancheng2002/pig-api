package com.zhu.project.utils;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

public class OrderUtils {

    //生成唯一的订单号
    public static String genOrderId(){
        String time = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        String randomString = RandomStringUtils.randomNumeric(8);
        return time+randomString;
    }


}
