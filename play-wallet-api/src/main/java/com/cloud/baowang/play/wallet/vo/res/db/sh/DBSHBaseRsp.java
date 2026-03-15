package com.cloud.baowang.play.wallet.vo.res.db.sh;

import com.cloud.baowang.play.wallet.vo.req.db.sh.enums.SHErrorEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBSHBaseRsp<T> {
    private int code;
    private String message;

    private String signature;
    private T data;


    public DBSHBaseRsp(int code, String message,String signature, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.signature = signature;
    }



    public static <T> DBSHBaseRsp<T> success(String signature, T data) {
        return new DBSHBaseRsp<>(SHErrorEnum.SUCCESS.getCode(),SHErrorEnum.SUCCESS.getMsg(),signature,data);
    }



    public static <T> DBSHBaseRsp<T> failed(SHErrorEnum resultCodeEnums) {
        return new DBSHBaseRsp<>(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null,null);
    }


}
