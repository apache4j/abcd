package com.cloud.baowang.system.po.verify;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:25
 * @description: 邮箱站点关联表
 */
@Data
@TableName("mail_site_link")
public class MailSiteLinkPO extends BasePO {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 通道代码
     */
    private String channelCode;
    /**
     * 状态 0 禁用 1启用
     */
    private String status;

}
