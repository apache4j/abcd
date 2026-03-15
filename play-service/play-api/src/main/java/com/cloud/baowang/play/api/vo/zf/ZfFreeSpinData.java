package com.cloud.baowang.play.api.vo.zf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfFreeSpinData {

    /**
     * 免费局数序号
     */
    @JsonProperty("referenceId")
    private String referenceId;

    /**
     * 剩余免费局数
     */
    @JsonProperty("remain")
    private Integer remain;

}
