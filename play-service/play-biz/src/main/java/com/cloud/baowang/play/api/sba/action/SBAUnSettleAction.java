package com.cloud.baowang.play.api.sba.action;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@Service
public class SBAUnSettleAction extends SBABase implements SBASportInterface {

    @Autowired
    private SBASettleService sbaSettleService;

    public SBAUnSettleAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.UN_SETTLE;
    }

    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBSettleReq req = JSONObject.parseObject(baseReq.getMessage(), SBSettleReq.class);

        SBResBaseVO result = new SBResBaseVO();
        if (!req.validate()) {
            log.info("{} 缺少参数:{}", getAction().getName(), req);
            result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return result;
        }
        result.setStatus(SBResultCode.SUCCESS.getCode());

        for (SBSettleDetailReq item : req.getTxns()) {

            TransferRecordResultVO transferRecordResultVO = validateOrder(TransferRecordResultVO.getSBARecordId(item.getRefId()));

            if (ObjectUtil.isEmpty(transferRecordResultVO)) {
                log.info("{} 订单不存在:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.NO_SUCH_BET_FOUND.getCode());
                return result;
            }

            if (transferRecordResultVO.getSettleCount() > 3) {
                log.info("{} 订单重试超过4次:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.THE_SYSTEM_IS_BUSY.getCode());
                return result;
            }


            Integer orderStatus = transferRecordResultVO.getOrderStatus();

            //重复的交易
            if(SBATransferEnums.UN_SETTLE.getCode().equals(orderStatus)){
                result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                log.info("{} 收到重结算订单状态异常, 重复的交易:{}", getAction().getName(), item);
                return result;
            }

            if (!SBATransferEnums.SETTLE.getCode().equals(orderStatus) && !SBATransferEnums.CONFIRM_BET.getCode().equals(orderStatus)
                    && !SBATransferEnums.RE_SETTLE.getCode().equals(orderStatus)) {
                result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
                log.info("{} 收到重结算订单状态异常, 当前订单状态并不是已结算或者代结算状态:{}", getAction().getName(), item);
                return result;
            }

            //已结算成功的订单 重新进行结算不允许 增加金额
            if (SBATransferEnums.SETTLE.getCode().equals(orderStatus) && item.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.info("{} 已结算成功的订单 进行重结算的场景.只允许扣,不允许加:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
                return result;
            }
            //待结算 变已结算
            if (orderStatus.equals(SBATransferEnums.CONFIRM_BET.getCode())) {
                sbaSettleService.confirmToSettle(getAction(), item, result, transferRecordResultVO);
            } else {
                //已结算 转 重结算
                sbaSettleService.unSettle(getAction(), SBATransferEnums.UN_SETTLE, item, result, transferRecordResultVO);
            }
        }
        return result;
    }


}
