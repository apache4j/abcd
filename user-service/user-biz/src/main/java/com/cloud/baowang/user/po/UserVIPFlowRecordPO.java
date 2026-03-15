package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @Author : 小智
 * @Date : 29/6/23 6:19 PM
 * @Version : 1.0
 */
@Data
@Accessors(chain = true)
@TableName("user_vip_flow_record")
@Schema(title = "会员VIP流水记录")
public class UserVIPFlowRecordPO extends BasePO implements Serializable {

    /* 会员账号 */
    private String userAccount;

    /* 会员ID */
    private String userId;

    /* vip等级code */
    private Integer vipGradeCode;

    /* VIP升降级标识(0:升级,1:降级,2:保级) */
    private String status;

    /* 单次有效流水金额 */
    private BigDecimal validExe;

    /* 该会员累计有效流水 */
    private BigDecimal validSumExe;

    private String lastVipTime;

    /**
     * 站点code
     */
    @TableField(value = "site_code")
    private String siteCode;


    public BigDecimal getValidExe() {
        return Optional.ofNullable(validExe).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getValidSumExe() {
        return Optional.ofNullable(validSumExe).orElse(BigDecimal.ZERO);
    }

}
