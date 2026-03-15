package com.cloud.baowang.wallet.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityBaseApi;
import com.cloud.baowang.activity.api.api.v2.ActivityBaseV2Api;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.system.api.enums.AgentParamValueEnum;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.ManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.enums.ManualDownAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentActiveVO;
import com.cloud.baowang.wallet.api.vo.agent.AgentUserTeamParam;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.system.api.api.AgentParamConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceQueryVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.GetUserBalanceVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualAccountVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualDownAccountResultVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpRecordResult;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserManualUpSubmitVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListResponse;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.GetDepositWithdrawManualRecordListVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinManualDown.UserManualDownRecordVO;
import com.cloud.baowang.wallet.po.SiteCurrencyInfoPO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserManualUpDownRecordPO;
import com.cloud.baowang.wallet.repositories.SiteCurrencyInfoRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.wallet.repositories.UserManualUpDownRecordRepository;
import com.cloud.baowang.wallet.util.WalletServerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会员人工加减额记录 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserManualUpDownRecordService extends ServiceImpl<UserManualUpDownRecordRepository, UserManualUpDownRecordPO> {
    private final ActivityBaseApi activityBaseApi;
    private final SiteCurrencyInfoRepository currencyInfoRepository;
    private final UserManualUpDownRecordRepository userManualUpDownRecordRepository;
    private final UserInfoApi userInfoApi;
    private final AgentParamConfigApi agentParamConfigApi;
    private final VipGradeApi vipGradeApi;
    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final UserCoinRepository userCoinRepository;

    private final ActivityBaseV2Api activityBaseV2Api;

    @Transactional
    public ResponseVO<Boolean> submit(UserManualUpSubmitVO vo, String operator) {
        // 校验会员信息
        if (null == vo.getUserAccounts() || vo.getUserAccounts().isEmpty()) {
            return ResponseVO.fail(ResultCode.USER_INFO_NOT_NULL);
        }
        String siteCode = vo.getSiteCode();
        //如果类型是会员活动的，校验一下活动id是否存在
        ManualAdjustTypeEnum enums = ManualAdjustTypeEnum.nameOfCode(vo.getAdjustType());
        if (enums == null ) {
            throw new BaowangDefaultException(ResultCode.ADJUST_TYPE_IS_ERROR);
        }
        checkAdjustType(vo, enums);



        List<UserManualUpDownRecordPO> pos = new ArrayList<>();
        String currencyCode = vo.getCurrencyCode();
        SiteCurrencyInfoRespVO resp = siteCurrencyInfoService.getByCurrencyCode(vo.getSiteCode(), currencyCode);
        BigDecimal finalRate = resp.getFinalRate();
        for (UserManualAccountVO userManualAccountVO : vo.getUserAccounts()) {
            BigDecimal adjustAmount = userManualAccountVO.getAdjustAmount();
            if(null == adjustAmount){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            // 校验调整金额
            checkAdjustAmount(userManualAccountVO.getAdjustAmount().toString(), vo.getSiteCode(), vo.getCurrencyCode());
            // 校验流水倍数
            checkRunningWaterMultiple(userManualAccountVO.getRunningWaterMultiple());

            UserBasicRequestVO userBasicRequestVO = new UserBasicRequestVO();
            userBasicRequestVO.setUserAccount(userManualAccountVO.getUserAccount());
            userBasicRequestVO.setSiteCode(siteCode);
            UserInfoVO userInfoVO = userInfoApi.getUserInfoVO(userBasicRequestVO);

            // 开始保存
            String orderNo = WalletServerUtil.getUserManualOrderNo();
            UserManualUpDownRecordPO po = new UserManualUpDownRecordPO();
            po.setFinalRate(finalRate);
            po.setSiteCode(siteCode);
            po.setAgentId(userInfoVO.getSuperAgentId());
            po.setAgentAccount(userInfoVO.getSuperAgentAccount());
            po.setUserAccount(userInfoVO.getUserAccount());
            po.setUserId(userInfoVO.getUserId());
            po.setUserName(userInfoVO.getUserName());
            po.setCurrencyCode(vo.getCurrencyCode());
            po.setVipGradeCode(userInfoVO.getVipGradeCode());
            po.setOrderNo(orderNo);
            po.setAdjustWay(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());

            po.setAdjustType(vo.getAdjustType());
            if (ManualAdjustTypeEnum.PROMOTIONS.getCode().equals(vo.getAdjustType())) {
                //只有类型是活动时,才保存活动id
                po.setActivityTemplate(vo.getActivityTemplate());
                po.setActivityId(vo.getActivityId());
            }
            po.setAdjustAmount(userManualAccountVO.getAdjustAmount());
            po.setRunningWaterMultiple(new BigDecimal(userManualAccountVO.getRunningWaterMultiple()));
            po.setCertificateAddress(vo.getCertificateAddress());
            po.setApplyReason(vo.getApplyReason());
            po.setApplyTime(System.currentTimeMillis());
            po.setApplicant(operator);
            po.setAuditId(operator);
            po.setAuditDatetime(System.currentTimeMillis());
            po.setAuditStatus(ReviewStatusEnum.REVIEW_PENDING.getCode());
            po.setReviewOperation(ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());
            po.setLockStatus(LockStatusEnum.UNLOCK.getCode());
            po.setCreator(operator);
            po.setUpdater(operator);
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            pos.add(po);
        }
        this.saveBatch(pos);
        return ResponseVO.success();
    }

    private void checkAdjustType(UserManualUpSubmitVO vo, ManualAdjustTypeEnum enums) {

        if (!enums.getCode().equals(ManualAdjustTypeEnum.PROMOTIONS.getCode())){
            return;
        }
        if (StrUtil.isEmpty(vo.getActivityId()) || StrUtil.isEmpty(vo.getActivityTemplate())){
            throw new BaowangDefaultException(ResultCode.MISSING_PARAMETERS);
        }
        ResponseVO<ActivityBaseRespVO> responseVO = null;
        //会员活动，根据活动id查询是否存在这个活动
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            responseVO = activityBaseV2Api.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }else{
            responseVO = activityBaseApi.queryActivityByActivityNoAndTemplate(vo.getActivityId(), vo.getActivityTemplate(), vo.getSiteCode());
        }

        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }
        ActivityBaseRespVO data = responseVO.getData();
        if (data == null) {
            throw new BaowangDefaultException(ResultCode.MANUAL_ACTIVITY_ID_NOT_EXIT);
        }
    }

    public ResponseVO<GetUserBalanceVO> getUserBalance(GetUserBalanceQueryVO vo) {
        if (StrUtil.isEmpty(vo.getUserAccount())) {
            throw new BaowangDefaultException(ResultCode.USER_INFO_NOT_NULL);
        }
        GetUserBalanceVO result = getUserBalancing(vo);
        return ResponseVO.success(result);
    }

    /**
     * 批量查询会员账号-币种信息
     *
     * @param vo
     * @return
     */
    private GetUserBalanceVO getUserBalancing(GetUserBalanceQueryVO vo) {
        GetUserBalanceVO result = new GetUserBalanceVO();
        String userAccount = vo.getUserAccount();
        List<String> userAccounts = Arrays.asList(userAccount.split(CommonConstant.COMMA));
        List<UserInfoVO> userInfoVOS = userInfoApi.getUserBalanceBySiteCodeUserAccount(vo.getSiteCode(), userAccounts);
        if (CollectionUtil.isEmpty(userInfoVOS)) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
        }
        //会员账号与实际查询到的会员信息不一致，账号有误
        if (userAccounts.size() != userInfoVOS.size()) {
            throw new BaowangDefaultException(ResultCode.USER_ACCOUNT_HAVE_NO);
        }

        String firstCurrency = userInfoVOS.get(0).getMainCurrency();
        boolean hasMismatch = userInfoVOS.stream()
                .anyMatch(userInfo -> !Objects.equals(firstCurrency, userInfo.getMainCurrency()));
        if (hasMismatch) {
            throw new BaowangDefaultException(ResultCode.USER_CURRENCY_MISMATCH);
        }
        result.setUserIds(userInfoVOS.stream().map(UserInfoVO::getUserId).collect(Collectors.joining(",")));
        result.setUserAccounts(userAccount);
        result.setCurrencyCode(firstCurrency);
        return result;
    }

    /**
     * 校验变更金额
     *
     * @param adjustAmount 金额
     */
    private void checkAdjustAmount(String adjustAmount, String siteCode, String currencyCode) {
        if (StringUtils.isBlank(currencyCode)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        //校验币种
        LambdaQueryWrapper<SiteCurrencyInfoPO> query = Wrappers.lambdaQuery();
        query.eq(SiteCurrencyInfoPO::getSiteCode, siteCode).eq(SiteCurrencyInfoPO::getCurrencyCode, currencyCode);
        SiteCurrencyInfoPO siteCurrencyInfoPO = currencyInfoRepository.selectOne(query);
        if (siteCurrencyInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        try {
            Double.parseDouble(adjustAmount);
            if (new BigDecimal(adjustAmount).compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
            if (ConvertUtil.getDecimalPlace(adjustAmount) > 2) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
    }

    private void checkRunningWaterMultiple(String runningWaterMultiple) {
        try {
            Double.parseDouble(runningWaterMultiple);
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
        if (new BigDecimal(runningWaterMultiple).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
        if (ConvertUtil.getDecimalPlace(runningWaterMultiple) > 0) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_ERROR_DESC);
        }
    }

    public UserManualUpRecordResult getUpRecordPage(UserManualUpRecordPageVO vo) {
        UserManualUpRecordResult result = new UserManualUpRecordResult();
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        Page<UserManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        query.orderByDesc(UserManualUpDownRecordPO::getApplyTime);
        Long applyStartTime = vo.getApplyStartTime();
        Long applyEndTime = vo.getApplyEndTime();
        String orderNo = vo.getOrderNo();
        String userAccount = vo.getUserAccount();
        Integer auditStatus = vo.getAuditStatus();
        Integer adjustType = vo.getAdjustType();
        String adjustAmountMax = vo.getAdjustAmountMax();
        String adjustAmountMin = vo.getAdjustAmountMin();

        query.eq(UserManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        if (applyStartTime != null) {
            query.ge(UserManualUpDownRecordPO::getApplyTime, applyStartTime);
        }

        if (applyEndTime != null) {
            query.le(UserManualUpDownRecordPO::getApplyTime, applyEndTime);
        }

        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserManualUpDownRecordPO::getOrderNo, orderNo);
        }

        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserManualUpDownRecordPO::getUserAccount, userAccount);
        }

        if (auditStatus != null) {
            query.eq(UserManualUpDownRecordPO::getAuditStatus, auditStatus);
        }

        if (adjustType != null) {
            query.eq(UserManualUpDownRecordPO::getAdjustType, adjustType);
        }

        if (StringUtils.isNotBlank(adjustAmountMin)) {
            query.ge(UserManualUpDownRecordPO::getAdjustAmount, new BigDecimal(adjustAmountMin));
        }

        if (StringUtils.isNotBlank(adjustAmountMax)) {
            query.le(UserManualUpDownRecordPO::getAdjustAmount, new BigDecimal(adjustAmountMax));
        }
        page = userManualUpDownRecordRepository.selectPage(page, query);
        List<UserManualUpDownRecordPO> records = page.getRecords();


        Map<Integer, String> gradeCodeNameMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<Integer> gradeCodes = records.stream()
                    .map(UserManualUpDownRecordPO::getVipGradeCode)
                    .filter(Objects::nonNull)
                    .toList();
            gradeCodeNameMap = vipGradeApi.queryAllVIPGradeNameMap(vo.getSiteCode());
            /*gradeCodeNameMap = gradeVOS.stream()
                    .collect(Collectors.toMap(
                            SiteVIPGradeVO::getVipGradeCode,
                            SiteVIPGradeVO::getVipGradeName
                    ));*/

        }
        Map<Integer, String> finalGradeCodeNameMap = gradeCodeNameMap;

        IPage<UserManualUpRecordResponseVO> convert = page.convert(item -> {
            UserManualUpRecordResponseVO responseVO = BeanUtil.copyProperties(item, UserManualUpRecordResponseVO.class);
            Integer vipGradeCode = responseVO.getVipGradeCode();
            if (finalGradeCodeNameMap.containsKey(vipGradeCode)) {
                responseVO.setVipGradeCodeName(finalGradeCodeNameMap.get(vipGradeCode));
            }
            return responseVO;
        });

        BigDecimal adjustAmountAll = BigDecimal.ZERO;
        for (UserManualUpRecordResponseVO record : convert.getRecords()) {
            // 调整金额相加
            adjustAmountAll = adjustAmountAll.add(record.getAdjustAmount());
        }
        // 小计
        UserManualUpRecordResponseVO currentPage = new UserManualUpRecordResponseVO();
        currentPage.setOrderNo("小计");
        currentPage.setAdjustAmount(adjustAmountAll);
        result.setCurrentPage(currentPage);

        UserManualUpRecordResponseVO total = new UserManualUpRecordResponseVO();
        total.setOrderNo("总计");
        List<UserManualUpDownRecordPO> totalList = userManualUpDownRecordRepository.selectList(query);
        BigDecimal totalAdjustAmount = BigDecimal.ZERO;
        for (UserManualUpDownRecordPO userManualUpDownRecordPO : totalList) {
            totalAdjustAmount = totalAdjustAmount.add(userManualUpDownRecordPO.getAdjustAmount());
        }
        total.setAdjustAmount(totalAdjustAmount);
        // 总计
        result.setTotalPage(total);
        result.setPageList(ConvertUtil.toConverPage(convert));
        return result;
    }

    public ResponseVO<Long> getUpRecordPageCount(UserManualUpRecordPageVO vo) {
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        Long pageCount = userManualUpDownRecordRepository.getPageCount(vo);
        return ResponseVO.success(pageCount);
    }

    public WalletAgentActiveVO getDepositActiveInfo(final AgentUserTeamParam param) {
        WalletAgentActiveVO vo = new WalletAgentActiveVO();
        // 获取活跃，有效活跃判断有条件
        /*Map<String, List<CodeValueVO>> map = systemParamApi
                .getSystemParamsByList(List.of(CommonConstant.AGENT_ACTIVE_CONFIG)).getData();
        List<CodeValueVO> activeList = map.get(CommonConstant.AGENT_ACTIVE_CONFIG);*/
        param.setStartTime(DateUtil.beginOfDay(new Date(param.getStartTime())).getTime());
        param.setEndTime(DateUtil.endOfDay(new Date(param.getStartTime())).getTime());
        String activeUser = agentParamConfigApi.queryAgentParamConfigByCode(AgentParamValueEnum.ACTIVE_USERS_DEFINITION.getType()).getParamValue();

        String activeValidUser = agentParamConfigApi.queryAgentParamConfigByCode(AgentParamValueEnum.ACTIVE_VALID_USERS_DEFINITION.getType()).getParamValue();

        param.setValidAmount(new BigDecimal(activeUser));
        List<HashMap> todayActiveUserAccount = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(param.getAllDownAgentNum())) {
            // 充值今日活跃
            todayActiveUserAccount = userManualUpDownRecordRepository.selectDepositActiveInfo(param);
        }
        // 充值今日有效活跃
        param.setStartTime(DateUtil.beginOfDay(new Date(param.getStartTime())).getTime());
        param.setEndTime(DateUtil.endOfDay(new Date(param.getStartTime())).getTime());
        param.setValidAmount(new BigDecimal(activeValidUser));
        List<HashMap> todayValidActiveUserAccount = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(param.getAllDownAgentNum())) {
            todayValidActiveUserAccount = userManualUpDownRecordRepository.selectDepositActiveInfo(param);
        }
        // 充值本月活跃
        param.setStartTime(DateUtil.beginOfMonth(new Date(param.getStartTime())).getTime());
        param.setEndTime(DateUtil.endOfMonth(new Date(param.getStartTime())).getTime());
        param.setValidAmount(new BigDecimal(activeUser));
        List<HashMap> monthActiveUserAccount = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(param.getAllDownAgentNum())) {
            monthActiveUserAccount = userManualUpDownRecordRepository.selectDepositActiveInfo(param);
        }
        // 充值本月有效活跃
        param.setStartTime(DateUtil.beginOfMonth(new Date(param.getStartTime())).getTime());
        param.setEndTime(DateUtil.endOfMonth(new Date(param.getStartTime())).getTime());
        param.setValidAmount(new BigDecimal(activeValidUser));
        List<HashMap> monthValidActiveUserAccount = Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(param.getAllDownAgentNum())) {
            monthValidActiveUserAccount = userManualUpDownRecordRepository.selectDepositActiveInfo(param);
        }
        vo.setTodayActive(todayActiveUserAccount.stream().filter(obj -> null != obj.get("userAccount"))
                .map(obj -> obj.get("userAccount").toString()).toList());
        vo.setTodayValidActive(todayValidActiveUserAccount.stream().filter(obj -> null != obj.get("userAccount"))
                .map(obj -> obj.get("userAccount").toString()).toList());
        vo.setMonthActive(monthActiveUserAccount.stream().filter(obj -> null != obj.get("userAccount"))
                .map(obj -> obj.get("userAccount").toString()).distinct().toList());
        vo.setMonthValidActive(monthValidActiveUserAccount.stream().filter(obj -> null != obj.get("userAccount"))
                .map(obj -> obj.get("userAccount").toString()).distinct().toList());
        return vo;
    }

    public GetDepositWithdrawManualRecordListResponse getDepositWithdrawManualRecordList(GetDepositWithdrawManualRecordListVO vo) {
        List<UserManualUpDownRecordPO> list = this
                .lambdaQuery()
                .eq(UserManualUpDownRecordPO::getSiteCode, vo.getSiteCode())
                .eq(UserManualUpDownRecordPO::getAgentAccount, vo.getAgentAccount())
                .eq(StrUtil.isNotEmpty(vo.getUserAccount()), UserManualUpDownRecordPO::getUserAccount, vo.getUserAccount())
                .eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode())
                .ge(UserManualUpDownRecordPO::getUpdatedTime, vo.getStart())
                .le(UserManualUpDownRecordPO::getUpdatedTime, vo.getEnd())
                .and(query ->
                        query.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())
                                .eq(UserManualUpDownRecordPO::getAdjustType, ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode())
                                .or()
                                .eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode())
                                .eq(UserManualUpDownRecordPO::getAdjustType, ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode()))
                .list();

        Map<Integer, List<UserManualUpDownRecordPO>> collect = list.stream()
                .collect(Collectors.groupingBy(UserManualUpDownRecordPO::getAdjustWay));
        // 加额记录(会员存款(后台))
        List<UserManualUpDownRecordPO> userManualUpRecords =
                null == collect.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode()) ? Lists.newArrayList() : collect.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        // 减额记录(会员提款(后台))
        List<UserManualUpDownRecordPO> userManualDownRecords =
                null == collect.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode()) ? Lists.newArrayList() : collect.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());

        Map<String, BigDecimal> userDepositsMap = userManualUpRecords.stream()
                .collect(Collectors.groupingBy(UserManualUpDownRecordPO::getUserAccount, Collectors.reducing(BigDecimal.ZERO, UserManualUpDownRecordPO::getAdjustAmount, BigDecimal::add)));
        Map<String, BigDecimal> userWithdrawsMap = userManualDownRecords.stream()
                .collect(Collectors.groupingBy(UserManualUpDownRecordPO::getUserAccount, Collectors.reducing(BigDecimal.ZERO, UserManualUpDownRecordPO::getAdjustAmount, BigDecimal::add)));

        return GetDepositWithdrawManualRecordListResponse.builder().depositAmount(userDepositsMap).withdrawAmount(userWithdrawsMap).build();
    }


    public long getTotalPendingReviewBySiteCode(String siteCode) {
        LambdaQueryWrapper<UserManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.eq(UserManualUpDownRecordPO::getSiteCode, siteCode);
        query.eq(UserManualUpDownRecordPO::getAdjustWay, ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())
                .eq(UserManualUpDownRecordPO::getReviewOperation, ReviewOperationEnum.FIRST_INSTANCE_REVIEW.getCode());

        return this.count(query);
    }


    public Map<String, UserManualDownRecordVO> listStaticData(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Maps.newHashMap();
        }
        LambdaQueryWrapper<UserManualUpDownRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserManualUpDownRecordPO::getAuditStatus, ReviewStatusEnum.REVIEW_PASS.getCode());
        queryWrapper.in(UserManualUpDownRecordPO::getUserId, userIds);
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOS = userManualUpDownRecordRepository.selectList(queryWrapper);
        List<UserManualDownRecordVO> resultList = ConvertUtil.entityListToModelList(userManualUpDownRecordPOS, UserManualDownRecordVO.class);
        Map<String, UserManualDownRecordVO> resultMap = Maps.newHashMap();
        for (UserManualDownRecordVO userManualDownRecordVO : resultList) {
            if (
                    (Objects.equals(userManualDownRecordVO.getAdjustWay(), ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode())) &&
                            (Objects.equals(userManualDownRecordVO.getAdjustType(), ManualAdjustTypeEnum.MEMBER_DEPOSIT.getCode()))
            ) {
                //会员存款
                userManualDownRecordVO.setDepositWithDrawType(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode());
                userManualDownRecordVO.setAdjustTimes(1);
                String mapKey = userManualDownRecordVO.getUserId().concat(DepositWithdrawalOrderTypeEnum.DEPOSIT.getCode().toString());
                if (resultMap.containsKey(mapKey)) {
                    UserManualDownRecordVO depositRecord = resultMap.get(mapKey);
                    depositRecord.setAdjustAmount(depositRecord.getAdjustAmount().add(userManualDownRecordVO.getAdjustAmount()));
                    depositRecord.setAdjustTimes(depositRecord.getAdjustTimes() + userManualDownRecordVO.getAdjustTimes());
                    resultMap.put(mapKey, depositRecord);
                } else {
                    resultMap.put(mapKey, userManualDownRecordVO);
                }
            }
            if (
                    (Objects.equals(userManualDownRecordVO.getAdjustWay(), ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode())) &&
                            (Objects.equals(userManualDownRecordVO.getAdjustType(), ManualDownAdjustTypeEnum.MEMBER_WITHDRAWAL.getCode()))
            ) {
                //会员取款
                userManualDownRecordVO.setDepositWithDrawType(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode());
                userManualDownRecordVO.setAdjustTimes(1);
                String mapKey = userManualDownRecordVO.getUserId().concat(DepositWithdrawalOrderTypeEnum.WITHDRAWAL.getCode().toString());
                if (resultMap.containsKey(mapKey)) {
                    UserManualDownRecordVO depositRecord = resultMap.get(mapKey);
                    depositRecord.setAdjustAmount(depositRecord.getAdjustAmount().add(userManualDownRecordVO.getAdjustAmount()));
                    depositRecord.setAdjustTimes(depositRecord.getAdjustTimes() + userManualDownRecordVO.getAdjustTimes());
                    resultMap.put(mapKey, depositRecord);
                } else {
                    resultMap.put(mapKey, userManualDownRecordVO);
                }
            }
        }
        return resultMap;
    }

    public Page<UserManualDownRecordVO> listPage(UserManualDownRecordRequestVO vo) {

        Page<UserManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = buildLqw(vo);

        Page<UserManualUpDownRecordPO> userManualUpDownRecordPOPage = userManualUpDownRecordRepository.selectPage(page, lqw);
        Page<UserManualDownRecordVO> resultPage = new Page<UserManualDownRecordVO>(vo.getPageNumber(), vo.getPageSize());
        BeanUtils.copyProperties(userManualUpDownRecordPOPage, resultPage);
        List<UserManualDownRecordVO> userManualDownRecordVOList = ConvertUtil.entityListToModelList(userManualUpDownRecordPOPage.getRecords(), UserManualDownRecordVO.class);
        //转换数据
        resultPage.setRecords(userManualDownRecordVOList);
        return resultPage;
    }


    public LambdaQueryWrapper<UserManualUpDownRecordPO> buildLqw(UserManualDownRecordRequestVO vo) {
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        lqw.ge(null != vo.getUpdateStartTime(), UserManualUpDownRecordPO::getUpdatedTime, vo.getUpdateStartTime());
        lqw.le(null != vo.getUpdateEndTime(), UserManualUpDownRecordPO::getUpdatedTime, vo.getUpdateEndTime());
        if (vo.getAuditStatus() != null) {
            lqw.eq(UserManualUpDownRecordPO::getAuditStatus, vo.getAuditStatus());
        }
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(UserManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }
        return lqw;
    }

    public BigDecimal getActivityAmountByUserId(String userId) {
        LambdaQueryWrapper<UserManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserManualUpDownRecordPO::getUserId,userId);
        lqw.eq(UserManualUpDownRecordPO::getAdjustType,ManualAdjustTypeEnum.PROMOTIONS.getCode());
        lqw.eq(UserManualUpDownRecordPO::getAuditStatus,ReviewStatusEnum.REVIEW_PASS.getCode());
        lqw.eq(UserManualUpDownRecordPO::getBalanceChangeStatus,CommonConstant.business_one);
        // 执行查询
        List<UserManualUpDownRecordPO> userManualUpDownRecordPOs = baseMapper.selectList(lqw);

        if(CollectionUtil.isEmpty(userManualUpDownRecordPOs)){
            return BigDecimal.ZERO;
        }
        Map<Integer, List<UserManualUpDownRecordPO>> collect = userManualUpDownRecordPOs.stream().collect(Collectors.groupingBy(UserManualUpDownRecordPO::getAdjustWay));
        //加额总计
        // 加额记录
        List<UserManualUpDownRecordPO> userManualUpRecords =
                null == collect.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode()) ? Lists.newArrayList() : collect.get(ManualAdjustWayEnum.MANUAL_UP_QUOTA.getCode());
        // 减额记录
        List<UserManualUpDownRecordPO> userManualDownRecords =
                null == collect.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode()) ? Lists.newArrayList() : collect.get(ManualAdjustWayEnum.MANUAL_DOWN_QUOTA.getCode());

        BigDecimal upAmount = userManualUpRecords.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal downAmount = userManualDownRecords.stream().map(UserManualUpDownRecordPO::getAdjustAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return upAmount.add(downAmount.negate());
    }

    public ResponseVO<UserManualAccountResponseVO> checkUpUserAccountInfo(List<UserManualAccountResultVO> list) {
        GetUserBalanceQueryVO vo  = new GetUserBalanceQueryVO();

        String userAccount = list.stream().map(UserManualAccountResultVO::getUserAccount).collect(Collectors.joining(","));
        vo.setUserAccount(userAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<GetUserBalanceVO> getUserBalanceVO =  getUserBalance(vo);
        for (UserManualAccountResultVO userManualAccountResultVO:list) {
            if(null == userManualAccountResultVO.getAdjustAmount()){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            if(null == userManualAccountResultVO.getRunningWaterMultiple()){
                throw new BaowangDefaultException(ResultCode.ACTIVITY_BET_IS_NULL_DESC);
            }
            BigDecimal adjustAmount = userManualAccountResultVO.getAdjustAmount();
            // 校验调整金额
            try {
                if (adjustAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
                if (ConvertUtil.getDecimalPlace(adjustAmount.toString()) > 2) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
            } catch (Exception e) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }
            checkRunningWaterMultiple(userManualAccountResultVO.getRunningWaterMultiple());

        }
        UserManualAccountResponseVO userManualAccountResponseVO = new UserManualAccountResponseVO();
        userManualAccountResponseVO.setUserManualAccountResultVOList(list);
        userManualAccountResponseVO.setCurrencyCode(getUserBalanceVO.getData().getCurrencyCode());
        return ResponseVO.success(userManualAccountResponseVO);
    }
    public ResponseVO<UserManualDownAccountResponseVO> checkDownUserAccountInfo(List<UserManualDownAccountResultVO> list) {

        GetUserBalanceQueryVO vo  = new GetUserBalanceQueryVO();

        String userAccount = list.stream().map(UserManualDownAccountResultVO::getUserAccount).collect(Collectors.joining(","));
        vo.setUserAccount(userAccount);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        ResponseVO<GetUserBalanceVO> getUserBalanceVO =  getUserBalance(vo);
        for (UserManualDownAccountResultVO userManualDownAccountResultVO:list) {
            BigDecimal adjustAmount = userManualDownAccountResultVO.getAdjustAmount();
            if(null == userManualDownAccountResultVO.getAdjustAmount()){
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_IS_NULL);
            }
            // 校验调整金额
            try {
                if (adjustAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
                if (ConvertUtil.getDecimalPlace(adjustAmount.toString()) > 2) {
                    throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
                }
            } catch (Exception e) {
                throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
            }

        }
        LambdaQueryWrapper<UserCoinPO> lqw = new LambdaQueryWrapper<>();
        String userIds = getUserBalanceVO.getData().getUserIds();
        lqw.in(UserCoinPO::getUserId,Arrays.asList(userIds.split(CommonConstant.COMMA)));
        List<UserCoinPO> userCoinPOS = userCoinRepository.selectList(lqw);
        Map<String, UserCoinPO> userCoinPOMap = userCoinPOS.stream().collect(Collectors.toMap(UserCoinPO::getUserAccount, Function.identity()));
        List<String> balanceUserAccountList = new ArrayList<>();
        for (UserManualDownAccountResultVO UserManualDownAccountResultVO : list) {
            String account = UserManualDownAccountResultVO.getUserAccount();
            UserCoinPO userCoinPO = userCoinPOMap.get(UserManualDownAccountResultVO.getUserAccount());
            if(null == userCoinPO || userCoinPO.getAvailableAmount().compareTo(UserManualDownAccountResultVO.getAdjustAmount()) < 0){
                balanceUserAccountList.add(account);
            }
        }
        if(!balanceUserAccountList.isEmpty()){
            String userAccountStr = String.join(",",balanceUserAccountList);
            return ResponseVO.failAppend(ResultCode.USER_AMOUNT_INSUFFICIENT_BALANCE,userAccountStr);
        }
        UserManualDownAccountResponseVO userManualDownAccountResponseVO = new UserManualDownAccountResponseVO();
        userManualDownAccountResponseVO.setUserManualDownAccountResultVOS(list);
        userManualDownAccountResponseVO.setCurrencyCode(getUserBalanceVO.getData().getCurrencyCode());
        return ResponseVO.success(userManualDownAccountResponseVO);
    }
}
