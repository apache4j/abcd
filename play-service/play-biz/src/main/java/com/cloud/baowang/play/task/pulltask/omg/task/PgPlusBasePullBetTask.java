package com.cloud.baowang.play.task.pulltask.omg.task;

import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.play.api.constants.ThirdGamePullBetTaskTypeConstant;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component(ThirdGamePullBetTaskTypeConstant.PG_PLUS_GAME_PULL_BET_TASK)
public class PgPlusBasePullBetTask extends OmgBasePullBetTask {

    @Override
    protected String getVenuePlatform() {
        return VenuePlatformConstants.PGPLUS;
    }
}
