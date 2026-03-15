package com.cloud.baowang.common.core.vo.base;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.TraceUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> implements Serializable {
    @Schema(description = "返回码")
    private int code;
    @Schema(description = "返回信息")
    private String message;// message固定翻译
    @Schema(description = "链路id")
    private String transactionId;
    private T data;

    private String messAppend;

    public String getMessage() {
        if(!StringUtils.hasText(messAppend)){
            return message;
        }else {
            return String.format(message,messAppend);
        }
    }

    public static <T> ResponseVO<T> success() {
        return new ResponseVO<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessageCode(), TraceUtil.getTraceId(),null, null);
    }

    public static <T> ResponseVO<T> success(String message, T data) {
        return new ResponseVO<>(ResultCode.SUCCESS.getCode(), message, TraceUtil.getTraceId(), data,null);
    }

    public static <T> ResponseVO<T> success(ResultCode resultCode, T data) {
        return new ResponseVO<>(resultCode.getCode(), resultCode.getMessageCode(), TraceUtil.getTraceId(), data,null);
    }

    public static <T> ResponseVO<T> success(T data) {
        return new ResponseVO<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessageCode(), TraceUtil.getTraceId(), data,null);
    }

    public static <T> ResponseVO<T> fail(final ResultCode resultCode) {
        return new ResponseVO<>(resultCode.getCode(), resultCode.getMessageCode(), TraceUtil.getTraceId(), null,null);
    }

    public static <T> ResponseVO<T> fail(final Integer resultCode) {
        return new ResponseVO<>(resultCode, ResultCode.SERVER_INTERNAL_ERROR.getMessageCode(), TraceUtil.getTraceId(), null,null);
    }

    public static <T> ResponseVO<T> fail(final ResultCode resultCode, final String message) {
        return new ResponseVO<>(resultCode.getCode(), message, TraceUtil.getTraceId(), null,null);
    }
    public static <T> ResponseVO<T> failAppend(final ResultCode resultCode , final String messAppend) {
        return new ResponseVO<>(resultCode.getCode(), resultCode.getMessageCode(), TraceUtil.getTraceId(), null,messAppend);
    }

    public static <T> ResponseVO<T> fail(final ResultCode resultCode, final T data) {
        return new ResponseVO<>(resultCode.getCode(), resultCode.getMessageCode(), TraceUtil.getTraceId(), data,null);
    }

    public boolean isOk() {
        return this.code == ResultCode.SUCCESS.getCode();
    }
}
