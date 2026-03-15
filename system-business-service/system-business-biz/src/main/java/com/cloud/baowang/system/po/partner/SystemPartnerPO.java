package com.cloud.baowang.system.po.partner;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@TableName("system_partner")
@Data
public class SystemPartnerPO extends BasePO {
    /**
     * 赞助商名称
     */
    private String partnerName;
    /**
     * 赞助商图标
     */
    private String partnerIcon;
    /**
     * 启用状态0.禁用，1.启用
     */
    private Integer status;

}
