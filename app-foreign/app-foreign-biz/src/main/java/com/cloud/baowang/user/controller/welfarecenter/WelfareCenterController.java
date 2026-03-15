package com.cloud.baowang.user.controller.welfarecenter;

import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.vo.ActivityRewardVO;
import com.cloud.baowang.activity.api.vo.task.TaskReceiveBatchAppReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.api.WelfareCenterApi;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardResultVO;
import com.cloud.baowang.user.vo.ReceiveVO;
import com.cloud.baowang.user.vo.enums.BenefitTypeEnum;
import com.cloud.baowang.wallet.api.api.SiteRebateRewardRecordAPI;
import com.cloud.baowang.wallet.api.api.UserTypingAmountApi;
import com.cloud.baowang.wallet.api.api.VIPAwardRecordApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "会员-福利中心")
@RestController
@RequestMapping("/user-welfareCenter/api")
@AllArgsConstructor
@Slf4j
public class WelfareCenterController {
    private final SystemParamApi systemParamApi;
    private final WelfareCenterApi welfareCenterApi;
    private final TaskConfigApi taskApi;
    private final ActivityParticipateApi participateApi;
    private final VIPAwardRecordApi vipAwardRecordApi;
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
            result.put("welfareCenterRewardType", data.get(CommonConstant.WELFARE_CENTER_REWARD_TYPE));
            return ResponseVO.success(result);
        }
        throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
    }

    @PostMapping("pageQuery")
    @Operation(summary = "福利中心-分页查询")
    public ResponseVO<WelfareCenterRewardResultVO> pageQuery(@RequestBody @Validated WelfareCenterRewardPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        queryVO.setUserAccount(CurrReqUtils.getAccount());
        return welfareCenterApi.pageQuery(queryVO);
    }

    @PostMapping("detail")
    @Operation(summary = "查询详情")
    public ResponseVO<WelfareCenterRewardRespVO> detail(@RequestBody ReceiveVO receiveVO) {
        WelfareCenterRewardPageQueryVO queryVO = new WelfareCenterRewardPageQueryVO();
        queryVO.setId(receiveVO.getId());
        queryVO.setWelfareCenterRewardType(receiveVO.getWelfareCenterRewardType());
        queryVO.setUserAccount(CurrReqUtils.getAccount());
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return welfareCenterApi.detail(queryVO);
    }

    @PostMapping("clickReceive")
    @Operation(summary = "点击领取")
    public ResponseVO<CodeValueVO> clickReceive(@RequestBody ReceiveVO receiveVO) {
        String oneId = CurrReqUtils.getOneId();
        String siteCode = CurrReqUtils.getSiteCode();
//        //清除打码量
//        userTypingAmountApi.userTypingAmountCleanZeroByUserId(oneId);
        //勋章相关只展示了已领取的
        boolean falg=false;
        if (BenefitTypeEnum.VIP_BENEFIT.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //vip福利领取
            falg=  vipAwardRecordApi.receiveActiveAward(receiveVO.getId()).isOk();
        } else if (BenefitTypeEnum.EVENT_DISCOUNT.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //活动优惠
            falg= participateApi.getActivityReward(receiveVO.getId()).isOk();
        } else if (BenefitTypeEnum.TASK_NOVICE_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())
                || BenefitTypeEnum.TASK_DAILY_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())
                || BenefitTypeEnum.TASK_WEEK_REWARD.getCode().equals(receiveVO.getWelfareCenterRewardType())) {
            //任务奖励
            TaskReceiveBatchAppReqVO reqVO = new TaskReceiveBatchAppReqVO();
            reqVO.setId(receiveVO.getId());
            reqVO.setSiteCode(siteCode);
            reqVO.setUserId(oneId);
            falg=  taskApi.receiveTask(reqVO).isOk();
        }
        //返水领取 海外盘不用返水现在取消了 只有华人盘使用
//        else if(BenefitTypeEnum.REBATE.getCode().equals(receiveVO.getWelfareCenterRewardType())){
//          //反水领取
//            falg=   siteRebateRewardRecordAPI.rebateReward(receiveVO.getId()).isOk();
//        }
        CodeValueVO codeValueVO = new CodeValueVO();
        ResultCode resultCode =null;
        if (falg){
             resultCode = ResultCode.RECEIVE_SUCCESS;
        }else{
            resultCode = ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL;
        }
        codeValueVO.setCode(resultCode.getMessageCode());
        codeValueVO.setValue(resultCode.getMessageCode());
        if (falg){
            return ResponseVO.success(codeValueVO);
        }else{
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

//        //清除打码量
//        userTypingAmountApi.userTypingAmountCleanZeroByUserId(oneId);

        log.info("一键领取====》》开始调用任务领取,当前会员id:{},siteCode:{}",oneId,siteCode);
        ResponseVO<Boolean> taskResp = taskApi.receiveTask(reqVO);
        log.info("一键领取====》》调用任务领取完成,当前会员id:{},siteCode:{},结果:{}",oneId,siteCode,taskResp.getData());
        //活动
        log.info("一键领取====》》开始调用活动领取,当前会员id:{},siteCode:{}",oneId,siteCode);
        ResponseVO<ActivityRewardVO> activityResp = participateApi.getBatchActivityReward(oneId);
        log.info("一键领取====》》调用活动领取完成,当前会员id:{},siteCode:{},结果:{}",oneId,siteCode,activityResp.getData());
        //vip
        log.info("一键领取====》》开始调用vip领取,当前会员id:{},siteCode:{}",oneId,siteCode);
        ResponseVO<Boolean> vipResp = vipAwardRecordApi.receiveUserAward(oneId);
        log.info("一键领取====》》调用vip领取完成,当前会员id:{},siteCode:{},结果:{}",oneId,siteCode,vipResp.getData());
        //返水领取 海外盘不用返水现在取消了 只有华人盘使用
        log.info("一键领取====》》开始调用返水领取,当前会员id:{},siteCode:{}",oneId,siteCode);
        ResponseVO<Boolean> rebateResp = siteRebateRewardRecordAPI.rebateUserReward(oneId);
        log.info("一键领取====》》调用返水领取完成,当前会员id:{},siteCode:{},结果:{}",oneId,siteCode,rebateResp.getData());
        CodeValueVO codeValueVO = new CodeValueVO();
        ResultCode applySuccess = ResultCode.RECEIVE_SUCCESS;
        codeValueVO.setCode(applySuccess.getMessageCode());
        codeValueVO.setValue(applySuccess.getMessageCode());
        return ResponseVO.success(codeValueVO);
    }

}
