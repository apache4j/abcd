package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Schema(description = "IP归属币种请求类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpAddressAreaCurrencyQueryReqVO extends PageVO  {

    @Schema(description = "id")
    //@NotNull
    private String id;

    /**
     *  分类名称
     */
    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     *  包含国家
     */
    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "包含国家")
    private List<AreaVO> areaNameList;


    /**
     *  映射币种
     */
    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "映射币种")
    private String currencyCode;

    /**
     *  映射币种
     */
    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "映射币种")
    private String currencyName;


    /**
     *  优先级
     */
    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "优先级")
    private Integer orderSort;

    /**
     *  状态: (1 开启中 2 维护中 3 已禁用)
     */
    @Schema(description = "状态: (1 开启 0 禁用)")
    private Integer status;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "更新人")
    private String updater;

    /**
     *  备注
     */
    @Schema(description = "备注")
    private String remark;


}
