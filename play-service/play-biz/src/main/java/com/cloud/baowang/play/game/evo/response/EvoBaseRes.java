package com.cloud.baowang.play.game.evo.response;

import com.cloud.baowang.play.game.pg.response.PgErrorRes;
import lombok.Data;

@Data
public class EvoBaseRes<T> {

    private T data;

    private PgErrorRes error;
}
