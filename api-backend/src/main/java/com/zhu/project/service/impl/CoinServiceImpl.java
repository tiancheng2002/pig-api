package com.zhu.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhu.project.common.ErrorCode;
import com.zhu.project.exception.BusinessException;
import com.zhu.project.mapper.CoinMapper;
import com.zhu.project.model.entity.Coin;
import com.zhu.project.service.CoinService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiaozhu
 * @since 2024-02-23
 */
@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService {

    @Override
    public void validCoin(Coin coin, boolean add) {
        if (coin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BigDecimal prince = coin.getPrice();
        // 创建时，所有参数必须非空
        if (add) {
            if (prince==null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
    }

}
