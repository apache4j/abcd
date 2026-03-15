package com.cloud.baowang.wallet.service;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.wallet.api.enums.UpdateBalanceStatusEnums;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformCoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WalletEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinQueryVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinWalletVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.userwallet.UserPlatformTransferVO;
import com.cloud.baowang.wallet.po.UserCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformCoinPO;
import com.cloud.baowang.wallet.po.UserPlatformTransferRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author qiqi
 */

@Service
@Slf4j
@AllArgsConstructor
public class UserPlatformCoinService extends ServiceImpl<UserPlatformCoinRepository, UserPlatformCoinPO> {

    private final UserPlatformCoinRepository userPlatformCoinRepository;

    private final UserPlatformCoinAddService userCoinAddService;

    private final SiteCurrencyInfoService siteCurrencyInfoService;

    private final UserInfoApi userInfoApi;

    private final UserCoinService userCoinService;




    public CoinRecordResultVO addPlatformCoin(UserPlatformCoinAddVO userCoinAddVO) {
        CoinRecordResultVO coinRecordResultVO = new CoinRecordResultVO();
        coinRecordResultVO.setResult(true);
        coinRecordResultVO.setCoinAfterBalance(BigDecimal.ZERO);
        coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.SUCCESS);
        if (userCoinAddVO.getCoinValue().compareTo(BigDecimal.ZERO) <= 0) {
            log.info("平台币订单编号为{}的订单账变金额小于0", userCoinAddVO.getOrderNo());
            coinRecordResultVO.setResult(false);
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.AMOUNT_LESS_ZERO);
            return coinRecordResultVO;
        }
        try {
            return userCoinAddService.userPlatformCoinAdd(userCoinAddVO, coinRecordResultVO);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            coinRecordResultVO.setResult(false);
            coinRecordResultVO.setResultStatus(UpdateBalanceStatusEnums.FAIL);
        }
        return coinRecordResultVO;
    }

    public UserPlatformCoinWalletVO getUserPlatformCoin(UserCoinQueryVO userCoinQueryVO) {
        String userAccount = userCoinQueryVO.getUserAccount();
        LambdaQueryWrapper<UserPlatformCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserId()), UserPlatformCoinPO::getUserId, userCoinQueryVO.getUserId());
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getUserAccount()), UserPlatformCoinPO::getUserAccount, userAccount);
        userCoinLqw.eq(StringUtils.isNotBlank(userCoinQueryVO.getSiteCode()), UserPlatformCoinPO::getSiteCode, userCoinQueryVO.getSiteCode());
        UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinRepository.selectOne(userCoinLqw);
        UserPlatformCoinWalletVO userCoinWalletVO = new UserPlatformCoinWalletVO();
        userCoinWalletVO.setUserAccount(userCoinQueryVO.getUserAccount());
        userCoinWalletVO.setSiteCode(userCoinQueryVO.getSiteCode());
        userCoinWalletVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        userCoinWalletVO.setTotalAmount(BigDecimal.ZERO);
        userCoinWalletVO.setCenterTotalAmount(BigDecimal.ZERO);
        userCoinWalletVO.setCenterAmount(BigDecimal.ZERO);
        userCoinWalletVO.setCenterFreezeAmount(BigDecimal.ZERO);
        if (null != userPlatformCoinPO) {
            userCoinWalletVO.setCenterTotalAmount(userPlatformCoinPO.getTotalAmount());
            userCoinWalletVO.setCenterAmount(userPlatformCoinPO.getAvailableAmount());
            userCoinWalletVO.setTotalAmount(userPlatformCoinPO.getAvailableAmount());
            userCoinWalletVO.setCenterFreezeAmount(userPlatformCoinPO.getFreezeAmount());
        }
        return userCoinWalletVO;
    }

    /**
     * 获取平台币余额 全部信息
     *
     * @param userCoinQueryVO
     * @return
     */
    public UserPlatformBalanceRespVO getUserPlatformBalance(UserCoinQueryVO userCoinQueryVO) {
        LambdaQueryWrapper<UserPlatformCoinPO> userCoinLqw = new LambdaQueryWrapper<>();
        userCoinLqw.eq(UserPlatformCoinPO::getUserId, userCoinQueryVO.getUserId());
        userCoinLqw.eq(UserPlatformCoinPO::getUserAccount, userCoinQueryVO.getUserAccount());
        userCoinLqw.eq(UserPlatformCoinPO::getSiteCode, userCoinQueryVO.getSiteCode());
        UserPlatformCoinPO userPlatformCoinPO = userPlatformCoinRepository.selectOne(userCoinLqw);

        UserPlatformBalanceRespVO userPlatformBalanceRespVO = new UserPlatformBalanceRespVO();
        userPlatformBalanceRespVO.setSiteCode(userCoinQueryVO.getSiteCode());
        userPlatformBalanceRespVO.setPlatAvailableAmount(BigDecimal.ZERO);
        userPlatformBalanceRespVO.setUserAvailableAmount(BigDecimal.ZERO);
        userPlatformBalanceRespVO.setUserAccount(userCoinQueryVO.getUserAccount());
        userPlatformBalanceRespVO.setPlatCurrency(CommonConstant.PLAT_CURRENCY_CODE);
        BigDecimal finalRate = siteCurrencyInfoService.getCurrencyFinalRate(userCoinQueryVO.getSiteCode(), userCoinQueryVO.getCurrencyCode());
        userPlatformBalanceRespVO.setTransferRate(finalRate);
        userPlatformBalanceRespVO.setUserCurrencyCode(userCoinQueryVO.getCurrencyCode());
        if (null != userPlatformCoinPO) {
            userPlatformBalanceRespVO.setPlatAvailableAmount(userPlatformCoinPO.getAvailableAmount());
        }
        UserCoinPO userCoinPO = userCoinService.getUserCoin(userCoinQueryVO.getSiteCode(), userCoinQueryVO.getUserId());
        if (userCoinPO != null) {
            userPlatformBalanceRespVO.setUserAvailableAmount(userCoinPO.getAvailableAmount());
        }

        return userPlatformBalanceRespVO;
    }




}
