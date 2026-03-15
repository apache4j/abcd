package com.cloud.baowang.play.game.fc.enums;

import com.cloud.baowang.play.api.vo.fastSpin.res.FSBaseRes;
import com.cloud.baowang.play.api.vo.fc.res.FCBaseRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FCErrorCodeEnum {
    CODE_0(0, "成功", "success"),
    CODE_200(200, "充提点数为0", "The number of charge points is 0"),
    CODE_201(201, "点数异常", "Points abnormal"),
    CODE_202(202, "充提方式代码错误", "Recharge method code error"),
    CODE_203(203, "玩家余额不足", "Insufficient player balance"),
    CODE_204(204, "交易对应单号过长", "The transaction number is too long"),
    CODE_205(205, "交易对应单号重复", "Repeat the transaction number"),
    CODE_206(206, "起始时间错误", "Start time error"),
    CODE_207(207, "结束时间错误", "End time error"),
    CODE_208(208, "起始时间超前结束时间", "Start time ahead of end time"),
    CODE_209(209, "页码不是数字", "Page number is not a number"),
    CODE_210(210, "交易对应单号为空", "The transaction number is empty"),
    CODE_211(211, "详细投注记录参数错误", "Detailed betting record parameters error"),
    CODE_212(212, "交易单号不是数字", "The transaction number is not a number"),
    CODE_213(213, "对应单号不合法", "The corresponding order number is illegal"),
    CODE_214(214, "此游戏编号未提供彩金模式", "No bonus mode is available for this game number"),
    CODE_215(215, "未开放此语系", "This language system is not open"),
    CODE_217(217, "传入资料不是JSON格式", "The incoming data is not JSON format"),
    CODE_218(218, "资料笔数不是数字", "The number of data is not a number"),
    CODE_219(219, "点数超过上限额度", "Points exceed the upper limit"),
    CODE_220(220, "搜寻时间超出最大范围", "Search time exceeds the maximum range"),
    CODE_221(221, "交易单号不存在", "The transaction number does not exist"),
    CODE_222(222, "游戏类型错误", "Game Type Error"),
    CODE_223(223, "页码超过总页数", "Page number exceeds the total number of pages"),
    CODE_224(224, "页码错误", "Page number error"),
    CODE_225(225, "笔数错误", "Error in number of words"),
    CODE_226(226, "排序错误", "Sorting error"),
    CODE_227(227, "查询时间超过最小范围", "Query time exceeds the minimum range"),
    CODE_228(228, "活动类型错误", "Error in activity type"),
    CODE_301(301, "无法取得商户密钥", "Unable to obtain the merchant key"),
    CODE_303(303, "资料解密失败", "Data decryption failed"),
    CODE_304(304, "商户签章比对失败", "Merchant signature comparison failed"),
    CODE_401(401, "此商户代码禁用", "This merchant code is disabled"),
    CODE_402(402, "此方法禁用", "This method is disabled"),
    CODE_403(403, "此商户代码的方法禁用", "This merchant code method is disabled"),
    CODE_405(405, "游戏不存在", "The game does not exist"),
    CODE_406(406, "游戏关闭", "Game Close"),
    CODE_407(407, "账号锁定中", "Account locked"),
    CODE_408(408, "游戏维护中", "Game maintenance"),
    CODE_409(409, "没有此账号的权限", "No permissions for this account"),
    CODE_410(410, "不允许的IP", "Not allowed IP"),
    CODE_411(411, "此方法维护中", "This method is under maintenance"),
    CODE_416(416, "查无此活动", "No such activity"),
    CODE_417(417, "已达绑定人数上限", "The maximum number of bound people has been reached"),
    CODE_418(418, "玩家不在活动内", "Players are not in the event"),
    CODE_419(419, "EventID重复", "EventID duplicate"),
    CODE_500(500, "账号不存在", "The account does not exist"),
    CODE_501(501, "账号过长", "Account too long"),
    CODE_502(502, "账号重复", "Duplicate account"),
    CODE_503(503, "账号在线", "Account online"),
    CODE_504(504, "账号不在线", "Account not online"),
    CODE_505(505, "账号过短", "Account too short"),
    CODE_601(601, "URL空白", "URL blank"),
    CODE_602(602, "商户响应为空", "Merchant response is empty"),
    CODE_603(603, "回传资料不是 JSON 格式", "Returning information is not in JSON format"),
    CODE_604(604, "验证失败", "Verification failed"),
    CODE_605(605, "没有回传验证结果", "No back-received verification results"),
    CODE_606(606, "取得平台点数非数字", "Get the platform points non-digit"),
    CODE_607(607, "没有回传点数", "No return points"),
    CODE_701(701, "点数取得失败", "Points failed"),
    CODE_702(702, "交易单写入失败", "Transaction order writing failed"),
    CODE_703(703, "充提错误", "Recharge error"),
    CODE_704(704, "玩家信息查询空白", "Player information query blank"),
    CODE_705(705, "游戏记录总数查询空白", "Query blank for total number of game records"),
    CODE_706(706, "游戏记录查询空白", "Game record query blank"),
    CODE_708(708, "交易记录总数查询空白", "Query blank for total transaction records"),
    CODE_709(709, "查无资料", "No information found"),
    CODE_799(799, "将失败注单改为成功注单", "Change failed bets to successful bets"),
    CODE_899(899, "传送Cancel请求", "Send Cancel request"),
    CODE_901(901, "网址过期", "The URL expires"),
    CODE_902(902, "找不到游戏载体网址", "The game vector URL cannot be found"),
    CODE_903(903, "语系修改失败", "Language system modification failed"),
    CODE_910(910, "资料传输方式错误", "Error in data transmission method"),
    CODE_911(911, "无法抓取资料", "Unable to capture information"),
    CODE_912(912, "访问者IP过长", "Visitor IP is too long"),
    CODE_999(999, "无法预期的错误", "Unexpected error"),
    CODE_1011(1011, "没有带入商户代码", "No merchant code brought in"),
    CODE_1012(1012, "没有带入币别", "No coin included"),
    CODE_1013(1013, "没有带入资料", "No information was brought in"),
    CODE_1014(1014, "没有带入商户签章", "Not brought to the merchant's seal"),
    CODE_1015(1015, "没有带入开始时间", "No start time brought in"),
    CODE_1016(1016, "没有带入结束时间", "No end time is brought in"),
    CODE_1017(1017, "没有带入页数", "No pages brought"),
    CODE_1018(1018, "没有带入日期", "No date brought in"),
    CODE_1019(1019, "没有带入游戏类型", "Not brought into the game type"),
    CODE_1020(1020, "没有带入游戏记录编号", "No game record number is included"),
    CODE_1021(1021, "赢分为0", "Win points are 0"),
    CODE_1022(1022, "找不到局号", "The bureau number cannot be found"),
    CODE_1023(1023, "非老虎机游戏", "Non-slot game"),
    CODE_1098(1098, "参数错误", "Error parameters"),
    CODE_1099(1099, "参数错误", "Error parameters"),


    ;

    private final int code;
    private final String desc;
    private final String descEn;

    public static FCErrorCodeEnum fromCode(int code) {
        for (FCErrorCodeEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }

    // 抽象方法：每个枚举值实现自己的返回逻辑
    public FCBaseRes toResVO(FCBaseRes resVO){
        resVO.setResult(this.code);
        return resVO;
    }

    public FCBaseRes toResVO(){
        return FCBaseRes.builder().Result(this.code).ErrorText(this.descEn).build();
    }
}
