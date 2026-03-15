package com.cloud.baowang.common.core.utils.tool.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 5/5/23 6:32 PM
 * @Version 1.0
 */
@Data
public class Comparison implements Serializable {
    //字段
    private String Field;
    //字段旧值
    private Object before;
    //字段新值
    private Object after;
}
