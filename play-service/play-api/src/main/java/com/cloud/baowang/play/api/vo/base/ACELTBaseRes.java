package com.cloud.baowang.play.api.vo.base;

import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;
import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ACELTBaseRes<T> {
    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息内容
     */
    private String msg;

    /**
     * 返回对象
     */
    private T data;

    public ACELTBaseRes(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ACELTBaseRes<T> failed(ACELTResultCodeEnums resultCodeEnums, T balanceRes) {
        return new ACELTBaseRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), balanceRes);
    }

    public static <T> ACELTBaseRes<T> failed(ACELTResultCodeEnums resultCodeEnums) {
        return new ACELTBaseRes<>(resultCodeEnums.getCode(), resultCodeEnums.getMessage());
    }

    public static <T> ACELTBaseRes<T> success(T balanceRes) {
        return new ACELTBaseRes<>(ACELTResultCodeEnums.SUCCESS.getCode(), ShResultCodeEnums.SUCCESS.getMessage(), balanceRes);
    }
}
