package com.cloud.baowang.play.api.third;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.game.shaba.SBAGameServiceImpl;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class ThirdPullBetApiImpl implements ThirdPullBetApi {

    @Autowired
    private Map<String,BasePullBetTask> pullBetTaskMap;

    /**
     * 注单拉取
     * @param gamePullReqVO
     */
    @Override
    public void gamePullTask(@RequestBody @Valid GamePullReqVO gamePullReqVO) {
        BasePullBetTask pullBetTask = pullBetTaskMap.get(gamePullReqVO.getType());
        if(ObjectUtil.isEmpty(pullBetTask)){
            log.error("获取注单服务失败{}",gamePullReqVO);
            return;
        }
        pullBetTask.pullBetRecordTask(gamePullReqVO);
    }

    /**
     * 后台手动注单拉取
     * @param manualGamePullReqVO
     */
    @Override
    public void manualGamePullTask(@RequestBody @Valid ManualGamePullReqVO manualGamePullReqVO) {
        BasePullBetTask pullBetTask = pullBetTaskMap.get(manualGamePullReqVO.getType());
        if(ObjectUtil.isEmpty(pullBetTask)){
            log.error("获取注单服务失败{}",manualGamePullReqVO);
            return;
        }
        pullBetTask.manualPullBetRecordTask(manualGamePullReqVO);
    }


}
