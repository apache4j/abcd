package com.cloud.baowang.common.core.handler;


import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.*;
import com.cloud.baowang.common.core.vo.base.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * 只处理系统内部公共异常
 * 特殊处理在各自业务内部完成
 *
 */
@Slf4j
@Order(1)
@ControllerAdvice
public class APIExceptionHandler {

    @ExceptionHandler(value = {BaowangDefaultException.class})
    public ResponseEntity<ResponseVO<?>> baowangDefaultExceptionHandler(final BaowangDefaultException exception, final WebRequest request) {
        return ResponseEntity.ok(ResponseVO.fail(exception.getResultCode(), exception.getMessage()));
    }

    @ExceptionHandler(value = {NullPointerException.class})
    public ResponseEntity<ResponseVO<?>> nullPointerExceptionHandler(NullPointerException exception) {
        log.error("error", exception);
        return ResponseEntity.ok(ResponseVO.fail(ResultCode.SYSTEM_ERROR));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ResponseVO<?>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) throws NoSuchFieldException {
        return ResponseEntity.ok(ResponseVO.fail(ResultCode.PARAM_ERROR, String.join(";", exception.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList())));
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<ResponseVO<?>> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("error", exception);
        return ResponseEntity.ok(ResponseVO.fail(ResultCode.PARAM_NOT_VALID));
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ResponseVO<?>> globalException(Exception exception) {
        log.error("error", exception);
        return ResponseEntity.ok(ResponseVO.fail(ResultCode.SYSTEM_ERROR));
    }

}
