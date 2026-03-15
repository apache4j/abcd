package com.cloud.baowang.play.game.cq9;


import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.vo.venue.VenueInfoRspVO;
import com.cloud.baowang.play.game.base.GameService;
import com.cloud.baowang.play.task.pulltask.cq9.params.CQ9PullBetParams;

/**
 * CQ9平台Api接口类
 */
public interface CQ9GameApiService extends GameService {

    /**
     * 获取平台枚举
     *
     * @return
     */
    VenueEnum platformEnum();

    /**
     * 获取平台名称
     *
     * @return
     */
    String platformName();

    /**
     * 根据拉单参数拉取投注记录列表
     *
     * @param venueInfoVO
     * @param params
     * @return
     */
    ResponseVO<CQ9PullBetParams> getBetRecordListByParams(VenueInfoRspVO venueInfoVO, CQ9PullBetParams params);
}
