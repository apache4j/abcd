package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: sheldon
 * @Date: 3/29/24 5:29 下午
 */

@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "侧边栏-活动入口开关")
public class LobbyLabelActivitySwitchResVO {

    @Schema(description = "  返回的活动模板代表开启.活动模板:  " +
            "RED_BAG_RAIN:红包雨 " +
            "    FIRST_DEPOSIT,首存活动 " +
            "    SECOND_DEPOSIT,次存活动 " +
            "    FREE_WHEEL,免费旋转 " +
            "    ASSIGN_DAY,指定日期存款 " +
            "    LOSS_IN_SPORTS,体育负盈利 " +
            "    DAILY_COMPETITION,每日竞赛 " +
            "    SPIN_WHEEL,转盘")
    private List<String> activityTemplate;


}
