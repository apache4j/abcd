package com.cloud.baowang.play.api.sba.action;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.cloud.baowang.play.api.enums.SBResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.FreezeFlagEnum;
import com.cloud.baowang.wallet.api.enums.wallet.SBATransferEnums;
import com.cloud.baowang.play.api.api.order.PlayServiceApi;
import com.cloud.baowang.play.api.enums.SBActionEnum;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.sba.SBASportInterface;
import com.cloud.baowang.play.api.sba.action.base.SBABaseConfirm;
import com.cloud.baowang.play.api.vo.base.SBResBaseVO;
import com.cloud.baowang.play.api.vo.sba.*;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.config.VenueUserAccountConfig;
import com.cloud.baowang.play.service.TransferRecordService;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SBABetParlayActionConfirm extends SBABaseConfirm implements SBASportInterface {


    public SBABetParlayActionConfirm(TransferRecordService transferRecordService) {
        super(transferRecordService);
    }

    @Override
    public SBActionEnum getAction() {
        return SBActionEnum.CONFIRM_BET_PARLAY;
    }

    @Override
    public SBResBaseVO toAction(SBBaseReq baseReq) {
        SBConfirmBetParlayReq req = JSONObject.parseObject(baseReq.getMessage(), SBConfirmBetParlayReq.class);

        SBConfirmBetRes result = SBConfirmBetRes.builder().account(req.getUserId()).build();
        if (!req.validate()) {
            log.info("{} 确认串关 缺少参数:{}", getAction().getName(), req);
            result.setStatus(SBResultCode.PARAMETER_ERROR.getCode());
            return result;
        }

        UserInfoVO userInfoVOS = getUserInfo(req.getUserId());
        if (ObjectUtils.isEmpty(userInfoVOS)) {
            log.info("{} 确认串关。用户不存在  req:[{}] ", getAction().getName(), req);
            result.setStatus(SBResultCode.ACCOUNT_DOES_NOT_EXIST.getCode());
            return result;
        }

        String userId = userInfoVOS.getUserId();
        result.setStatus(SBResultCode.SUCCESS.getCode());
        for (SBTicketInfoReq item : req.getTxns()) {

            TransferRecordResultVO transferRecordResultVO = validateOrder(TransferRecordResultVO.getSBARecordId(item.getRefId()));
            if (ObjectUtil.isEmpty(transferRecordResultVO)) {
                log.info("{} 订单不存在:{}", getAction().getName(), item);
                result.setStatus(SBResultCode.NO_SUCH_BET_FOUND.getCode());
                return result;
            }


            //也许在其他线程已经把 初始状态变更到 待确认状态了.此处直接返回成功
            if (transferRecordResultVO.getOrderStatus().equals(SBATransferEnums.CONFIRM_BET.getCode())) {
                log.info("{}  订单已被其他线程完毕:{}", getAction().getName(), item);
                continue;
            }

            if (!transferRecordResultVO.getOrderStatus().equals(SBATransferEnums.PLACE_BET.getCode())) {
                log.info("{} 订单状态异常 :{}", getAction().getName(), item);
                result.setStatus(SBResultCode.DUPLICATE_TRANSACTION.getCode());
                return result;
            }
            transferRecordResultVO.setBetId(item.getTxId());

            //冻结支出
            SBToCoinAddVO sbToCoinAddVO = getBuilderCoinAdd(item.getRefId(), userId, item.getCreditAmount(),
                    item.getDebitAmount(), CoinBalanceTypeEnum.EXPENSES, FreezeFlagEnum.UNFREEZE);
            sbToCoinAddVO.setRemark(getAction().getName());
            SBResultCode resultCode = toConfirmToCoin(SBActionEnum.CONFIRM_BET_PARLAY,sbToCoinAddVO, transferRecordResultVO);
            result.setStatus(resultCode.getCode());
            if (!resultCode.getCode().equals(SBResultCode.SUCCESS.getCode())) {
                throw new SBDefaultException(resultCode.getCode());
            }
        }
        return result;
    }
}
