package com.cloud.baowang.play.api.enums;


public class SBDefaultException extends RuntimeException {

    private final SBResultCode resultCode;

    public SBResultCode getResultCode() {
        return resultCode;
    }

    private final String message;

    public SBDefaultException(final SBResultCode resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    public SBDefaultException(final String message) {
        this.resultCode = SBResultCode.SYSTEM_ERROR;
        this.message = message;
    }

    public SBDefaultException(final SBResultCode resultCode, final String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public SBDefaultException(final SBResultCode resultCode, final Throwable e) {
        super(e);
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    public SBDefaultException(final SBResultCode resultCode, final String message, final Throwable e) {
        super(e);
        this.resultCode = resultCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
