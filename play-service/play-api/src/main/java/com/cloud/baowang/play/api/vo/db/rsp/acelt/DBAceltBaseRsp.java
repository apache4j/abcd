package com.cloud.baowang.play.api.vo.db.rsp.acelt;

import com.cloud.baowang.play.api.vo.db.rsp.enums.DBAceltErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBEVGErrorEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBAceltBaseRsp<T> {
    private int code;
    private String msg;

    private T data;


    public DBAceltBaseRsp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> DBAceltBaseRsp<T> success(Integer code, T data) {
        return new DBAceltBaseRsp<>(code, DBAceltErrorEnum.SUCCESS.getMsg(),data);
    }

    public static <T> DBAceltBaseRsp<T> success() {
        return new DBAceltBaseRsp<>(DBEVGErrorEnum.SUCCESS.getCode(),DBEVGErrorEnum.SUCCESS.getMsg(),null);
    }

    public static <T> DBAceltBaseRsp<T> success(T data) {
        return new DBAceltBaseRsp<T>(DBEVGErrorEnum.SUCCESS.getCode(),DBEVGErrorEnum.SUCCESS.getMsg(),data);
    }

    public static <T> DBAceltBaseRsp<T> failed(DBEVGErrorEnum resultCodeEnums) {
        return new DBAceltBaseRsp<>(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null);
    }

    public static <T> DBAceltBaseRsp<T> success(DBAceltErrorEnum resultCodeEnums, T data) {
        return new DBAceltBaseRsp<>(resultCodeEnums.getCode(),resultCodeEnums.getMsg(), data);
    }



    public static <T> DBAceltBaseRsp<T> failed(DBAceltErrorEnum resultCodeEnums) {
        return new DBAceltBaseRsp<>(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null);
    }



    public static <T> DBAceltBaseRsp<T> success(Integer code) {
        return new DBAceltBaseRsp<>(code,DBAceltErrorEnum.SUCCESS.getMsg(),null);
    }
}
