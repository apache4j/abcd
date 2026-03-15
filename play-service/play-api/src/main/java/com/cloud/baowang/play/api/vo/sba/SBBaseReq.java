package com.cloud.baowang.play.api.vo.sba;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SBBaseReq {

    @Schema(title = "凭证文件中之密钥值")
    private String key;

    @Schema(title = "Json 格式: 请参阅下方说明")
    private String message;



}

