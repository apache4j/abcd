package com.cloud.baowang.user.service.vipV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.MD5Util;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserInfoReqVO;
import com.cloud.baowang.user.api.vo.user.UserInfoResVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.VIPSendRewardReqVO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.service.SiteVIPGradeService;
import com.cloud.baowang.user.service.SiteVipOptionService;
import com.cloud.baowang.user.service.UserInfoService;
import com.cloud.baowang.user.vo.VipUpGradeAwardVO;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardEnum;
import com.cloud.baowang.wallet.api.enums.wallet.VIPAwardV2Enum;
import com.cloud.baowang.wallet.api.vo.activityV2.UserAwardRecordV2ReqVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.vipV2.UserAwardRecordV2VO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum.RECEIVE;


/**
 * NOTE 功能方法汇总：
 *      1. 计算出会员VIP等级奖励，并发放
 *      2. VIP每月1号和16号定时任务调用实现方法
 * <p>
 *   NOTE tips： 先生成记录，再发放如果发放成再改发放状态，如果是手动领取，跳过。
 */
@Service
@Slf4j
public class VipRewardsService {

    @Autowired
    private VIPAwardRecordV2Api vipAwardRecordV2Api;

    @Autowired
    private SystemDictConfigApi systemDictConfigApi;

    @Autowired
    private SiteVipOptionService siteVipOptionService;

    @Autowired
    private SiteVIPGradeService siteVIPGradeService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SiteApi siteApi;

    @Autowired
    private ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    @Autowired
    private UserTypingAmountApi userTypingAmountApi;


    public void vipUpGradeRewardBatchHandle(List<VipUpGradeAwardVO> vipUpGradeAwardVOList) {
        log.info("该用户升级发放升级奖励入口:{}", vipUpGradeAwardVOList);
        List<UserAwardRecordV2VO> userAwardRecordVOList = new ArrayList<>();
        vipUpGradeAwardVOList.forEach(vipUpGradeAwardVO -> {
            UserAwardRecordV2VO userAwardRecordVO = vipRewardHandle(vipUpGradeAwardVO.getSiteVipOptionVO(), vipUpGradeAwardVO.getUserInfoVO(), VIPAwardV2Enum.UPGRADE_BONUS);
            if (userAwardRecordVO != null) {
                userAwardRecordVOList.add(userAwardRecordVO);
            }
        });
        saveVipAward(userAwardRecordVOList);
    }

    public UserAwardRecordV2VO vipRewardHandle(SiteVipOptionVO siteVipOptionVO, UserInfoVO userInfoVO, VIPAwardV2Enum typeEnum) {

        try {
            String siteCode = userInfoVO.getSiteCode();

            String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();

            //订单ID
            StringBuilder orderId = new StringBuilder(1);

            //NOTE 1. 计算当前vip等级对应的奖励，计算逻辑，对应币种
            BigDecimal promotionBonus = BigDecimal.ZERO;
            BigDecimal requireTypingAmount = BigDecimal.ZERO;

            // 获取该时区下的当前时间
            TimeZone tz = TimeZone.getTimeZone(timeZone);
            Calendar calendar = Calendar.getInstance(tz);
            Date siteTimeNow = calendar.getTime();
            DateTime thisWeekStart = DateUtil.beginOfWeek(siteTimeNow);
            String timeStr = "";
            Integer EXPIR_CODE = 0;
            switch (typeEnum) {
                case UPGRADE_BONUS -> {
                    EXPIR_CODE = DictCodeConfigEnums.VIP_GRATEUPBONUE_EXPIRATION_TIME.getCode();
                    promotionBonus = siteVipOptionVO.getPromotionBonus();
                    //NOTE 2. 判断vip之前是否被调过级，根据 user_lable_id
                    if (StrUtil.isNotEmpty(userInfoVO.getUserLabelId()) && userInfoVO.getUserLabelId().contains("100007")) {
                        log.error("{} 跳过级， 不用处理, 标签id {}", userInfoVO.getUserAccount(), userInfoVO.getUserLabelId());
                        return null;
                    }
                    requireTypingAmount = promotionBonus.multiply(siteVipOptionVO.getPromotionBonusMultiple());
                    Long registerTime = userInfoVO.getRegisterTime();
                    DateTime dateTimeMs = DateUtil.date(registerTime);
                    timeStr = DateUtil.format(dateTimeMs , DatePattern.PURE_DATE_PATTERN);
                    if (siteVipOptionVO.getVipGradeCode()<10){
                        timeStr += ("2" + siteVipOptionVO.getVipGradeCode());
                    }else {
                        timeStr +=  siteVipOptionVO.getVipGradeCode().toString();
                    }

                    orderId.append(typeEnum.getCode()).append(new StringBuilder(userInfoVO.getUserId()).reverse()).append(timeStr).append(md5Str(timeStr));

                }
                case WEEK_BONUS -> {

                    EXPIR_CODE = DictCodeConfigEnums.VIP_WEEKBONUE_EXPIRATION_TIME.getCode();
                    promotionBonus = siteVipOptionVO.getWeekBonus();

                    //NOTE 3. 查询上一次领取的周奖励流水, 上期奖励的打码量目标，默认为0, 如果没有上期，不用处理
                    UserAwardRecordV2ReqVO awardRecordReqVO = new UserAwardRecordV2ReqVO();
                    awardRecordReqVO.setSiteCode(siteCode);
                    awardRecordReqVO.setAwardType(CommonConstant.business_one_str);
                    awardRecordReqVO.setReceiveStatus(RECEIVE.getCode());
                    awardRecordReqVO.setUserId(userInfoVO.getUserId());

                    BigDecimal lastRequireTypingAmount = BigDecimal.ZERO;
                    ResponseVO<UserAwardRecordV2VO> awardRecordResponseVO = vipAwardRecordV2Api.awardRecordByUserId(awardRecordReqVO);
                    UserAwardRecordV2VO awardRecordV2VO = awardRecordResponseVO.getData();
                    if (awardRecordV2VO != null && awardRecordV2VO.getRequireTypingAmount() != null) {
                        lastRequireTypingAmount = awardRecordResponseVO.getData().getRequireTypingAmount();
                        //NOTE 4. 上期到本期期间，奖励流水有没有打， 有没有发生打码量清0
                        long previousStartTime = awardRecordV2VO.getRecordStartTime();
                        UserTypingRecordRequestVO typingRecordRequestVO = new UserTypingRecordRequestVO();
                        typingRecordRequestVO.setSiteCode(siteCode);
                        typingRecordRequestVO.setUserAccount(userInfoVO.getUserAccount());
                        typingRecordRequestVO.setRecordStartTime(previousStartTime);
                        typingRecordRequestVO.setRecordEndTime(thisWeekStart.getTime());
                        ResponseVO<Long> pageResponseVO = userTypingAmountApi.userTypingRecordPageCount(typingRecordRequestVO);
                        //NOTE 5. 没有清0， 查询上周的流水， 如果达到就发奖励。
                        if (pageResponseVO.getData() == 0) {

                            ReportUserWinLossReqVO userWinLossReqVO = ReportUserWinLossReqVO.builder().siteCode(siteCode)
                                    .dayMillisStartTime(previousStartTime)
                                    .dayMillisEndTime(System.currentTimeMillis())
                                    .userId(userInfoVO.getUserId())
                                    .build();
                            ReportUserVenueBetsTopVO previousReportVO = reportUserVenueFixedWinLoseApi.queryUserWinLossInfo(userWinLossReqVO);
                            //NOTE 上期到当前周开始时间， 如果打码量不够上期的地奖励的金额，就不会再发奖励
                            if (previousReportVO.getValidAmount().compareTo(lastRequireTypingAmount) < 0) {
                                log.error("账号：{} 的上期打码量目录未达到， 不用派发, 派发类型：{}", userInfoVO.getUserAccount(), typeEnum);
                                return null;
                            }
                        }

                    }

                    //NOTE 6. 周红包类型：0-先发后打, 1-先打后发
                    if (Objects.equals(siteVipOptionVO.getWeekBonusType(), CommonConstant.business_one)) {
                        //NOTE 流水目标
                        BigDecimal weekBonusAmountTotal = siteVipOptionVO.getWeekBonusAmountTotal();

                        //NOTE 查询上周的流水， 如果达到就发奖励。
                        DateTime lastWeekStart = DateUtil.offsetWeek(thisWeekStart, -1);
                        ReportUserWinLossReqVO userWinLossReqVO = ReportUserWinLossReqVO.builder().siteCode(siteCode)
                                .dayMillisStartTime(lastWeekStart.getTime())
                                .dayMillisEndTime(thisWeekStart.getTime())
                                .userId(userInfoVO.getUserId())
                                .build();
                        ReportUserVenueBetsTopVO reportUserVenueBetsTopVO = reportUserVenueFixedWinLoseApi.queryUserWinLossInfo(userWinLossReqVO);
                        if (reportUserVenueBetsTopVO == null || reportUserVenueBetsTopVO.getValidAmount() == null || reportUserVenueBetsTopVO.getValidAmount().compareTo(weekBonusAmountTotal) < 0) {
                            log.error("账号：{} 的打码量目录未达到， 不用派发, 派发类型：{}", userInfoVO.getUserAccount(), typeEnum);
                            return null;
                        }
                        requireTypingAmount = promotionBonus.multiply(BigDecimal.ONE);

                    } else {
                        requireTypingAmount = promotionBonus.multiply(siteVipOptionVO.getWeekBonusAmountMultiple());
                    }
                    //NOTE 获取当时时间格式 20250102
                    timeStr = DateUtil.format(thisWeekStart, DatePattern.PURE_DATE_PATTERN);

                    orderId.append(typeEnum.getCode()).append(new StringBuilder(userInfoVO.getUserId()).reverse()).append(timeStr).append(md5Str(timeStr));

                }
                case BIRTH_BONUS -> {

                    EXPIR_CODE = DictCodeConfigEnums.VIP_BIRTHBONUE_EXPIRATION_TIME.getCode();

                    promotionBonus = siteVipOptionVO.getAgeAmount();
                    //NOTE 7. 判断今天是不是这个会员生日

                    String siteTimeNowFormat = DateUtil.format(siteTimeNow, DatePattern.NORM_DATE_PATTERN).substring(5);
                    if (StrUtil.isEmpty(userInfoVO.getBirthday())|| !siteTimeNowFormat.equals(userInfoVO.getBirthday().substring(5))
                        || (System.currentTimeMillis() - userInfoVO.getRegisterTime()) <= (90 * 24 * 3600 * 1000L)
                    ) {
                        log.error("账号：{} 当天不是生日或注册不满三个月，无能领取生日礼金, 派发类型：{}", userInfoVO.getUserAccount(), typeEnum);
                        return null;
                    }
                    requireTypingAmount = promotionBonus.multiply(siteVipOptionVO.getAgeAmountMultiple());

                    timeStr = DateUtil.format(siteTimeNow, DatePattern.PURE_DATE_PATTERN);

                    orderId.append(typeEnum.getCode()).append(new StringBuilder(userInfoVO.getUserId()).reverse()).append(timeStr).append(md5Str(timeStr));

                }
            }

            //NOTE orderId生成逻辑 1. （1 + 类型） , 2. 会员Id 倒序, 3. 最后一位是 timeStr md5取 6位
            //orderId.append(typeEnum.getCode()).append(new StringBuilder(userInfoVO.getUserId()).reverse()).append(md5Str(timeStr));

            //NOTE 8. 判断会员是否已经领取， 判断逻辑
            UserAwardRecordV2ReqVO receiveVO = new UserAwardRecordV2ReqVO();
            receiveVO.setSiteCode(siteCode);
            receiveVO.setUserId(userInfoVO.getUserId());
            receiveVO.setAwardType(typeEnum.getCode());
            receiveVO.setOrderId(orderId.toString());

            ResponseVO<Boolean> hasReceiveresponseVO = vipAwardRecordV2Api.awardHasReceive(receiveVO);
            if (BooleanUtil.isTrue(hasReceiveresponseVO.getData())) {
                log.error("账号：{} 的奖励已处理， 不用再派发, 奖励类型：{}", userInfoVO.getUserAccount(), typeEnum);
                return null;
            }
            //NOTE 9. 生成发放记录,获取过期策略配置, 站点读配置，如果没有拿总控的值。

            //TODO 每个VIP奖励是配置不同的配置

            ResponseVO<List<SystemDictConfigRespVO>> dicResponseVO = systemDictConfigApi
                    .getListByCode(EXPIR_CODE);
            List<SystemDictConfigRespVO> dataList = dicResponseVO.getData();
            if (CollUtil.isEmpty(dataList)) {
                log.error("获取VIP奖励过期配置配置数据为空或者异常, CODE:{}", EXPIR_CODE);
                return null;
            }
            List<SystemDictConfigRespVO> dictList = dataList.stream().filter(obj ->
                    siteCode.equals(obj.getSiteCode()) || CommonConstant.business_zero.toString().equals(obj.getSiteCode())).toList();


            Map<String, String> dictMap = dictList.stream().collect(Collectors
                    .toMap(SystemDictConfigRespVO::getSiteCode, SystemDictConfigRespVO::getConfigParam));
            // 过期时间
            long expireMillSecond = dictMap.containsKey(siteCode) ? Long.parseLong(dictMap.get(siteCode))
                    * 3600 * 1000 : Long.parseLong(dictMap.get(CommonConstant.business_zero_str)) * 3600 * 1000;


            return UserAwardRecordV2VO.builder().awardType(VIPAwardEnum.UPGRADE_BONUS.getCode())
                    .awardAmount(promotionBonus)
                    .awardType(typeEnum.getCode())
                    .vipGradeCode(siteVipOptionVO.getVipGradeCode())
                    .userAccount(userInfoVO.getUserAccount())
                    .userId(userInfoVO.getUserId())
                    .receiveType(BigDecimal.ZERO.intValue())
                    .vipRankCode(siteVipOptionVO.getVipGradeCode())
                    .expiredTime(expireMillSecond)
                    .recordStartTime(System.currentTimeMillis())
                    .recordEndTime(System.currentTimeMillis())
                    .agentId(userInfoVO.getSuperAgentId())
                    .siteCode(siteCode)
                    .accountType(userInfoVO.getAccountType())
                    .agentAccount(userInfoVO.getSuperAgentAccount())
                    .currency(userInfoVO.getMainCurrency())
                    .requireTypingAmount(requireTypingAmount)
                    .orderId(orderId.toString())
                    .build();

        } catch (Exception e) {
            log.error("vipRewardHandle 处理异常， userInfoVO {}, 奖励类型{}", userInfoVO, typeEnum, e);
        }
        return null;
    }

    public ResponseVO<Boolean> autoRewardHandle(VIPAwardV2Enum typeEnum, VIPSendRewardReqVO vipSendRewardReqVO) {

        ResponseVO<List<SiteVO>> listResponseVO = siteApi.siteInfoAllstauts();

        List<SiteVO> siteVOList = listResponseVO.getData();

        if (vipSendRewardReqVO!=null && StrUtil.isNotEmpty(vipSendRewardReqVO.getSiteCode())){
            siteVOList = siteVOList.stream().filter(siteVO ->  siteVO.getSiteCode().equals(vipSendRewardReqVO.getSiteCode())).toList();
        }

        List<UserAwardRecordV2VO> userAwardRecordVOList = new ArrayList<>();

        siteVOList.stream().filter(siteVO -> SiteHandicapModeEnum.China.getCode().equals(siteVO.getHandicapMode())).forEach(siteVO -> {

            String siteCode = siteVO.getSiteCode();

            // 获取该时区下的当前时间
            TimeZone tz = TimeZone.getTimeZone(siteVO.getTimezone());
            Calendar calendar = Calendar.getInstance(tz);
            Date siteTimeNow = calendar.getTime();
            if (!vipSendRewardReqVO.getManualFlag()){
                if (calendar.get(Calendar.HOUR_OF_DAY)>0){
                    return;
                }
                if (typeEnum==VIPAwardV2Enum.WEEK_BONUS){
                    if (calendar.get(Calendar.DAY_OF_WEEK)>1){
                        return;
                    }
                }
            }
            UserInfoReqVO userReqVO = new UserInfoReqVO();
            userReqVO.setSiteCode(siteCode);
            userReqVO.setLimitCount(2000L);

            if (VIPAwardV2Enum.BIRTH_BONUS == typeEnum) {
                userReqVO.setBirthdayLeft(DateUtil.format(siteTimeNow, DatePattern.NORM_DATE_PATTERN).substring(5));
            }

            Map<String, SiteVipOptionVO> vipOptionMap = new HashMap<>();

            int pageNo = 1;

            do {
                List<UserInfoVO> userInfoList = userInfoService.getUserInfoListByMinId(userReqVO);

                if (CollUtil.isEmpty(userInfoList)){
                    break;
                }
                UserInfoVO lastVO = userInfoList.get(userInfoList.size() - 1);
                userReqVO.setMinId(lastVO.getId());
                log.error("VIP奖励会员查询第 {} 页, 类型{}", pageNo, typeEnum );
                for (UserInfoVO userInfoVO : userInfoList) {

                    //NOTE 币种和VIP等级查会员的VIP配置
                    SiteVipOptionVO vipOptionVO = vipOptionMap.get(userInfoVO.getVipGradeCode() + "_" + userInfoVO.getMainCurrency());
                    if (null == vipOptionVO) {
                        List<SiteVipOptionVO> list = siteVipOptionService.getList(siteCode, userInfoVO.getMainCurrency());
                        vipOptionMap.putAll(list.stream().collect(Collectors.toMap(vo -> vo.getVipGradeCode() + "_" + vo.getCurrencyCode(), vo -> vo)));
                        vipOptionVO = vipOptionMap.get(userInfoVO.getVipGradeCode() + "_" + userInfoVO.getMainCurrency());
                    }
                    if (vipOptionVO == null) {
                        log.error("VIP周礼金任务，获取玩家VIP配置失败，配置数据为空或者异常，用户等级:{}, 用户币种： {}", userInfoVO.getVipGradeCode(), userInfoVO.getMainCurrency());
                        continue;
                    }
                    if (VIPAwardV2Enum.BIRTH_BONUS == typeEnum) {
                        if (vipOptionVO.getAgeAmount().compareTo(BigDecimal.ZERO)<=0){
                            log.error("VIP生日礼金任务，配置数据为空，用户等级:{}, 用户币种： {}", userInfoVO.getVipGradeCode(), userInfoVO.getMainCurrency());
                            continue;
                        }
                    }
                    UserAwardRecordV2VO userAwardRecordVO = vipRewardHandle(vipOptionVO, userInfoVO, typeEnum);
                    if (userAwardRecordVO != null) {
                        userAwardRecordVOList.add(userAwardRecordVO);
                    }
                }
                saveVipAward(userAwardRecordVOList);
                pageNo++;
            }while (pageNo<10000);
        });

        return ResponseVO.success(true);
    }


    public ResponseVO<Boolean> autoWeekRedBagRewardHandle(VIPSendRewardReqVO vo) {
        return autoRewardHandle(VIPAwardV2Enum.WEEK_BONUS,vo);
    }

    public ResponseVO<Boolean> autoBirthRedBagRewardHandle(VIPSendRewardReqVO vo) {
        return autoRewardHandle(VIPAwardV2Enum.BIRTH_BONUS,vo);
    }

    //NOTE 事务应该是同一个
    public void saveVipAward(List<UserAwardRecordV2VO> userAwardRecordVOList) {
        if (CollUtil.isNotEmpty(userAwardRecordVOList)) {
            vipAwardRecordV2Api.recordVIPAward(userAwardRecordVOList);
        }
    }


    public String md5Str(String arg) {
        if (StrUtil.isEmpty(arg) || arg.length()<3){
            return arg;
        }
        String md5Str = MD5Util.md5(arg);
        md5Str = md5Str.replaceAll("[a-zA-Z]", "");
        return md5Str.substring(2,6);
    }
}
