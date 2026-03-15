package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

@Data
public class PgCreateRes {

    /**
     * 1:成功 0：失败
     */
    private Integer action_result;

    private Boolean actionResult;

}
