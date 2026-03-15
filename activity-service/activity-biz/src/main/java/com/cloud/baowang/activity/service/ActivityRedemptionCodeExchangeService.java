package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeExchangeVO;
import com.cloud.baowang.activity.po.SiteActivityFirstRechargePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeExchangePO;
import com.cloud.baowang.activity.repositories.ActivityRedemptionCodeExchangeRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ActivityRedemptionCodeExchangeService extends ServiceImpl<ActivityRedemptionCodeExchangeRepository, SiteActivityRedemptionCodeExchangePO> {


    /**
     * 码已兑换的数量
     * @param exchangeVO
     * @return
     */
    public Long countExchanged(SiteActivityRedemptionCodeExchangeVO exchangeVO){
        
        LambdaQueryWrapper<SiteActivityRedemptionCodeExchangePO> queryWrapper = Wrappers.lambdaQuery();

        if (Objects.isNull(exchangeVO)){
           throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        String code = exchangeVO.getCode();
        Integer category = exchangeVO.getCategory();
        String currency = exchangeVO.getCurrency();
        String batchNo = exchangeVO.getBatchNo();
        String orderNo = exchangeVO.getOrderNo();
        //按兑换码统计
        if (StrUtil.isNotBlank(code)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getCode,code);
        }
        //按兑换码类型统计
        if (category != null){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getCategory,category);
        }
        //按批次号统计
        if (StrUtil.isNotBlank(batchNo)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getBatchNo,batchNo);
        }
        //按订单号统计
        if (StrUtil.isNotBlank(orderNo)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getOrderNo,orderNo);
        }

        return baseMapper.selectCount(queryWrapper);
    }

    /**
     * 获取已兑换过的兑换码对象
     * @param vo
     * @return
     */
    public ResponseVO<SiteActivityRedemptionCodeExchangeVO> info(SiteActivityRedemptionCodeExchangeVO vo){
        SiteActivityRedemptionCodeExchangePO po;
        SiteActivityRedemptionCodeExchangeVO exchangeVO = null;
        LambdaQueryWrapper<SiteActivityRedemptionCodeExchangePO> queryWrapper = Wrappers.lambdaQuery();

        if (Objects.isNull(vo)){
            throw new BaowangDefaultException(ResultCode.NULL_PARAMETERS);
        }

        Long id = vo.getId();
        String code = vo.getCode();
        String userId = vo.getUserId();
        String batchNo = vo.getBatchNo();

        if(id != null && id != 0){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getId,id);
        }

        if (StrUtil.isNotBlank(code)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getCode,code);
        }

        if (StrUtil.isNotBlank(userId)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getUserId,userId);
        }

        if(StrUtil.isNotBlank(batchNo)){
            queryWrapper.eq(SiteActivityRedemptionCodeExchangePO::getBatchNo,batchNo);
        }

        po = this.baseMapper.selectOne(queryWrapper);
        if(Objects.nonNull(po)){
            exchangeVO = BeanUtil.copyProperties(po,SiteActivityRedemptionCodeExchangeVO.class);
            return ResponseVO.success(exchangeVO);
        }
        return ResponseVO.success();
    }
}
