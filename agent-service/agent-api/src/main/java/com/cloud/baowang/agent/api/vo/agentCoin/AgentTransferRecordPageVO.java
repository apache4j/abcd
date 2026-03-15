package com.cloud.baowang.agent.api.vo.agentCoin;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author : 小智
 * @Date : 26/10/23 5:24 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账记录查询返回分页对象")
public class AgentTransferRecordPageVO implements Serializable {

    @Schema(description ="订单号")
    private String orderNo;

    @Schema(description ="转出代理账号")
    private String fromTransAccount;

    @Schema(description ="转出代理层级")
    private Integer fromTransLevel;

    @Schema(description ="转入代理账号")
    private String toTransAccount;

    @Schema(description ="转出钱包code")
    private String agentWalletType;

    @Schema(description ="转出钱包名称")
    private String agentWalletTypeName;

    @Schema(description ="订单状态")
    private Integer orderStatus;

    @Schema(description ="订单状态名称")
    private String orderStatusName;

    @Schema(description ="转账金额")
    private BigDecimal transferAmount;

    @Schema(description ="转账时间")
    private Long transferTime;

    public String getTransferTimeStr() {
        return transferTime == null ? null : TimeZoneUtils.formatTimestampToTimeZone(transferTime, CurrReqUtils.getTimezone());
    }

    @Schema(description ="转账时间")
    private String transferTimeStr;

    @Schema(description ="备注")
    private String remark;

}
