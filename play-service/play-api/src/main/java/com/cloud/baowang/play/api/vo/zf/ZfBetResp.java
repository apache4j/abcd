package com.cloud.baowang.play.api.vo.zf;

import com.cloud.baowang.play.api.enums.zf.ZfBetErrorCodeEnum;
import com.cloud.baowang.play.api.enums.zf.ZfErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfBetResp  extends ZfResp{

    /**
     * 营运商承认注单后提供的交易识别唯一值
     */
    private String txId;


    public static ZfBetResp fail(ZfBetErrorCodeEnum errCode){
        ZfBetResp zfResp = new ZfBetResp();
        zfResp.setErrorCode(errCode.getCode());
        zfResp.setMessage(errCode.getMessage());
        return zfResp;
    }

    public static ZfBetResp success(){
        ZfBetResp zfResp = new ZfBetResp();
        zfResp.setErrorCode(ZfErrorCodeEnum.SUCCESS.getCode());
        zfResp.setMessage(ZfErrorCodeEnum.SUCCESS.getMessage());
        return zfResp;
    }
}
