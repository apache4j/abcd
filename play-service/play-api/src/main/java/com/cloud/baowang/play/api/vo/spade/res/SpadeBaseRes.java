package com.cloud.baowang.play.api.vo.spade.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SpadeBaseRes {

    String code;
    String msg;
}
