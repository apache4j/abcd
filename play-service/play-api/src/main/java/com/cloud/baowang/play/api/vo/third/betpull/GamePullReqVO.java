package com.cloud.baowang.play.api.vo.third.betpull;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏注单拉取参数")
public class GamePullReqVO implements Serializable {

    /**
     * 服务类型
     */
    @NotNull
    private String type;

    /**
     * 拉单参数
     */
    private String jsonParam;
}
