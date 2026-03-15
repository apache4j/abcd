package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 会员每日盈亏-MQ消息体
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("report_user_win_lose_message")
@Schema(title = "会员每日盈亏-消息体")
public class ReportUserWinLoseMessagePO extends BasePO {

    @Schema(title = "消息类型 1下注 2结算 3人工加额 4人工减额 5优惠活动 6会员返水 7会员VIP福利")
    private Integer type;

    /**
     *
     */
    @Schema(title = "消息类型 订单号码")
    private String typeOrder;


    @Schema(title = "消息体")
    private String jsonStr;

    @Schema(title = "消息体 对应的唯一UUID")
    private String jsonStrUuid;

    @Schema(title = "处理结果 0:失败,1:成功 2:参数不全 3：重复消费")
    private Integer status;


}
