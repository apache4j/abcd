package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@TableName("site_user_invite_icon")
public class SiteUserInviteIconPO extends BasePO implements Serializable {
    //site_user_invite_config 主键
    private String configId;
    //语言
    private String language;
    //图片地址
    private String iconUrl;
    //终端类型  1 PC  2 移动端
    private String deviceType;
}
