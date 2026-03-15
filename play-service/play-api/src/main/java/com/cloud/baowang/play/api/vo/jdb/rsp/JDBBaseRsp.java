package com.cloud.baowang.play.api.vo.jdb.rsp;

import com.cloud.baowang.play.api.enums.jdb.JDBErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JDBBaseRsp {

    private String status;

    private Double balance;

    private String err_text;

    public JDBBaseRsp(String status, String err_text) {
        this.status = status;
        this.err_text = err_text;
    }

    public JDBBaseRsp(String status, String err_text, Double balance) {
        this.status = status;
        this.err_text = err_text;
        this.balance = balance;
    }

    public static JDBBaseRsp failed(JDBErrorEnum resultCodeEnums) {
        return new JDBBaseRsp(resultCodeEnums.getCode(), resultCodeEnums.getMessageEn());
    }

    public static JDBBaseRsp failed(JDBErrorEnum resultCodeEnums,Double balance) {
        return new JDBBaseRsp(resultCodeEnums.getCode(), resultCodeEnums.getMessageEn(),balance);
    }


    public static JDBBaseRsp success(Double balance) {
        return new JDBBaseRsp(JDBErrorEnum.SUCCESS.getCode(),JDBErrorEnum.SUCCESS.getMessageEn(), balance);
    }
}
