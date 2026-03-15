package com.cloud.baowang.play.api.vo.dbPanDaSport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DbPanDaSportBetResVO {
    private BigDecimal balance;
}
