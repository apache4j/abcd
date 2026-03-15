package com.cloud.baowang.play.wallet.enums;

import lombok.Getter;

@Getter
public enum EvoErrorEnums {
	SUCCESS("0000", "成功"),
	;

	private final String code;
	private final String message;
	EvoErrorEnums(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
