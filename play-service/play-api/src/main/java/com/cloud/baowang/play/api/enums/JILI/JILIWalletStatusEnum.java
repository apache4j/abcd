package com.cloud.baowang.play.api.enums.JILI;


public enum JILIWalletStatusEnum {

//    SC_OK,                //OK
//    SC_INVALID_SIGNATURE, //签名错误
//    SC_INVALID_TOKEN, //非法请求码
//    SC_USER_NOT_EXISTS, //用户不存在
//    SC_WRONG_CURRENCY, //错误的币种
//    SC_INVALID_REQUEST, //非法请求
//    SC_INSUFFICIENT_FUNDS, //余额不足
//
//    SC_WRONG_PARAMETERS, //参数错误

    SC_OK,
    SC_USER_IS_LOCKED, //用户锁定

    SC_VENUE_IS_CLOSED,  //场馆关闭状态

    SC_HAS_NO_VENUE_AUTHENTICATION, //站点没有开放场馆


    SC_UNKNOWN_ERROR, //Generic status code for unknown errors.

    SC_INVALID_REQUEST, //Wrong/missing parameters sent in request body.

    SC_AUTHENTICATION_FAILED, //Authentication failed. X-API-Key is missing or invalid.

    SC_INVALID_SIGNATURE, //X-Signature verification failed.

    SC_INVALID_TOKEN, //Invalid token on Operator's system.

    SC_INVALID_GAME, //Not a valid game.

    SC_DUPLICATE_REQUEST, //Duplicate request.

    SC_CURRENCY_NOT_SUPPORTED, //Currency is not supported.

    SC_WRONG_CURRENCY, //Transaction's currency is different from user's wallet currency.

    SC_INSUFFICIENT_FUNDS, //User's wallet does not have enough funds.

    SC_USER_NOT_EXISTS, //User does not exists in Operator's system

    SC_USER_DISABLED, //User is disabled and not allowed to place bets.

    SC_TRANSACTION_DUPLICATED, //Duplicate transaction Id was sent.

    SC_TRANSACTION_NOT_EXISTS, //Corresponding bet transaction cannot be found.

    SC_VENDOR_ERROR, //Error encountered on game vendor

    SC_UNDER_MAINTENANCE, //Game is under maintenance.

    SC_MISMATCHED_DATA_TYPE, //Invalid data type.

    SC_INVALID_RESPONSE, //Invalid response.

    SC_INVALID_VENDOR, Vendor, // is not supported

    SC_INVALID_LANGUAGE, //Language is not supported.

    SC_GAME_DISABLED, //Game is disabled.

    SC_INVALID_PLATFORM, //Platform is not supported.

    SC_GAME_LANGUAGE_NOT_SUPPORTED, //Game language is not supported.

    SC_GAME_PLATFORM_NOT_SUPPORTED, //Game platform is not supported.

    SC_GAME_CURRENCY_NOT_SUPPORTED, //Game currency is not supported.

    SC_VENDOR_LINE_DISABLED, //Vendor line is disabled.

    SC_VENDOR_CURRENCY_NOT_SUPPORTED, //Vendor currency is not supported.

    SC_VENDOR_LANGUAGE_NOT_SUPPORTED, //Vendor language is not supported.

    SC_VENDOR_PLATFORM_NOT_SUPPORTED, //Vendor platform is not supported.

    SC_TRANSACTION_STILL_PROCESSING, //Transaction is still processing, please retry.

    SC_EXCEEDED_NUMBER_OF_RETRIES, //Exceeded number of retries.

    SC_OPERATOR_TIMEOUT, //Operator timed out

    SC_INVALID_FROM_TIME, // Data only available last 60 days

    SC_INVALID_DATE_RANGE, //Date range should be within one day.

    SC_REFERENCE_ID_DUPLICATED, //Duplicate reference Id was sent.

    SC_TRANSACTION_DOES_NOT_EXIST, //Corresponding reference Id cannot be found.

    SC_INTERNAL_ERROR, //Internal error. please checked in relevant support channel

    SC_WALLET_NOT_SUPPORTED, //Wallet Type is not supported.


}
