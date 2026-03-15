package com.cloud.baowang.play.api.vo.jili;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JILIBaseRes implements Serializable {
    String traceId;
    String status;
    Object data;

}
