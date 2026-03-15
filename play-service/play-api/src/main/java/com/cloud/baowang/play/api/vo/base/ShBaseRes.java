package com.cloud.baowang.play.api.vo.base;

import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShBaseRes<T> {
    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息内容
     */
    private String msg;

    /**
     * 返回对象
     */
    private T data;

    public ShBaseRes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ShBaseRes<T> failed(ShResultCodeEnums resultCodeEnums, T balanceRes) {
        return new ShBaseRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), balanceRes);
    }

    public static <T> ShBaseRes<T> failed(ShResultCodeEnums resultCodeEnums) {
        return new ShBaseRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage());
    }

    public static <T> ShBaseRes<T> success(T balanceRes) {
        return new ShBaseRes<>(ShResultCodeEnums.SUCCESS.getCode(), ShResultCodeEnums.SUCCESS.getMessage(), balanceRes);
    }
}
