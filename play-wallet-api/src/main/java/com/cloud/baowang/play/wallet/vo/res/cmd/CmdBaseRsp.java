package com.cloud.baowang.play.wallet.vo.res.cmd;

import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import com.cloud.baowang.play.wallet.enums.CmdRespErrEnums;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmdBaseRsp  implements Serializable {

    /**
     * 状态编码
     */
    private Integer StatusCode;

    /**
     *  状态信息, 回复此次请求之状态内容
     */
    private String StatusMessage;

    /**
     *  回传接收输入的 Package ID
     */
    private String PackageId;
    /**
     * CMD 请求之日期, Ticks 数据
     */
    private Long DateReceived;

    /**
     * 响应 CMD 之日期, Ticks 数据
     */
    private Long DateSent;

    public static String success(CmdReq request){
        Gson gson = new Gson();
        CmdBaseRsp cmdBaseRsp = new CmdBaseRsp();
        cmdBaseRsp.setStatusCode(CmdRespErrEnums.SUCCESS.getCode());
        cmdBaseRsp.setStatusMessage(CmdRespErrEnums.SUCCESS.getDescription());
        cmdBaseRsp.setPackageId(request.getPackageId());
        cmdBaseRsp.setDateSent(request.getDateSent());
        cmdBaseRsp.setDateReceived(System.currentTimeMillis());
        return gson.toJson(cmdBaseRsp);
    }
    public static String err(CmdRespErrEnums cmdRespErrEnums,CmdReq request){
        Gson gson = new Gson();
        CmdBaseRsp cmdBaseRsp = new CmdBaseRsp();
        cmdBaseRsp.setStatusCode(cmdRespErrEnums.getCode());
        cmdBaseRsp.setStatusMessage(cmdRespErrEnums.getDescription());
        cmdBaseRsp.setPackageId(request.getPackageId());
        cmdBaseRsp.setDateSent(request.getDateSent());
        cmdBaseRsp.setDateReceived(System.currentTimeMillis());
        return gson.toJson(cmdBaseRsp);
    }


}
