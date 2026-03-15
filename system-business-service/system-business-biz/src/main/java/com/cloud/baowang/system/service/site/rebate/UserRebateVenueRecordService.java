package com.cloud.baowang.system.service.site.rebate;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateInfoVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.ReportUserRebateRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateDetailsRspVO;
import com.cloud.baowang.system.po.site.rebate.UserRebateRecordPO;
import com.cloud.baowang.system.po.site.rebate.UserRebateVenueRecordPO;
import com.cloud.baowang.system.repositories.site.rebate.UserRebateVenueRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserRebateVenueRecordService extends ServiceImpl<UserRebateVenueRecordRepository, UserRebateVenueRecordPO> {

    private final UserRebateVenueRecordRepository repository;


    /**
     * 用户返水明细查询
     * @return
     */
    public List<UserRebateDetailsRspVO> userRebateDetails(UserRebateAuditQueryVO vo) {
        List<UserRebateDetailsRspVO> result = Collections.emptyList();
        LambdaQueryWrapper<UserRebateVenueRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRebateVenueRecordPO::getSiteCode, vo.getSiteCode());
        wrapper.eq(UserRebateVenueRecordPO::getUserId, vo.getUserId());
        wrapper.eq(UserRebateVenueRecordPO::getOrderNo, vo.getOrderNo());
        List<UserRebateVenueRecordPO> poList = repository.selectList(wrapper);
        try {
            poList.forEach(item -> {
                item.setRebatePercent(item.getRebatePercent().multiply(new BigDecimal("100")));
            });
            result = ConvertUtil.convertListToList(poList,new UserRebateDetailsRspVO());
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return result;
    }


    /**
     * 返水报表
     * @param reqVO
     * @return
     */
    public ResponseVO<ReportUserRebateRspVO> siteRebateInfoPage(ReportUserRebateQueryVO reqVO) {
        Page<ReportUserRebateInfoVO> issuePage = new Page<>(reqVO.getPageNumber(),reqVO.getPageSize());
        ReportUserRebateRspVO result = new ReportUserRebateRspVO();
        issuePage = repository.siteRebateListPage(issuePage,reqVO);
        for (ReportUserRebateInfoVO issueVo : issuePage.getRecords()) {
            if(!"-1".equals(issueVo.getVenueType())){
                issueVo.setVenueTypeText(I18nMessageUtil.getSystemParamAndTrans(CommonConstant.VENUE_TYPE,issueVo.getVenueType()));
            }
        }
    /*
        issuePage = repository.siteRebateInfoPage(issuePage,reqVO);

        Page<ReportUserRebateInfoVO> receivePage = new Page<>(reqVO.getPageNumber(),reqVO.getPageSize());
        receivePage = repository.siteReceiveRebatePage(receivePage,reqVO);*/

        /*//发放
        List<ReportUserRebateInfoVO> issueList = new ArrayList<>(issuePage.getRecords());
        //领取
        List<ReportUserRebateInfoVO> receiveList = new ArrayList<>(receivePage.getRecords());

        Map<String, ReportUserRebateInfoVO> receiveMap = receiveList.stream()
                .collect(Collectors.toMap(
                        vo -> vo.getVenueType() + "_" + vo.getCurrencyCode(),
                        vo -> vo));

        // 合并数据
        for (ReportUserRebateInfoVO issueVo : issueList) {
            String key = issueVo.getVenueType() + "_" + issueVo.getCurrencyCode();
            ReportUserRebateInfoVO receiveVo = receiveMap.get(key);

            if (receiveVo != null) {
                issueVo.setReceiveNums(receiveVo.getReceiveNums());
                issueVo.setRebateAmount(receiveVo.getRebateAmount());
            } else {
                issueVo.setReceiveNums(0L);
                issueVo.setRebateAmount(BigDecimal.ZERO);
            }
        }*/
       /* Page<ReportUserRebateInfoVO> manualPage = new Page<>(reqVO.getPageNumber(),reqVO.getPageSize());
        Page<ReportUserRebateInfoVO> manualRebateInfo =  repository.manualRebateInfo(manualPage,reqVO);
        if (manualRebateInfo == null) {
            manualRebateInfo = new ReportUserRebateInfoVO();
            manualRebateInfo.setReceiveNums(0L);
            manualRebateInfo.setRebateAmount(BigDecimal.ZERO);
        }
        manualRebateInfo.setVenueTypeText("人工加减额");
        issueList.add(manualRebateInfo);
        issuePage.setRecords(issueList);*/

        ReportUserRebateInfoVO totalRebateInfo = repository.siteTotalRebateInfo(reqVO);
        if (totalRebateInfo == null) {
            totalRebateInfo = new ReportUserRebateInfoVO();
            totalRebateInfo.setReceiveNums(0L);
            totalRebateInfo.setRebateAmount(BigDecimal.ZERO);
        }
        totalRebateInfo.setVenueTypeText("总计");
        result.setTotalRebateInfo(totalRebateInfo);
        issuePage.setRecords(issuePage.getRecords());
        result.setPageInfo(issuePage);
        return ResponseVO.success(result);
    }

    /**
     * 场馆返水明细
     * @param reqVO
     * @return
     */
    public ResponseVO<Page<ReportUserRebateInfoVO>> venueRebateDetails(ReportUserRebateQueryVO reqVO) {
        Page<ReportUserRebateInfoVO> page = new Page<>(reqVO.getPageNumber(),reqVO.getPageSize());
        if (reqVO.getVenueType().equals(String.valueOf(CommonConstant.business_negative1))){
            page=repository.siteBackAdjustRebatePage(page,reqVO);
        }else {
            page = repository.venueRebatePage(page,reqVO);
        }
        return ResponseVO.success(page);
    }

    public Boolean onUserRebateReceived(UserRebateAuditQueryVO vo) {
        LambdaUpdateWrapper<UserRebateVenueRecordPO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRebateVenueRecordPO::getSiteCode, vo.getSiteCode());
        wrapper.eq(UserRebateVenueRecordPO::getUserId, vo.getUserId());
        wrapper.eq(UserRebateVenueRecordPO::getOrderNo, vo.getOrderNo());
        wrapper.set(UserRebateVenueRecordPO::getStatus, CommonConstant.business_two);
        wrapper.set(UserRebateVenueRecordPO::getReceiveTime, vo.getReceiveTime());
        this.update(wrapper);
        return true;
    }

    /**
     * 更新派发时间
     * @param rebateList
     */
    @Async
    public void updateRecordIssueTime(List<UserRebateRecordPO> rebateList) {
        rebateList.forEach(item -> {
            LambdaUpdateWrapper<UserRebateVenueRecordPO> wrapper = new LambdaUpdateWrapper<>();
            long now = System.currentTimeMillis();
            String orderNo = item.getOrderNo();
            wrapper.eq(UserRebateVenueRecordPO::getOrderNo, orderNo);
            wrapper.set(UserRebateVenueRecordPO::getIssueTime, now);
            this.update(wrapper);
        });
    }


    public void clearUserVenueRebateRecord(Long inputTime, String timeZoneStr, String siteCode,String currencyCode) {
        Long startTime = DateUtils.getDayStartTime(inputTime,timeZoneStr);
        Long endTime = DateUtils.getDayEndTime(inputTime,timeZoneStr);
        LambdaUpdateWrapper<UserRebateVenueRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserRebateVenueRecordPO::getSiteCode,siteCode);
        updateWrapper.eq(UserRebateVenueRecordPO::getCurrencyCode,currencyCode);
        updateWrapper.ge(UserRebateVenueRecordPO::getCreatedTime,startTime);
        updateWrapper.le(UserRebateVenueRecordPO::getCreatedTime,endTime);
        repository.delete(updateWrapper);
    }
}
