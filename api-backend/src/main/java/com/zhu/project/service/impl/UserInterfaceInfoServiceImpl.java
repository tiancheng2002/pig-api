package com.zhu.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.constant.RedisConstant;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.mapper.UserInterfaceInfoMapper;
import com.zhu.project.model.entity.InterfaceInfo;
import com.zhu.project.model.entity.User;
import com.zhu.project.model.entity.UserInterfaceInfo;
import com.zhu.project.service.InterfaceInfoService;
import com.zhu.project.service.UserInterfaceInfoService;
import com.zhu.project.service.UserService;
import com.zhu.project.utils.RedisUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户接口信息服务实现类
 *
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
    }

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.将接口总调用次数加一（可以先用redis存储），然后用定时任务去更新接口调用次数
        redisUtils.hincr(RedisConstant.INTERFACE_INVOKE_COUNT,String.valueOf(interfaceInfoId),1);
        //2.减少用户对应数量的z币
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceInfoId);
        boolean userUpdateResult = true;
        if(interfaceInfo.getZCoin()>0){
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.setSql("zCoinNum = zCoinNum - "+interfaceInfo.getZCoin());
            userUpdateResult = userService.update(userUpdateWrapper);
        }

        //3.将调用接口记录进行日志记录（请求ip地址、用户id、接口id等）
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setUserId(userId);
        userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
//        userInterfaceInfo.setIp();
        userUpdateResult = save(userInterfaceInfo);

        return userUpdateResult;
    }

}

