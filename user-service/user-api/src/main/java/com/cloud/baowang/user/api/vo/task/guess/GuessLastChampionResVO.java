package com.cloud.baowang.user.api.vo.task.guess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "上届冠军响应")
public class GuessLastChampionResVO {

    @Schema(title = "用户头像")
    private String accountPicture;

    @Schema(title = "账号")
    private String account;

    @Schema(title = "比赛奖池")
    private BigDecimal prizePool;

    @Schema(title = "奖池比例")
    private BigDecimal prizePoolRate;

}
