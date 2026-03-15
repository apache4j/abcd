package com.cloud.baowang.play.wallet.vo.req.db.acelt.vo;

import lombok.Data;

import java.util.List;

@Data
public class BalanceQueryVO {
    private List<String> members;

    private String timestamp;

    private String sign;

    @Override
    public String toString() {
        String membersStr = members != null ? members.toString() : "[]";
        return "members" + membersStr
                + "timestamp" + timestamp;
    }
}
