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
public class SBAReSettleAction extends SBABase implements SBASportInterface {

    @Autowired
    private SBASettleService sbaSettleService;

    public SBAReSettleAction(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }


    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.RE_SETTLE;
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

            //沙巴说重结算的概率很低.为防止出现意外 我方限制重结算不可超过3次
            if (transferRecordResultVO.getSettleCount() > 3) {
                log.info("{} 订单重结算超出最高次数:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.THE_SYSTEM_IS_BUSY.getCode());
                return result;
            }

            if (transferRecordResultVO.getOrderStatus().equals(SBATransferEnums.RE_SETTLE.getCode())) {
                log.info("{} 异常,该订单现处于重新结算状态,不能进行重新结算:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return result;
            }
            //重新结算该订单必须是已结算
            if (!transferRecordResultVO.getOrderStatus().equals(SBATransferEnums.SETTLE.getCode())) {
                log.info("{} 异常,该单还未变成结算状态,不能进如重新结算:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.UNABLE_TO_EXECUTE_PLEASE_TRY_AGAIN_LATER.getCode());
                return result;
            }
            sbaSettleService.unSettle(getAction(), SBATransferEnums.RE_SETTLE, item, result, transferRecordResultVO);
        }
        return result;
    }


}
