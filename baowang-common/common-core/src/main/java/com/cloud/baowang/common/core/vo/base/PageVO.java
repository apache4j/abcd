package com.cloud.baowang.common.core.vo.base;


import cn.hutool.core.collection.CollectionUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kimi
 */
@Data
@Schema(title = "后台-分页公共对象")
public class PageVO implements Serializable {

    /* 升降排序-start */
    @Schema(title = "升降排序-字段")
    private String orderField;

    @Schema(title = "升降排序-方式 asc/desc")
    private String orderType;
    /* 升降排序-end */

    /* 状态排序-start */
    @Schema(title = "状态排序-字段")
    private String orderName;

    @Schema(title = "状态排序-传递值")
    private String orderValue;
    /* 状态排序-end */

    @Schema(title = "当前页(默认第1页)", example = "1")
    private Integer pageNumber = 1;

    @Schema(title = "每页条数(默认10条)", example = "10")
    private Integer pageSize = 10;

    @Schema(title = "导出Excel需要的字段")
    List<String> includeColumnList;

    public List<String> getIncludeColumnList() {
        if (CollectionUtil.isEmpty(includeColumnList)) {
            return null;
        }
        List<String> includeColumnListStr = new ArrayList<>();
        for (String str : includeColumnList) {
            if (str.endsWith("Time") || str.endsWith("time")||str.endsWith("$Arr")) {
                includeColumnListStr.add(str + "Str");
            }

            includeColumnListStr.add(str);
        }//
        for (String str : includeColumnList) {
            if (str.endsWith("Amount") || str.endsWith("profitAndLoss") || str.endsWith("betWinLose")) {
                includeColumnListStr.add(str + "Text");
            }
            includeColumnListStr.add(str);
        }
        return includeColumnListStr;
    }

    public Integer getPageNumber() {
        if (null == pageNumber) {
            return 1;
        }
        return pageNumber;
    }

    public Integer getPageSize() {
        if (null == pageSize) {
            return 10;
        }
        Integer max = Integer.MAX_VALUE - 1;
        if (pageSize > max) {
            return max;
        }
        return pageSize;
    }
}
