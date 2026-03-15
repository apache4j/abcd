package com.cloud.baowang.system.api.vo.site;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/26 13:42
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="所有站点列表返回对象")
@I18nClass
public class SiteAllVO {

    /* 站点编号 */
    @Schema(description = "站点编号")
    private String siteCode;

    /* 站点名称 */
    @Schema(description = "站点名称")
    private String siteName;

    /* 所属公司 */
    @Schema(description = "所属公司")
    private String company;

    /* 站点类型 */
    @Schema(description = "站点类型")
    private Integer siteType;


}
