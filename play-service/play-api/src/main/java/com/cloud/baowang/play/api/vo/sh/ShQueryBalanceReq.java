package com.cloud.baowang.play.api.vo.sh;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ShQueryBalanceReq {

    @Schema(title = "玩家账号")
    private String userName;

    @Schema(title = "签名")
    private String md5Sign;

    @Schema(title = "商户")
    private String merchantNo;

    /**
     * 验证参数是否齐全
     *
     * @return true表示成功 false表示失败
     */
    public boolean validate() {
        return StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(merchantNo)
            && StringUtils.isNotBlank(md5Sign);
    }

}
