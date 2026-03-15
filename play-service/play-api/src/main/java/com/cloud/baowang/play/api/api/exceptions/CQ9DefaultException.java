package com.cloud.baowang.play.api.api.exceptions;

import com.cloud.baowang.play.api.enums.cq9.CQ9ResultCodeEnums;
import lombok.Getter;

public class CQ9DefaultException extends RuntimeException {

    @Getter
    private final CQ9ResultCodeEnums resultCode;

    private final String message;

    @Getter
    private final String uuid;

    public CQ9DefaultException(final CQ9ResultCodeEnums resultCode, String uuid) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
        this.uuid = uuid;
    }

    public CQ9DefaultException(final CQ9ResultCodeEnums resultCode, final String message, String uuid) {
        this.resultCode = resultCode;
        this.message = message;
        this.uuid = uuid;
    }

    public CQ9DefaultException(final CQ9ResultCodeEnums resultCode, final Throwable e, String uuid) {
        super(e);
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
        this.uuid = uuid;
    }

    public CQ9DefaultException(final CQ9ResultCodeEnums resultCode, final String message, final Throwable e, String uuid) {
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
