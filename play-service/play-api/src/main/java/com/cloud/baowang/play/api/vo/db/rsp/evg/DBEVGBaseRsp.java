package com.cloud.baowang.play.api.vo.db.rsp.evg;


import com.cloud.baowang.play.api.vo.db.evg.vo.CommonTradeInfo;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBAceltErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBEVGErrorEnum;
import com.cloud.baowang.play.api.vo.db.rsp.enums.DBFishingChessErrorEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DBEVGBaseRsp {
    private int code;
    private String msg;

    private CommonTradeInfo data;


    public DBEVGBaseRsp(int code,String msg, CommonTradeInfo data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    public static DBEVGBaseRsp failed(DBEVGErrorEnum resultCodeEnums, CommonTradeInfo data) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(), data);
    }


    public static DBEVGBaseRsp success(CommonTradeInfo data) {
        return new DBEVGBaseRsp(DBEVGErrorEnum.SUCCESS.getCode(),DBEVGErrorEnum.SUCCESS.getMsg(),data);
    }

    public static DBEVGBaseRsp failed(DBEVGErrorEnum resultCodeEnums) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null);
    }


    public static DBEVGBaseRsp failed(DBFishingChessErrorEnum resultCodeEnums, CommonTradeInfo data) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(), data);
    }


    public static DBEVGBaseRsp failed(DBFishingChessErrorEnum resultCodeEnums) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null);
    }

    public static DBEVGBaseRsp success(DBFishingChessErrorEnum resultCodeEnums, CommonTradeInfo data) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(), data);
    }



    public static DBEVGBaseRsp failed(DBAceltErrorEnum resultCodeEnums) {
        return new DBEVGBaseRsp(resultCodeEnums.getCode(),resultCodeEnums.getMsg(),null);
    }
}
