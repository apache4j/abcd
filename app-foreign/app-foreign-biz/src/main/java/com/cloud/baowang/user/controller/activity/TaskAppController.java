package com.cloud.baowang.user.controller.activity;

import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.web.bind.annotation.*;

/**
 * @className: SpinWheelController
 * @author: wade
 * @description: 任务
 * @date: 14/9/24 09:16
 */
@Tag(name = "任务客户端-详情")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/task/api")
public class TaskAppController {

    private final TaskConfigApi taskConfigApi;

    @PostMapping("/detail")
    @Operation(summary = "任务详情")
    public ResponseVO<APPTaskResponseVO> detail() {
        APPTaskReqVO reqVO = new APPTaskReqVO();
        reqVO.setUserId(CurrReqUtils.getOneId());
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return taskConfigApi.detail(reqVO);
    }


    @PostMapping("/config")
    @Operation(summary = "任务列表")
    public ResponseVO<APPTaskConfigResponseVO> config() {
        APPTaskReqVO reqVO = new APPTaskReqVO();
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setTimeZone(CurrReqUtils.getTimezone());
        return taskConfigApi.config(reqVO);
    }

    @PostMapping("/receive")
    @Operation(summary = "领取奖励")
    public ResponseVO<TaskReceiveAppResVO> receive(@RequestBody TaskReceiveAppReqVO requestVO) {
        requestVO.setUserId(CurrReqUtils.getOneId());
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        requestVO.setTimeZone(CurrReqUtils.getTimezone());
        return taskConfigApi.receive(requestVO);
    }



}
