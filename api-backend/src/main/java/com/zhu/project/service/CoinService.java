package com.zhu.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhu.project.model.entity.Coin;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xiaozhu
 * @since 2024-02-23
 */
public interface CoinService extends IService<Coin> {

    void validCoin(Coin coin, boolean b);

}
