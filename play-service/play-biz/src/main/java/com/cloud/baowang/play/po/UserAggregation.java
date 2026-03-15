package com.cloud.baowang.play.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAggregation {
    private String id;

    private Long age;

    private String name;
    private String address;
    private long count;
    private long sumAge;
    private double avgAge;

    // 省略构造函数和getter/setter方法


}

