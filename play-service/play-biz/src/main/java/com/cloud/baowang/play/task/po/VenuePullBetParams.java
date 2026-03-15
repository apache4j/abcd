package com.cloud.baowang.play.task.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

/**
 * 拉单参数父类
 *
 * @author: lavine
 * @creat: 2023/9/2 14:13
 */
@Data
public abstract class VenuePullBetParams {
    /**
     * 本次拉单结束时间
     * 仅手动拉单使用
     * 时间戳
     */
    @JsonIgnore
    private String manualCurrentPullEndTime;

    /**
     * false = 手动拉单,true = 自动拉单
     */
    protected Boolean pullType;
}
