package com.cloud.baowang.play.api.vo.AbnormalOrder;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "异常注单详情返回对象")
public class OrderAbnormalDetailLabelVO implements Serializable {
    @Schema(description = "历史详情信息List", title = "历史详情信息List")
    List<OrderHistoryVO> historyVOList;
    @Schema(description = "注单信息标签", title = "注单信息标签")
    private List<CodeValueNoI18VO> label;
    @Schema(description = "串关列表信息标签", title = "串关列表信息标签")
    private List<CodeValueNoI18VO> tableLabel;
}
