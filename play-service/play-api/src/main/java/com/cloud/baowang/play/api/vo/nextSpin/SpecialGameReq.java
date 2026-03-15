package com.cloud.baowang.play.api.vo.nextSpin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialGameReq implements Serializable {
    // 分辨特别游戏种，例子: bonus, free, bonusfree, freebonus
    private String type;
    // 玩家所得特别游戏的总数量
    private Integer count;
    // 	玩家已完成的特别游戏数量
    private Integer sequence;

}
