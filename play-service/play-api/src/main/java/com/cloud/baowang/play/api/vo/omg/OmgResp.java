package com.cloud.baowang.play.api.vo.omg;


import com.cloud.baowang.play.api.enums.omg.OmgRespErrEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OmgResp {
    private int code;
    private String msg;
    private OmgRespData data;

    public static OmgResp fail(OmgRespErrEnums omgRespErrEnums) {
        OmgResp omgResp = new OmgResp();
        omgResp.setCode(omgRespErrEnums.getCode());
        omgResp.setMsg(omgRespErrEnums.getDescription());
        return omgResp;
    }
    public static OmgResp success(OmgRespData data) {
        OmgResp omgResp = new OmgResp();
        omgResp.setCode(OmgRespErrEnums.SUCCESS.getCode());
        omgResp.setMsg(OmgRespErrEnums.SUCCESS.getDescription());
        omgResp.setData(data);
        return omgResp;
    }
    public static OmgResp success() {
        OmgResp omgResp = new OmgResp();
        omgResp.setCode(OmgRespErrEnums.SUCCESS.getCode());
        omgResp.setMsg(OmgRespErrEnums.SUCCESS.getDescription());
        return omgResp;
    }
}
