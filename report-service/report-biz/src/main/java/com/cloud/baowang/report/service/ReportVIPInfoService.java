package com.cloud.baowang.report.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.vip.*;
import com.cloud.baowang.report.po.ReportVIPInfoPO;
import com.cloud.baowang.report.repositories.ReportVIPInfoRepository;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 2024/11/5 18:50
 * @Version : 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReportVIPInfoService extends ServiceImpl<ReportVIPInfoRepository, ReportVIPInfoPO> {

    private SiteApi siteApi;
    private VipGradeApi vipGradeApi;
    private VipRankApi vipRankApi;
    private ReportVIPInfoRepository reportVIPInfoRepository;
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    private SiteVipOptionApi siteVipOptionApi;

    public void collectVIPDataReport(final long startTime, final long endTime, final String timeZone, String siteCode) {
        try {
            List<SiteVO> list = siteApi.getSiteInfoByTimezone(timeZone);
            Map<String, SiteVO> siteMap = list.stream().collect(Collectors.toMap(SiteVO::getSiteCode, e -> e));

            List<String> siteCodes = Lists.newArrayList();
            if(ObjectUtil.isNotEmpty(siteCode)){
                siteCodes.add(siteCode);
            }else{
                siteCodes = list.stream().map(SiteVO::getSiteCode).toList();
            }
            for(String newSiteCode : siteCodes){
                SiteVO siteVO= siteMap.get(newSiteCode);
                List<SiteVIPGradeVO> siteVIPGradePOList =null;
                List<UserVIPVO> userVIPVOList =null;
                List<VIPAwardVO> vipAwardVOList =null;
                List<VIPAchieveVO> vipAchieveVOS =null;
                Map<Integer, Integer> gradeMap =null;
                if (SiteHandicapModeEnum.Internacional.getCode().equals(siteVO.getHandicapMode())){
                    // Vip升级经验配置
                    siteVIPGradePOList = vipGradeApi.queryAllVIPGradeBySiteCode(newSiteCode);
                    vipAwardVOList = reportVIPInfoRepository.getUserBonus(startTime, endTime, newSiteCode);
                    vipAchieveVOS = reportVIPInfoRepository.getVIPAchieve(startTime, endTime, newSiteCode);
                    gradeMap = siteVIPGradePOList.stream().collect(Collectors
                            .toMap(SiteVIPGradeVO::getVipGradeCode, SiteVIPGradeVO::getVipRankCode));
                }else{
                    List<VIPGradeVO> data=  siteVipOptionApi.getCnVipGradeList().getData();
                    vipAwardVOList = reportVIPInfoRepository.getCnUserBonus(startTime, endTime, newSiteCode);
                    vipAchieveVOS = reportVIPInfoRepository.getCnVIPAchieve(startTime, endTime, newSiteCode);
                    gradeMap =data.stream().collect(Collectors.toMap(VIPGradeVO::getVipGradeCode, VIPGradeVO::getVipGradeCode));
                }
                userVIPVOList = reportVIPInfoRepository.getUserVIPData(startTime, endTime, newSiteCode);
                Map<Integer, VIPAwardVO> awardVOMap = vipAwardVOList.stream().collect(Collectors
                        .toMap(VIPAwardVO::getVipGradeCode, p->p));
                Map<Integer, VIPAchieveVO> achieveMap = vipAchieveVOS.stream().collect(Collectors
                        .toMap(VIPAchieveVO::getVipGradeCode, p->p));
                Map<Integer, Integer> userVIPMap = userVIPVOList.stream()
                        .collect(Collectors.toMap(UserVIPVO::getVipGradeCode, UserVIPVO::getCurrentGradeNum));
                // 新达成所有VIP等级人数
                Set<Integer> arriveVIPGrade = new HashSet<>(vipAchieveVOS.stream()
                        .map(VIPAchieveVO::getVipGradeCode).toList());
                // 会员最新VIP等级人数
                Set<Integer> newVIPGrade = new HashSet<>(userVIPVOList.stream()
                        .map(UserVIPVO::getVipGradeCode).toList());
                // 领取奖励对应VIP等级人数
                Set<Integer> awardVIPGrade = new HashSet<>(vipAwardVOList.stream()
                        .map(VIPAwardVO::getVipGradeCode).toList());
                arriveVIPGrade.addAll(newVIPGrade);
                arriveVIPGrade.addAll(awardVIPGrade);
                List<ReportVIPInfoPO> reportVIPInfoPOList = Lists.newArrayList();
                for(Integer vipGradeCode : arriveVIPGrade){
                    VIPAwardVO awardVO = Optional.ofNullable(awardVOMap.get(vipGradeCode))
                            .orElse(new VIPAwardVO());
                    VIPAchieveVO achieveVO = Optional.ofNullable(achieveMap.get(vipGradeCode))
                            .orElse(new VIPAchieveVO());
                    if (SiteHandicapModeEnum.Internacional.getCode().equals(siteVO.getHandicapMode())){
                        reportVIPInfoPOList.add(ReportVIPInfoPO.builder().vipGradeCode(vipGradeCode)
                                .siteCode(newSiteCode).vipRankCode(gradeMap.get(vipGradeCode)).dateShow(startTime)
                                .currentGradeNum(userVIPMap.get(vipGradeCode)).achieveGradeNum(achieveVO.getAchieveNum())
                                .receiveBonus(awardVO.getReceiveBonus()).build());
                    }else{
                        reportVIPInfoPOList.add(ReportVIPInfoPO.builder().vipGradeCode(vipGradeCode)
                                .siteCode(newSiteCode).dateShow(startTime)
                                .currentGradeNum(userVIPMap.get(vipGradeCode)).achieveGradeNum(achieveVO.getAchieveNum())
                                .receiveBonus(awardVO.getReceiveBonus()).build());
                    }
                }
                this.saveBatch(reportVIPInfoPOList);
            }
        } catch (Exception e) {
            log.error("vip data report collect have error timeZone:{}, startTime:{}, endTime:{}",
                    timeZone, startTime, endTime, e);
        }
    }

    public ResponseVO<ReportVIPDataVO> pageVIPData(final ReportVIPDataReq req) {
        ReportVIPDataVO result = new ReportVIPDataVO();
        String siteCode = CurrReqUtils.getSiteCode();
        req.setSiteCode(siteCode);
        Page<ReportVIPInfoPO> page = new Page<>(req.getPageNumber(), req.getPageSize());
        LambdaQueryWrapper<ReportVIPInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(siteCode), ReportVIPInfoPO::getSiteCode, siteCode);
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getStartTime()), ReportVIPInfoPO::getDateShow,
                req.getStartTime());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getVipRankCode()), ReportVIPInfoPO::getVipRankCode,
                req.getVipRankCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getVipGradeCode()), ReportVIPInfoPO::getVipGradeCode,
                req.getVipGradeCode());
        queryWrapper.orderByAsc(ReportVIPInfoPO::getVipRankCode).orderByAsc(ReportVIPInfoPO::getVipGradeCode);
        Page<ReportVIPInfoPO> resultPage = reportVIPInfoRepository.selectPage(page, queryWrapper);
        Page<ReportVIPDataPage> resPage = new Page<>();
        List<ReportVIPDataPage> voList = Lists.newArrayList();
        // 查询vip等级
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGrade(siteCode);
        Map<Integer, String> vipMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)){
            vipMap = siteVIPGradeVOS.stream().collect(Collectors.toMap(SiteVIPGradeVO::getVipGradeCode,SiteVIPGradeVO::getVipGradeName,(k1, k2)->k2));
        }
        // 查询vip Rank
        Map<Integer, String> vipRankMap = new HashMap<>();
        if(!ObjectUtil.equals(req.getHandicapMode(), SiteHandicapModeEnum.China.getCode())){
            List<SiteVIPRankVO> siteVIPRankVOS = vipRankApi.getVipRankListBySiteCode(siteCode).getData();
            if (CollectionUtil.isNotEmpty(siteVIPRankVOS)){
                vipRankMap = siteVIPRankVOS.stream().collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode,SiteVIPRankVO::getVipRankNameI18nCode,(k1, k2)->k2));
            }
        }
        if(null != resultPage && ObjectUtils.isNotEmpty(resultPage.getRecords())){
            Map<Integer, String> finalVipMap = vipMap;
            Map<Integer, String> finalVipRankMap = vipRankMap;
            resultPage.getRecords().forEach(obj->{
                ReportVIPDataPage vo = new ReportVIPDataPage();
                BeanUtils.copyProperties(obj, vo);
                if(ObjectUtils.isNotEmpty(obj.getVipGradeCode())){
                    vo.setVipGradeCodeName(finalVipMap.get(obj.getVipGradeCode()));
                }
                if(ObjectUtils.isNotEmpty(obj.getVipRankCode())){
                    if(CollectionUtil.isNotEmpty(finalVipRankMap)){
                        vo.setVipRankCodeName(finalVipRankMap.get(obj.getVipRankCode()));
                    }
                }
                voList.add(vo);
            });
            BeanUtils.copyProperties(resultPage, resPage);
        }
        resPage.setRecords(voList);
        result.setPageList(resPage);
        ReportVIPDataPage total = new ReportVIPDataPage();
        BigDecimal receiveBonus = reportVIPInfoRepository.selectBonusTotal(req);
//        QueryWrapper<ReportVIPInfoPO> queryWrapperTotal = new QueryWrapper<>();
//        queryWrapperTotal.eq(ObjectUtil.isNotEmpty(req.getSiteCode()), "site_code",
//                req.getSiteCode());
//        queryWrapperTotal.eq(ObjectUtil.isNotEmpty(req.getStartTime()), "date_show",
//                req.getStartTime());
//        queryWrapperTotal.eq(ObjectUtil.isNotEmpty(req.getVipRankCode()), "vip_rank_code",
//                req.getVipRankCode());
//        queryWrapperTotal.eq(ObjectUtil.isNotEmpty(req.getVipGradeCode()), "vip_grade_code",
//                req.getVipGradeCode());
//        queryWrapperTotal.select(" SUM(receive_bonus) As receiveBonus");
        total.setReceiveBonus(BigDecimal.ZERO);
//        ReportVIPInfoPO po = this.baseMapper.selectOne(queryWrapperTotal);
        if(null != receiveBonus){
            total.setReceiveBonus(receiveBonus);
        }
        result.setTotalPage(total);
        return ResponseVO.success(result);
    }

    public ResponseVO<Long> getTotalCount(final ReportVIPDataReq req) {
        LambdaQueryWrapper<ReportVIPInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getSiteCode()), ReportVIPInfoPO::getSiteCode, req.getSiteCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getStartTime()), ReportVIPInfoPO::getDateShow,
                req.getStartTime());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getVipRankCode()), ReportVIPInfoPO::getVipRankCode,
                req.getVipRankCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getVipGradeCode()), ReportVIPInfoPO::getVipGradeCode,
                req.getVipGradeCode());
        return ResponseVO.success(this.baseMapper.selectCount(queryWrapper));
    }
}
