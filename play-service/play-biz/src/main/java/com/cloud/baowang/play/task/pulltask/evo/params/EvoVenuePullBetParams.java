package com.cloud.baowang.play.task.pulltask.evo.params;


import com.cloud.baowang.play.task.po.VenuePullBetParams;
import lombok.Data;

@Data
public class EvoVenuePullBetParams extends VenuePullBetParams {
    /**
     * 自动拉单
     */

    private String versionKey;
    /**
     * 时间戳- 手工拉单
     */
    private String startTime;
    /**
     * 时间戳 - 手工拉单
     */
    private String endTime;

    // 构造方法里设置默认值
    public EvoVenuePullBetParams() {
        this.pullType = true; // 默认自动拉单
    }

}
