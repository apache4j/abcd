package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.*;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendPageVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendRequestVO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.google.common.collect.Lists;
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
public class UserCoinRecordService extends ServiceImpl<UserCoinRecordRepository, UserCoinRecordPO> {

    private final UserCoinRecordRepository userCoinRecordRepository;

    private final RiskApi riskApi;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    public UserCoinRecordResponseVO listUserCoinRecordPage(UserCoinRecordRequestVO vo) {
        Page<UserCoinRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserCoinRecordPO> lqw = buildLqw(vo);

        Page<UserCoinRecordPO> userCoinRecordPOPage = userCoinRecordRepository.selectPage(page, lqw);

        Page<UserCoinRecordVO> userCoinRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userCoinRecordPOPage, userCoinRecordVOPage);

        List<UserCoinRecordVO> userCoinRecordVOList = ConvertUtil.entityListToModelList(userCoinRecordPOPage.getRecords(), UserCoinRecordVO.class);
//        convertProperty(userCoinRecordVOList);


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
                .map(UserCoinRecordVO::getRiskControlLevelId)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
        Map<String, RiskLevelDetailsVO> riskLevelDetailsVOMap = riskApi.getByIds(new ArrayList<>(riskIds));

        for (UserCoinRecordVO record : userCoinRecordVOList) {
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
        UserCoinRecordResponseVO userCoinRecordResponseVO = new UserCoinRecordResponseVO();
        userCoinRecordResponseVO.setUserCoinRecordVOPage(userCoinRecordVOPage);
        if (!vo.getExportFlag()) {
            //汇总小计
            userCoinRecordResponseVO.setCurrentPage(getSubtotal(userCoinRecordVOList));

            //汇总总计
            userCoinRecordResponseVO.setTotalPage(getTotal(vo));
        }
        return userCoinRecordResponseVO;
    }

    public Long userCoinRecordPageCount(UserCoinRecordRequestVO vo) {
        //绑定条件
        LambdaQueryWrapper<UserCoinRecordPO> lqw = buildLqw(vo);
        return userCoinRecordRepository.selectCount(lqw);
    }

    private List<UserCoinRecordVO> convertProperty(List<UserCoinRecordVO> userCoinRecordVOList) {
        /*Map<String, List<SystemParamVO>> map = systemBusinessFeignResource
                .getSystemParamsByList(List.of(CommonConstant.BUSINESS_COIN_TYPE,CommonConstant.COIN_TYPE,CommonConstant.COIN_BALANCE_TYPE,
                        CommonConstant.USER_ACCOUNT_STATUS,CommonConstant.RISK_CONTROL_LEVEL));*/
        List<UserCoinRecordVO> list = userCoinRecordVOList.stream().map(record -> {

           /* Optional<SystemParamVO> businessCoinType = map.get(CommonConstant.BUSINESS_COIN_TYPE)
                    .stream().filter(item -> item.getCode().equals(String.valueOf(record.getBusinessCoinType()))).findFirst();
            businessCoinType.ifPresent(systemParamVO -> record.setBusinessCoinTypeName(systemParamVO.getValue()));

            Optional<SystemParamVO> coinType = map.get(CommonConstant.COIN_TYPE)
                    .stream().filter(item -> item.getCode().equals(String.valueOf(record.getCoinType()))).findFirst();
            coinType.ifPresent(systemParamVO -> record.setCoinTypeName(systemParamVO.getValue()));

            Optional<SystemParamVO> coinBalanceType = map.get(CommonConstant.COIN_BALANCE_TYPE)
                    .stream().filter(item -> item.getCode().equals(String.valueOf(record.getBalanceType()))).findFirst();
            coinBalanceType.ifPresent(systemParamVO -> record.setBalanceTypeName(systemParamVO.getValue()));
*/

            if (null != record.getVipRank()) {
                record.setVipRankName("VIP" + record.getVipRank());
            }

            /*if(StringUtils.isNotBlank(record.getAccountStatus())){
                Map<String, String> statusMap = map.get(CommonConstant.USER_ACCOUNT_STATUS).stream()
                        .collect(Collectors.toMap(SystemParamVO::getCode, SystemParamVO::getValue));
                List<CodeValueVO> userAccountStatusList = Lists.newArrayList();
                for (String str : record.getAccountStatus().split(",")) {
                    userAccountStatusList.add(CodeValueVO.builder().code(str).value(statusMap.get(str)).build());
                }
                record.setAccountStatusName(userAccountStatusList);
            }*/


            return record;
        }).toList();

        return list;
    }


    public LambdaQueryWrapper<UserCoinRecordPO> buildLqw(UserCoinRecordRequestVO vo) {
        LambdaQueryWrapper<UserCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ObjectUtil.isNotEmpty(vo.getSiteCode()),UserCoinRecordPO::getSiteCode, vo.getSiteCode());
        lqw.ge(null != vo.getCoinRecordStartTime(), UserCoinRecordPO::getCreatedTime, vo.getCoinRecordStartTime());
        lqw.lt(null != vo.getCoinRecordEndTime(), UserCoinRecordPO::getCreatedTime, vo.getCoinRecordEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserCoinRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrencyCode()), UserCoinRecordPO::getCurrency, vo.getCurrencyCode());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserCoinRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()), UserCoinRecordPO::getAgentName, vo.getAgentAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountStatus()), UserCoinRecordPO::getAccountStatus, vo.getAccountStatus());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountType()), UserCoinRecordPO::getAccountType, vo.getAccountType());
        lqw.eq(StringUtils.isNotBlank(vo.getRiskLevelId()), UserCoinRecordPO::getRiskControlLevelId, vo.getRiskLevelId());
        lqw.ge(StringUtils.isNotBlank(vo.getMinVipRank()), UserCoinRecordPO::getVipRank, vo.getMinVipRank());
        lqw.le(StringUtils.isNotBlank(vo.getMaxVipRank()), UserCoinRecordPO::getVipRank, vo.getMaxVipRank());
        lqw.ge(StringUtils.isNotBlank(vo.getMinVipGradeCode()), UserCoinRecordPO::getVipGradeCode, vo.getMinVipGradeCode());
        lqw.le(StringUtils.isNotBlank(vo.getMaxVipGradeCode()), UserCoinRecordPO::getVipGradeCode, vo.getMaxVipGradeCode());
        if (!CollectionUtils.isEmpty(vo.getBusinessCoinTypeList())) {
            lqw.in(UserCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getBusinessCoinType()), UserCoinRecordPO::getBusinessCoinType, vo.getBusinessCoinType());
        }
        if (!CollectionUtils.isEmpty(vo.getCoinTypeList())) {
            lqw.in(UserCoinRecordPO::getCoinType, vo.getCoinTypeList());
        } else {
            lqw.eq(StringUtils.isNotBlank(vo.getCoinType()), UserCoinRecordPO::getCoinType, vo.getCoinType());
        }
        lqw.eq(StringUtils.isNotBlank(vo.getBalanceType()), UserCoinRecordPO::getBalanceType, vo.getBalanceType());
        lqw.ge(null != vo.getMinCoinValue(), UserCoinRecordPO::getCoinValue, vo.getMinCoinValue());
        lqw.le(null != vo.getMaxCoinValue(), UserCoinRecordPO::getCoinValue, vo.getMaxCoinValue());

        if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("vipRank".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserCoinRecordPO::getVipRank);
            }
            if ("vipGradeCode".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserCoinRecordPO::getVipGradeCode);
            }
            if ("coinFrom".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserCoinRecordPO::getCoinFrom);
            }
            if ("coinValue".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserCoinRecordPO::getCoinValue);
            }
            if ("coinTo".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), UserCoinRecordPO::getCoinTo);
            }
        } else {
            lqw.orderByDesc(UserCoinRecordPO::getCreatedTime);
            lqw.orderByAsc(UserCoinRecordPO::getId);
        }


        return lqw;
    }

    public UserCoinRecordVO getSubtotal(List<UserCoinRecordVO> userCoinRecordVOList) {
        //汇总小计
        BigDecimal sumCoinFrom = userCoinRecordVOList.stream()
                .map(UserCoinRecordVO::getCoinFrom)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCoinTo = userCoinRecordVOList.stream()
                .map(UserCoinRecordVO::getCoinTo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumCoinValue = userCoinRecordVOList.stream()
                .map(UserCoinRecordVO::getCoinValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        UserCoinRecordVO userCoinRecordVO = new UserCoinRecordVO();
        userCoinRecordVO.setOrderNo("小计");
        userCoinRecordVO.setCoinFrom(sumCoinFrom);
        userCoinRecordVO.setCoinTo(sumCoinTo);
        userCoinRecordVO.setCoinValue(sumCoinValue);

        return userCoinRecordVO;
    }

    public UserCoinRecordVO getTotal(UserCoinRecordRequestVO vo) {
        return userCoinRecordRepository.sumUserCoinRecord(vo);
    }

    public ResponseVO<Page<UserDividendPageVO>> userDividendPage(final UserDividendRequestVO requestVO) {
        Page<UserDividendPageVO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        requestVO.setSystemTime(System.currentTimeMillis());
        Page<UserDividendPageVO> pageList = userCoinRecordRepository.userDividendPage(page, requestVO);
        pageList.getRecords().forEach(obj -> {
        });
        return ResponseVO.success(pageList);
    }

    public List<UserCoinRecordVO> getUserCoinRecords(UserCoinRecordRequestVO req) {
        List<UserCoinRecordPO> userCoinRecordPOS = userCoinRecordRepository.selectList(Wrappers.<UserCoinRecordPO>lambdaQuery()
                .eq(StringUtils.isNotEmpty(req.getUserId()), UserCoinRecordPO::getUserId, req.getUserId())
                .eq(StringUtils.isNotEmpty(req.getOrderNo()), UserCoinRecordPO::getOrderNo, req.getOrderNo())
                .in(CollUtil.isNotEmpty(req.getOrderNoList()), UserCoinRecordPO::getOrderNo, req.getOrderNoList())
                .eq(StringUtils.isNotEmpty(req.getUserAccount()), UserCoinRecordPO::getUserAccount, req.getUserAccount())
                .eq(StringUtils.isNotEmpty(req.getSiteCode()), UserCoinRecordPO::getSiteCode, req.getSiteCode())
                .eq(StringUtils.isNotEmpty(req.getBusinessCoinType()), UserCoinRecordPO::getBusinessCoinType, req.getBusinessCoinType())
                .eq(StringUtils.isNotEmpty(req.getCoinType()), UserCoinRecordPO::getCoinType, req.getCoinType())
                .eq(StringUtils.isNotEmpty(req.getBalanceType()), UserCoinRecordPO::getBalanceType, req.getBalanceType())
                .eq(StringUtils.isNotEmpty(req.getRemark()), UserCoinRecordPO::getRemark, req.getRemark())
                .in(CollUtil.isNotEmpty(req.getRemarkList()), UserCoinRecordPO::getRemark, req.getRemarkList())
                .likeRight(StringUtils.isNotEmpty(req.getRoundIdBetId()), UserCoinRecordPO::getRemark, req.getRoundIdBetId())
        );
        if (CollectionUtil.isEmpty(userCoinRecordPOS)) {
            return Lists.newArrayList();
        }
        return BeanUtil.copyToList(userCoinRecordPOS, UserCoinRecordVO.class);
    }

    public Long callFriendRechargeCount(UserCoinRecordCallFriendsRequestVO requestVO) {
        return userCoinRecordRepository.callFriendRechargeCount(requestVO);
    }

    public List<String> getOrderNoByOrders(List<String> orders) {
        return userCoinRecordRepository.getOrderNoByOrders(orders);
    }

    public Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(WinLoseRecalculateReqWalletVO vo) {
        // 统计，3 VIP福利 4 活动优惠 8平台币转换 9 其他调整' 10 返水 11 封控
        vo.setBusinessType(Arrays.asList(WalletEnum.BusinessCoinTypeEnum.MEMBER_VIP_BENEFITS.getCode(),
                WalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode(),
                WalletEnum.BusinessCoinTypeEnum.PLATFORM_CONVERSION.getCode(),
                WalletEnum.BusinessCoinTypeEnum.OTHER_ADJUSTMENTS.getCode(),
                WalletEnum.BusinessCoinTypeEnum.REBATE.getCode(),
                WalletEnum.BusinessCoinTypeEnum.RISK_CONTROL_ADJUSTMENT.getCode())
        );

        return userCoinRecordRepository.winLoseRecalculateMainPage(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public UserCoinRecordVO getUserCoinRecord(String remark, String userId, String balanceType) {
        LambdaQueryWrapper<UserCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserCoinRecordPO::getRemark, remark);
        lqw.eq(UserCoinRecordPO::getUserId, userId);
        lqw.eq(UserCoinRecordPO::getBalanceType, balanceType);
        UserCoinRecordPO userCoinRecordPO = userCoinRecordRepository.selectOne(lqw);
        return ConvertUtil.entityToModel(userCoinRecordPO, UserCoinRecordVO.class);
    }
    public List<UserCoinRecordVO> getUserCoinRecordsForEVO(String orderNo, String userId) {
        LambdaQueryWrapper<UserCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserCoinRecordPO::getOrderNo, orderNo);
        lqw.eq(UserCoinRecordPO::getUserId, userId);
        List<UserCoinRecordPO> userCoinRecordPOs = userCoinRecordRepository.selectList(lqw);
        return ConvertUtil.entityListToModelList(userCoinRecordPOs, UserCoinRecordVO.class);
    }
    public List<UserCoinRecordVO> getUserCoinRecordPG(String orderNo, String userId, String balanceType) {
        LambdaQueryWrapper<UserCoinRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserCoinRecordPO::getOrderNo, orderNo);
        lqw.eq(UserCoinRecordPO::getUserId, userId);
        lqw.eq(UserCoinRecordPO::getBalanceType, balanceType);
        List<UserCoinRecordPO> userCoinRecordPO = userCoinRecordRepository.selectList(lqw);
        return ConvertUtil.entityListToModelList(userCoinRecordPO, UserCoinRecordVO.class);
    }

    public UserCoinRecordVO getJDBUserCoinRecords(JDBUserCoinRecordVO req) {
        UserCoinRecordPO userCoinRecordPOS = userCoinRecordRepository.selectOne(Wrappers.<UserCoinRecordPO>lambdaQuery()
                .eq(StringUtils.isNotEmpty(req.getUserId()), UserCoinRecordPO::getUserId, req.getUserId())
                .eq(StringUtils.isNotEmpty(req.getOrdersNo()),UserCoinRecordPO::getRemark, req.getOrdersNo())
                .eq(StringUtils.isNotEmpty(req.getUserAccount()), UserCoinRecordPO::getUserAccount, req.getUserAccount())
                .eq(StringUtils.isNotEmpty(req.getSiteCode()), UserCoinRecordPO::getSiteCode, req.getSiteCode())
                .eq(StringUtils.isNotEmpty(req.getBusinessCoinType()), UserCoinRecordPO::getBusinessCoinType, req.getBusinessCoinType())
                .eq(StringUtils.isNotEmpty(req.getCoinType()), UserCoinRecordPO::getCoinType, req.getCoinType())
                .eq(StringUtils.isNotEmpty(req.getBalanceType()), UserCoinRecordPO::getBalanceType, req.getBalanceType())
        );
        if (userCoinRecordPOS == null) {
            return null;
        }
        return BeanUtil.copyProperties(userCoinRecordPOS, UserCoinRecordVO.class);
    }

    public List<UserCoinRecordVO> getJDBBetRecords(JDBUserCoinRecordVO req) {
        List<UserCoinRecordPO> userCoinRecordPOS = userCoinRecordRepository.selectList(Wrappers.<UserCoinRecordPO>lambdaQuery()
                .eq(StringUtils.isNotEmpty(req.getUserId()), UserCoinRecordPO::getUserId, req.getUserId())
                .in(CollUtil.isNotEmpty(req.getReferIds()),UserCoinRecordPO::getRemark, req.getReferIds())
                .eq(StringUtils.isNotEmpty(req.getUserAccount()), UserCoinRecordPO::getUserAccount, req.getUserAccount())
                .eq(StringUtils.isNotEmpty(req.getSiteCode()), UserCoinRecordPO::getSiteCode, req.getSiteCode())
                .eq(StringUtils.isNotEmpty(req.getBusinessCoinType()), UserCoinRecordPO::getBusinessCoinType, req.getBusinessCoinType())
                .eq(StringUtils.isNotEmpty(req.getCoinType()), UserCoinRecordPO::getCoinType, req.getCoinType())
                .eq(StringUtils.isNotEmpty(req.getBalanceType()), UserCoinRecordPO::getBalanceType, req.getBalanceType())
        );
        if (userCoinRecordPOS.isEmpty()) {
            return Lists.newArrayList();
        }
        return BeanUtil.copyToList(userCoinRecordPOS, UserCoinRecordVO.class);
    }
}
