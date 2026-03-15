package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "异常注单历史信息对象")
public class OrderHistoryVO implements Serializable {
    @Schema(description = "注单信息", title = "注单信息")
    private OrderAbnormalDetailVO orderAbnormalDetailVO;
    @Schema(title = "串关信息列表")
    private List<Map<String, Object>> gameDetailList;
}
