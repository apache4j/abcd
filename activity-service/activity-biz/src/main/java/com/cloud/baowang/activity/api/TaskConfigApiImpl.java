package com.cloud.baowang.activity.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameTriggerVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameVO;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.api.vo.test.PPFreeGameRecordReqVOTest;
import com.cloud.baowang.activity.api.vo.test.UserVenueWinLossSendVOTest;
import com.cloud.baowang.activity.api.vo.test.UserWinLoseMqVOTest;
import com.cloud.baowang.activity.service.SiteTaskConfigService;
import com.cloud.baowang.activity.service.SiteTaskFlashCardBaseService;
import com.cloud.baowang.activity.service.SiteTaskOrderRecordService;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.kafka.vo.UserWinLoseMqVO;
import com.cloud.baowang.play.api.vo.mq.PPFreeGameRecordReqVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawTriggerVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@AllArgsConstructor
public class TaskConfigApiImpl implements TaskConfigApi {

    private final SiteTaskConfigService siteTaskConfigService;

    private final SiteTaskFlashCardBaseService siteTaskFlashCardBaseService;

    private final SiteTaskOrderRecordService siteTaskOrderRecordService;
    private final UserInfoApi userInfoApi;

    @Override
    public ResponseVO<List<SiteTaskConfigResVO>> taskList(TaskConfigReqVO taskConfigReqVO) {
        return ResponseVO.success(siteTaskConfigService.taskConfigList(taskConfigReqVO));
    }

    @Override
    public ResponseVO<SiteTaskOverViewConfigResVO> taskOverViewConfig(TaskConfigOverViewReqVO taskConfigReqVO) {
        return ResponseVO.success(siteTaskConfigService.taskOverViewConfig(taskConfigReqVO));
    }

    @Override
    public ResponseVO<Boolean> updateTaskOverViewConfig(TaskConfigOverViewReqVO taskConfigReqVO) {
        return ResponseVO.success(siteTaskConfigService.updateTaskOverViewConfig(taskConfigReqVO));
    }

    @Override
    public ResponseVO<SiteTaskConfigResVO> taskDetail(TaskConfigDetailReqVO reqVO) {
        return ResponseVO.success(siteTaskConfigService.taskDetail(reqVO));
    }

    @Override
    public List<ReportSiteTaskConfigResVO> taskAllList(TaskConfigReqVO taskConfigReqVO) {
        return siteTaskConfigService.taskAllList(taskConfigReqVO);
    }

    @Override
    public List<String> taskAllListByTaskName(ReportTaskConfigReqVO vo) {
        return siteTaskConfigService.taskAllListByTaskName(vo);
    }

    @Override
    public ResponseVO<Void> save(SiteTaskConfigReqVO vo) {
        siteTaskConfigService.save(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> saveTaskFlashCard(SiteTaskFlashCardSaveVO vo) {
        siteTaskFlashCardBaseService.saveTaskFlashCard(vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Boolean> operateStatus(SiteTaskOnOffVO reqVO) {
        return siteTaskConfigService.operateStatus(reqVO);
    }

    @Override
    public ResponseVO<List<SiteTaskConfigSortRespVO>> getTaskTabSort(String siteCode, String taskType) {
        return ResponseVO.success(siteTaskConfigService.getTaskTabSort(siteCode, taskType));
    }

    @Override
    public ResponseVO<Boolean> taskTabSort(SiteTasKConfigSortReqVO reqVO) {
        return ResponseVO.success(siteTaskConfigService.taskTabSort(reqVO));
    }

    @Override
    public ResponseVO<Page<SiteTaskOrderRecordResVO>> recordPageList(SiteTaskOrderRecordReqVO reqVO) {
        return ResponseVO.success(siteTaskOrderRecordService.recordPageList(reqVO));
    }

    @Override
    public Long getTotalCount(SiteTaskOrderRecordReqVO reqVO) {
        return siteTaskOrderRecordService.getTotalCount(reqVO);
    }

    @Override
    public ResponseVO<APPTaskResponseVO> detail(APPTaskReqVO reqVO) {
        return siteTaskOrderRecordService.detail(reqVO);
    }

    @Override
    public ResponseVO<APPTaskConfigResponseVO> config(APPTaskReqVO reqVO) {
        return siteTaskOrderRecordService.config(reqVO);
    }

    @Override
    public ResponseVO<TaskReceiveAppResVO> receive(TaskReceiveAppReqVO requestVO) {
        return siteTaskOrderRecordService.receive(requestVO);
    }

    @Override
    public ResponseVO<Boolean> receiveTask(TaskReceiveBatchAppReqVO requestVO) {
        return siteTaskOrderRecordService.receiveTask(requestVO);
    }

    @Override
    public ResponseVO<Void> taskAwardExpire() {
        siteTaskOrderRecordService.taskAwardExpire();
        return ResponseVO.success();
    }

    @Override
    public void processSendMealJob(String siteCode) {
        siteTaskOrderRecordService.processSendMealJob(siteCode);
    }

    @Override
    public ResponseVO<Void> updateEffective() {
        siteTaskConfigService.updateEffective();
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> test(String userId) {
        UserInfoVO userInfo = userInfoApi.getByUserId(userId);
        /*
        UserVenueWinLossSendVO requestVO = new UserVenueWinLossSendVO();
        requestVO.setSiteCode(userInfo.getSiteCode());
        UserVenueWinLossMqVO userVenueWinLossMqVO = new UserVenueWinLossMqVO();
        userVenueWinLossMqVO.setUserId(userId);
        userVenueWinLossMqVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setVoList(Arrays.asList(userVenueWinLossMqVO));
        */
        //siteTaskOrderRecordService.processDailyAndWeek(requestVO);
        //activitySpinWheelService.processBetAward(requestVO);
        //
        /*TaskNoviceTriggerVO triggerVO = new TaskNoviceTriggerVO();
        triggerVO.setUserId(userId);
        triggerVO.setVipGradeCode(userInfo.getVipGradeCode());
        triggerVO.setVipRankCode(userInfo.getVipRank());
        triggerVO.setSiteCode(userInfo.getSiteCode());
        triggerVO.setUserName(userInfo.getUserName());
        triggerVO.setSuperAgentId(userInfo.getSuperAgentId());
        triggerVO.setUserAccount(userInfo.getUserAccount());
        triggerVO.setSubTaskTypes(Arrays.asList(TaskEnum.NOVICE_WELCOME.getSubTaskType(), TaskEnum.NOVICE_PHONE.getSubTaskType(),
                TaskEnum.NOVICE_CURRENCY.getSubTaskType(), TaskEnum.NOVICE_EMAIL.getSubTaskType()));
        siteTaskOrderRecordService.process(triggerVO);*/
        /*
        // 触发存款。
        RechargeTriggerVO rechargeTriggerVO = new RechargeTriggerVO();
        rechargeTriggerVO.setRechargeTime(System.currentTimeMillis());
        rechargeTriggerVO.setRechargeAmount(new BigDecimal("1105"));
        rechargeTriggerVO.setUserAccount(userInfo.getUserAccount());
        rechargeTriggerVO.setSiteCode(userInfo.getSiteCode());
        rechargeTriggerVO.setCurrencyCode(userInfo.getMainCurrency());
        rechargeTriggerVO.setUserId(userId);
        rechargeTriggerVO.setOrderNumber("or" + System.currentTimeMillis());
        //NOTE 0 首充, 1 是次充
        rechargeTriggerVO.setDepositType(0);
        KafkaUtil.send(TopicsConstants.MEMBER_RECHARGE, rechargeTriggerVO);
        */
        //会员提现
        /*UserWithdrawTriggerVO withdrawTriggerVO = new UserWithdrawTriggerVO();
        withdrawTriggerVO.setWithdrawAmount(BigDecimal.valueOf(1000));
        withdrawTriggerVO.setUserId(userId);
        withdrawTriggerVO.setUserAccount(userInfo.getUserAccount());
        withdrawTriggerVO.setMsgId(StrUtil.uuid());
        withdrawTriggerVO.setSiteCode(userInfo.getSiteCode());
        withdrawTriggerVO.setRegisterTime(userInfo.getRegisterTime());
        withdrawTriggerVO.setOrderNumber(StrUtil.uuid());
        withdrawTriggerVO.setWithdrawTime(System.currentTimeMillis());
        withdrawTriggerVO.setTraceId(StrUtil.uuid());
        withdrawTriggerVO.setCurrencyCode(userInfo.getMainCurrency());*/

        UserWithdrawTriggerVO withdrawTriggerVO = new UserWithdrawTriggerVO();
        withdrawTriggerVO.setWithdrawTime(1L);
        withdrawTriggerVO.setWithdrawAmount(BigDecimal.ONE);
        withdrawTriggerVO.setUserAccount(userInfo.getUserAccount());
        withdrawTriggerVO.setSiteCode(userInfo.getSiteCode());
        withdrawTriggerVO.setCurrencyCode(userInfo.getMainCurrency());
        withdrawTriggerVO.setUserId(userInfo.getUserId());
        withdrawTriggerVO.setOrderNumber(StrUtil.uuid());
        withdrawTriggerVO.setRegisterTime(userInfo.getRegisterTime());


        KafkaUtil.send(TopicsConstants.MEMBER_WITHDRAW,withdrawTriggerVO);

        return ResponseVO.success();
    }


    @Override
    public ResponseVO<Void> testKafka(UserWinLoseMqVOTest vo) {
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        BeanUtils.copyProperties(vo, userWinLoseMqVO);
        List<UserWinLoseMqVO> orderList = ConvertUtil.entityListToModelList(vo.getOrderList(), UserWinLoseMqVO.class);
        userWinLoseMqVO.setOrderList(orderList);
        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
        return null;
    }

    @Override
    public ResponseVO<Void> testKafkaPP(PPFreeGameRecordReqVOTest vo) {
        PPFreeGameRecordReqVO sendVO = new PPFreeGameRecordReqVO();

        BeanUtils.copyProperties(vo, sendVO);
        KafkaUtil.send(TopicsConstants.FREE_GAME_RECORD_CONSUME, sendVO);
        return null;
    }

    @Override
    public ResponseVO<Void> testKafkaPPActivity(ActivityFreeGameVO vo) {
        ActivityFreeGameTriggerVO activityFreeGameTriggerVO = new ActivityFreeGameTriggerVO();
        activityFreeGameTriggerVO.setFreeGameVOList(Collections.singletonList(vo));
        KafkaUtil.send(TopicsConstants.FREE_GAME, activityFreeGameTriggerVO);
        return null;
    }

    @Override
    public ResponseVO<Void> taskKafka(UserVenueWinLossSendVOTest vo) {
        UserVenueWinLossSendVO userVenueWinLossSendVO = new UserVenueWinLossSendVO();
        List<UserVenueWinLossMqVO> voList = ConvertUtil.entityListToModelList(vo.getVoList(), UserVenueWinLossMqVO.class);
        userVenueWinLossSendVO.setVoList(voList);
        KafkaUtil.send(TopicsConstants.TASK_DAILY_WEEK_ORDER_RECORD_TOPIC, userVenueWinLossSendVO);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<SiteTaskFlashCardBaseRespVO> queryFlashCard(SiteTaskFlashCardBaseReqVO vo) {
        return ResponseVO.success(siteTaskFlashCardBaseService.queryFlashCard(vo));
    }

    @Override
    public ResponseVO<Void> updateTaskFlashCardStatus(SiteTaskFlashCardStatusVO vo) {
        siteTaskFlashCardBaseService.updateTaskFlashCardStatus(vo);
        return ResponseVO.success();
    }


}
