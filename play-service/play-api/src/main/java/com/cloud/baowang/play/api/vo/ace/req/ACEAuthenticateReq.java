package com.cloud.baowang.play.api.vo.ace.req;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ACEAuthenticateReq extends ACEBaseReq{

    String userName;
    String password;

    public boolean isValid() {
        return super.isValid()
                ||StrUtil.isNotEmpty(userName)
                ||StrUtil.isNotEmpty(password)

                ;
    }
}
