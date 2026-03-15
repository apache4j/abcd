package com.cloud.baowang.play.api.vo.zf;

import com.cloud.baowang.play.api.enums.zf.ZfCancelBetErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZfSessionBetResp extends ZfResp{

    /**
     * 营运商承认注单后提供的交易识别唯一值
     */
    private String txId;


    public static ZfSessionBetResp fail(ZfCancelBetErrorCodeEnum errCode){
        ZfSessionBetResp zfResp = new ZfSessionBetResp();
        zfResp.setErrorCode(errCode.getCode());
        zfResp.setMessage(errCode.getMessage());
        return zfResp;
    }

    public static ZfSessionBetResp success(){
        ZfSessionBetResp zfResp = new ZfSessionBetResp();
        zfResp.setErrorCode(ZfCancelBetErrorCodeEnum.SUCCESS.getCode());
        zfResp.setMessage(ZfCancelBetErrorCodeEnum.SUCCESS.getMessage());
        return zfResp;
    }
}
