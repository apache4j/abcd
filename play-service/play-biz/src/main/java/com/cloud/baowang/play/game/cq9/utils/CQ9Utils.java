package com.cloud.baowang.play.game.cq9.utils;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.enums.cq9.CQ9ResultCodeEnums;
import com.cloud.baowang.play.api.vo.cq9.response.CQ9BalanceRsp;
import com.cloud.baowang.play.api.vo.cq9.response.CQ9BaseRsp;
import com.cloud.baowang.play.api.vo.cq9.response.CQ9StatusRsp;
import com.cloud.baowang.play.api.vo.cq9.response.TransactionRecordRsp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @className: Cq9JWTUtils
 * @author: wade
 * @description: 对CQ9游戏进行jwt加密与解密
 * @date: 21/2/25 09:32
 */
public class CQ9Utils {

    public static final ZoneId gmt4ZoneId = ZoneId.of("Etc/GMT+4");

    public static final TimeZone gmt4TimeZone = TimeZone.getTimeZone("GMT-4");

    // 生成 JWT
    public static String createJWT(String userName, String secretKey) {
        Algorithm signature = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withClaim("userName", userName) // 绑定 userName
                .withIssuedAt(new Date())
                .sign(signature);
    }


    /**
     * 校验 JWT，并提取 userName
     *
     * @param token     token
     * @param secretKey 用户密码
     * @return 返回用户名字
     */
    public static String verifyJWT(String token, String secretKey) {
        try {
            Algorithm signature = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(signature).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            // 提取 userName
            return decodedJWT.getClaim("userName").asString();
        } catch (JWTVerificationException e) {
            // 校验失败，返回 null 或抛出异常
            return null;
        }
    }

    private static final DateTimeFormatter RFC3339_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static String getCurrentRFC3339Time() {
        String currentDatetime = ZonedDateTime.now().format(RFC3339_FORMATTER);
        return currentDatetime.length() > 35 ? currentDatetime.substring(0, 35) : currentDatetime;
    }

    private static CQ9StatusRsp createStatus(String code, String message) {
        CQ9StatusRsp status = new CQ9StatusRsp();
        status.setCode(code);
        status.setMessage(message);
        status.setDatetime(CQ9Utils.getNowTimeRF33());
        return status;
    }

    public static  CQ9BaseRsp<CQ9BalanceRsp> getSuccessStatus(CQ9BalanceRsp data) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(data);
        res.setStatus(createStatus("0", "Success"));
        return res;
    }
    public static  CQ9BaseRsp<TransactionRecordRsp> getSuccessRecordStatus(TransactionRecordRsp data) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(data);
        res.setStatus(createStatus("0", "Success"));
        return res;
    }

    public static CQ9BaseRsp<Boolean> getUserSuccessStatus() {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(true);
        res.setStatus(createStatus("0", "Success"));
        return res;
    }

    public static CQ9BaseRsp<CQ9BalanceRsp> getFailStatus(CQ9ResultCodeEnums enums, CQ9BalanceRsp t) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(null);
        res.setStatus(createStatus(enums.getCode(), "Fail"));
        return res;
    }
    public static CQ9BaseRsp<TransactionRecordRsp> getRecordFailStatus(CQ9ResultCodeEnums enums, TransactionRecordRsp t) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(t);
        res.setStatus(createStatus(enums.getCode(), "Fail"));
        return res;
    }
    public static CQ9BaseRsp<TransactionRecordRsp> getRecordFailStatus(CQ9ResultCodeEnums enums,String message) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(null);
        res.setStatus(createStatus(enums.getCode(), message));
        return res;
    }
    public static CQ9BaseRsp<Boolean> checkFailStatus(CQ9ResultCodeEnums enums, Boolean t) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(t);
        res.setStatus(createStatus(enums.getCode(), "Fail"));
        return res;
    }
    public static CQ9BaseRsp<Boolean> checkFailStatusCheck(CQ9ResultCodeEnums enums, Boolean t) {
        CQ9BaseRsp res = new CQ9BaseRsp();
        res.setData(t);
        res.setStatus(createStatus(enums.getCode(), enums.getMessage()));
        return res;
    }

    /**
     * 将 Date 转为固定 UTC-4（Etc/GMT+4）时区的 RFC3339 格式字符串
     */
    public static String getNowTimeRF33() {
        // 获取 UTC-4 时区对应的 Date 对象（你已有的方法）
        Date canadaCurrentDate = TimeZoneUtils.getCurrentDate4ZoneId(gmt4ZoneId);

        // 将 Date 转为 ZonedDateTime，指定为固定 UTC-4 时区
        ZonedDateTime zonedTime = canadaCurrentDate.toInstant().atZone(gmt4ZoneId);

        // 格式化为 RFC3339 格式
        return zonedTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String toRfc3339(String dateTimeStr) {
        if(ObjectUtil.isEmpty(dateTimeStr)){
            return dateTimeStr;
        }
        // 先尝试用 RFC3339 格式解析
        try {
            OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            // 解析成功，说明已经是 RFC3339，直接返回原字符串
            return dateTimeStr;
        } catch (DateTimeParseException e) {
            // 解析失败，说明不是 RFC3339，继续处理
        }

        // 不是 RFC3339，尝试用自定义格式解析
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, inputFormatter);

        // 绑定时区
        ZonedDateTime zonedDateTime = localDateTime.atZone(gmt4ZoneId);

        // 格式化成 RFC3339（ISO_OFFSET_DATE_TIME）
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static boolean isRFC3339TimeFormat(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return false;
        }

        try {
            OffsetDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
