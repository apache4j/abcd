package com.cloud.baowang.play.api.exception;


import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;

public class SHDefaultException extends RuntimeException {

    private final ShResultCodeEnums shResultCodeEnums;

    public ShResultCodeEnums getResultCode() {
        return shResultCodeEnums;
    }

    private final String message;

    public SHDefaultException(final ShResultCodeEnums shResultCodeEnums) {
        this.shResultCodeEnums = shResultCodeEnums;
        this.message = shResultCodeEnums.getMessage();
    }



    @Override
    public String getMessage() {
        return this.message;
    }

}
