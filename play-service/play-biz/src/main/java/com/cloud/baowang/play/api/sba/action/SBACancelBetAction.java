package com.cloud.baowang.play.api.sba.action;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.enums.sb.SBErrorCodeEnum;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.sba.SBASportInterface;
import com.cloud.baowang.play.api.sba.action.base.SBABase;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.*;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.wallet.api.enums.wallet.TransferTypeEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class SBACancelBetAction extends SBABase implements SBASportInterface {


    public SBACancelBetAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }

    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.CANCEL_BET;
    }

    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBCancelBetReq req = JSONObject.parseObject(baseReq.getMessage(), SBCancelBetReq.class);
        SBConfirmBetRes result = SBConfirmBetRes.builder().account(req.getUserId()).build();

        if (!req.validate()) {
            log.info("{} 上下分,取消下分 缺少参数:{}", getAction().getName(), req);
            result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return result;
        }
        result.setStatus(SBResultCode.SUCCESS.getCode());

        for (TxnsReq item : req.getTxns()) {

            TransferRecordResultVO transferRecordResultVO = validateOrder(TransferRecordResultVO.getSBARecordId(item.getRefId()));

            //如果出现订单不存在直接返回给沙巴成功.因为不存在的订单是下注失败了.我们也没扣钱.所以请求我们取消的时候直接告诉沙巴取消成功
            if (ObjectUtil.isEmpty(transferRecordResultVO)) {
                log.info("{} 上下分,订单不存在:{}", getAction().getName(), item);
                continue;
            }

            if (SBATransferEnums.CANCEL_BET.getCode().equals(transferRecordResultVO.getOrderStatus())) {
                log.info("{} 上下分, 订单已被其他线程完毕:{}", getAction().getName(), item);
                return result;
            }

            //此次返回无法执行,让三方过会重试
            if (!SBATransferEnums.PLACE_BET.getCode().equals(transferRecordResultVO.getOrderStatus())) {
                log.info("{} 上下分, 订单状态异常,{}", getAction().getName(), item);
                result.setStatus(SBResultCode.UNABLE_TO_EXECUTE_PLEASE_TRY_AGAIN_LATER.getCode());
                return result;
            }

            //解冻
            SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(item.getRefId(), req.getUserId(), item.getCreditAmount(),
                    item.getDebitAmount(), CoinBalanceTypeEnum.UN_FREEZE, null);
            sbToCoinAddVO.setRemark(getAction().getName());

            TransferRecordResultVO walletTransferRecordVO = TransferRecordResultVO.builder()
                    .orderId(item.getRefId())
                    .userAccount(req.getUserId())
                    .venueCode(VenueEnum.SBA.getVenueCode())
                    .orderStatus(SBATransferEnums.CANCEL_BET.getCode())
                    .amount(sbToCoinAddVO.getAmount())
                    .transferType(TransferTypeEnums.IN.getCode())
                    .remark(SBActionEnum.CANCEL_BET.getName() + "-" + SBErrorCodeEnum.of(req.getErrorMessage()))
                    .transId(transferRecordResultVO.getTransId())
                    .build();

            SBResultCode sbResultCode = toCoin(SBActionEnum.CANCEL_BET, sbToCoinAddVO, walletTransferRecordVO);
            result.setStatus(sbResultCode.getCode());
            if (!sbResultCode.getCode().equals(SBResultCode.SUCCESS.getCode())) {
                throw new SBDefaultException(sbResultCode.getCode());
            }
        }
        return result;
    }

}
