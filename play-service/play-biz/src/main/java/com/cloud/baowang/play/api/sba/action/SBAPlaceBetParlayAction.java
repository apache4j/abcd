package com.cloud.baowang.play.api.sba.action;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.sb.SBCurrencyEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.user.api.enums.UserStatusEnum;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.sba.SBASportInterface;
import com.cloud.baowang.play.api.sba.action.base.SBABase;
import com.cloud.baowang.play.api.vo.sba.*;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.SiteVenueService;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.play.service.VenueInfoService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TransferTypeEnums;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinWalletVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SBAPlaceBetParlayAction extends SBABase implements SBASportInterface {


    public SBAPlaceBetParlayAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.PLACE_BET_PARLAY;
    }

    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBPlaceBetParlayRes result = SBPlaceBetParlayRes.builder().build();

        SBPlaceBetParlayReq req = JSONObject.parseObject(baseReq.getMessage(), SBPlaceBetParlayReq.class);
        if (!req.validate()) {
            log.info("{} 串关 缺少参数:{}", getAction().getName(), req);
            result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return result;
        }
        result.setAccount(req.getUserId());
        UserInfoVO userInfoVO = getUserInfo(req.getUserId());
        if (ObjectUtil.isNotEmpty(userInfoVO.getAccountStatus())) {

            List<String> accountStatusList = Arrays.asList(userInfoVO.getAccountStatus().split(","));
            if (CollectionUtil.isNotEmpty(accountStatusList) && accountStatusList.contains(UserStatusEnum.GAME_LOCK.getCode())) {
                log.info("沙巴体育-用户信息异常。锁定:{}", userInfoVO);
                throw new SBDefaultException(SBResultCode.ACCOUNT_DEACTIVATED);
            }
        }

        if(venueMaintainClosed(VenuePlatformConstants.SBA,userInfoVO.getSiteCode())){
            log.info("{},场馆未开启不允许下注 ", getAction().getName());
            throw new SBDefaultException(SBResultCode.NO_PERMISSION);
        }
        checkGameUser(userInfoVO);

        List<String> currencyPlatList = SBCurrencyEnum.getPlatCurrencyCodeEnumList(req.getCurrency());
        if (CollectionUtil.isEmpty(currencyPlatList)) {
            log.info("{},查询订单异常,币种异常:{}", getAction().getName(), req.getCurrency());
            throw new SBDefaultException(SBResultCode.INVALID_CURRENCY);
        }



        //产线环境才校验币种,测试环境不校验.因为测试环境沙巴传的是测试币
        if(venueUserAccountConfig.getEvn()){
            if (!currencyPlatList.contains(userInfoVO.getMainCurrency())) {
                log.info("沙巴体育-下注币种与用户法币不一致,currency:{},{}", req.getCurrency(), userInfoVO);
                throw new SBDefaultException(SBResultCode.INVALID_CURRENCY);
            }
        }

        String lock = String.format(RedisConstants.SBA_PLAT_BET_LOCK, req.getVendorTransId());
        String lockCode = RedisUtil.acquireImmediate(lock, 100L);

        try {
            if (lockCode == null) {
                log.info("{},串关重复投注锁异常,{}", getAction().getName(), req);
                result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return result;
            }

            TransferRecordResultVO transferRecordBet = TransferRecordResultVO.builder()
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .transId(req.getVendorTransId())
                    .build();
            TransferRecordResultVO transferRecordBetVO = validateOrder(transferRecordBet);
            if (ObjectUtils.isNotEmpty(transferRecordBetVO)) {
                log.info("{},查询订单异常,注单ID已存在:{}", getAction().getName(), transferRecordBetVO);
                result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return result;
            }


            List<SBPlaceBetRes> txns = Lists.newArrayList();

            int index = 1;

            String vendorTransId = req.getVendorTransId();

            String userAccount = req.getUserId();


            BigDecimal amount = req.getTxns().stream()
                    .map(SBComboInfo::getDebitAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            UserCoinWalletVO userCenterCoin = getUserCenterCoin(getUserAccount(userAccount));
            log.info("{} 串关下注 累计 串关的总金额,对比余额  userAccount:[{}],result:{}", getAction().getName(), userAccount, userCenterCoin);
            if (ObjectUtils.isEmpty(userCenterCoin)) {
                log.info("{} 串关下注 累计 串关的总金额,对比余额  用户钱包不存在 userAccount:[{}],result:{}", getAction().getName(), userAccount, userCenterCoin);
                result.setStatus(SBResultCode.SYSTEM_ERROR.getCode());
                return result;
            }

            // 余额如果小于0，说明余额不足
            BigDecimal balance = userCenterCoin.getTotalAmount().subtract(amount);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                log.info("{}  串关的总金额,对比余额  余额不足 userAccount:[{}],result:{}", getAction().getName(), userAccount, userCenterCoin);
                result.setStatus(SBResultCode.INSUFFICIENT_PLAYER_BALANCE.getCode());
                return result;
            }


            for (SBComboInfo item : req.getTxns()) {

                TransferRecordResultVO transferRecordResultVo = validateOrder(TransferRecordResultVO.getSBARecordId(item.getRefId()));
                if (ObjectUtils.isNotEmpty(transferRecordResultVo)) {
                    log.info("{},查询订单异常,订单已存在:{}", getAction().getName(), transferRecordResultVo);
                    result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                    return result;
                }

                //冻结金额
                SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(item.getRefId(), userAccount, item.getCreditAmount(),
                        item.getDebitAmount(), CoinBalanceTypeEnum.FREEZE, null);
                sbToCoinAddVO.setRemark(getAction().getName());


                String transId = vendorTransId + "_" + index;
                TransferRecordResultVO walletTransferRecordVO = TransferRecordResultVO.builder()
                        .orderId(item.getRefId())
                        .transId(transId)
                        .userAccount(req.getUserId())
                        .venueCode(VenueEnum.SBA.getVenueCode())
                        .orderStatus(SBATransferEnums.PLACE_BET.getCode())
                        .amount(sbToCoinAddVO.getAmount())
                        .transferType(sbToCoinAddVO.getType() ? TransferTypeEnums.IN.getCode() : TransferTypeEnums.OUT.getCode())
                        .build();


                SBResultCode sbResultCode = toCoin(SBActionEnum.PLACE_BET_PARLAY, sbToCoinAddVO, walletTransferRecordVO);
                if (!sbResultCode.getCode().equals(SBResultCode.SUCCESS.getCode())) {
                    result.setStatus(sbResultCode.getCode());
                    return result;
                }
                txns.add(SBPlaceBetRes.builder().refId(item.getRefId()).licenseeTxId(transId).build());
                index++;
            }
            result.setStatus(SBResultCode.SUCCESS.getCode());
            result.setTxns(txns);
        } catch (Exception e) {
            log.error("{},串关下注异常", getAction().getName(), e);
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("{},串关执行结束,删除锁:{}", lock, release);
            }
        }
        return result;
    }
}
