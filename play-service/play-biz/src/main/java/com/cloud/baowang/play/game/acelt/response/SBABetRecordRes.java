package com.cloud.baowang.play.game.acelt.response;

import com.cloud.baowang.play.game.shaba.SBABetRecordData;
import lombok.Data;

/**
 * <h2></h2>
 *
 */
@Data
public class SBABetRecordRes {
    /**
     * 响应码
     */
    private Integer error_code;

    /**
     * 响应消息内容
     */
    private String message;

    /**
     * 返回对象
     */
    private SBABetRecordData Data;



}
