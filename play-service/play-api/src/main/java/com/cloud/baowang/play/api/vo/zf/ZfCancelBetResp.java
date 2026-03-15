package com.cloud.baowang.play.api.vo.zf;

import com.cloud.baowang.play.api.enums.zf.ZfCancelBetErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfCancelBetResp extends ZfResp {

    /**
     * 营运商承认注单后提供的交易识别唯一值
     */
    private String txId;


    public static ZfCancelBetResp fail(ZfCancelBetErrorCodeEnum errCode){
        ZfCancelBetResp zfResp = new ZfCancelBetResp();
        zfResp.setErrorCode(errCode.getCode());
        zfResp.setMessage(errCode.getMessage());
        return zfResp;
    }

    public static ZfCancelBetResp success(){
        ZfCancelBetResp zfResp = new ZfCancelBetResp();
        zfResp.setErrorCode(ZfCancelBetErrorCodeEnum.SUCCESS.getCode());
        zfResp.setMessage(ZfCancelBetErrorCodeEnum.SUCCESS.getMessage());
        return zfResp;
    }
}
