package com.cloud.baowang.play.api.sba.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.wallet.api.enums.wallet.TransferTypeEnums;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.sb.SBCurrencyEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.sba.SBASportInterface;
import com.cloud.baowang.play.api.sba.action.base.SBABase;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.SBBaseReq;
import com.cloud.baowang.play.api.vo.sba.SBPlaceBetReq;
import com.cloud.baowang.play.api.vo.sba.SBPlaceBetRes;
import com.cloud.baowang.play.api.vo.sba.SBToCoinAddVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.SiteVenueService;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SBAPlaceBetAction extends SBABase implements SBASportInterface {


    public SBAPlaceBetAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.PLACE_BET;
    }


    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBPlaceBetReq req = JSONObject.parseObject(baseReq.getMessage(), SBPlaceBetReq.class);
        SBPlaceBetRes sbPlaceBetRes = SBPlaceBetRes.builder()
                .licenseeTxId(req.getVendorTransId())
                .refId(req.getRefId())
                .build();

        if (!req.validate()) {
            log.info("{},缺少参数 ", getAction().getName());
            sbPlaceBetRes.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return sbPlaceBetRes;
        }


        String lock = String.format(RedisConstants.SBA_PLAT_BET_LOCK, req.getVendorTransId());
        String lockCode = RedisUtil.acquireImmediate(lock, 100L);

        try {
            if (lockCode == null) {
                log.info("{},重复投注锁异常,{}", getAction().getName(), req);
                sbPlaceBetRes.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return sbPlaceBetRes;
            }

            UserInfoVO userInfoVO = getUserInfo(req.getUserId());
            if (ObjectUtil.isNotEmpty(userInfoVO.getAccountStatus())) {
                List<String> accountStatusList = Arrays.asList(userInfoVO.getAccountStatus().split(","));
                if (CollectionUtil.isNotEmpty(accountStatusList) && accountStatusList.contains(UserStatusEnum.GAME_LOCK.getCode())) {
                    log.info("沙巴体育-用户信息异常。锁定:{}", userInfoVO);
                    sbPlaceBetRes.setStatus(SBResultCode.ACCOUNT_DEACTIVATED.getCode());
                    return sbPlaceBetRes;
                }
            }

            if (venueMaintainClosed(VenuePlatformConstants.SBA,userInfoVO.getSiteCode())) {
                log.info("{},场馆未开启不允许下注 ", getAction().getName());
                sbPlaceBetRes.setStatus(SBResultCode.NO_PERMISSION.getCode());
                return sbPlaceBetRes;
            }

            //校验游戏下注
            checkGameUser(userInfoVO);

            List<String> currencyPlatList = SBCurrencyEnum.getPlatCurrencyCodeEnumList(req.getCurrency());
            if (CollectionUtil.isEmpty(currencyPlatList)) {
                log.info("{},查询订单异常,币种异常:{}", getAction().getName(), req.getCurrency());
                sbPlaceBetRes.setStatus(SBResultCode.INVALID_CURRENCY.getCode());
                return sbPlaceBetRes;
            }

            //产线环境才校验币种,测试环境不校验.因为测试环境沙巴传的是测试币
            if (venueUserAccountConfig.getEvn()) {
                if (!currencyPlatList.contains(userInfoVO.getMainCurrency())) {
                    log.info("沙巴体育-下注币种与用户法币不一致,currency:{},{}", req.getCurrency(), userInfoVO);
                    sbPlaceBetRes.setStatus(SBResultCode.INVALID_CURRENCY.getCode());
                    return sbPlaceBetRes;
                }
            }


            UserCoinWalletVO userCenterCoin = getUserCenterCoin(userInfoVO.getUserId());
            log.info("{} 下注 累计 的总金额,对比余额  userAccount:[{}],result:{}", getAction().getName(), userInfoVO.getUserId(), userCenterCoin);
            if (ObjectUtils.isEmpty(userCenterCoin)) {
                log.info("{} 下注 累计 的总金额,对比余额  用户钱包不存在 userAccount:[{}],result:{}", getAction().getName(), userInfoVO.getUserId(), userCenterCoin);
                sbPlaceBetRes.setStatus(SBResultCode.SYSTEM_ERROR.getCode());
                return sbPlaceBetRes;
            }

            BigDecimal amount = req.getBetAmount();

            // 余额如果小于0，说明余额不足
            BigDecimal balance = userCenterCoin.getTotalAmount().subtract(amount);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}  的总金额,对比余额  余额不足 userAccount:[{}],result:{}", getAction().getName(), userInfoVO.getUserId(), userCenterCoin);
                sbPlaceBetRes.setStatus(SBResultCode.INSUFFICIENT_PLAYER_BALANCE.getCode());
                return sbPlaceBetRes;
            }


            //验证三方订单是否存在
            TransferRecordResultVO transferRecordResultVo = validateOrder(TransferRecordResultVO.getSBARecordId(req.getRefId()));
            if (ObjectUtils.isNotEmpty(transferRecordResultVo)) {
                log.info("{},查询订单异常,订单已存在:{}", getAction().getName(), transferRecordResultVo);
                sbPlaceBetRes.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return sbPlaceBetRes;
            }

            //验证我方订单是否存在
            TransferRecordResultVO transferRecordBet = TransferRecordResultVO.builder()
                    .venueCode(VenuePlatformConstants.SBA)
                    .transId(req.getVendorTransId())
                    .build();
            TransferRecordResultVO transferRecordBetVO = validateOrder(transferRecordBet);
            if (ObjectUtils.isNotEmpty(transferRecordBetVO)) {
                log.info("{},查询订单异常,注单ID已存在:{}", getAction().getName(), transferRecordBetVO);
                sbPlaceBetRes.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return sbPlaceBetRes;
            }

            //冻结金额
            SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(req.getRefId(), req.getUserId(), req.getCreditAmount(),
                    req.getDebitAmount(), CoinBalanceTypeEnum.FREEZE, null);
            sbToCoinAddVO.setRemark(getAction().getName());
            TransferRecordResultVO walletTransferRecordVO = TransferRecordResultVO.builder()
                    .orderId(req.getRefId())
                    .transId(req.getVendorTransId())
                    .userAccount(req.getUserId())
                    .venueCode(VenuePlatformConstants.SBA)
                    .orderStatus(SBATransferEnums.PLACE_BET.getCode())
                    .amount(sbToCoinAddVO.getAmount())
                    .transferType(sbToCoinAddVO.getType() ? TransferTypeEnums.IN.getCode() : TransferTypeEnums.OUT.getCode())
                    .build();

            SBResultCode sbConfirmBetRes = toCoin(SBActionEnum.PLACE_BET, sbToCoinAddVO, walletTransferRecordVO);
            sbPlaceBetRes.setStatus(sbConfirmBetRes.getCode());

        } catch (SBDefaultException e) {
            throw new SBDefaultException(e.getResultCode());
        } catch (Exception e) {
            log.error("{},下注异常", getAction().getName(), e);
            throw new SBDefaultException(SBResultCode.SYSTEM_ERROR);
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("{},执行结束,删除锁:{}", lock, release);
            }
        }
        return sbPlaceBetRes;
    }
}
