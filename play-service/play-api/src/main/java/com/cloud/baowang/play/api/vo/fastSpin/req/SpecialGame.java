package com.cloud.baowang.play.api.vo.fastSpin.req;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialGame {

    String type;
    Integer count;
    Integer sequence;
}
