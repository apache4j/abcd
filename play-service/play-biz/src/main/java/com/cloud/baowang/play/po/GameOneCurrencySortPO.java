package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_one_currency_sort")
public class GameOneCurrencySortPO extends BasePO implements Serializable {

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 目录排序
     */
    private Integer directorySort;

    /**
     * 首页排序
     */
    private Integer homeSort;

    /**
     * 一级分类
     */
    private String gameOneId;
}
