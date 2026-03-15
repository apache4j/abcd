package com.cloud.baowang.user.api.vo.ads;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CustomData {
    private String currency;
    private BigDecimal value;
    private List<String> contentIds;
}