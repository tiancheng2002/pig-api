//package com.zhu.project.job;
//
//import com.zhu.project.constant.RedisConstant;
//import com.zhu.project.model.entity.InterfaceInfo;
//import com.zhu.project.service.InterfaceInfoService;
//import com.zhu.project.utils.RedisUtils;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//@EnableScheduling
//public class ScheduleHandler {
//
//    @Resource
//    private RedisUtils redisUtils;
//
//    @Resource
//    private InterfaceInfoService interfaceInfoService;
//
//    @Scheduled(cron = "0 0/1  * * * ?")
//    private void updatePlayNum(){
//        //每一分钟更新一次浏览量
//        Map<Object, Object> playNums = redisUtils.hmget(RedisConstant.INTERFACE_INVOKE_COUNT);
//        Iterator<Map.Entry<Object, Object>> iterator =playNums.entrySet().iterator();
//        List<InterfaceInfo> interfaceInfos = new ArrayList<>();
//        while (iterator.hasNext()) {
//            Map.Entry<Object, Object> item = iterator.next();
//            InterfaceInfo interfaceInfo = new InterfaceInfo();
//            interfaceInfo.setId(Long.parseLong((String) item.getKey()));
//            interfaceInfo.setInvokeCount(Integer.parseInt(String.valueOf(item.getValue())));
//            interfaceInfos.add(interfaceInfo);
//        }
//        interfaceInfoService.updateBatchById(interfaceInfos);
//    }
//
//}
