package com.cloud.baowang.play.api.vo.dbDj.heroSummoning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHero {

    private String id;        // 英雄id
    private String name;      // 英雄名
    private Integer gender;   // 性别
    private String clazz;     // 职业（避免和 Java class 冲突，这里用 clazz）
    private Integer hit;      // 战力
    private String attack_range; // 攻击距离

}
