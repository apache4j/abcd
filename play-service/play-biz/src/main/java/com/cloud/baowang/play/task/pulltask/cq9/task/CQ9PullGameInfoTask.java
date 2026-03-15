package com.cloud.baowang.play.task.pulltask.cq9.task;


import com.cloud.baowang.play.game.cq9.impl.CQ9ApiServiceImpl;
import com.cloud.baowang.play.service.ThirdPullGameInfoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CQ9拉取游戏信息任务
 *
 * @author: lavine
 * @creat: 2023/9/13
 */
@Log4j2
@Component
public class CQ9PullGameInfoTask {

    @Autowired
    private CQ9ApiServiceImpl apiService;

    @Autowired
    private ThirdPullGameInfoService thirdGameInfoService;

    /**
     * 拉取游戏列表
     */
   /* public void pullGameInfo() {
        try {
            VenueInfoRspVO venueInfoVO = ThirdUtil.getVenueInfo(VenueEnum.CQ9.getVenueCode());
            log.info("{} 执行拉取游戏任务!!", venueInfoVO.getVenueName());
            ResponseVO<List<ThirdGameInfoPO>> responseVO = apiService.findGameList(venueInfoVO);

            if (responseVO.isOk()) {
                List<ThirdGameInfoPO> thirdGameInfoList = responseVO.getData();
                // 将三方的游戏信息持久化
                if (CollectionUtils.isNotEmpty(thirdGameInfoList)) {
                    thirdGameInfoService.saveThirdGameInfoList(venueInfoVO.getVenueCode(), thirdGameInfoList);
                    log.warn("{} 执行拉取游戏任务成功, 游戏数量: {}", venueInfoVO.getVenueName(), thirdGameInfoList.size());
                }
            } else {
                log.warn("{} 执行拉取游戏任务失败!!", venueInfoVO.getVenueName());
            }
        } catch (Exception e) {
            log.error(VenueEnum.CQ9.getVenueName() + " + 执行拉取游戏任务异常！", e);
        }
    }*/
}
