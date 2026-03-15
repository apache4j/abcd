package com.cloud.baowang.common.core.exceptions;

import com.cloud.baowang.common.core.enums.ResultCode;

public class BaowangDefaultException extends RuntimeException {
    private final ResultCode resultCode;
    private final String message;

    public BaowangDefaultException(final ResultCode resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessageCode();
    }

    public BaowangDefaultException(final String message) {
        this.resultCode = ResultCode.SERVER_INTERNAL_ERROR;
        this.message = message;
    }

    public BaowangDefaultException(final ResultCode resultCode, final String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public BaowangDefaultException(final ResultCode resultCode, final Throwable e) {
        super(e);
        this.resultCode = resultCode;
        this.message = resultCode.getMessageCode();
    }

    public BaowangDefaultException(final ResultCode resultCode, final String message, final Throwable e) {
        super(e);
        this.resultCode = resultCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
