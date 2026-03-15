package com.cloud.baowang.play.api.vo.ace.req;


import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ACEBaseReq {

     @Schema(title = "商户代码")
     String playerID;

     @Schema(title = "token")
     String token;


     public boolean isValid() {
          return
                  StrUtil.isNotEmpty(this.getPlayerID()) &&
                          StrUtil.isNotEmpty(this.getToken())
                  ;
     }
}
