package com.cloud.baowang.user.controller.welfarecenter;

import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.vo.ActivityRewardVO;
import com.cloud.baowang.activity.api.vo.task.TaskReceiveBatchAppReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.WelfareCenterCnApi;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import com.cloud.baowang.user.vo.ReceiveVO;
import com.cloud.baowang.user.vo.enums.BenefitTypeEnum;
import com.cloud.baowang.wallet.api.api.SiteRebateRewardRecordAPI;
import com.cloud.baowang.wallet.api.api.vipV2.VIPAwardRecordV2Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Stream;

@Tag(name = "会员-福利中心V2")
@RestController
@RequestMapping("/user-welfareCenter/v2/api")
@AllArgsConstructor
@Slf4j
public class WelfareCenterV2Controller {
    private final SystemParamApi systemParamApi;
    private final WelfareCenterCnApi welfareCenterCnApi;
    private final TaskConfigApi taskApi;
    private final ActivityParticipateV2Api participateApi;
    private final VIPAwardRecordV2Api vipAwardRecordV2Api;
    private final SiteRebateRewardRecordAPI siteRebateRewardRecordAPI;
//    private final UserTypingAmountApi userTypingAmountApi;


    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<String> param = new ArrayList<>();
        param.add(CommonConstant.ACTIVITY_RECEIVE_STATUS);
        param.add(CommonConstant.WELFARE_CENTER_REWARD_TYPE);
        ResponseVO<Map<String, List<CodeValueVO>>> resp = systemParamApi.getSystemParamsByList(param);
        if (resp.isOk()) {
            Map<String, List<CodeValueVO>> result = new HashMap<>();
            Map<String, List<CodeValueVO>> data = resp.getData();
            result.put("activityReceiveStatus", data.get(CommonConstant.ACTIVITY_RECEIVE_STATUS));
            List<CodeValueVO> codeValueVOS = data.get(CommonConstant.WELFARE_CENTER_REWARD_TYPE);
            Set<String> targetIds = Set.of("0", "7", "1");
            List<CodeValueVO> list = codeValueVOS.stream().filter(codeValueVO -> targetIds.contains(codeValueVO.getCode())).toList();
            result.put("welfareCenterRewardType", list);
            return ResponseVO.success(result);
        }
        throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "福利中心-分页查询")
    public ResponseVO<WelfareCenterRewardResultVO> pageQuery(@RequestBody @Validated WelfareCenterRewardPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        queryVO.setUserAccount(CurrReqUtils.getAccount());
        return welfareCenterCnApi.pageQuery(queryVO);
    }

    @PostMapping("detail")
    @Operation(summary = "查询详情")
    public ResponseVO<WelfareCenterRewardRespVO> detail(@RequestBody ReceiveVO receiveVO) {
        WelfareCenterRewardPageQueryVO queryVO = new WelfareCenterRewardPageQueryVO();
        queryVO.setId(receiveVO.getId());
        queryVO.setWelfareCenterRewardType(receiveVO.getWelfareCenterRewardType());
        queryVO.setUserAccount(CurrReqUtils.getAccount());
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return welfareCenterCnApi.detail(queryVO);
    }

    @PostMapping("clickReceive")
    @Operation(summary = "点击领取")
    public ResponseVO<CodeValueVO> clickReceive(@RequestBody ReceiveVO receiveVO) {
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();

        //勋章相关只展示了已领取的
        boolean falg = false;
        if (BenefitTypeEnum.VIP_BENEFIT.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //vip福利领取
            falg = vipAwardRecordV2Api.receiveActiveAward(receiveVO.getId()).isOk();
        } else if (BenefitTypeEnum.EVENT_DISCOUNT.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //活动优惠
            falg = participateApi.getActivityReward(receiveVO.getId()).isOk();
            //NOTE 先屏蔽掉任务
    /*  } else if (BenefitTypeEnum.TASK_NOVICE_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())
                || BenefitTypeEnum.TASK_DAILY_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())
                || BenefitTypeEnum.TASK_WEEK_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //任务奖励
            TaskReceiveBatchAppReqVO reqVO = new TaskReceiveBatchAppReqVO();
            reqVO.setId(receiveVO.getId());
            reqVO.setSiteCode(siteCode);
            reqVO.setUserId(oneId);
            falg = taskApi.receiveTask(reqVO).isOk();*/
        } else if (BenefitTypeEnum.REBATE.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //反水领取
            falg=   siteRebateRewardRecordAPI.rebateReward(receiveVO.getId()).isOk();
        }
        CodeValueVO codeValueVO = new CodeValueVO();
        if (falg) {
            codeValueVO.setCode(ResultCode.RECEIVE_SUCCESS.getMessageCode());
            codeValueVO.setValue(ResultCode.RECEIVE_SUCCESS.getMessageCode());
            return ResponseVO.success(codeValueVO);
        } else {
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL);
        }
    }

    @GetMapping("oneClickReceive")
    @Operation(summary = "一键领取")
    public ResponseVO<CodeValueVO> oneClickReceive() {
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
        //任务奖励
        TaskReceiveBatchAppReqVO reqVO = new TaskReceiveBatchAppReqVO();
        reqVO.setSiteCode(siteCode);
        reqVO.setUserId(oneId);

        /*log.info("一键领取====》》开始调用任务领取,当前会员id:{},siteCode:{}", oneId, siteCode);
        ResponseVO<Boolean> taskResp = taskApi.receiveTask(reqVO);
        log.info("一键领取====》》调用任务领取完成,当前会员id:{},siteCode:{},结果:{}", oneId, siteCode, taskResp.getData());*/
        //活动
        log.info("一键领取====》》开始调用活动领取,当前会员id:{},siteCode:{}", oneId, siteCode);
        ResponseVO<ActivityRewardVO> activityResp = participateApi.getBatchActivityReward(oneId);
        log.info("一键领取====》》调用活动领取完成,当前会员id:{},siteCode:{},结果:{}", oneId, siteCode, activityResp);
        //vip
        log.info("一键领取====》》开始调用vip领取,当前会员id:{},siteCode:{}", oneId, siteCode);
        ResponseVO<Boolean> vipResp = vipAwardRecordV2Api.receiveUserAward(oneId);
        log.info("一键领取====》》调用vip领取完成,当前会员id:{},siteCode:{},结果:{}", oneId, siteCode, vipResp);
        //返水领取
        log.info("一键领取====》》开始调用返水领取,当前会员id:{},siteCode:{}", oneId, siteCode);
        ResponseVO<Boolean> rebateResp = siteRebateRewardRecordAPI.rebateUserReward(oneId);
        log.info("一键领取====》》调用返水领取完成,当前会员id:{},siteCode:{},结果:{}", oneId, siteCode, rebateResp);
        CodeValueVO codeValueVO = new CodeValueVO();
        ResultCode applySuccess = ResultCode.RECEIVE_SUCCESS;
        codeValueVO.setCode(applySuccess.getMessageCode());
        codeValueVO.setValue(applySuccess.getMessageCode());
        return ResponseVO.success(codeValueVO);
    }

}
