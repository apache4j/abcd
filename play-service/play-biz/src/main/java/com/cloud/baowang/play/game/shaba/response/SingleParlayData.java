package com.cloud.baowang.play.game.shaba.response;


import lombok.Data;


/**
 * 单场混合过关资料实体类
 */
@Data
public class SingleParlayData {
    /** 下注类型 (英文) */
    private String selection_name;

    /** 下注类型 (简中) */
    private String selection_name_cs;

    /** 注单状态 */
    private String status;
}