package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 代理账变记录
 * </p>
 *
 * @author qiqi
 * @since 2023-10-13
 */
@Getter
@Setter
@TableName("agent_coin_record")
@Schema(title = "代理账变记录对象")
public class AgentCoinRecordPO extends BasePO {

    private static final long serialVersionUID = 1L;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "代理名称")
    private String agentName;

    @Schema(description = "代理ID父节点")
    private String parentId;

    @Schema(description = "层次id逗号分隔")
    private String path;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "账号状态")
    private String accountStatus;


    @Schema(description = "风控级别Id")
    private String riskControlLevelId;

    @Schema(description = "风控级别")
    private String riskControlLevel;


    @Schema(description = "钱包类型 (1佣金钱包 2额度钱包）")
    private String walletType;

    @Schema(description = "关联订单号")
    private String orderNo;

    @Schema(description = "账变业务类型 ")
    private String businessCoinType;

    @Schema(description = "账变类型 (1充值 2提现)")
    private String coinType;

    @Schema(description = "客户端账变类型(1充值 2提现)")
    private String customerCoinType;

    @Schema(description = "收支类型1收入,2支出 3冻结 4 解冻")
    private String balanceType;

    @Schema(description = "账变前金额")
    private BigDecimal coinFrom;

    @Schema(description = "账变后金额")
    private BigDecimal coinTo;

    @Schema(description = "账变金额")
    private BigDecimal coinAmount;

    @Schema(description = "备注")
    private String remark;

}
