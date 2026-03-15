package com.cloud.baowang.play.task.pulltask.cq9.params;


import com.cloud.baowang.play.game.cq9.constant.CQ9Constant;
import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.Data;

/**
 * 开心拉单参数
 *
 * @author: lavine
 * @creat: 2023/9/2 14:54
 */
@Data
public class CQ9PullBetParams extends VenuePullBetParams {

    /**
     * 查詢日期開始時間，格式為RFC3339
     */
    private String starttime;

    /**
     * 查詢日期開始時間，格式為RFC3339
     */
    private String endtime;

    /**
     * 查詢頁數
     */
    private Integer page;

    /**
     * 每頁筆數（預設為 500，最大 20000）
     */
    private Integer pagesize;

    /**
     * 间隔时间，默认：10分钟
     */
    private Integer timeInterval = CQ9Constant.DEFAULT_TIME_INTERVAL;
}
