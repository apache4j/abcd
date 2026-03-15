package com.cloud.baowang.wallet.api.vo.userDividend;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author : 小智
 * @Date : 21/2/24 5:35 PM
 * @Version : 1.0
 */
@Schema(title = "用户红利传入对象")
@Data
public class UserDividendRequestVO extends PageVO implements Serializable {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "下级代理集合", hidden = true)
    private List<String> agentIds;

    @Schema(description = "发放开始时间")
    private Long beginTime;

    @Schema(description = "发放结束时间")
    private Long endTime;

    @Schema(description = "红利类型 数据字典code: user_dividend_type")
    private String dividendType;

    @Schema(description = "红利状态 数据字典code: dividend_status")
    private String dividendStatus;

    @Schema(description = "当前系统时间戳", hidden = true)
    private Long systemTime;

}
