package com.cloud.baowang.play.util.cmd;

import com.cloud.baowang.play.api.enums.cmd.CmdRespErrEnums;
import com.cloud.baowang.play.api.vo.cmd.CmdReq;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmdUserRsp  extends  CmdBaseRsp implements Serializable {
    /**
     * 用户剩余余额
     */
    private BigDecimal Balance;

    public static String success(CmdReq request, BigDecimal balance){
        Gson gson = new Gson();
        CmdUserRsp cmdBaseRsp = new CmdUserRsp();
        cmdBaseRsp.setStatusCode(CmdRespErrEnums.SUCCESS.getCode());
        cmdBaseRsp.setStatusMessage(CmdRespErrEnums.SUCCESS.getDescription());
        cmdBaseRsp.setPackageId(request.getPackageId());
        cmdBaseRsp.setDateSent(request.getDateSent());
        cmdBaseRsp.setDateReceived(System.currentTimeMillis());
        cmdBaseRsp.setBalance(balance);
        return gson.toJson(cmdBaseRsp);
    }
    public static String err(CmdRespErrEnums cmdRespErrEnums,CmdReq request, BigDecimal balance){
        Gson gson = new Gson();
        CmdUserRsp cmdBaseRsp = new CmdUserRsp();
        cmdBaseRsp.setStatusCode(cmdRespErrEnums.getCode());
        cmdBaseRsp.setStatusMessage(cmdRespErrEnums.getDescription());
        cmdBaseRsp.setPackageId(request.getPackageId());
        cmdBaseRsp.setDateSent(request.getDateSent());
        cmdBaseRsp.setDateReceived(System.currentTimeMillis());
        cmdBaseRsp.setBalance(balance);
        return gson.toJson(cmdBaseRsp);
    }
}
