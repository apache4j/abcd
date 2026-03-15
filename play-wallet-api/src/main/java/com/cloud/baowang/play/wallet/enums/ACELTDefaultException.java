package com.cloud.baowang.play.wallet.enums;


import com.cloud.baowang.play.api.enums.acelt.ACELTResultCodeEnums;

public class ACELTDefaultException extends RuntimeException {

    private final ACELTResultCodeEnums resultCode;

    public ACELTResultCodeEnums getResultCode() {
        return resultCode;
    }

    private final String message;

    public ACELTDefaultException(final ACELTResultCodeEnums resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }


    @Override
    public String getMessage() {
        return this.message;
    }

}
