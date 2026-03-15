package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.user.api.enums.WelfareCenterRewardType;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.WelfareCenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WelfareCenterService {
    private final SystemParamApi paramApi;
    private final UserInfoRepository userInfoRepository;
    private final WelfareCenterRepository welfareCenterRepository;

    public ResponseVO<WelfareCenterRewardResultVO> pageQuery(WelfareCenterRewardPageQueryVO queryVO) {
        //福利中心默认查90天以内的数据
        long queryTime = DateUtils.getStartOfDayBefore(System.currentTimeMillis(),CurrReqUtils.getTimezone(),90) ;
        queryVO.setSiteStartTime(queryTime);
        queryVO.setSystemTime(System.currentTimeMillis());
        /*if (queryVO.getPfTimeStartTime() == null && queryVO.getPfTimeEndTime() == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }*/
        if (queryVO.getWelfareCenterRewardType() != null && queryVO.getWelfareCenterRewardType().equals(-1)) {
            queryVO.setWelfareCenterRewardType(null);
        }
        if (queryVO.getReceiveStatus() != null && queryVO.getReceiveStatus().equals(-1)) {
            queryVO.setReceiveStatus(null);
        }
        WelfareCenterRewardResultVO result = new WelfareCenterRewardResultVO();
        LambdaQueryWrapper<UserInfoPO> userQuery = Wrappers.lambdaQuery();
        userQuery.eq(UserInfoPO::getSiteCode, queryVO.getSiteCode()).eq(UserInfoPO::getUserAccount, queryVO.getUserAccount());
        UserInfoPO userInfoPO = userInfoRepository.selectOne(userQuery);
        queryVO.setUserId(userInfoPO.getUserId());
        if (userInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //获取总数
        List<WelfareCenterRewardRespVO> totalVo = welfareCenterRepository.getList(queryVO);
        //获取分页
        Page<WelfareCenterRewardRespVO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        //获取当前页
        page = welfareCenterRepository.pageQuery(page, queryVO);

        List<WelfareCenterRewardRespVO> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {

            List<String> param = new ArrayList<>();
            //任务
            param.add(CommonConstant.SUB_TASK_TYPE);
            //活动模板
            param.add(CommonConstant.ACTIVITY_TEMPLATE);
            //vip奖励
            param.add(CommonConstant.VIP_AWARD_TYPE);

            ResponseVO<Map<String, List<CodeValueVO>>> resp = paramApi.getSystemParamsByList(param);
            Map<String, List<CodeValueVO>> map = new HashMap<>();
            if (resp.isOk()) {
                map = resp.getData();
            }
            Map<String, List<CodeValueVO>> finalMap = map;
            records.forEach(item -> {
                //转换下具体类型
                if (WelfareCenterRewardType.TASK_NOVICE_REWARD.getCode().equals(item.getWelfareCenterRewardType()) ||
                        WelfareCenterRewardType.TASK_DAILY_REWARD.getCode().equals(item.getWelfareCenterRewardType()) ||
                        WelfareCenterRewardType.TASK_WEEK_REWARD.getCode().equals(item.getWelfareCenterRewardType())) {
                    //新人,每日,每周任务类型转多语言
                    //任务类型
                    Optional<CodeValueVO> taskOpt = finalMap.get(CommonConstant.SUB_TASK_TYPE)
                            .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
                    taskOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
                }
                //活动优惠
                if (WelfareCenterRewardType.EVENT_DISCOUNT.getCode().equals(item.getWelfareCenterRewardType())) {
                    //活动类型
                    Optional<CodeValueVO> activityTemOpt = finalMap.get(CommonConstant.ACTIVITY_TEMPLATE)
                            .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
                    activityTemOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
                }
                //vip奖励
                if (WelfareCenterRewardType.VIP_BENEFIT.getCode().equals(item.getWelfareCenterRewardType())) {
                    //vip
                    Optional<CodeValueVO> vipOpt = finalMap.get(CommonConstant.VIP_AWARD_TYPE)
                            .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
                    vipOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
                }
                if (ActivityReceiveStatusEnum.UN_RECEIVE.getCode().equals(item.getReceiveStatus())) {
                    //待领取数据,计算时间差
                    if (item.getPfEndTime() == null) {
                        //长期福利
                        item.setIsPermanentValidity(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                    } else {
                        //活动过期时间
                        long pfEndTime = item.getPfEndTime();
                        //当前时间
                        long nowTime = System.currentTimeMillis();
                        //时间还有多久过期
                        long expiryTimeRemaining = 0L;
                        if (pfEndTime - nowTime >= 0) {
                            expiryTimeRemaining = pfEndTime - nowTime;
                        }
                        item.setExpiryTimeRemaining(expiryTimeRemaining);
                        item.setIsPermanentValidity(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                    }
                }
            });

        }


        //设置分页数据
        result.setPages(page);
        //平台币
        result.setPlatCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        //主货币
        result.setMainCurrency(userInfoPO.getMainCurrency());


        if (CollectionUtil.isNotEmpty(totalVo)) {
            long alReceiveTotal = 0L;
            for (WelfareCenterRewardRespVO item : totalVo) {
                if (ActivityReceiveStatusEnum.UN_RECEIVE.getCode().equals(item.getReceiveStatus())) {
                    //待领取数据,计算时间差
                    if (item.getPfEndTime() == null) {
                        //长期福利
                        item.setIsPermanentValidity(Integer.parseInt(YesOrNoEnum.YES.getCode()));
                    } else {
                        //活动过期时间
                        long pfEndTime = item.getPfEndTime();
                        //当前时间
                        long nowTime = System.currentTimeMillis();
                        //时间还有多久过期
                        long expiryTimeRemaining = 0L;
                        if (pfEndTime - nowTime >= 0) {
                            expiryTimeRemaining = pfEndTime - nowTime;
                        } else {
                            //存在可能定时任务还没有更新待领取状态,当前数据过期时间已经过了,这里查询手动设置一下领取状态
                            item.setReceiveStatus(ActivityReceiveStatusEnum.EXPIRED.getCode());
                        }
                        item.setExpiryTimeRemaining(expiryTimeRemaining);
                        item.setIsPermanentValidity(Integer.parseInt(YesOrNoEnum.NO.getCode()));
                    }
                } else if (ActivityReceiveStatusEnum.RECEIVE.getCode().equals(item.getReceiveStatus())) {
                    alReceiveTotal++;
                }
            }
            //已领取总数
            result.setAlReceiveTotal(alReceiveTotal);
            result.setTotalSize((long) totalVo.size());
            long waitReceiveTotal = totalVo.stream()
                    .filter(item -> ActivityReceiveStatusEnum.UN_RECEIVE.getCode().equals(item.getReceiveStatus()))
                    .count(); // 统计未领取的数量
            result.setWaitReceiveTotal(waitReceiveTotal);
            //组装各种币种对应的金额数据
            Map<String, BigDecimal> currencyAmountMap = totalVo.stream()
                    .collect(Collectors.groupingBy(
                            WelfareCenterRewardRespVO::getCurrencyCode, // 按 currencyCode 分组
                            Collectors.reducing(BigDecimal.ZERO,
                                    WelfareCenterRewardRespVO::getAmount,
                                    BigDecimal::add) // 汇总金额，使用 BigDecimal 的加法
                    ));
            //组装主货币金额
            if (currencyAmountMap.containsKey(userInfoPO.getMainCurrency())) {
                result.setMainCurrencyTotal(currencyAmountMap.get(userInfoPO.getMainCurrency()));
            }
            //组装平台币
            if (currencyAmountMap.containsKey(CommonConstant.PLAT_CURRENCY_CODE)) {
                result.setPlatCurrencyTotal(currencyAmountMap.get(CommonConstant.PLAT_CURRENCY_CODE));
            }
        }
        return ResponseVO.success(result);
    }

    /**
     * 获取详情
     *
     * @param queryVO
     * @return
     */
    public ResponseVO<WelfareCenterRewardRespVO> detail(WelfareCenterRewardPageQueryVO queryVO) {
        List<String> param = new ArrayList<>();
        //任务
        param.add(CommonConstant.SUB_TASK_TYPE);
        //活动模板
        param.add(CommonConstant.ACTIVITY_TEMPLATE);
        //vip奖励
        param.add(CommonConstant.VIP_AWARD_TYPE);

        ResponseVO<Map<String, List<CodeValueVO>>> resp = paramApi.getSystemParamsByList(param);
        Map<String, List<CodeValueVO>> map = new HashMap<>();
        if (resp.isOk()) {
            map = resp.getData();
        }

        Map<String, List<CodeValueVO>> finalMap = map;
        queryVO.setUserId(CurrReqUtils.getOneId());
        queryVO.setSystemTime(System.currentTimeMillis());

        WelfareCenterRewardRespVO item = welfareCenterRepository.detail(queryVO);
        //转换下具体类型
        if (WelfareCenterRewardType.TASK_NOVICE_REWARD.getCode().equals(item.getWelfareCenterRewardType()) ||
                WelfareCenterRewardType.TASK_DAILY_REWARD.getCode().equals(item.getWelfareCenterRewardType()) ||
                WelfareCenterRewardType.TASK_WEEK_REWARD.getCode().equals(item.getWelfareCenterRewardType())) {
            //新人,每日,每周任务类型转多语言
            //任务类型
            Optional<CodeValueVO> taskOpt = finalMap.get(CommonConstant.SUB_TASK_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
            taskOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
        }
        //活动优惠
        if (WelfareCenterRewardType.EVENT_DISCOUNT.getCode().equals(item.getWelfareCenterRewardType())) {
            //活动类型
            Optional<CodeValueVO> activityTemOpt = finalMap.get(CommonConstant.ACTIVITY_TEMPLATE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
            activityTemOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
        }
        //vip奖励
        if (WelfareCenterRewardType.VIP_BENEFIT.getCode().equals(item.getWelfareCenterRewardType())) {
            //vip
            Optional<CodeValueVO> vipOpt = finalMap.get(CommonConstant.VIP_AWARD_TYPE)
                    .stream().filter(obj -> obj.getCode().equals(String.valueOf(item.getDetailType()))).findFirst();
            vipOpt.ifPresent(opt -> item.setDetailType(opt.getValue()));
        }

        return ResponseVO.success(item);
    }

    public Integer getWaitReceiveByUserId(String userId) {
        LambdaQueryWrapper<UserInfoPO> query = Wrappers.lambdaQuery();
        query.eq(UserInfoPO::getUserId, userId);
        UserInfoPO userInfoPO = userInfoRepository.selectOne(query);
        if (userInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        WelfareCenterRewardPageQueryVO queryVO = new WelfareCenterRewardPageQueryVO();
        long queryTime = DateUtils.getStartOfDayBefore(System.currentTimeMillis(),CurrReqUtils.getTimezone(),90) ;
        queryVO.setSiteStartTime(queryTime);
        queryVO.setSystemTime(System.currentTimeMillis());
        queryVO.setUserAccount(userInfoPO.getUserAccount());
        queryVO.setSiteCode(userInfoPO.getSiteCode());
        queryVO.setSystemTime(System.currentTimeMillis());
        queryVO.setUserId(userId);
        int result = 0;
        List<WelfareCenterRewardRespVO> totalVo = welfareCenterRepository.getList(queryVO);
        if (CollectionUtil.isNotEmpty(totalVo)) {
            long nowTime = System.currentTimeMillis();
            for (WelfareCenterRewardRespVO item : totalVo) {
                Long pfEndTime = item.getPfEndTime();
                //待领取,并且是没有过期的福利
                if (ActivityReceiveStatusEnum.UN_RECEIVE.getCode().equals(item.getReceiveStatus())
                        && pfEndTime != null
                        && pfEndTime - nowTime >= 0) {
                    result++;
                }
            }
        }
        return result;
    }
}
