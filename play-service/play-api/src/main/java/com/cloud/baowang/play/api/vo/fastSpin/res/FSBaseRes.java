package com.cloud.baowang.play.api.vo.fastSpin.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FSBaseRes {

    String code;
    String msg;
}
