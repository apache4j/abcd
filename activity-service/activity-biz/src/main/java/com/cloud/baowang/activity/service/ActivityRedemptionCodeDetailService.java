package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.ActivityRedemptionCodeReqVO;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeBaseRespVO;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeDetailVO;
import com.cloud.baowang.activity.api.vo.SiteActivityRedemptionCodeExchangeVO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeDetailPO;
import com.cloud.baowang.activity.repositories.ActivityRedemptionCodeDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ActivityRedemptionCodeDetailService extends ServiceImpl<ActivityRedemptionCodeDetailRepository, SiteActivityRedemptionCodeDetailPO> {

    @Autowired
    private ActivityRedemptionCodeDetailRepository activityRedemptionCodeDetailRepository;
    @Autowired
    private ActivityRedemptionCodeExchangeService activityRedemptionCodeExchangeService;
    /**
     *
     * @param requestVO
     * @return
     */
    public Page<SiteActivityRedemptionCodeBaseRespVO> pageList(ActivityRedemptionCodeReqVO requestVO){
        Long exchangedCount;
        SiteActivityRedemptionCodeBaseRespVO respVO;
        Page<SiteActivityRedemptionCodeDetailPO> activityInfoPage;
        Page<SiteActivityRedemptionCodeBaseRespVO> resultPage;
        List<SiteActivityRedemptionCodeDetailPO> detailPOList;
        List<SiteActivityRedemptionCodeBaseRespVO> baseRespVOS;
        Page<SiteActivityRedemptionCodeDetailPO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        LambdaQueryWrapper<SiteActivityRedemptionCodeDetailPO> queryWrapper = Wrappers.lambdaQuery(SiteActivityRedemptionCodeDetailPO.class);

        if (Objects.nonNull(requestVO.getUpdater())) {
            queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getUpdater, requestVO.getUpdater());
        }
        if(Objects.nonNull(requestVO.getOrderNo())){
            queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getOrderNo,requestVO.getOrderNo());
        }
        if (Objects.nonNull(requestVO.getCategory())){
            queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getCategory,requestVO.getCategory());
        }
        if (Objects.nonNull(requestVO.getCreatedStartTime()) && Objects.nonNull(requestVO.getCreatedEndTime())){
            queryWrapper.between(SiteActivityRedemptionCodeDetailPO::getCreatedTime,requestVO.getCreatedStartTime(),requestVO.getCreatedEndTime());
        }

        if(Objects.nonNull(requestVO.getCondition()) && requestVO.getCondition() != 0){
            queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getCondition,requestVO.getCondition());
        }
        if (StrUtil.isNotBlank(requestVO.getCurrency())){
            queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getCurrency,requestVO.getCurrency());
        }

        activityInfoPage = this.activityRedemptionCodeDetailRepository.selectPage(respVOPage,queryWrapper);
        resultPage = new Page<SiteActivityRedemptionCodeBaseRespVO>();
        detailPOList = activityInfoPage.getRecords();
        baseRespVOS = new ArrayList<>();

        if (!CollectionUtils.isEmpty(detailPOList)) {

            for (SiteActivityRedemptionCodeDetailPO po:detailPOList){

                respVO = SiteActivityRedemptionCodeBaseRespVO.builder().build();
                SiteActivityRedemptionCodeDetailVO detailVO = BeanUtil.copyProperties(po,SiteActivityRedemptionCodeDetailVO.class);
                respVO.setSiteActivityRedemptionCodeDetailVO(detailVO);
                respVO.setQuantity(detailVO.getQuantity());
                respVO.setOrderNo(detailVO.getOrderNo());
                SiteActivityRedemptionCodeExchangeVO exchangeVO = SiteActivityRedemptionCodeExchangeVO.builder().build();
                exchangeVO.setOrderNo(detailVO.getOrderNo());
                exchangedCount = activityRedemptionCodeExchangeService.countExchanged(exchangeVO);
                respVO.setExchangedCount(exchangedCount.intValue());
                baseRespVOS.add(respVO);
            }
            resultPage.setRecords(baseRespVOS);
        }
        return resultPage;
    }

    /**
     *
     * @param detailVO
     * @return
     */
    public SiteActivityRedemptionCodeDetailPO info(SiteActivityRedemptionCodeDetailVO detailVO){

        LambdaQueryWrapper<SiteActivityRedemptionCodeDetailPO> queryWrapper = Wrappers.lambdaQuery(SiteActivityRedemptionCodeDetailPO.class);
        if (Objects.nonNull(detailVO)){

            if (detailVO.getActivityId() > 0){
                queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getActivityId,detailVO.getActivityId());
            }
            if(StrUtil.isNotBlank(detailVO.getOrderNo())){
                queryWrapper.eq(SiteActivityRedemptionCodeDetailPO::getOrderNo,detailVO.getOrderNo());
            }
            return this.baseMapper.selectOne(queryWrapper);
        }
        return null;

    }

}
