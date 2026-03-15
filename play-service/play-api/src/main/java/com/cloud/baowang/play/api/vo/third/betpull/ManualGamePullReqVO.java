package com.cloud.baowang.play.api.vo.third.betpull;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : 小智
 * @Date : 27/5/23 6:36 PM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "手动游戏注单拉取参数")
public class ManualGamePullReqVO implements Serializable {

    /**
     * 服务类型
     */
    @NotNull
    private String type;

    /**
     * 拉单开始时间 时间戳
     */
    @NotNull
    private String startTime ;

    /**
     * 拉单结束时间 时间戳
     */
    @NotNull
    private String endTime ;
}
