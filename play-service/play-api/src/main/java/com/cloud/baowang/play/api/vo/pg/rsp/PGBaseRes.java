package com.cloud.baowang.play.api.vo.pg.rsp;

import com.cloud.baowang.play.api.vo.pg.enums.PGErrorEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PGBaseRes<T> {

	@Schema(title = "发生错误或异常时的错误信息")
	private Error error;

	@Schema(title = "返回数据对象")
	private T data;

	public PGBaseRes(T object) {
		this.data = object;
	}

	public PGBaseRes(String code, String msg) {
		error = new Error();
		error.code = code;
		error.message = msg;
	}

	public static <T> PGBaseRes<T> failed(PGErrorEnums pgErrorEnum) {
		return new PGBaseRes<>(pgErrorEnum.getCode(), pgErrorEnum.getMessage());
	}

	public static <T> PGBaseRes<T> success(T data) {
		return new PGBaseRes<>(data);

	}

	public static <T> PGBaseRes<T> failed(PGErrorEnums pgErrorEnum, String msg) {
		return new PGBaseRes<>(pgErrorEnum.getCode(), msg);
	}

	@Data
	public static class Error {
		String message;
		String code;
	}
}
