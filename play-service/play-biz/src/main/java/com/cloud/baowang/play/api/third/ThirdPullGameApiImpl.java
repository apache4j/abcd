package com.cloud.baowang.play.api.third;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.api.third.ThirdPullBetApi;
import com.cloud.baowang.play.api.api.third.ThirdPullGameApi;
import com.cloud.baowang.play.api.vo.third.GameInfoPullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.GamePullReqVO;
import com.cloud.baowang.play.api.vo.third.betpull.ManualGamePullReqVO;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.game.shaba.SBAGameServiceImpl;
import com.cloud.baowang.play.service.ThirdPullGameInfoService;
import com.cloud.baowang.play.task.pulltask.BasePullBetTask;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class ThirdPullGameApiImpl implements ThirdPullGameApi {

    private final ThirdPullGameInfoService thirdPullGameInfoService;

    @Override
    public void gameInfoPullTask(GameInfoPullReqVO gameInfoPullReqVO) {
        thirdPullGameInfoService.gameInfoPullTask(gameInfoPullReqVO);
    }
}
