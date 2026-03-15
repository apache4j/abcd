package com.cloud.baowang.activity.api.vo.test;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "pp免费游戏类test，更新免费旋转使用记录")
@Data
public class PPFreeGameRecordReqVOTest extends MessageBaseVO {

    @Schema(description = "会员id")
    private String userId;



    @Schema(description = "获取来源订单号 唯一值 做防重处理,用于查找送的记录")
    private String orderNo;



    @Schema(description = "消费次数")
    private Integer acquireNum;


    @Schema(description = "三方注单号码")
    private String betId;


    @Schema(description = "免费旋转派奖金额")
    private BigDecimal payOutAmount;


    public boolean isValid() {
        return
            StrUtil.isNotEmpty(this.getUserId()) &&
            StrUtil.isNotEmpty(this.getOrderNo()) &&
            ObjUtil.isNotNull(this.getAcquireNum()) &&
            StrUtil.isNotEmpty(this.getBetId())
        ;
    }
}
