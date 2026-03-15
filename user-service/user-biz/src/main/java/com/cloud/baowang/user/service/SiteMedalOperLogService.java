package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.SystemParamTypeEnum;
import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalOperLogRespVO;
import com.cloud.baowang.user.enums.MedalOperationEnum;
import com.cloud.baowang.user.po.SiteMedalInfoPO;
import com.cloud.baowang.user.po.SiteMedalOperLogPO;
import com.cloud.baowang.user.repositories.SiteMedalOperLogRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Desciption: 站点勋章操作记录
 * @Author: Ford
 * @Date: 2024/8/15 16:40
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteMedalOperLogService extends ServiceImpl<SiteMedalOperLogRepository, SiteMedalOperLogPO> {

    private final SystemParamApi systemParamApi;

    private final I18nApi i18nApi;

    /**
     * 记录站点勋章变更记录
     * @param siteMedalOperLogPO 参数
     */
    public void recordOperLog(SiteMedalOperLogPO siteMedalOperLogPO) {
        siteMedalOperLogPO.setCreatedTime(System.currentTimeMillis());
        MedalOperationEnum medalOperationEnum=MedalOperationEnum.parseByFieldCode(siteMedalOperLogPO.getOperItem());
        siteMedalOperLogPO.setOperItemI18(medalOperationEnum.getDesc());
        this.baseMapper.insert(siteMedalOperLogPO);
    }

    /**
     * 分页查询
     * @param siteMedalOperLogReqVO
     * @return
     */
    public ResponseVO<Page<SiteMedalOperLogRespVO>> listPage(SiteMedalOperLogReqVO siteMedalOperLogReqVO) {
        Page<SiteMedalOperLogPO> page = new Page<SiteMedalOperLogPO>(siteMedalOperLogReqVO.getPageNumber(), siteMedalOperLogReqVO.getPageSize());
        LambdaQueryWrapper<SiteMedalOperLogPO> lqw = new LambdaQueryWrapper<SiteMedalOperLogPO>();
        if(StringUtils.hasText(siteMedalOperLogReqVO.getSiteCode())){
            lqw.eq(SiteMedalOperLogPO::getSiteCode, siteMedalOperLogReqVO.getSiteCode());
        }
        if(siteMedalOperLogReqVO.getOperBeginTime()!=null){
            lqw.ge(SiteMedalOperLogPO::getOperTime, siteMedalOperLogReqVO.getOperBeginTime());
        }
        if(siteMedalOperLogReqVO.getOperEndTime()!=null){
            lqw.le(SiteMedalOperLogPO::getOperTime, siteMedalOperLogReqVO.getOperEndTime());
        }
        if(StringUtils.hasText(siteMedalOperLogReqVO.getOperItem())){
            lqw.eq(SiteMedalOperLogPO::getOperItem, siteMedalOperLogReqVO.getOperItem());
        }
        if(siteMedalOperLogReqVO.getOperUserNo()!=null){
            lqw.like(SiteMedalOperLogPO::getCreator, siteMedalOperLogReqVO.getOperUserNo());
        }
        if(!StringUtils.hasText(siteMedalOperLogReqVO.getOrderType())&&!StringUtils.hasText(siteMedalOperLogReqVO.getOrderField())){
            lqw.orderByDesc(SiteMedalOperLogPO::getOperTime);
        }
        if("operTime".equals(siteMedalOperLogReqVO.getOrderField())){
            if("asc".equals(siteMedalOperLogReqVO.getOrderType())){
                lqw.orderByAsc(SiteMedalOperLogPO::getOperTime);
            }
            if("desc".equals(siteMedalOperLogReqVO.getOrderType())){
                lqw.orderByDesc(SiteMedalOperLogPO::getOperTime);
            }
        }
        IPage<SiteMedalOperLogPO> siteMedalOperLogPOIPage =  this.baseMapper.selectPage(page,lqw);
        Page<SiteMedalOperLogRespVO> siteMedalOperLogRespVOPage=new Page<SiteMedalOperLogRespVO>(siteMedalOperLogReqVO.getPageNumber(), siteMedalOperLogReqVO.getPageSize());
        siteMedalOperLogRespVOPage.setTotal(siteMedalOperLogPOIPage.getTotal());
        siteMedalOperLogRespVOPage.setPages(siteMedalOperLogPOIPage.getPages());
        List<SiteMedalOperLogRespVO> resultLists= Lists.newArrayList();
        Map<String, String> systemParamMap = systemParamApi.getSystemParamMap(SystemParamTypeEnum.ENABLE_DISABLE_STATUS.getType()).getData();
        Map<String, String> i18nMap=i18nApi.getMessageInKey(systemParamMap.values().stream().toList(),CurrReqUtils.getLanguage()).getData();
        for(SiteMedalOperLogPO siteMedalOperLogPO:siteMedalOperLogPOIPage.getRecords()){
            SiteMedalOperLogRespVO siteMedalOperLogRespVO=new SiteMedalOperLogRespVO();
            BeanUtils.copyProperties(siteMedalOperLogPO,siteMedalOperLogRespVO);
            if(siteMedalOperLogRespVO.getOperItem().equals(SiteMedalInfoPO.Fields.status)){
                if(StringUtils.hasText(siteMedalOperLogRespVO.getOperBefore())){
                    String i18nBeforeVal=i18nMap.get(systemParamMap.get(siteMedalOperLogRespVO.getOperBefore()));
                    siteMedalOperLogRespVO.setOperBefore(BigDecimalUtils.formatFourVal(i18nBeforeVal));
                }
                if(StringUtils.hasText(siteMedalOperLogRespVO.getOperAfter())){
                    String i18nAfterVal=i18nMap.get(systemParamMap.get(siteMedalOperLogRespVO.getOperAfter()));
                    siteMedalOperLogRespVO.setOperAfter(BigDecimalUtils.formatFourVal(i18nAfterVal));
                }
            }
            resultLists.add(siteMedalOperLogRespVO);
        }
        siteMedalOperLogRespVOPage.setRecords(resultLists);
        return ResponseVO.success(siteMedalOperLogRespVOPage);


    }
}
