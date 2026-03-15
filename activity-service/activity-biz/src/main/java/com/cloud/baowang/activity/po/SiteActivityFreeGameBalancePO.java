package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 免费旋转次数余额表实体类
 */
@Data
@TableName(value = "site_activity_free_game_balance")
public class SiteActivityFreeGameBalancePO extends SiteBasePO implements Serializable {

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "当前次数余额")
    private Integer balance;

    @Schema(description = "平台编号")
    private String venueCode;

}
