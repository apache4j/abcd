package com.cloud.baowang.report.api.vo.userwinlose;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema( title = "点击会员账号 ResponseVO")
public class ClickUserAccountResponseVO {

    @Schema(title =  "yyyy-MM-dd 00:00:00对应的时间戳")
    private String day;

    @Schema(title =  "日期对应集合")
    private List<ClickUserAccountVO> dayAmount;


}
