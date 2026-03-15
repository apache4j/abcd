package com.cloud.baowang.play.game.dg2.vo;

import lombok.Data;
import java.util.Map;

@Data
public class BjlResult {
    private int bankerPoint;   // 庄点数
    private int playerPoint;   // 闲点数
    private String size;       // 大小（Big/Small）
    private Map<String, String> poker; // 原始牌数据
}
