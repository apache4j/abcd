package com.cloud.baowang.entity;

import cn.hutool.core.util.ObjUtil;
import lombok.Data;

/**
 * {"code":200,"msg":null,"content":"3"}
 */
@Data
public class XxlJobRes {
    private Integer code;
    private String msg;
    private String content;

    public Boolean isSuccess() {
        if (ObjUtil.isNotNull(code) && code == 200) {
            return true;
        }
        return false;
    }
}
