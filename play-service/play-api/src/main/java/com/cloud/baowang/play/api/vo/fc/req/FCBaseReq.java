package com.cloud.baowang.play.api.vo.fc.req;


import cn.hutool.core.util.ObjUtil;
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
public class FCBaseReq {

     @Schema(title = "商户代码")
     String AgentCode;

     @Schema(title = "币别")
     String Currency;

     @Schema(title = "将Json string使用AES加密后的数据")
     String Params;

     @Schema(title = "将Json string使用MD5加密后的数据")
     String Sign;

     public boolean isValid() {
          return
                  StrUtil.isNotEmpty(this.getAgentCode()) &&
                          StrUtil.isNotEmpty(this.getParams())&&
                          StrUtil.isNotEmpty(this.getSign())
                  ;
     }
}
