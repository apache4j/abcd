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

@Slf4j
@Service
public class SBASettleAction extends SBABase implements SBASportInterface {

    @Autowired
    private SBASettleService sbaSettleService;

    public SBASettleAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.SETTLE;
    }

    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBSettleReq req = JSONObject.parseObject(baseReq.getMessage(), SBSettleReq.class);

        SBResBaseVO result = new SBResBaseVO();
        if (!req.validate()) {
            log.info("{}  缺少参数:{}", getAction().getName(), req);
            result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return result;
        }
        result.setStatus(SBResultCode.SUCCESS.getCode());

        for (SBSettleDetailReq item : req.getTxns()) {
            TransferRecordResultVO transferRecordResultVO = validateOrder(TransferRecordResultVO.getSBARecordId(item.getRefId()));

            if (ObjectUtil.isEmpty(transferRecordResultVO)) {
                log.info("{}  订单不存在:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.NO_SUCH_BET_FOUND.getCode());
                return result;
            }

            if (transferRecordResultVO.getSettleCount() > 3) {
                log.info("{} 订单重试超过3次:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.THE_SYSTEM_IS_BUSY.getCode());
                return result;
            }

            Integer orderStatus = transferRecordResultVO.getOrderStatus();
            if (orderStatus.equals(SBATransferEnums.SETTLE.getCode())) {
                log.info("{} 订单可能被其他线程处理完:{}", getAction().getName(), item);
                continue;
            }


            // confirm 待结算转已结算,UN_SETTLE = 重结算 转结算
            if (!orderStatus.equals(SBATransferEnums.CONFIRM_BET.getCode())
                    && !transferRecordResultVO.getOrderStatus().equals(SBATransferEnums.UN_SETTLE.getCode())) {
                log.info("{} 异常状态订单,该订单未收到确认调用 :{}", getAction().getName(), item);
                result.setStatus(SBResultCode.UNABLE_TO_EXECUTE_PLEASE_TRY_AGAIN_LATER.getCode());
                return result;
            }

            //待结算 变已结算
            if (orderStatus.equals(SBATransferEnums.CONFIRM_BET.getCode())) {
                sbaSettleService.confirmToSettle(getAction(), item, result, transferRecordResultVO);
            } else {
                //重结算转结算
                sbaSettleService.unSettle(SBActionEnum.UN_SETTLE, SBATransferEnums.SETTLE, item, result, transferRecordResultVO);
            }


        }
        return result;
    }


}
