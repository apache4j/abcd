package com.cloud.baowang.wallet.api.vo.pay;

import com.cloud.baowang.common.core.vo.base.CodeNameVO;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代付订单状态
 *
 */
@Getter
public enum ThirdPayOrderStatusEnum {

	Pending(0, "处理中"),
	Success(1, "成功"),
	Fail(2, "失败"),
    Abnormal(3, "异常状态"),
    NotExist(4, "订单号不存在"),
    ErrorAmount(5, "金额不一致"),
    ;

    private Integer code;
    private String name;
    private CodeNameVO codeNameVO;

    ThirdPayOrderStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static List<CodeNameVO> getCodeNameVOList() {
    	return Arrays.asList(values()).stream()
    			.collect(Collectors.mapping(ThirdPayOrderStatusEnum::getCodeNameVO, Collectors.toList()));
    }

    public static ThirdPayOrderStatusEnum nameOfCode(Integer code) {
        if (code == null) {
            return null;
        }
        ThirdPayOrderStatusEnum[] types = ThirdPayOrderStatusEnum.values();
        for (ThirdPayOrderStatusEnum type : types) {
            if (code.equals(type.getCode())) {
                return type;
            }
        }
        return null;
    }

//    public static List<AgentOrderCustomerStatusEnum> getList() {
//        return Arrays.asList(values());
//    }

}
