package com.cloud.baowang.play.task.pulltask.cmd.params;


import com.cloud.baowang.play.game.nextSpin.constant.NextSpinConstant;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.Data;

/**
 * 拉单参数
 * @author: mufan
 */
@Data
public class CmdPullBetParams extends VenuePullBetParams {

    /**
     * 查詢日期開始時間，格式為RFC3339
     */
    private String starttime;

    /**
     * 查詢日期開始時間，格式為RFC3339
     */
    private String endtime;

    /**
     * 间隔时间，默认：10分钟
     */
    private Integer timeInterval = NextSpinConstant.DEFAULT_TIME_INTERVAL;
}
