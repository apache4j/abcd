package com.cloud.baowang.play.api.vo.jili;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JILIBaseReq {

    String traceId;
    String currency;
    String username;
    String signature;




}
