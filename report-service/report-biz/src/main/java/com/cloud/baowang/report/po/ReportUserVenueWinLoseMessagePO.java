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
@TableName("report_user_venue_win_lose_message")
@Schema(title = "场馆盈亏-消息体")
public class ReportUserVenueWinLoseMessagePO extends BasePO {

    @Schema(title = "消息类型 订单号码")
    private String typeOrder;

    @Schema(title = "消息体")
    private String jsonStr;

    @Schema(title = "消息体 对应的唯一UUID")
    private String jsonStrUuid;

    @Schema(title = "处理结果 0:失败,1:成功")
    private Integer status;


}
