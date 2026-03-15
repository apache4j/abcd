package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@TableName("db_sport_transfer_record_detail")
@Schema(title = "DB体育场馆转账详情记录")
public class DbSportTransferRecordDetailPO extends BasePO {


    /**
     * 场馆
     */
    private String venueCode;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 下注订单号
     */
    private String betId;

    /**
     * 金额
     */
    private BigDecimal amount;



    /**
     * 订单状态: DbPanDaSportBizTypeEnum.java
     * 1:投注(扣款),
     * 2:结算派彩(加款),
     * 3:注单取消(加款),
     * 4:注单取消回滚(扣款),
     * 5:结算回滚(扣款),
     * 6:拒单 (加款),
     * 9:提前部分结算（加款),
     * 10:提前全额结算（加款),
     * 11:提前结算取消（扣款),
     * 12:提前结算取消回滚（加款),
     * 13:人工加款（加款),
     * 14:人工扣款 (扣款),
     * 20:用户预约下注(扣款),
     * 21:用户预约投注取消（加款）
     */
    private Integer orderStatus;

    /**
     * DbPanDaBetTypeEnum.java
     * 0:未确认,1:已确认,2:已取消
     */
    private Integer type;

    /**
     * 结算次数
     */
    private Integer settleCount;

    /**
     * DbPanDaSportTransferTypeEnum.java
     * 1:加款,2:扣款
     */
    private Integer transferType;



}
