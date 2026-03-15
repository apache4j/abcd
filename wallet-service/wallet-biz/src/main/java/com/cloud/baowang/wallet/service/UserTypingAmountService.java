package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.user.api.vo.UserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserBasicRequestVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserWithdrawRunningWaterVO;
import com.cloud.baowang.play.api.enums.ClassifyEnum;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountPO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.po.UserTypingAmountPO;
import com.cloud.baowang.wallet.repositories.UserActivityTypingAmountRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import com.cloud.baowang.wallet.repositories.UserTypingAmountRepository;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingAmountVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserTypingAmountService extends ServiceImpl<UserTypingAmountRepository, UserTypingAmountPO> {

    private final UserTypingAmountAddService userTypingAmountAddService;

    private final UserCoinRepository userCoinRepository;

    private final UserTypingAmountRepository userTypingAmountRepository;

    private final OrderRecordApi orderRecordApi;

    private final UserInfoApi userInfoApi;

    private final UserActivityTypingAmountRecordService activityTypingAmountService;

    private final UserActivityTypingAmountService userActivityTypingAmountService;

    private final SystemDictConfigApi systemDictConfigApi;

    private final UserCoinRecordRepository userCoinRecordRepository;

    private final SiteApi siteApi;

    private final UserPlatformCoinRepository userPlatformCoinRepository;

    private final UserActivityTypingAmountRepository userActivityTypingAmountRepository;

    private final SiteCurrencyInfoService siteCurrencyInfoService;




    @DistributedLock(name = RedisConstants.ADD_TYPING_AMOUNT_LOCK_KEY, unique = "#userId", waitTime = 60, leaseTime = 180)
    public boolean addUserTypingAmount(UserTypingAmountRequestVO userTypingAmountRequestVO, UserInfoVO userInfoVO) {

        for (UserTypingAmountRequestVO vo:userTypingAmountRequestVO.getTypingList()) {
            if(TypingAmountEnum.SUBTRACT.getCode().equals(vo.getType())){
                vo.setTypingAmount(vo.getTypingAmount().negate());
                if(null != vo.getActivityTypingAmount()){
                    vo.setActivityTypingAmount(vo.getActivityTypingAmount().negate());
                }
            }
        }

        boolean result = true;
        if(null == userTypingAmountRequestVO.getOnlyActivity() || !userTypingAmountRequestVO.getOnlyActivity()){
            result =  userTypingAmountAddService.userTypingAmountAdd(userTypingAmountRequestVO,userInfoVO);
        }
        //同步存款活动
        result = activityTypingAmountService.insertActivityTypingRecord(userTypingAmountRequestVO,userInfoVO);
        if (!result){
            return false;
        }

        return result;
    }

    public UserTypingAmountVO getUserTypingAmountByAccount(String siteCode,String userAccount) {
        LambdaQueryWrapper<UserTypingAmountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTypingAmountPO::getUserAccount, userAccount);
        queryWrapper.eq(UserTypingAmountPO::getSiteCode,siteCode);
        UserTypingAmountPO typingAmountPO = this.baseMapper.selectOne(queryWrapper);
        if (typingAmountPO != null) {
            UserTypingAmountVO vo = new UserTypingAmountVO();
            BeanUtils.copyProperties(typingAmountPO, vo);
            return vo;
        }

        return null;
    }

    public UserWithdrawRunningWaterVO getWithdrawRunningWater(WalletUserBasicRequestVO requestVO) {
        UserWithdrawRunningWaterVO userWithdrawRunningWaterVO = new UserWithdrawRunningWaterVO();
        UserInfoVO userInfoVO = userInfoApi.getUserInfoByAccountAndSiteCode(requestVO.getUserAccount(),requestVO.getSiteCode());
        /*LambdaQueryWrapper<UserCoinPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(requestVO.getUserAccount()),UserCoinPO::getUserAccount,requestVO.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(requestVO.getSiteCode()),UserCoinPO::getSiteCode,requestVO.getSiteCode());
        UserCoinPO userCoinPO =  userCoinRepository.selectOne(lqw);
        userWithdrawRunningWaterVO.setUserBalance(userCoinPO == null || userCoinPO.getAvailableAmount() ==null?BigDecimal.ZERO:userCoinPO.getAvailableAmount());*/
        LambdaQueryWrapper<UserTypingAmountPO> typingLqw =  new LambdaQueryWrapper<>();
        typingLqw.eq(StringUtils.isNotBlank(requestVO.getUserAccount()),UserTypingAmountPO::getUserAccount,requestVO.getUserAccount());
        typingLqw.eq(StringUtils.isNotBlank(requestVO.getSiteCode()),UserTypingAmountPO::getSiteCode,requestVO.getSiteCode());
        UserTypingAmountPO userTypingAmountPO =  userTypingAmountRepository.selectOne(typingLqw);

        Long startTime = 0L,endTime = System.currentTimeMillis();

        userWithdrawRunningWaterVO.setCurrency(userInfoVO.getMainCurrency());
        if(null != userTypingAmountPO){
            userWithdrawRunningWaterVO.setRemainingRunningWater(userTypingAmountPO.getTypingAmount());
            userWithdrawRunningWaterVO.setRunningWaterStartTime(userTypingAmountPO.getStartTime());
            userWithdrawRunningWaterVO.setNeedRunningWater(userTypingAmountPO.getTypingAmount());
            startTime = userTypingAmountPO.getStartTime();
            if(BigDecimal.ZERO.compareTo(userTypingAmountPO.getTypingAmount()) >= 0){
                userWithdrawRunningWaterVO.setRunningWaterStartTime(null);
                userWithdrawRunningWaterVO.setNeedRunningWater(BigDecimal.ZERO);
                userWithdrawRunningWaterVO.setRemainingRunningWater(BigDecimal.ZERO);
                userWithdrawRunningWaterVO.setCompletedRunningWater(BigDecimal.ZERO);
            }else{
                BigDecimal runningWaterVO   = orderRecordApi.getTotalAmountByUserId(userTypingAmountPO.getUserId(),startTime,endTime);
//                if(null != runningWaterVO && null != runningWaterVO.getCompletedRunningWater() && null != userTypingAmountPO ){
                if(null != runningWaterVO){
                    userWithdrawRunningWaterVO.setNeedRunningWater(userTypingAmountPO.getTypingAmount().add(runningWaterVO));
                    userWithdrawRunningWaterVO.setCompletedRunningWater(runningWaterVO);
                }else{
                    userWithdrawRunningWaterVO.setCompletedRunningWater(BigDecimal.ZERO);
                }
            }

        }else{
            userWithdrawRunningWaterVO.setRemainingRunningWater(BigDecimal.ZERO);
            userWithdrawRunningWaterVO.setNeedRunningWater(BigDecimal.ZERO);
            userWithdrawRunningWaterVO.setCompletedRunningWater(BigDecimal.ZERO);
            userWithdrawRunningWaterVO.setRunningWaterStartTime(null);
        }
        BigDecimal activityRunningWater = userActivityTypingAmountService.getUserActivityTypingAmount(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userWithdrawRunningWaterVO.setActivityRunningWater(activityRunningWater);


        return userWithdrawRunningWaterVO;
    }

    public List<UserTypingAmountVO> getUserTypingAmountListByAccounts(List<String> userAccount) {
        LambdaQueryWrapper<UserTypingAmountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserTypingAmountPO::getUserAccount, userAccount);
        List<UserTypingAmountPO> typingAmountPO = this.baseMapper.selectList(queryWrapper);
        try {
           return ConvertUtil.convertListToList(typingAmountPO, new UserTypingAmountVO());
        } catch (Exception e) {
            log.info("typingAmountPO 转换错误");
        }

        return null;
    }

    public UserTypingAmountVO getUserTypingAmount(String userAccount, String siteCode) {
        LambdaQueryWrapper<UserTypingAmountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTypingAmountPO::getUserAccount, userAccount);
        queryWrapper.eq(UserTypingAmountPO::getSiteCode,siteCode);
        UserTypingAmountPO typingAmountPO = this.baseMapper.selectOne(queryWrapper);
        if (typingAmountPO != null) {
            UserTypingAmountVO vo = new UserTypingAmountVO();
            BeanUtils.copyProperties(typingAmountPO, vo);
            return vo;
        }
        return  null;
    }


    public void userTypingAmountCleanZeroByUserId(String userId) {
        try {
            log.info("清除打码量校验开始,会员id{}",userId);
            UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
            //获取系统配置参数
            SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.SYSTEM_CLEANUP_BET_AMOUNT.getCode(),userInfoVO.getSiteCode()).getData();
            BigDecimal configParam = new BigDecimal(systemDictConfigRespVO.getConfigParam());
            //获取站点汇率
            BigDecimal finalRate = siteCurrencyInfoService.getCurrencyFinalRate(userInfoVO.getSiteCode(),userInfoVO.getMainCurrency());
            //会员是否有未结算订单
            List<OrderRecordVO> orderRecordVOS = orderRecordApi.getNotSettleOrderListByUserId(userId);
            log.info("清除打码量校验,会员id{},WTC参数值{},汇率{},未结算订单数量{}",userId,configParam,finalRate,orderRecordVOS.size());
            if(orderRecordVOS.isEmpty()){
                LambdaQueryWrapper<UserCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
                userCoinLqw.eq(UserCoinPO::getUserId,userId);
                UserCoinPO userCoinPO = userCoinRepository.selectOne(userCoinLqw);

                LambdaQueryWrapper<UserPlatformCoinPO> userPlatformCoinLqw = new LambdaQueryWrapper<>();
                userPlatformCoinLqw.eq(UserPlatformCoinPO::getUserId,userId);
                UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinRepository.selectOne(userPlatformCoinLqw);
                BigDecimal amount = BigDecimal.ZERO;
                if(null != userCoinPO){
                    amount = userCoinPO.getAvailableAmount().divide(finalRate, CommonConstant.business_two, RoundingMode.HALF_UP);
                    log.info("清除打码量校验,会员id{},主货币余额转换WTC之后金额{}",userId,amount);
                }
                if(null != userPlatformCoinPO){
                    amount = amount.add(userPlatformCoinPO.getAvailableAmount());
                    log.info("清除打码量校验,会员id{},WTC金额{}",userId,userPlatformCoinPO.getAvailableAmount());
                }
                if(amount.compareTo(configParam) < 0){
                    LambdaQueryWrapper<UserTypingAmountPO> typingAmountLqw = new LambdaQueryWrapper<>();
                    typingAmountLqw.eq(UserTypingAmountPO::getUserId,userId);
                    UserTypingAmountPO userTypingAmountPO = userTypingAmountRepository.selectOne(typingAmountLqw);
                    log.info("清除打码量校验,会员id{},剩余流水{}",userId,null == userTypingAmountPO?BigDecimal.ZERO:userTypingAmountPO.getTypingAmount());
                    if(null != userTypingAmountPO && userTypingAmountPO.getTypingAmount().compareTo(BigDecimal.ZERO)>0){

                        LambdaQueryWrapper<UserCoinRecordPO> coinLqw = new LambdaQueryWrapper<>();
                        coinLqw.eq(UserCoinRecordPO::getUserId,userId);
                        List<String> typeList = List.of(WalletEnum.CoinTypeEnum.GAME_BET.getCode(),
                                WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                        coinLqw.in(UserCoinRecordPO::getCoinType,typeList);
                        Long startTime = DateUtils.addHour(System.currentTimeMillis(),-1);
                        coinLqw.gt(UserCoinRecordPO::getCreatedTime, startTime);
                        coinLqw.orderByDesc(UserCoinRecordPO::getCreatedTime);
                        coinLqw.last(" limit 1");
                        UserCoinRecordPO userCoinRecordPO  = userCoinRecordRepository.selectOne(coinLqw);
                        log.info("清除打码量校验,会员id{},账变记录类型{}",userId, null== userCoinRecordPO?"":userCoinRecordPO.getCoinType());
                        if(null != userCoinRecordPO && WalletEnum.CoinTypeEnum.GAME_BET.getCode().equals(userCoinRecordPO.getCoinType())){
                            OrderRecordVO orderRecordVO = orderRecordApi.getByThirdOrderId(userCoinRecordPO.getOrderNo());

                            if(null != orderRecordVO && !ClassifyEnum.NOT_SETTLE.getCode().equals( orderRecordVO.getOrderStatus())){
                                cleanTypingAmount(userInfoVO,userTypingAmountPO.getTypingAmount(),configParam);
                            }
                        }else{
                            cleanTypingAmount(userInfoVO,userTypingAmountPO.getTypingAmount(),configParam);
                        }
                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            log.info("校验清除打码量失败,会员ID{},失败原因",userId,e.getMessage());
        }


    }

    private void cleanTypingAmount(UserInfoVO userInfoVO,BigDecimal typingAmount,BigDecimal configParam){


        log.info("清除打码量校验,会员id{},清除打码量{}",userInfoVO,typingAmount);
        UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
        userTypingAmount.setSiteCode(userInfoVO.getSiteCode());
        userTypingAmount.setUserId(userInfoVO.getUserId());
        userTypingAmount.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmount.setTypingAmount(typingAmount);
        userTypingAmount.setType(TypingAmountEnum.SUBTRACT.getCode());
        userTypingAmount.setOrderNo("W"+ SnowFlakeUtils.getSnowId());
        userTypingAmount.setCurrencyCode(userTypingAmount.getCurrencyCode());
        userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.SYSTEM.getCode());
        userTypingAmount.setRemark("余额(平台币+主货币）小于"+configParam+"的，且没有未结算，用户的剩余流水修改为0");
        //同时清除会员活动流水
        LambdaQueryWrapper<UserActivityTypingAmountPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserActivityTypingAmountPO::getUserId,userInfoVO.getUserId());
        UserActivityTypingAmountPO userActivityTypingAmountPO = userActivityTypingAmountRepository.selectOne(lqw);
        if(null != userActivityTypingAmountPO){
            userTypingAmount.setActivityTypingAmount(userActivityTypingAmountPO.getTypingAmount());
        }

        List<UserTypingAmountRequestVO> list = new ArrayList<>();
        list.add(userTypingAmount);
        userTypingAmount.setTypingList(list);
        addUserTypingAmount(userTypingAmount,userInfoVO);
    }
}
