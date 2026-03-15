package com.cloud.baowang.play.api.vo.acelt;


import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ACELTGetBalanceReq extends ACELTBaseReq {

    /**
     * 用户名
     */
    private String operatorAccount;



    public Boolean valid() {
        return ObjectUtil.isAllNotEmpty(this.getOperatorAccount(), this.getOperatorId(), this.getSign(), this.getTimestamp());
    }

}
