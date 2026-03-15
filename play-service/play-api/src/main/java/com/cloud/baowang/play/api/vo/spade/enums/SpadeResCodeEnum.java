package com.cloud.baowang.play.api.vo.spade.enums;

import com.cloud.baowang.play.api.vo.fastSpin.res.FSBaseRes;
import com.cloud.baowang.play.api.vo.spade.res.SpadeBaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpadeResCodeEnum {

    CODE_0("0", "成功", "Success", "接口调用成功") ,

    CODE_1("1", "系统错误", "System Error", "系统内部出错，程序bug、数据库连接不上等"),

    CODE_2("2", "非法请求", "Invalid Request", "不是合法的请求"),

    CODE_3("3", "服务暂时不可用", "Service Inaccessible", "服务不可用"),

    CODE_100("100", "请求超时", "Request Timeout", "这个通常用于单一钱包的Transfer API"),

    CODE_101("101", "用户调用次数超限", "Call Limited", "调用API 接口的次数已超限"),

    CODE_104("104", "请求被禁止", "Request Forbidden", "Acct ID 格式不正确"),

    CODE_105("105", "缺少必要的参数", "Missing Parameters", "Parse Json Data Failed"),

    CODE_106("106", "非法的参数", "Invalid Parameters", "Deposit/withdraw 的币种与用户的币种不一致，或者merchant 没有相应的币种"),

    CODE_107("107", "批次号重复", "Duplicated Serial NO.", "Deposit/withdraw 金额必须>0"),

    CODE_109("109", "关联id 找不到", "Related id not found", "时间格式不正确"),

    CODE_110("110", "批次号不存在", "Record ID Not Found", ""),

    CODE_111("111", "重复请求", "Duplicated request", ""),

    CODE_112("112", "API 调用次数超限", "API Call Limited", ""),

    CODE_113("113", "Acct ID 不正确", "Invalid Acct ID", ""),

    CODE_118("118", "格式不正确", "Invalid Format", ""),

    CODE_120("120", "ip 不在白名单内", "IP no whitelisted", ""),

    C_5003("5003", "游戏正在维护中", "System Maintenance", ""),

    C_10113("10113", "Merchant 不存在", "Merchant Not Found", ""),

    C_10116("10116", "Merchan 被暂停", "merchant suspend", ""),

    C_50099("50099", "账号已存在", "Acct Exist", ""),

    C_50100("50100", "账号不存在", "Acct Not Found", ""),

    C_50101("50101", "帐号未激活", "Acct Inactive", ""),

    C_50102("50102", "帐号已锁", "Acct Locked", ""),

    C_50103("50103", "帐号suspend", "Acct Suspend", ""),

    C_50104("50104", "Token 验证失败", "Token Validation Failed", ""),

    C_50110("50110", "帐号余额不足", "Insufficient Balance", ""),

    C_50111("50111", "超过帐号交易限制", "Exceed Max Amount", ""),

    C_50112("50112", "币种不支持", "Currency Invalid", ""),

    C_50113("50113", "金额不合法", "Amount Invalid", ""),

    C_50115("50115", "日期格式错误", "Date Format Invalid", "");

    private final String code;
    private final String msgCn;
    private final String msgEn;
    private final String desc;


    public static SpadeResCodeEnum fromCode(String code) {
        for (SpadeResCodeEnum c : SpadeResCodeEnum.values()) {
            if (c.code.equals(code)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }

    // 抽象方法：每个枚举值实现自己的返回逻辑
    public SpadeBaseRes toResVO(SpadeBaseRes resVO){
        resVO.setCode(this.getCode());
        resVO.setMsg(this.getMsgEn());
        return resVO;
    }

    // 抽象方法：每个枚举值实现自己的返回逻辑
    public SpadeBaseRes toResVO(){
        SpadeBaseRes resVO = new SpadeBaseRes();
        resVO.setCode(this.getCode());
        resVO.setMsg(this.getMsgEn());
        return resVO;
    }
}
