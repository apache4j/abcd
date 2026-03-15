package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.repositories.UserCoinRepository;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */

@Service
@Slf4j
@AllArgsConstructor
public class UserCoinService extends ServiceImpl<UserCoinRepository, UserCoinPO> {

    private final UserCoinRepository userCoinRepository;

    private final UserCoinAddService userCoinAddService;

    private final UserInfoApi userInfoApi;

    private final UserPlatformCoinRepository userPlatformCoinRepository;

    private final SiteCurrencyInfoService siteCurrencyInfoService;


    public CoinRecordResultVO addCoin(UserCoinAddVO userCoinAddVO) {
        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
        coinRecordResultVO.setResult(true);
        coinRecordResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.SUCCESS);
        try {
            if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("订单编号为{}的订单账变金额小于0",userCoinAddVO.getOrderNo());
                coinRecordResultVO.setResult(false);
                coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO);
                return coinRecordResultVO;
            }
            coinRecordResultVO = userCoinAddService.userCoinAdd(userCoinAddVO,coinRecordResultVO);
            if(coinRecordResultVO.getResult()){
                userCoinAddService.sendNoticeBalanceChanges(userCoinAddVO.getUserInfoVO(),coinRecordResultVO);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            coinRecordResultVO.setResult(false);
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
        }
        return coinRecordResultVO;
    }


    public UserCoinWalletVO getUserCenterCoin(UserCoinQueryVO userCoinQueryVO) {
        UserCoinPO userCoinPO = getUserCoin(userCoinQueryVO);

        String userAccount = userCoinQueryVO.getUserAccount();
        UserCoinWalletVO userCoinWalletVO = new UserCoinWalletVO();
        userCoinWalletVO.setUserAccount(userAccount);
        if (null == userCoinPO || StringUtils.isEmpty(userCoinPO.getUserId())) {
            userCoinWalletVO.setSiteCode(userCoinQueryVO.getSiteCode());
            userCoinWalletVO.setTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterFreezeAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCurrency(userCoinQueryVO.getCurrencyCode());
        } else {
            userCoinWalletVO.setUserAccount(userCoinPO.getUserAccount());
            userCoinWalletVO.setSiteCode(userCoinPO.getSiteCode());
            userCoinWalletVO.setCenterTotalAmount(userCoinPO.getTotalAmount());
            userCoinWalletVO.setCenterAmount(userCoinPO.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0?BigDecimal.ZERO:userCoinPO.getAvailableAmount());
            userCoinWalletVO.setTotalAmount(userCoinPO.getAvailableAmount().compareTo(BigDecimal.ZERO) < 0?BigDecimal.ZERO:userCoinPO.getAvailableAmount());
            userCoinWalletVO.setCenterFreezeAmount(userCoinPO.getFreezeAmount());
            userCoinWalletVO.setCurrency(userCoinPO.getCurrency());
            userCoinWalletVO.setUserId(userCoinPO.getUserId());
        }

        return userCoinWalletVO;
    }


    public UserCoinWalletVO getUserActualBalance(UserCoinQueryVO userCoinQueryVO) {
        UserCoinPO userCoinPO = getUserCoin(userCoinQueryVO);

        String userAccount = userCoinQueryVO.getUserAccount();
        UserCoinWalletVO userCoinWalletVO = new UserCoinWalletVO();
        userCoinWalletVO.setUserAccount(userAccount);
        if (null == userCoinPO || StringUtils.isEmpty(userCoinPO.getUserId())) {
            userCoinWalletVO.setSiteCode(userCoinQueryVO.getSiteCode());
            userCoinWalletVO.setTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterFreezeAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCurrency(userCoinQueryVO.getCurrencyCode());
        } else {
            userCoinWalletVO.setUserAccount(userCoinPO.getUserAccount());
            userCoinWalletVO.setSiteCode(userCoinPO.getSiteCode());
            userCoinWalletVO.setCenterTotalAmount(userCoinPO.getTotalAmount());
            userCoinWalletVO.setCenterAmount(userCoinPO.getAvailableAmount());
            userCoinWalletVO.setTotalAmount(userCoinPO.getAvailableAmount());
            userCoinWalletVO.setCenterFreezeAmount(userCoinPO.getFreezeAmount());
            userCoinWalletVO.setCurrency(userCoinPO.getCurrency());
        }

        return userCoinWalletVO;
    }


    private UserCoinPO getUserCoin(UserCoinQueryVO userCoinQueryVO){
        String userAccount = userCoinQueryVO.getUserAccount();
        LambdaQueryWrapper<UserCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserId()),UserCoinPO::getUserId, userCoinQueryVO.getUserId());
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserAccount()),UserCoinPO::getUserAccount, userAccount);
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getSiteCode()),UserCoinPO::getSiteCode, userCoinQueryVO.getSiteCode());
        UserCoinPO userCoinPO = userCoinRepository.selectOne(userCoinLqw);
        return userCoinPO;
    }

    public UserCoinPO getUserCoin(String siteCode,String userId){
        LambdaQueryWrapper<UserCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
        userCoinLqw.eq(UserCoinPO::getUserId, userId);
        userCoinLqw.eq(UserCoinPO::getSiteCode, siteCode);
        UserCoinPO userCoinPO = userCoinRepository.selectOne(userCoinLqw);
        return userCoinPO;
    }

    private UserPlatformCoinPO getUserPlatformCoin(UserCoinQueryVO userCoinQueryVO){
        LambdaQueryWrapper<UserPlatformCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserId()),UserPlatformCoinPO::getUserId, userCoinQueryVO.getUserId());
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserAccount()),UserPlatformCoinPO::getUserAccount, userCoinQueryVO.getUserAccount());
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getSiteCode()),UserPlatformCoinPO::getSiteCode, userCoinQueryVO.getSiteCode());

        UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinRepository.selectOne(userCoinLqw);

        return userPlatformCoinPO;
    }

    /**
     * 获取会员余额，包括平台币，必须siteCode
     * @param userCoinQueryVO
     * @return
     */
    public UserCoinWalletVO getUserCenterCoinAndPlatform(UserCoinQueryVO userCoinQueryVO) {
        UserCoinWalletVO userCoinWalletVO = new UserCoinWalletVO();
        //获取会员法币钱包
        UserCoinPO userCoinPO = getUserCoin(userCoinQueryVO);
        if (null == userCoinPO || StringUtils.isEmpty(userCoinPO.getId())) {
            UserInfoVO userInfoVO = new UserInfoVO();
            if(StringUtils.isNotBlank(userCoinQueryVO.getUserId())){
                userInfoVO = userInfoApi.getByUserId(userCoinQueryVO.getUserId());
            }else{
                UserQueryVO userQueryVO = new UserQueryVO();
                userQueryVO.setUserAccount(userCoinQueryVO.getUserAccount());
                userQueryVO.setSiteCode(userCoinQueryVO.getSiteCode());
                userInfoVO = userInfoApi.getUserInfoByQueryVO(userQueryVO);
            }
            userCoinWalletVO.setSiteCode(userInfoVO.getSiteCode());
            userCoinWalletVO.setTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterTotalAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCenterFreezeAmount(BigDecimal.ZERO);
            userCoinWalletVO.setCurrency(userInfoVO.getMainCurrency());
        } else {
            userCoinWalletVO.setUserAccount(userCoinPO.getUserAccount());
            userCoinWalletVO.setSiteCode(userCoinPO.getSiteCode());
            userCoinWalletVO.setCenterTotalAmount(userCoinPO.getTotalAmount());
            userCoinWalletVO.setCenterAmount(userCoinPO.getAvailableAmount());
            userCoinWalletVO.setTotalAmount(userCoinPO.getTotalAmount());
            userCoinWalletVO.setCenterFreezeAmount(userCoinPO.getFreezeAmount());
            userCoinWalletVO.setCurrency(userCoinPO.getCurrency());
        }

        //获取平台币钱包
        UserPlatformCoinPO userPlatformCoinPO = getUserPlatformCoin(userCoinQueryVO);

        String platCurrencyName = (String) RedisUtil.getMapValue(CacheConstants.KEY_SITE_PLAT_CURRENCY, userCoinQueryVO.getSiteCode());
        if (null == userPlatformCoinPO) {
            userCoinWalletVO.setPlatformAmount(BigDecimal.ZERO);
            userCoinWalletVO.setPlatformCurrency(platCurrencyName);
        } else {
            if(BigDecimal.ZERO.compareTo(userPlatformCoinPO.getAvailableAmount()) >=0){
                PlatCurrencyFromTransferVO platCurrencyFromTransferVO = new PlatCurrencyFromTransferVO();
                platCurrencyFromTransferVO.setSiteCode(userCoinQueryVO.getSiteCode());
                platCurrencyFromTransferVO.setTargetCurrencyCode(userCoinWalletVO.getCurrency());
                platCurrencyFromTransferVO.setSourceAmt(userPlatformCoinPO.getAvailableAmount());
                BigDecimal amount =  siteCurrencyInfoService.transferPlatToMainCurrency(platCurrencyFromTransferVO).getData();
                userCoinWalletVO.setTotalAmount(userCoinWalletVO.getTotalAmount().add(amount));
            }

            userCoinWalletVO.setPlatformCurrency(platCurrencyName);
            userCoinWalletVO.setPlatformAmount(userPlatformCoinPO.getAvailableAmount());
        }

        return userCoinWalletVO;
    }


    public List<UserCoinWalletVO> getUserCenterCoinList(List<String> userIds) {
        List<UserCoinWalletVO> result = Lists.newArrayList();
        for (String userId : userIds) {
            UserCoinQueryVO vo = UserCoinQueryVO.builder().userId(userId).build();
            UserCoinWalletVO userCenterCoin = getUserCenterCoin(vo);
            result.add(userCenterCoin);
        }
        return result;
    }
}
