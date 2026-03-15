package com.cloud.baowang.play.api.sba.action.base;

import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.wallet.api.enums.wallet.TransferTypeEnums;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.vo.sba.SBToCoinAddVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class SBABaseConfirm extends SBABase {


    public SBABaseConfirm(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }

    /**
     * 收到三方请求,需要将待确认的单处理为已确认需要调用此方法
     */
    protected SBResultCode toConfirmToCoin(SBActionEnum actionEnum, SBToCoinAddVO sbToCoinAddVO, TransferRecordResultVO transferRecordResultVO) {
        String refId = sbToCoinAddVO.getOrderId();
        String userId = sbToCoinAddVO.getUserId();
        BigDecimal debitAmount = sbToCoinAddVO.getSubAmount();
        BigDecimal creditAmount = sbToCoinAddVO.getAddAmount();
        String remark = sbToCoinAddVO.getRemark();

        TransferRecordResultVO transferRecordVO = TransferRecordResultVO.builder()
                .orderId(refId)
                .userAccount(userId)
                .betId(transferRecordResultVO.getBetId())
                .transId(transferRecordResultVO.getTransId())
                .venueCode(VenuePlatformConstants.SBA)
                .orderStatus(SBATransferEnums.CONFIRM_BET.getCode())
                .build();

        BigDecimal amount;
        Integer transferType;

        //如果传入的金额没有值,则代表这笔订单没有出现调整,直接用下注时候的金额去扣款,取 transferRecordResultVO.getAmount() 并且修改转账记录待确认为完成
        if (debitAmount.compareTo(BigDecimal.ZERO) <= 0 && creditAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // 如果传入的金额没有值, 则用下注时候的金额去扣款
            amount = transferRecordResultVO.getAmount();
            transferType = TransferTypeEnums.OUT.getCode();
            sbToCoinAddVO = getBuilderCoinAdd(refId, userId, creditAmount,
                    amount, null, FreezeFlagEnum.UNFREEZE);
        } else {
            //如果传入的金额 creditAmount 有值,则代表 先把第一笔冻结的订单冻结支出, 并且要修改转账记录待确认为完成 . 并且需要加款
            amount = creditAmount;
            transferType = TransferTypeEnums.IN.getCode();
            sbToCoinAddVO = getBuilderCoinAdd(refId, userId, creditAmount,
                    transferRecordResultVO.getAmount(), CoinBalanceTypeEnum.UN_FREEZE, null);
        }

        // 更新转账记录对象
        transferRecordVO.setAmount(amount);
        transferRecordVO.setTransferType(transferType);

        // 更新备注信息
        sbToCoinAddVO.setRemark(remark);

        return toCoin(actionEnum, sbToCoinAddVO, transferRecordVO);
    }






}
