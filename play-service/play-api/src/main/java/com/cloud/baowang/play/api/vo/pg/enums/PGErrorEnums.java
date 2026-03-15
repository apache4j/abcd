package com.cloud.baowang.play.api.vo.pg.enums;

import lombok.Getter;

@Getter
public enum PGErrorEnums {
	SUCCESS("0000", "成功"),
	CONFIG_ERROR("3017", "无效配置"),
	INVALID_REQUEST("1034", "无效请求"),
	ACTION_FAILED("1035", "行动失败"),
	SERVER_INTERNAL_ERROR("1200", "内部服务器错误"),
	INVALID_OPERATOR("1204", "无效运营商"),
	INVALID_PLAYER_TOKEN_1300("1300", "无效玩家令牌"),
	PLAYER_TOKEN_EMPTY("1301", "玩家令牌空置"),
	INVALID_PLAYER_TOKEN_1302("1302", "无效玩家令牌"),
	SERVER_ERROR_1303("1303", "发生服务器错误"),
	INVALID_PLAYER("1305", "无效玩家"),
	PLAYER_BLOCKED("1306", "玩家被阻止访问当前游戏"),
	INVALID_PLAYER_TOKEN_1307("1307", "无效玩家令牌"),
	PLAYER_TOKEN_EXPIRED("1308", "玩家令牌已过期"),
	PLAYER_BLOCKED_1309("1309", "玩家已被封锁"),
	OPERATOR_PLAYER_TOKEN_FAIL("1310", "运营商和玩家令牌验证失败"),
	PLAYER_OPERATION_IN_PROGRESS("1315", "玩家操作进行中"),
	GAME_MAINTENANCE("1400", "游戏正在维护中"),
	INVALID_GAME("1401", "游戏无效"),
	GAME_NOT_EXIST("1402", "游戏不存在"),
	NOT_NULL("3001", "不能空值"),
	USER_NOT_EXIST("3004", "玩家不存在"),
	WALLET_NOT_EXIST("3005", "玩家钱包不存在"),
	WALLET_ALREADY_EXIST("3006", "玩家钱包已存在"),
	FREE_GAME_NOT_EXIST("3009", "免费游戏不存在"),
	INSUFFICIENT_BALANCE_CANT_WITHDRAW("3013", "余额不足无法转出"),
	FREE_GAME_CANT_CANCEL("3014", "免费游戏无法取消"),
	INSUFFICIENT_FREE_GAME("3019", "免费游戏不足"),
	BET_NOT_EXIST("3021", "投注不存在"),
	BET_PAID("3022", "投注已支付"),
	FREE_GAME_EXPIRED("3030", "免费游戏过期"),
	FREE_GAME_CONVERTED("3031", "免费游戏已经转换"),
	BET_ALREADY_EXIST("3032", "投注已存在"),
	BET_FAILED("3033", "投注失败"),
	PAYMENT_FAILED("3034", "支付失败"),
	MULTIPLIER_ERROR("3035", "倍数错误"),
	INSUFFICIENT_BALANCE_CANT_CONVERT("3036", "余额不足无法转换"),
	TRANSACTION_NOT_EXIST("3040", "交易不存在"),
	INSUFFICIENT_BALANCE_CANT_BET("3202", "余额不足无法投注"),
	BET_FAILED_EXCEPTION("3073", "BetFailedException"),

	BET_AMOUNT("3107", "配置错误");
	private final String code;
	private final String message;
	PGErrorEnums(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
