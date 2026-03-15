package com.cloud.baowang.play.wallet.vo.exception;

import com.cloud.baowang.play.wallet.enums.PGErrorEnums;
import lombok.Getter;

public class PGDefaultException extends RuntimeException {

    @Getter
    private final PGErrorEnums resultCode;

    private final String message;

    @Getter
    private final String uuid;

    public PGDefaultException(final PGErrorEnums resultCode, String uuid) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
        this.uuid = uuid;
    }

    public PGDefaultException(final PGErrorEnums resultCode, final String message, String uuid) {
        this.resultCode = resultCode;
        this.message = message;
        this.uuid = uuid;
    }

    public PGDefaultException(final PGErrorEnums resultCode, final Throwable e, String uuid) {
        super(e);
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
        this.uuid = uuid;
    }

    public PGDefaultException(final PGErrorEnums resultCode, final String message, final Throwable e, String uuid) {
        super(e);
        this.resultCode = resultCode;
        this.message = message;
        this.uuid = uuid;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}
