package com.cloud.baowang.user.api.vo.medal;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/29 09:52
 * @Version: V1.0
 **/
@Data
@Schema(description = "宝箱奖励返回结果")
public class MedalRewardRespVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;

    @Schema(description = "宝箱编号")
    private Integer rewardNo;

    /**
     * 宝箱解锁条件数
     */
    @Schema(description = "宝箱解锁条件数")
    private Integer unlockMedalNum;

    /**
     * 奖励金额
     */
    @Schema(description = "奖励金额")
    @JsonFormat(pattern = "0.00")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rewardAmount;

    /**
     * 打码倍数
     */
    @Schema(description = "打码倍数")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal typingMultiple;


    @Schema(description = "会员账号")
    private String userAccount;

    /**
     * 解锁勋章数
     */
    @Schema(description = "宝箱解锁条件数")
    private Integer condNum;


    @Schema(description = "打开状态 0:可打开 1:已打开 2:未获得")
    private int openStatus;

}
