package com.cloud.baowang.service.vendor.TSPay;


import lombok.Getter;

@Getter
public enum TSOrderStatusEnum {
    PENDING,
    SUCCESS,
    FAILED,
    REFUND,
    PART_SUC;

    TSOrderStatusEnum() {
    }
}
