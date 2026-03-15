package com.cloud.baowang.play.api.vo.third.SBA;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "沙巴体育登陆返回")
public class SBALoginResVO implements Serializable {

    private String access_token;


    private String token_type;


}
