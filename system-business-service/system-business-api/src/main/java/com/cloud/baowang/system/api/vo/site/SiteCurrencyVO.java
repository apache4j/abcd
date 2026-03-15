package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/8/23 13:51
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="站点归属币种对象")
public class SiteCurrencyVO {

    @Schema(description ="站点code")
    private String siteCode;

    @Schema(description ="站点归属币种集合")
    private List<CodeValueVO> currency;

}
