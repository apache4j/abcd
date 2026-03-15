package com.cloud.baowang.play.api.vo.order;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRecordAdminInfoResVO {
    @NotBlank(message = ConstantsCode.NO_HAVE_DATA)
    private String orderId;
}
