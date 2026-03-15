package com.cloud.baowang.user.api.vo.task.guess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "竞赛信息请求对象")
public class GuessDailyReqVO {

    @Schema(title = "站点")
    private String siteCode;

    @Schema(title = "竞赛类型")
    private String guessCategoryCode;

    @Schema(title = "玩家账号")
    private String account;
}
