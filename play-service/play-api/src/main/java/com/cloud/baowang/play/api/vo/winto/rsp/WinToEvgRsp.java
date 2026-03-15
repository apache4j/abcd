package com.cloud.baowang.play.api.vo.winto.rsp;

import com.cloud.baowang.play.api.vo.winto.enums.WintoErrorEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class WinToEvgRsp<T> {
    private int code;
    private String msg;

    private String requestId;

    private String status;
    private T data;


    public WinToEvgRsp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.requestId = UUID.randomUUID().toString();
    }

    public WinToEvgRsp(int code, String msg,String status, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.requestId = UUID.randomUUID().toString();
        this.status = status;
    }





    public static <T> WinToEvgRsp<T> success(T data) {
        return new WinToEvgRsp<>(WintoErrorEnum.SUCCESS.getCode(),WintoErrorEnum.SUCCESS.getMsg(),data);
    }



    public static <T> WinToEvgRsp<T> failed(WintoErrorEnum resultCodeEnums) {
        return new WinToEvgRsp<>(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),"error",null);
    }


}
