package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionGenCodeVO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionGenCodePO;
import com.cloud.baowang.activity.repositories.ActivityRedemptionGenCodeRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.Objects;

/**
 * 兑换码生成服务层
 */
@Service
@Slf4j
public class ActivityRedemptionGenCodeSerivce extends ServiceImpl<ActivityRedemptionGenCodeRepository, SiteActivityRedemptionGenCodePO> {

    public ResponseVO<SiteActivityRedemptionGenCodeVO> getRedemptionGenCodeByCode(String code){

        SiteActivityRedemptionGenCodeVO vo;
        SiteActivityRedemptionGenCodePO po = null;
        LambdaQueryWrapper<SiteActivityRedemptionGenCodePO> wrapperQuery = new LambdaQueryWrapper<SiteActivityRedemptionGenCodePO>();
        if (StrUtil.isNotBlank(code)){
            wrapperQuery.eq(SiteActivityRedemptionGenCodePO::getCode,code);
            po = this.baseMapper.selectOne(wrapperQuery);
        }else {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_NOT);
        }
        vo = BeanUtil.copyProperties(po,SiteActivityRedemptionGenCodeVO.class);
        return ResponseVO.success(vo);
    }

    /**
     * 获取SiteActivityRedemptionGenCodeVO详情对象
     * @param vo
     * @return
     */
    public ResponseVO<SiteActivityRedemptionGenCodeVO> info(SiteActivityRedemptionGenCodeVO vo){

        LambdaQueryWrapper<SiteActivityRedemptionGenCodePO> queryWrapper = Wrappers.lambdaQuery(SiteActivityRedemptionGenCodePO.class);
        if(Objects.nonNull(vo)){

            if (vo.getActivityDetailId() > 0){
                queryWrapper.eq(SiteActivityRedemptionGenCodePO::getActivityDetailId,vo.getActivityDetailId());
            }

            if (StrUtil.isNotBlank(vo.getCode())){
                queryWrapper.eq(SiteActivityRedemptionGenCodePO::getCode,vo.getCode());
            }

            if (StrUtil.isNotBlank(vo.getBatchNo())){
                queryWrapper.eq(SiteActivityRedemptionGenCodePO::getBatchNo,vo.getBatchNo());
            }

            if(StrUtil.isNotBlank(vo.getCurrency())){
                queryWrapper.eq(SiteActivityRedemptionGenCodePO::getCurrency,vo.getCurrency());
            }
            SiteActivityRedemptionGenCodePO po = this.baseMapper.selectOne(queryWrapper);
            SiteActivityRedemptionGenCodeVO genCodeVO = BeanUtil.copyProperties(po,SiteActivityRedemptionGenCodeVO.class);
            return ResponseVO.success(genCodeVO);
        }
        return ResponseVO.success();
    }
}
