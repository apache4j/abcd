package com.cloud.baowang.play.api.vo.omg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmgRespData {

    private String uname;
    private String nickname;
    private BigDecimal balance;
}
