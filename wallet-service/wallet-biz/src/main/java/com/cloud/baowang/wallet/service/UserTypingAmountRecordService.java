package com.cloud.baowang.wallet.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountMqVO;
import com.cloud.baowang.common.kafka.vo.UserTypingAmountRequestVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userTypingAmount.UserTypingRecordVO;
import com.cloud.baowang.wallet.po.UserActivityTypingAmountPO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.po.UserTypingAmountPO;
import com.cloud.baowang.wallet.po.UserTypingAmountRecordPO;
import com.cloud.baowang.wallet.repositories.UserActivityTypingAmountRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRecordRepository;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import com.cloud.baowang.wallet.repositories.UserTypingAmountRecordRepository;
import com.cloud.baowang.wallet.repositories.UserTypingAmountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author kimi
 */

@Slf4j
@Service
@AllArgsConstructor
public class UserTypingAmountRecordService extends ServiceImpl<UserTypingAmountRecordRepository, UserTypingAmountRecordPO> {

    private final UserTypingAmountRecordRepository  userTypingAmountRecordRepository;

    private final SystemDictConfigApi systemDictConfigApi;

    private final SiteApi siteApi;

    private final UserPlatformCoinRepository userPlatformCoinRepository;

    private final UserCoinRepository userCoinRepository;

    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final OrderRecordApi orderRecordApi;

    private final UserTypingAmountRepository userTypingAmountRepository;

    private final UserActivityTypingAmountRepository userActivityTypingAmountRepository;

    private final UserInfoApi userInfoApi;

    private final UserCoinRecordRepository userCoinRecordRepository;



    public Page<UserTypingRecordVO> listUserTypingRecordPage(UserTypingRecordRequestVO vo) {
        Page<UserTypingAmountRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserTypingAmountRecordPO> lqw = buildLqw(vo);

        Page<UserTypingAmountRecordPO> userTypingAmountRecordPOPage = userTypingAmountRecordRepository.selectPage(page, lqw);

        Page<UserTypingRecordVO> userTypingRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userTypingAmountRecordPOPage, userTypingRecordVOPage);

        List<UserTypingRecordVO> userCoinRecordVOList = ConvertUtil.entityListToModelList(userTypingAmountRecordPOPage.getRecords(), UserTypingRecordVO.class);
        userTypingRecordVOPage.setRecords(userCoinRecordVOList);
        return userTypingRecordVOPage;
    }

    public LambdaQueryWrapper<UserTypingAmountRecordPO> buildLqw(UserTypingRecordRequestVO vo) {
        LambdaQueryWrapper<UserTypingAmountRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserTypingAmountRecordPO::getSiteCode,vo.getSiteCode());
        lqw.ge(null != vo.getRecordStartTime(), UserTypingAmountRecordPO::getCreatedTime, vo.getRecordStartTime());
        lqw.lt(null != vo.getRecordEndTime(), UserTypingAmountRecordPO::getCreatedTime, vo.getRecordEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserTypingAmountRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getCurrency()), UserTypingAmountRecordPO::getCurrency, vo.getCurrency());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserTypingAmountRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getAccountType()), UserTypingAmountRecordPO::getAccountType, vo.getAccountType());
        lqw.eq(StringUtils.isNotBlank(vo.getAdjustWay()), UserTypingAmountRecordPO::getAdjustWay, vo.getAdjustWay());
        lqw.eq(StringUtils.isNotBlank(vo.getAdjustType()), UserTypingAmountRecordPO::getAdjustType, vo.getAdjustType());
        lqw.orderByDesc(UserTypingAmountRecordPO::getCreatedTime);
        lqw.orderByDesc(UserTypingAmountRecordPO::getId);
        return lqw;
    }

    public Long userTypingRecordPageCount(UserTypingRecordRequestVO vo) {
        //绑定条件
        LambdaQueryWrapper<UserTypingAmountRecordPO> lqw = buildLqw(vo);
        return userTypingAmountRecordRepository.selectCount(lqw);
    }

    public void userTypingAmountCleanZero() {
        List<SiteVO> siteList = siteApi.siteInfoAllstauts().getData();
        for (SiteVO siteVO:siteList) {

            SystemDictConfigRespVO systemDictConfigRespVO=  systemDictConfigApi.getByCode(DictCodeConfigEnums.SYSTEM_CLEANUP_BET_AMOUNT.getCode(),siteVO.getSiteCode()).getData();
            if(null == systemDictConfigRespVO){
                continue;
            }
            Map<String,BigDecimal> map = new HashMap<>();
            //获取站点未结算订单
            List<OrderRecordVO> orderRecordVOS  =orderRecordApi.getNotSettleOrderListBySiteCode(siteVO.getSiteCode());
            Map<String, String> orderMap = orderRecordVOS.stream()
                    .collect(Collectors.toMap(
                            OrderRecordVO::getUserId,
                            OrderRecordVO::getId,
                            // 选择保留现有的值
                            (existing, replacement) -> existing,
                            // 提供一个Map工厂以避免重复键的问题
                            HashMap::new
                    ));



            //回去会员主货币信息集合
            LambdaQueryWrapper<UserCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
            userCoinLqw.eq(UserCoinPO::getSiteCode,siteVO.getSiteCode());
            List<UserCoinPO> userCoinPOS = userCoinRepository.selectList(userCoinLqw);


            BigDecimal configParam = new BigDecimal(systemDictConfigRespVO.getConfigParam());
            //查询平台币余额小于系统设置的会员集合
            LambdaQueryWrapper<UserPlatformCoinPO> platformCoinLqw = new LambdaQueryWrapper<>();
            platformCoinLqw.eq(UserPlatformCoinPO::getSiteCode,siteVO.getSiteCode());
            List<UserPlatformCoinPO> userPlatformCoinPOS  =  userPlatformCoinRepository.selectList(platformCoinLqw);
            Map<String, BigDecimal> platformMap = userPlatformCoinPOS.stream()
                    .collect(Collectors.toMap(UserPlatformCoinPO::getUserId, UserPlatformCoinPO::getAvailableAmount));

            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoService.getAllFinalRate(siteVO.getSiteCode());

            if (CollUtil.isNotEmpty(userPlatformCoinPOS)) {
                for (UserPlatformCoinPO userPlatformCoinPO:userPlatformCoinPOS) {
                    BigDecimal amount = userPlatformCoinPO.getAvailableAmount();
                    if(amount.compareTo(configParam) < 0){
                        map.put(userPlatformCoinPO.getUserId(),amount);
                    }
                }
            }
            if (CollUtil.isNotEmpty(userCoinPOS)) {
                for (UserCoinPO userCoinPO:userCoinPOS) {
                    String userId = userCoinPO.getUserId();
                    BigDecimal rate = allFinalRate.get(userCoinPO.getCurrency());
                    if(null == rate){
                        continue;
                    }
                    BigDecimal amount = userCoinPO.getAvailableAmount().divide(rate, CommonConstant.business_two, RoundingMode.HALF_UP);
                    if(amount.compareTo(configParam) >= 0){
                        map.remove(userId);
                        continue;
                    }
                    if(platformMap.containsKey(userId)){
                        BigDecimal platformAmount = platformMap.get(userId);
                        amount = amount.add(platformAmount);
                    }
                    if(amount.compareTo(configParam) < 0){
                        log.info("会员id{},账号{},平台币余额{},主货币余额{},汇率{}",userId,userCoinPO.getUserAccount(),platformMap.get(userId),userCoinPO.getAvailableAmount(),rate);
                        map.put(userId,amount);
                    }else{
                        map.remove(userId);
                    }
                }
            }

            //获取总额小于阈值的会员ID集合
            List<String> userIds = new ArrayList<>(map.keySet());
            for (String m :userIds) {
                if(orderMap.containsKey(m)){
                    map.remove(m);
                }
            }
            if(CollectionUtil.isNotEmpty(userIds)){
                List<String> clearZeroUserIdList = new ArrayList<>(map.keySet());
                if(CollectionUtil.isNotEmpty(clearZeroUserIdList)){
                    log.info("打码量清零 符合余额条件并且无未结算订单的会员数为:"+clearZeroUserIdList.size());
                    LambdaQueryWrapper<UserTypingAmountPO> typingAmountLqw = new LambdaQueryWrapper<>();
                    typingAmountLqw.in(UserTypingAmountPO::getUserId,clearZeroUserIdList);
                    typingAmountLqw.gt(UserTypingAmountPO::getTypingAmount,BigDecimal.ZERO);
                    List<UserTypingAmountPO> userTypingAmountPOS = userTypingAmountRepository.selectList(typingAmountLqw);
                    List<UserTypingAmountRequestVO> userTypingAmountRequestVOS = new ArrayList<>();
                    for (UserTypingAmountPO typingAmountPO:userTypingAmountPOS) {
                        /*LambdaQueryWrapper<UserCoinRecordPO> coinLqw = new LambdaQueryWrapper<>();
                        coinLqw.eq(UserCoinRecordPO::getUserId,typingAmountPO.getUserId());
                        List<String> typeList = List.of(WalletEnum.CoinTypeEnum.GAME_BET.getCode(),
                                WalletEnum.CoinTypeEnum.GAME_PAYOUT.getCode());
                        coinLqw.in(UserCoinRecordPO::getCoinType,typeList);
                        Long startTime = DateUtils.addHour(System.currentTimeMillis(),-1);
                        coinLqw.gt(UserCoinRecordPO::getCreatedTime, startTime);
                        coinLqw.orderByDesc(UserCoinRecordPO::getCreatedTime);
                        coinLqw.last(" limit 1");
                        UserCoinRecordPO userCoinRecordPO  = userCoinRecordRepository.selectOne(coinLqw);
                        if(null != userCoinRecordPO && WalletEnum.CoinTypeEnum.GAME_BET.getCode().equals(userCoinRecordPO.getCoinType())){
                            OrderRecordVO orderRecordVO = orderRecordApi.getByThirdOrderId(userCoinRecordPO.getOrderNo());
                            if(null == orderRecordVO || ClassifyEnum.NOT_SETTLE.getCode().equals( orderRecordVO.getOrderStatus())){
                                break;
                            }
                        }*/
                        UserTypingAmountRequestVO userTypingAmount = new UserTypingAmountRequestVO();
                        userTypingAmount.setSiteCode(typingAmountPO.getSiteCode());
                        userTypingAmount.setUserId(typingAmountPO.getUserId());
                        userTypingAmount.setUserAccount(typingAmountPO.getUserAccount());
                        userTypingAmount.setTypingAmount(typingAmountPO.getTypingAmount());
                        userTypingAmount.setType(TypingAmountEnum.SUBTRACT.getCode());
                        userTypingAmount.setOrderNo("W"+ SnowFlakeUtils.getSnowId());
                        userTypingAmount.setCurrencyCode(userTypingAmount.getCurrencyCode());
                        userTypingAmount.setAdjustType(TypingAmountAdjustTypeEnum.SYSTEM.getCode());
                        userTypingAmount.setRemark("余额(平台币+主货币）小于"+configParam+"的，且没有未结算，用户的剩余流水修改为0");
                        //同时清除会员活动流水
                        LambdaQueryWrapper<UserActivityTypingAmountPO> lqw = new LambdaQueryWrapper<>();
                        lqw.eq(UserActivityTypingAmountPO::getUserId,typingAmountPO.getUserId());
                        UserActivityTypingAmountPO userActivityTypingAmountPO = userActivityTypingAmountRepository.selectOne(lqw);
                        if(null != userActivityTypingAmountPO){
                            userTypingAmount.setActivityTypingAmount(userActivityTypingAmountPO.getTypingAmount());
                        }
                        userTypingAmountRequestVOS.add(userTypingAmount);
                    }
                    if(CollectionUtil.isNotEmpty(userTypingAmountRequestVOS)){
                        int batchSize = 1000;
                        int totalSize = userTypingAmountRequestVOS.size();
                        for (int i = 0; i < totalSize; i += batchSize) {
                            int end = Math.min(i + batchSize, totalSize);
                            List<UserTypingAmountRequestVO> subList = userTypingAmountRequestVOS.subList(i, end);
                            UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(subList).build();
                            KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);
                        }
                    }

                }
            }
        }
    }


}
