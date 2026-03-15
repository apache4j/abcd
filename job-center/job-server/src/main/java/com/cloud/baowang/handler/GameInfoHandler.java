package com.cloud.baowang.handler;

import com.cloud.baowang.context.XxlJobHelper;
import com.cloud.baowang.handler.annotation.XxlJob;
import com.cloud.baowang.play.api.api.third.ThirdPullGameApi;
import com.cloud.baowang.play.api.vo.third.GameInfoPullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
@AllArgsConstructor
public class GameInfoHandler {

    private final ThirdPullGameApi thirdPullGameApi;
    /**
     * 三方游戏同步
     */
    @XxlJob(value = "thirdGameInfoTask")
    public void thirdGameInfoTask() {
        // param parse
        GameInfoPullReqVO gamePullReqVO = new GameInfoPullReqVO();
        gamePullReqVO.setParam(XxlJobHelper.getJobParam());
        log.info("执行三方游戏同步:{}",gamePullReqVO);
//        thirdPullGameApi.gameInfoPullTask(gamePullReqVO);
    }


}
