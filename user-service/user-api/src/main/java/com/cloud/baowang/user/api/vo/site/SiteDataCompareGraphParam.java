package com.cloud.baowang.user.api.vo.site;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author: wade
 */
@Data
@Schema(description = "数据对比 曲线图 Param")
public class SiteDataCompareGraphParam {


    @Schema(title = "所选时间戳")
    private Long time;
    @Schema(title = "数据类型 1-日，2-月，3-年")
    private Integer timeType;

    /**
     * {@link com.cloud.baowang.user.enums.SiteDataCompareGraphEnum}
     */
    @Schema(description = "数据类型  1新注册人数 2首存人数 3.登录人数,4.存款金额 5.取款金额 6.总输赢, 7.投注人数," +
            " 8.会员存款人数, 9.会员提款人数, 10.平台游戏输赢,11.平台净输赢, 12.平台盈亏, 13.有效注单数量, 14.有效投注金额")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer dataType ;

    @Schema(title = "站点", hidden = true)
    private String siteCode;

    @Schema(title = "时区", hidden = true)
    private String timeZone;

    @Schema(title = "币种")
    private String currencyCode;

    @Schema(description = "是否转换为平台币",hidden = true)
    private Boolean convertPlatCurrency = Boolean.FALSE;

    @Schema(title = "开始时间戳")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    Long startTime;

    @Schema(title = "结束时间戳")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    Long endTime;

}
