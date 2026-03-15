package com.cloud.baowang.wallet.api;

import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.RechargeTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.wallet.api.api.UserDepositWithDrawStaticReportApi;
import com.cloud.baowang.wallet.api.vo.report.VirtualRechargeRankRespVO;
import com.cloud.baowang.wallet.service.UserDepositStaticReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/8 10:22
 * @Version: V1.0
 **/
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class UserDepositStaticReportApiImpl implements UserDepositWithDrawStaticReportApi {

    private final UserDepositStaticReportService userDepositStaticReportService;

    /**
     * 虚拟币充值 统计 派发勋章
     * 元宇宙大富翁
     * user_deposit_withdrawal status=101 type=1
     * @return
     */
    @Override
    public ResponseVO<Boolean> staticVirtualDepositMedal1016ByMonth(String siteCode,String timeZone) {
        List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList = userDepositStaticReportService.staticByMonth(RechargeTypeEnum.CRYPTO_CURRENCY,siteCode,timeZone);
        //符合要求的直接派发勋章
        MedalAcquireBatchReqVO medalAcquireBatchReqVO=new MedalAcquireBatchReqVO();
        List<MedalAcquireReqVO> medalAcquireReqVOList= Lists.newArrayList();
        for(VirtualRechargeRankRespVO virtualRechargeRankRespVO:virtualRechargeRankRespVOList){
            MedalAcquireReqVO medalAcquireReqVO=new MedalAcquireReqVO();
            medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1016.getCode());
            medalAcquireReqVO.setSiteCode(siteCode);
            medalAcquireReqVO.setUserAccount(virtualRechargeRankRespVO.getUserAccount());
            medalAcquireReqVO.setUserId(virtualRechargeRankRespVO.getUserId());
            medalAcquireReqVOList.add(medalAcquireReqVO);
        }
        medalAcquireBatchReqVO.setSiteCode(siteCode);
        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalAcquireReqVOList);
        log.info("元宇宙大富翁 站点:{}开始派发勋章:{},人数:{}",siteCode,MedalCodeEnum.MEDAL_1016.getName(),medalAcquireReqVOList.size());
        KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE,medalAcquireBatchReqVO);
        return ResponseVO.success(Boolean.TRUE);
    }

    /**
     * 富甲天下
     * user_deposit_withdrawal(type=1and status=101and deposit_withdraw_type_code<>electronic_wallet)
     * user_manual_up_down_record(adjust_way=1and audit_status=3and adjust_type=4)
     * agent_deposit_subordinates
     * @return
     */
    @Override
    public ResponseVO<Boolean> staticVirtualDepositMedal1018ByMonth(String siteCode,String timeZone) {
        List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList =  userDepositStaticReportService.staticNotInByMonth(RechargeTypeEnum.CRYPTO_CURRENCY,siteCode,timeZone);
        //符合要求的直接派发勋章
        MedalAcquireBatchReqVO medalAcquireBatchReqVO=new MedalAcquireBatchReqVO();
        List<MedalAcquireReqVO> medalAcquireReqVOList= Lists.newArrayList();
        for(VirtualRechargeRankRespVO virtualRechargeRankRespVO:virtualRechargeRankRespVOList){
            MedalAcquireReqVO medalAcquireReqVO=new MedalAcquireReqVO();
            medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1018.getCode());
            medalAcquireReqVO.setSiteCode(siteCode);
            medalAcquireReqVO.setUserAccount(virtualRechargeRankRespVO.getUserAccount());
            medalAcquireReqVO.setUserId(virtualRechargeRankRespVO.getUserId());
            medalAcquireReqVOList.add(medalAcquireReqVO);
        }
        medalAcquireBatchReqVO.setSiteCode(siteCode);
        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalAcquireReqVOList);
        log.info("富甲天下 站点:{}开始派发勋章:{},人数:{}",siteCode,MedalCodeEnum.MEDAL_1018.getName(),medalAcquireReqVOList.size());
        KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE,medalAcquireBatchReqVO);
        return ResponseVO.success(Boolean.TRUE);
    }

    /**
     *  MEDAL_1019("1019","大老板","任意单个自然月内总提款金额名次 n",1),
     * @return
     */
    @Override
    public ResponseVO<Boolean> staticVirtualWithdrawMedal1019ByMonth(String siteCode,String timeZone) {
        List<VirtualRechargeRankRespVO> virtualRechargeRankRespVOList = userDepositStaticReportService.staticWithDrawByMonth(siteCode,timeZone);
        //符合要求的直接派发勋章
        MedalAcquireBatchReqVO medalAcquireBatchReqVO=new MedalAcquireBatchReqVO();
        List<MedalAcquireReqVO> medalAcquireReqVOList= Lists.newArrayList();
        for(VirtualRechargeRankRespVO virtualRechargeRankRespVO:virtualRechargeRankRespVOList){
            MedalAcquireReqVO medalAcquireReqVO=new MedalAcquireReqVO();
            medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1019.getCode());
            medalAcquireReqVO.setSiteCode(siteCode);
            medalAcquireReqVO.setUserAccount(virtualRechargeRankRespVO.getUserAccount());
            medalAcquireReqVO.setUserId(virtualRechargeRankRespVO.getUserId());
            medalAcquireReqVOList.add(medalAcquireReqVO);
        }
        medalAcquireBatchReqVO.setSiteCode(siteCode);
        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalAcquireReqVOList);
        log.info("大老板 站点:{}开始派发勋章:{},人数:{}",siteCode,MedalCodeEnum.MEDAL_1019.getName(),medalAcquireReqVOList.size());
        KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE,medalAcquireBatchReqVO);
        return ResponseVO.success(Boolean.TRUE);
    }
}
