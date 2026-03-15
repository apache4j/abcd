package com.cloud.baowang.play.api.vo.base;

import com.cloud.baowang.play.api.enums.dbPanDaSport.DbPanDaSportResultCodeEnum;
import com.cloud.baowang.play.api.enums.ftg.FTGResultCodeEnums;
import com.cloud.baowang.play.api.enums.sh.ShResultCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DbPanDaBaseRes<T> {
    /**
     * 响应码
     */
    private String code;

    /**
     * 响应消息内容
     */
    private String msg;

    /**
     * 返回对象
     */
    private T data;

    private Long serverTime;

    private Boolean status;

    public DbPanDaBaseRes(String code, String msg, T data, Long serverTime, Boolean status) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.serverTime = serverTime;
        this.status = status;
    }


    public static <T> DbPanDaBaseRes<T> success(T data) {
        return new DbPanDaBaseRes<>(DbPanDaSportResultCodeEnum.SUCCESS.getCode(), DbPanDaSportResultCodeEnum.SUCCESS.getMessage(), data, System.currentTimeMillis(), true);
    }

    public static DbPanDaBaseRes<Void> success() {
        return new DbPanDaBaseRes<>(DbPanDaSportResultCodeEnum.SUCCESS.getCode(), DbPanDaSportResultCodeEnum.SUCCESS.getMessage(), null, System.currentTimeMillis(), true);
    }

    public static <T> DbPanDaBaseRes<T> failed(DbPanDaSportResultCodeEnum resultCodeEnums) {
        return new DbPanDaBaseRes(resultCodeEnums.getCode(), resultCodeEnums.getMessage(), null, System.currentTimeMillis(), false);
    }

}
