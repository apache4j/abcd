package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_one_venue")
public class GameOneVenuePO extends BasePO implements Serializable {


    /**
     * 站点
     */
    private String siteCode;

    /**
     * 一级分类
     */
    private String gameOneId;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 场馆
     */
    private String venueCode;

    /**
     * 排序
     */
    private Integer sort;


}
