package com.cloud.baowang.play.game.omg.resp;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmgOrderRespVO {
    private Integer code;
    private List<OmgOrderDataRespVO> data;
    private String msg;
    private Integer total;

}
