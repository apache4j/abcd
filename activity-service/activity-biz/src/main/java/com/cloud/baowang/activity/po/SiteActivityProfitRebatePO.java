package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @className: SiteActivityRewardVipGradePO
 * @author: wade
 * @description:
 * @date: 7/9/24 15:39
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_profit_rebate")
public class SiteActivityProfitRebatePO extends BasePO {


    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 注册天数
     */
    private Integer registerDay;

    /**
     * 状态: 0 - 全体会员, 1 - 新注册会员
     */
    private Integer userType;

    /**
     * 场馆类型
     */
    private Integer venueType;

    /**
     * venueCode
     */
    private String venueCode;

    /**
     * 优惠方式: 0 - 百分比, 1 - 固定金额
     */
    private Integer activityDiscountType;

    /**
     * 活动详情配置, 存数组
     */
    private String activityDetail;

    /**
     * 结算周期: 0 - 日结, 1 - 周结, 2 - 月结
     */
    private Integer calculateType;

    /**
     * 奖励上限
     */
    private BigDecimal upperLimit;

    /**
     * 派发方式: 0 - 玩家自领-过期作废, 1 - 玩家自领-过期自动派发, 2 - 立即派发
     */
    private Integer distributionType;

    /**
     * 领取方式: 0 - 次日领取, 1 - 每日领取
     */
    private Integer receiveType;

    /**
     * 领取时间。0表示周期结束才过期
     */
    private String receiveDate;


    /**
     * 参与方式,0.手动参与，1.自动参与
     */
    private Integer participationMode;


}
