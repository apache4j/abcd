package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏平台列表参数对象")
public class VenueInfoRequestVO extends PageVO {

    @Schema(description = "游戏平台id")
    private String id;

    @Schema(description = "游戏平台id")
    private List<String> ids;

    @Schema(description = "游戏平台名称")
    private String venueName;

    @Schema(description = "字典CODE:venue_type 场馆类型 1:体育,2:视讯,3:棋牌,4:电子,5:彩票,6:斗鸡,7:电竞")
    private Integer venueType;

    @Schema(description = "币种类型 字典CODE:venue_currency_type")
    private Integer venueCurrencyType;

    @Schema(description = "游戏平台code")
    private String venueCode;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "游戏平台CODE")
    private List<String> venueCodeList;

    @Schema(description = "平台名称")
    private List<String> venuePlatformNameList;

    @Schema(description = "状态 字典code:platform_class_status_type")
    private Integer status;

    @Schema(description = "状态 字典code:platform_class_status_type")
    private List<Integer> statusList;

    @Schema(description = "操作人")
    @Length(max = 10, message = ConstantsCode.NO_HAVE_DATA)
    private String updater;

    @Schema(description = "商户编码")
    private String merchantNo;

    @Schema(description = "商户密钥")
    private String merchantKey;

    @Schema(description = "操作人")
    private String creator;

    @Schema(description = "查询站点场馆",hidden = true)
    private Boolean siteType;

    @Schema(description = "场馆费率类型 字典CODE:venue_proportion_type")
    private Integer proportionType;

    @Schema(description = "站点",hidden = true)
    private String siteCode;

    @Schema(description = "场馆接入类型: 字典CODE:venue_join_type")
    private Integer venueJoinType;



}
