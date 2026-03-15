package com.cloud.baowang.play.api.vo.pp.req;


import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.play.api.vo.pp.PPBaseReqVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PPBalanceReqVO extends PPBaseReqVO {

    String hash;
    String providerId;
    String userId;


    public boolean isValid() {
        return
            StrUtil.isNotEmpty(this.getHash()) &&
            StrUtil.isNotEmpty(this.getProviderId())&&
            StrUtil.isNotEmpty(this.getUserId())

                ;
    }

}
