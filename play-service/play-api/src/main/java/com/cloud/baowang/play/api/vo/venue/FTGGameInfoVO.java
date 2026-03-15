package com.cloud.baowang.play.api.vo.venue;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FTGGameInfoVO {
    /**
     * 游戏编号
     * 该字段表示游戏的唯一标识符。
     */
    private Integer id;

    /**
     * 游戏大廳编号
     * 参考文档 6.3，表示具体的游戏大廳编号。
     */
    private String lobby_id;

    /**
     * 游戏版本
     * 表示游戏的版本信息。
     */
    private String edition;

    /**
     * 游戏状态
     * 表示游戏是否启用的状态。
     * 1：启用
     * 0：关闭
     */
    private Integer enable;

    /**
     * 游戏所属群组
     * 该字段表示游戏所归属的群组ID。
     */
    private Integer group_id;

    /**
     * 游戏类型
     * 该字段表示游戏的类型（例如：slots, table, fishing, arcade）。
     */
    private String category;

    /**
     * 英文名称
     * 表示游戏的英文名称。
     */
    private String name_en;

    /**
     * 简体中文名称
     * 表示游戏的简体中文名称。
     */
    private String name_cn;

    /**
     * 繁体中文名称
     * 表示游戏的繁体中文名称。
     */
    private String name_zh;

    /**
     * 日文名称
     * 表示游戏的日文名称。
     */
    private String name_ja;

    /**
     * 韩文名称
     * 表示游戏的韩文名称。
     */
    private String name_ko;

    /**
     * 西班牙文名称
     * 表示游戏的西班牙文名称。
     */
    private String name_es;

    /**
     * 印尼文名称
     * 表示游戏的印尼文名称。
     */
    private String name_in;

    /**
     * 葡萄牙文名称
     * 表示游戏的葡萄牙文名称。
     */
    private String name_pt;

    /**
     * 泰文名称
     * 表示游戏的泰文名称。
     */
    private String name_th;

    /**
     * 越南文名称
     * 表示游戏的越南文名称。
     */
    private String name_vi;



}
