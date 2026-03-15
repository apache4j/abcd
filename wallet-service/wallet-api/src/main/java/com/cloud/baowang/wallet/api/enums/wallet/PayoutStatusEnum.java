package com.cloud.baowang.wallet.api.enums.wallet;

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
public enum PayoutStatusEnum {
	Pending(0, "处理中"),
	Success(1, "成功"),
	Fail(2, "失败"),
    Abnormal(3, "异常状态"),
    NotExist(4, "订单号不存在"),
    NotEnough(5, "余额不足"),
    ;

    private final Integer code;
    private final String name;
    private final CodeNameVO codeNameVO;

    PayoutStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
        this.codeNameVO = CodeNameVO.builder().code(String.valueOf(code))
                .name(name).build();
    }
    
    public static List<CodeNameVO> getCodeNameVOList() {
    	return Arrays.asList(values()).stream()
    			.collect(Collectors.mapping(PayoutStatusEnum::getCodeNameVO, Collectors.toList()));
    }

    public static WithdrawalStatusEnum nameOfCode(Integer code) {
        if (code == null) {
            return null;
        }
        WithdrawalStatusEnum[] types = WithdrawalStatusEnum.values();
        for (WithdrawalStatusEnum type : types) {
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
