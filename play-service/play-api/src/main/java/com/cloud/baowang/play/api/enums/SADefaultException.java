package com.cloud.baowang.play.api.enums;


public class SADefaultException extends RuntimeException {

    private final Integer resultCode;

    public Integer getResultCode() {
        return resultCode;
    }


    public SADefaultException(final Integer resultCode) {
        this.resultCode = resultCode;
    }


}
