package com.cloud.baowang.play.api.sba.action;

import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.wallet.api.enums.wallet.TransferTypeEnums;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.sba.action.base.SBABase;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.SBSettleDetailReq;
import com.cloud.baowang.play.api.vo.sba.SBToCoinAddVO;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SBASettleService extends SBABase {

    @Autowired
    private TransferRecordService transferRecordService;

    public SBASettleService(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    /**
     * 已结算 转 重结算
     */
    public void unSettle(SBActionEnum actionEnum, SBATransferEnums sbaTransferEnums, SBSettleDetailReq item,
                         SBResBaseVO result, TransferRecordResultVO transferRecordResultVO) {

        //金额都是0的场景下不需要扣费。直接将单改成处理完成
        if (item.validAmount()) {
            TransferRecordResultVO transferRecordPO = TransferRecordResultVO.builder()
                    .orderId(item.getRefId())
                    .orderStatus(sbaTransferEnums.getCode())
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .build();
            transferRecordService.updateRecordStatus(transferRecordPO);
            return;
        }

        SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(item.getRefId(), item.getUserId(), item.getCreditAmount(),
                item.getDebitAmount(), null, null);
        TransferRecordResultVO walletTransferRecordVO = TransferRecordResultVO.builder()
                .orderId(item.getRefId())
                .betId(transferRecordResultVO.getBetId())
                .transId(transferRecordResultVO.getTransId())
                .userAccount(item.getUserId())
                .venueCode(VenueEnum.SBA.getVenueCode())
                .orderStatus(sbaTransferEnums.getCode())
                .amount(sbToCoinAddVO.getAmount())
                .transferType(sbToCoinAddVO.getType() ? TransferTypeEnums.IN.getCode() : TransferTypeEnums.OUT.getCode())
                .remark(actionEnum.getName())
                .settleCount(transferRecordResultVO.getSettleCount())
                .build();
        sbToCoinAddVO.setRemark(actionEnum.getName());
        SBResultCode sbResultCode = toCoin(actionEnum, sbToCoinAddVO, walletTransferRecordVO);
        result.setStatus(sbResultCode.getCode());
        if (!sbResultCode.getCode().equals(SBResultCode.SUCCESS.getCode())) {
            throw new SBDefaultException(sbResultCode.getCode());
        }
    }

    /**
     * 待结算转已结算
     */
    public void confirmToSettle(SBActionEnum actionEnum, SBSettleDetailReq item, SBResBaseVO result, TransferRecordResultVO transferRecordResultVO) {

        SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(item.getRefId(), item.getUserId(), item.getCreditAmount(),
                item.getDebitAmount(), null, null);

        //金额都是0的场景下不需要扣费。直接将单改成处理完成
        if (item.validAmount()) {
            TransferRecordResultVO transferRecordPO = TransferRecordResultVO.builder()
                    .orderId(item.getRefId())
                    .orderStatus(SBATransferEnums.SETTLE.getCode())
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .build();
            transferRecordService.updateRecordStatus(transferRecordPO);
            return;
        }

        TransferRecordResultVO walletTransferRecordVO = TransferRecordResultVO.builder()
                .orderId(item.getRefId())
                .userAccount(item.getUserId())
                .venueCode(VenueEnum.SBA.getVenueCode())
                .orderStatus(SBATransferEnums.SETTLE.getCode())
                .amount(sbToCoinAddVO.getAmount())
                .transferType(sbToCoinAddVO.getType() ? TransferTypeEnums.IN.getCode() : TransferTypeEnums.OUT.getCode())
                .transId(transferRecordResultVO.getTransId())
                .settleCount(transferRecordResultVO.getSettleCount())
                .build();
        sbToCoinAddVO.setRemark(actionEnum.getName());
        SBResultCode sbResultCode = toCoin(SBActionEnum.SETTLE, sbToCoinAddVO, walletTransferRecordVO);
        result.setStatus(sbResultCode.getCode());
        if (!sbResultCode.getCode().equals(SBResultCode.SUCCESS.getCode())) {
            throw new SBDefaultException(sbResultCode.getCode());
        }
    }


}
