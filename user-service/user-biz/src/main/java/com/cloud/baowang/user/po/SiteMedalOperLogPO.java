package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 勋章变更记录表
 * </p>
 *
 * @author ford
 * @since 2024-08-15 04:38:04
 */
@Getter
@Setter
@TableName("site_medal_oper_log")
public class SiteMedalOperLogPO extends BasePO {

    /**
     * 站点代码 -1代表总站默认值
     */
    private String siteCode;

    /**
     * 站点勋章主键
     */
    private String siteMedalId;

    /**
     * 勋章代码
     */
    private String medalCode;

    /**
     * 勋章名称
     */
    private String medalName;

    /**
     * 操作时间
     */
    private Long operTime;

    /**
     * 操作项
     */
    private String operItem;

    /**
     * 操作项多语言
     */
    private String operItemI18;

    /**
     * 变更前信息
     */
    private String operBefore;

    /**
     * 变更后信息
     */
    private String operAfter;

}
