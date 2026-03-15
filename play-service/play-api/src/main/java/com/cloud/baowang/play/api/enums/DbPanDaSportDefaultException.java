package com.cloud.baowang.play.api.enums;


import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportResultCodeEnum;
import lombok.Getter;

public class DbPanDaSportDefaultException extends RuntimeException {

    @Getter
    private final DbPanDaSportResultCodeEnum resultCode;

    private final String message;

    public DbPanDaSportDefaultException(final DbPanDaSportResultCodeEnum resultCode) {
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }


    @Override
    public String getMessage() {
        return this.message;
    }

}
