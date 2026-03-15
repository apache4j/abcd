package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

@Data
public class PgBaseRes<T> {

    private T data;

    private PgErrorRes error;
}
