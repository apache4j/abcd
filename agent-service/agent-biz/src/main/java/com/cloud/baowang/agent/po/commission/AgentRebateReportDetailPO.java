package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/03 22:22
 * @description: agent_rebate_report_detail
 */
@Data
@TableName("agent_rebate_report_detail")
@FieldNameConstants
@Schema(title = "AgentRebateReportDetailPO", description = "返点详情表")
public class AgentRebateReportDetailPO {
    //@TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;
    /** 返点表ID */
    private String rebateReportId;
    /** 场馆类型 */
    private Integer venueType;
    /** 币种 */
    private String currency;
    /** 有效流水 */
    private BigDecimal validAmount;
    /** 返点比例 */
    private String rebateRate;
    /** 有效流水返点结算金额 **/
    private BigDecimal rebateAmount;

    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id= SnowFlakeUtils.getSnowId();
        }
        return id;
    }
}
