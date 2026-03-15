package com.cloud.baowang.play.api.enums;

import com.cloud.baowang.play.api.enums.ftg.FTGResultCodeEnums;
import lombok.Getter;

public class FTGDefaultException extends RuntimeException {

    @Getter
    private final FTGResultCodeEnums resultCode;

    private final String message;

    @Getter
    private final String uuid;

    public FTGDefaultException(final FTGResultCodeEnums resultCode, String uuid) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
        this.uuid = uuid;
    }


    @Override
    public String getMessage() {
        return this.message;
    }

}
