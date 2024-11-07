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
import com.zhu.project.model.dto.coin.CoinAddRequest;
import com.zhu.project.model.dto.coin.CoinQueryRequest;
import com.zhu.project.model.dto.coin.CoinUpdateRequest;
import com.zhu.project.model.entity.Coin;
import com.zhu.project.service.CoinService;
import com.zhu.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
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
 * @since 2024-02-23
 */
@RestController
@RequestMapping("/coin")
public class CoinController {
    
    @Resource
    private CoinService coinService;

    @Resource
    private UserService userService;

    /**
     * 创建
     *
     * @param coinAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Long> addCoin(@RequestBody CoinAddRequest coinAddRequest, HttpServletRequest request) {
        if (coinAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Coin coin = new Coin();
        BeanUtils.copyProperties(coinAddRequest, coin);
        // 校验
        coinService.validCoin(coin, true);
        boolean result = coinService.save(coin);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        //将响应参数添加到数据库中；如果传入的响应参数的id值不为空的时候就代表插入，否则就更新
        return ResultUtils.success(coin.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteCoin(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Coin oldCoin = coinService.getById(id);
        if (oldCoin == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = coinService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param coinUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateCoin(@RequestBody CoinUpdateRequest coinUpdateRequest,
                                                     HttpServletRequest request) {
        if (coinUpdateRequest == null || coinUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Coin coin = new Coin();
        BeanUtils.copyProperties(coinUpdateRequest, coin);
        // 参数校验
        coinService.validCoin(coin, false);
        long id = coinUpdateRequest.getId();
        // 判断是否存在
        Coin oldCoin = coinService.getById(id);
        if (oldCoin == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = coinService.updateById(coin);
        //更新请求参数以及返回参数，首先会判断主键id是否有值，有的话就更新没有就添加，并且会过滤掉没有的参数，然后删除掉
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Coin> getCoinById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Coin coin = coinService.getById(id);
        return ResultUtils.success(coin);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param coinQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Coin>> listCoin(CoinQueryRequest coinQueryRequest) {
        Coin coinQuery = new Coin();
        if (coinQueryRequest != null) {
            BeanUtils.copyProperties(coinQueryRequest, coinQuery);
        }
        QueryWrapper<Coin> queryWrapper = new QueryWrapper<>(coinQuery);
        List<Coin> coinList = coinService.list(queryWrapper);
        return ResultUtils.success(coinList);
    }
    
    /**
     * 分页获取列表
     *
     * @param coinQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Coin>> listCoinByPage(CoinQueryRequest coinQueryRequest, HttpServletRequest request) {
        if (coinQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Coin coinQuery = new Coin();
        BeanUtils.copyProperties(coinQueryRequest, coinQuery);
        long current = coinQueryRequest.getCurrent();
        long size = coinQueryRequest.getPageSize();
        String sortField = coinQueryRequest.getSortField();
        String sortOrder = coinQueryRequest.getSortOrder();
        String description = coinQuery.getDescription();
        // description 需支持模糊搜索
        coinQuery.setDescription(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Coin> queryWrapper = new QueryWrapper<>(coinQuery);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Coin> coinPage = coinService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(coinPage);
    }

}
