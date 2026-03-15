package com.cloud.baowang.play.api.vo.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdReq implements Serializable {
    // 方法名
    private String method;
    // (Array)加密后的会员代码和操作 ID
    private String balancePackage;
    // Package ID
    private String packageId;
    // 日期, Ticks 数据
    private Long dateSent;

}
