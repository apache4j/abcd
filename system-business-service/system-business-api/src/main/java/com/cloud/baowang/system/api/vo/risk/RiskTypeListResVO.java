package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="风控类型下拉框返回对象")
public class RiskTypeListResVO extends CodeValueVO implements Serializable {
    @Schema(title = "label")
    private String label;
}
