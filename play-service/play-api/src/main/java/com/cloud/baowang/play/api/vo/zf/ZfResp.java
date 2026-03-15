package com.cloud.baowang.play.api.vo.zf;

import com.cloud.baowang.play.api.enums.zf.ZfErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfResp {

    /**
     * 状态码
     */
    private int errorCode;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 账号唯一识别名称
     */
    private String username;

    /**
     * 币种名称
     *
     */
    private String currency;

    /**
     * 持有货币量
     */
    private BigDecimal balance;

    /**
     * 运营商 access token 若有更新需要以此字段回传
     */
    private String token;


    public static ZfResp fail(ZfErrorCodeEnum errCode){
        ZfResp zfResp = new ZfResp();
        zfResp.setErrorCode(errCode.getCode());
        zfResp.setMessage(errCode.getMessage());
        return zfResp;
    }

    public static ZfResp success(){
        ZfResp zfResp = new ZfResp();
        zfResp.setErrorCode(ZfErrorCodeEnum.SUCCESS.getCode());
        zfResp.setMessage(ZfErrorCodeEnum.SUCCESS.getMessage());
        return zfResp;
    }

    public boolean isOk(){
        return this.getErrorCode() == ZfErrorCodeEnum.SUCCESS.getCode();
    }
}
