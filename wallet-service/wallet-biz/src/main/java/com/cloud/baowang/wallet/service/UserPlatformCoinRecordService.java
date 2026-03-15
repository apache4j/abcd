package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 会员账变记录 服务类
 *
 * @author qiqi
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserPlatformCoinRecordService extends ServiceImpl<UserPlatformCoinRecordRepository, UserPlatformCoinRecordPO> {

    private final UserPlatformCoinRecordRepository userPlatformCoinRecordRepository;

    private final RiskApi riskApi;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    public UserPlatformCoinRecordResponseVO listUserPlatformCoinRecordPage(UserPlatformCoinRecordRequestVO vo) {
        Page<UserPlatformCoinRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserPlatformCoinRecordPO> lqw = buildLqw(vo);

        Page<UserPlatformCoinRecordPO> userCoinRecordPOPage = userPlatformCoinRecordRepository.selectPage(page, lqw);

        Page<UserPlatformCoinRecordVO> userCoinRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userCoinRecordPOPage, userCoinRecordVOPage);

        List<UserPlatformCoinRecordVO> userCoinRecordVOList = ConvertUtil.entityListToModelList(userCoinRecordPOPage.getRecords(), UserPlatformCoinRecordVO.class);


        // 查询vip等级
        List<SiteVIPGradeVO> siteVIPGradeVOS = vipGradeApi.queryAllVIPGradeBySiteCode(vo.getSiteCode());
        Map<Integer, String> vipGradeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPGradeVOS)) {
            vipGradeMap = siteVIPGradeVOS.stream()
                    .filter(vip -> vip.getVipGradeCode() != null && StringUtils.isNotBlank(vip.getVipGradeName()))
                    .collect(Collectors.toMap(
                            SiteVIPGradeVO::getVipGradeCode,
                            SiteVIPGradeVO::getVipGradeName, (k1, k2) -> k2));
        }

        // 查询vip Rank
        List<SiteVIPRankVO> siteVIPRankVOS = vipRankApi.getVipRankListBySiteCode(vo.getSiteCode()).getData();
        Map<Integer, String> vipRankMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
            vipRankMap = siteVIPRankVOS.stream()
                    .filter(vip -> vip.getVipRankCode() != null
                            && StringUtils.isNotBlank(vip.getVipRankNameI18nCode())
                            && StringUtils.isNotBlank(vip.getVipGradeCodes())) // Corrected: ensure VipRankCode is not null (since it's an Integer)
                    .collect(Collectors.toMap(
                            SiteVIPRankVO::getVipRankCode,
                            SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2));
        }
        // 会员标签
        Set<String> labelIds = userCoinRecordVOList.stream()
                .filter(record -> StrUtil.isNotEmpty(record.getUserLabelId()))
                .flatMap(record -> Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA)))
                .collect(Collectors.toSet());

        List<GetUserLabelByIdsVO> userLabels = siteUserLabelConfigApi.getUserLabelByIds(new ArrayList<>(labelIds));
        Map<String, String> userLabelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userLabels)) {
            userLabelMap = userLabels.stream()
                    .collect(Collectors.toMap(GetUserLabelByIdsVO::getId, GetUserLabelByIdsVO::getLabelName, (K1, K2) -> K2));
        }


        // 风控层级
        Set<String> riskIds = userCoinRecordVOList.stream()
                .map(UserPlatformCoinRecordVO::getRiskControlLevelId)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskIds));

        for (UserPlatformCoinRecordVO record : userCoinRecordVOList) {
            // 会员标签
            if (StrUtil.isNotEmpty(record.getUserLabelId())) {
                // 直接通过流进行 split 和非空检查
                List<String> userLabelList = Arrays.stream(record.getUserLabelId().split(CommonConstant.COMMA))
                        .filter(StrUtil::isNotEmpty)// 过滤掉空的标签ID
                        .map(userLabelMap::get)// 映射到标签名称
                        .filter(StrUtil::isNotEmpty)// 过滤掉映射为空的情况
                        .map(label -> "#" + label)// 给标签加上 #
                        .toList();
                if (CollUtil.isNotEmpty(userLabelList)) {
                    record.setUserLabel(String.join("", userLabelList));
                }
            }

            // 风控层级
            if (null != record.getRiskControlLevelId()) {
                RiskLevelDetailsVO riskLevelDetailsVO = riskLevelDetailsVOMap.get(record.getRiskControlLevelId());
                record.setRiskControlLevel(riskLevelDetailsVO == null ? "" : riskLevelDetailsVO.getRiskControlLevel());
            }

            //VIP段位
            // vip等级
            if (ObjectUtils.isNotEmpty(record.getVipGradeCode())) {
                record.setVipGradeCodeName(vipGradeMap.get(record.getVipGradeCode()));
            }
            if (ObjectUtils.isNotEmpty(record.getVipRank())) {
                record.setVipRankName(vipRankMap.get(record.getVipRank()));
            }


        }

        userCoinRecordVOPage.setRecords(userCoinRecordVOList);
        UserPlatformCoinRecordResponseVO userPlatformCoinRecordResponseVO = new UserPlatformCoinRecordResponseVO();
        userPlatformCoinRecordResponseVO.setUserPlatformCoinRecordVOPage(userCoinRecordVOPage);
        if(!vo.getExportFlag()){
            //汇总小计
            userPlatformCoinRecordResponseVO.setCurrentPage(getSubtotal(userCoinRecordVOList));

            //汇总总计
            userPlatformCoinRecordResponseVO.setTotalPage(getTotal(vo));
        }
        return userPlatformCoinRecordResponseVO;
    }

    public Long userPlatformCoinRecordPageCount(UserPlatformCoinRecordRequestVO vo) {
        //绑定条件
        LambdaQueryWrapper<UserPlatformCoinRecordPO> lqw = buildLqw(vo);
        return userPlatformCoinRecordRepository.selectCount(lqw);
    }



    public LambdaQueryWrapper<UserPlatformCoinRecordPO> buildLqw(UserPlatformCoinRecordRequestVO vo) {
        LambdaQueryWrapper<UserPlatformCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserPlatformCoinRecordPO::getSiteCode,vo.getSiteCode());
        lqw.ge(null != vo.getCoinRecordStartTime(), UserPlatformCoinRecordPO::getCreatedTime, vo.getCoinRecordStartTime());
        lqw.lt(null != vo.getCoinRecordEndTime(), UserPlatformCoinRecordPO::getCreatedTime, vo.getCoinRecordEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserPlatformCoinRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()), UserPlatformCoinRecordPO::getCurrency, vo.getCurrencyCode());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserPlatformCoinRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()), UserPlatformCoinRecordPO::getAgentName, vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountStatus()), UserPlatformCoinRecordPO::getAccountStatus, vo.getAccountStatus());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountType()), UserPlatformCoinRecordPO::getAccountType, vo.getAccountType());
        lqw.eq(StringUtils.isNotBlank(vo.getRiskLevelId()), UserPlatformCoinRecordPO::getRiskControlLevelId, vo.getRiskLevelId());
        lqw.ge(StringUtils.isNotBlank(vo.getMinVipRank()), UserPlatformCoinRecordPO::getVipRank, vo.getMinVipRank());
        lqw.le(StringUtils.isNotBlank(vo.getMaxVipRank()), UserPlatformCoinRecordPO::getVipRank, vo.getMaxVipRank());
        lqw.ge(StringUtils.isNotBlank(vo.getMinVipGradeCode()), UserPlatformCoinRecordPO::getVipGradeCode, vo.getMinVipGradeCode());
        lqw.le(StringUtils.isNotBlank(vo.getMaxVipGradeCode()), UserPlatformCoinRecordPO::getVipGradeCode, vo.getMaxVipGradeCode());
        if (!CollectionUtils.isEmpty(vo.getBusinessCoinTypeList())) {
            lqw.in(UserPlatformCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getBusinessCoinType()), UserPlatformCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinType());
        }
        if (!CollectionUtils.isEmpty(vo.getCoinTypeList())) {
            lqw.in(UserPlatformCoinRecordPO::getCoinType, vo.getCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getCoinType()), UserPlatformCoinRecordPO::getCoinType, vo.getCoinType());
        }
        lqw.eq(StringUtils.isNotBlank(vo.getBalanceType()), UserPlatformCoinRecordPO::getBalanceType, vo.getBalanceType());
        lqw.ge(null != vo.getMinCoinValue(), UserPlatformCoinRecordPO::getCoinValue, vo.getMinCoinValue());
        lqw.le(null != vo.getMaxCoinValue(), UserPlatformCoinRecordPO::getCoinValue, vo.getMaxCoinValue());

       /* if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("createdTime".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserPlatformCoinRecordPO::getCreatedTime);
            }
            if ("vipRank".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserPlatformCoinRecordPO::getVipRank);
            }
            if ("coinFrom".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserPlatformCoinRecordPO::getCoinFrom);
            }
            if ("coinValue".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserPlatformCoinRecordPO::getCoinValue);
            }
            if ("coinTo".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserPlatformCoinRecordPO::getCoinTo);
            }
        } else {

        }*/
        lqw.orderByDesc(UserPlatformCoinRecordPO::getCreatedTime);
        lqw.orderByAsc(UserPlatformCoinRecordPO::getId);


        return lqw;
    }

    public UserPlatformCoinRecordVO getSubtotal(List<UserPlatformCoinRecordVO> userCoinRecordVOList) {
        //汇总小计
        BigDecimal sumCoinFrom = userCoinRecordVOList.stream()
                .map(UserPlatformCoinRecordVO::getCoinFrom)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCoinTo = userCoinRecordVOList.stream()
                .map(UserPlatformCoinRecordVO::getCoinTo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCoinValue = userCoinRecordVOList.stream()
                .map(UserPlatformCoinRecordVO::getCoinValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserPlatformCoinRecordVO userCoinRecordVO = new UserPlatformCoinRecordVO();
        userCoinRecordVO.setOrderNo("小计");
        userCoinRecordVO.setCoinFrom(sumCoinFrom);
        userCoinRecordVO.setCoinTo(sumCoinTo);
        userCoinRecordVO.setCoinValue(sumCoinValue);

        return userCoinRecordVO;
    }

    public UserPlatformCoinRecordVO getTotal(UserPlatformCoinRecordRequestVO vo) {
        return userPlatformCoinRecordRepository.sumUserPlatformCoinRecord(vo);
    }

    public Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(WinLoseRecalculateReqWalletVO vo) {

        vo.setBusinessType(Arrays.asList(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode(),
                PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode(),
                PlatformWalletEnum.BusinessCoinTypeEnum.MEDAL_REWARD.getCode(),
                //PlatformWalletEnum.BusinessCoinTypeEnum.REBATE.getCode(),
                PlatformWalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode()));
        return userPlatformCoinRecordRepository.winLoseRecalculateMainPage(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

}
