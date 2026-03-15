package com.cloud.baowang.play.api.vo.base;

import com.cloud.baowang.play.api.enums.SBResultCode;
import lombok.Data;
import java.io.Serializable;

@Data
public class SBResponseVO<T extends SBResBaseVO> implements Serializable {

    public static SBResBaseVO success() {
        SBResBaseVO vo = new SBResBaseVO();
        vo.setStatus(String.valueOf(SBResultCode.SUCCESS.getCode()));
        vo.setMsg(SBResultCode.SUCCESS.getMessage());
        return vo;
    }

    public static <T extends SBResBaseVO> T success(T data) {
        return data;
    }

    public static SBResBaseVO fail(final SBResultCode resultCode) {
        SBResBaseVO vo = new SBResBaseVO();
        vo.setStatus(String.valueOf(resultCode.getCode()));
        vo.setMsg(resultCode.getMessage());
        return vo;
    }


}
