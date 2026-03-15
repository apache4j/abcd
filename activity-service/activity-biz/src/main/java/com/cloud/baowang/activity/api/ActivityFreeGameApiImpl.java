package com.cloud.baowang.activity.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityFreeGameApi;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.free.*;
import com.cloud.baowang.activity.service.SiteActivityFreeGameRecordService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


/**
 * {@code @Desciption:} 转盘活动活动相关
 *
 * @Author: Ford
 * @Date: 2024/9/9 21:06
 * @Version: V1.0
 **/
@Slf4j
@RestController
@AllArgsConstructor
public class ActivityFreeGameApiImpl implements ActivityFreeGameApi {

    private final SiteActivityFreeGameRecordService activityFreeGameService;


    @Override
    public ResponseVO<Boolean> updateFreeGameCount(FreeGameRecordReqVO freeGameRecordVO) {
        return ResponseVO.success(activityFreeGameService.updateFreeGameCount(freeGameRecordVO));
    }

    @Override
    public ResponseVO<Integer> getFreeGameCount(String siteCode, String userId, String venueCode) {
        return ResponseVO.success(activityFreeGameService.getFreeGameCount(siteCode, userId, venueCode));
    }

    @Override
    public ResponseVO<Page<ActivityFreeGameRespVO>> freeGamePageList(FreeGameReqVO vo) {
        return ResponseVO.success(activityFreeGameService.freeGamePageList(vo));
    }

    @Override
    public ResponseVO<Long> getTotalCount(FreeGameReqVO vo) {
        return ResponseVO.success(activityFreeGameService.getTotalCount(vo));
    }

    @Override
    public ResponseVO<SiteActivityFreeGameRecordConfigVO> getFreeGameCountAll(String userId) {
        return ResponseVO.success(activityFreeGameService.getFreeGameCountAll(userId));
    }

    @Override
    public ResponseVO<Page<SiteActivityFreeGameConsumeResp>> getFreeGameConsumePageList(FreeGameConsumerReqVO vo) {
        return ResponseVO.success(activityFreeGameService.getFreeGameConsumePageList(vo));
    }

    @Override
    public ResponseVO<?> addFreeGameConfig(FreeGameSubmitModifyVO vo) {
        return activityFreeGameService.addFreeGameConfig(vo);
    }

    @Override
    public ResponseVO<Boolean> freeGameExpire() {
        activityFreeGameService.freeGameExpire();
        return ResponseVO.success(true);
    }

}
