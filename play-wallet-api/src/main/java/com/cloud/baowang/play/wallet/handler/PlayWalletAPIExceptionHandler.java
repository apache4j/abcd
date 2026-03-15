package com.cloud.baowang.play.wallet.handler;


import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.DbPanDaSportDefaultException;
import com.cloud.baowang.play.api.enums.FTGDefaultException;
import com.cloud.baowang.play.api.enums.SADefaultException;
import com.cloud.baowang.play.api.enums.SBDefaultException;
import com.cloud.baowang.play.api.vo.base.*;
import com.cloud.baowang.play.wallet.enums.ACELTDefaultException;
import com.cloud.baowang.play.wallet.enums.SHDefaultException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@Order(-1)
@ControllerAdvice
public class PlayWalletAPIExceptionHandler {

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

    @ExceptionHandler(value = {SBDefaultException.class})
    public ResponseEntity<SBResBaseVO> sbDefaultExceptionHandler(final SBDefaultException exception, final WebRequest request) {
        return ResponseEntity.ok(SBResponseVO.fail(exception.getResultCode()));
    }


    @ExceptionHandler(value = {ACELTDefaultException.class})
    public ResponseEntity<ACELTBaseRes> aceltDefaultExceptionHandler(final ACELTDefaultException exception, final WebRequest request) {
        return ResponseEntity.ok(ACELTBaseRes.failed(exception.getResultCode()));
    }


//    @ExceptionHandler(value = {FTGDefaultException.class})
//    public ResponseEntity<FTGErrorRes> ftgExceptionHandler(final FTGDefaultException exception, final WebRequest request) {
//        return ResponseEntity.ok(FTGErrorRes.failed(exception.getResultCode(),exception.getUuid()));
//    }

    @ExceptionHandler(value = {SHDefaultException.class})
    public ResponseEntity<ShBaseRes> shExceptionHandler(final SHDefaultException exception, final WebRequest request) {
        return ResponseEntity.ok(ShBaseRes.failed(exception.getResultCode()));
    }

    @ExceptionHandler(value = {SADefaultException.class})
    public ResponseEntity<String> saExceptionHandler(final SADefaultException exception, final WebRequest request) {
        String stringBuilder = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<RequestResponse>" +
                "<error>" +
                exception.getResultCode() +
                "</error>" +
                "</RequestResponse>";
        return ResponseEntity.ok(stringBuilder);
    }


    @ExceptionHandler(value = {DbPanDaSportDefaultException.class})
    public ResponseEntity<DbPanDaBaseRes<?>> dbPanDaSportExceptionHandler(final DbPanDaSportDefaultException exception, final WebRequest request) {
        return ResponseEntity.ok(DbPanDaBaseRes.failed(exception.getResultCode()));
    }






}
