package com.cloud.baowang.play.api.vo.evo;

import com.cloud.baowang.play.api.enums.evo.EvoErrorEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvoBaseRes<T> {

	@Schema(title = "发生错误或异常时的错误信息")
	private Error error;

	@Schema(title = "返回数据对象")
	private T data;

	public EvoBaseRes(T object) {
		this.data = object;
	}

	public EvoBaseRes(String code, String msg) {
		error = new Error();
		error.code = code;
		error.message = msg;
	}

	public static <T> EvoBaseRes<T> failed(EvoErrorEnums pgErrorEnum) {
		return new EvoBaseRes<>(pgErrorEnum.getCode(), pgErrorEnum.getMessage());
	}

	public static <T> EvoBaseRes<T> success(T data) {
		return new EvoBaseRes<>(data);

	}

	public static <T> EvoBaseRes<T> failed(EvoErrorEnums pgErrorEnum, String msg) {
		return new EvoBaseRes<>(pgErrorEnum.getCode(), msg);
	}

	@Data
	public static class Error {
		String message;
		String code;
	}
}
