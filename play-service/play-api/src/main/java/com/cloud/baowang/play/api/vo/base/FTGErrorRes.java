package com.cloud.baowang.play.api.vo.base;

import com.cloud.baowang.play.api.enums.ftg.FTGResultCodeEnums;
import com.cloud.baowang.play.api.vo.ftg.FTGGetBalanceRes;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FTGErrorRes<T> {
    /**
     * 响应码
     */
    private String error_code;

    /**
     * 响应消息内容
     */
    private String error_msg;

    /**
     * 返回对象
     */
    private String request_uuid;

    private T data;

    public FTGErrorRes(String error_code, String error_msg, String request_uuid) {
        this.error_code = error_code;
        this.error_msg = error_msg;
        this.request_uuid = request_uuid;
    }

    public FTGErrorRes(T data) {
        this.data = data;
    }

    public static <T> FTGErrorRes<T> failed(FTGResultCodeEnums resultCodeEnums, String request_uuid) {
        FTGErrorRes res = new FTGErrorRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), request_uuid);
        res.setData(new FTGErrorRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), request_uuid));
        return res;
    }


    public static <T> FTGErrorRes<T> success(T res) {
        return new FTGErrorRes<>(res);
    }

}
