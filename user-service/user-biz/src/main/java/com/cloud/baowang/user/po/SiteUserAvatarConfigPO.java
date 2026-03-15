package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("site_user_avatar_config")
public class SiteUserAvatarConfigPO extends BasePO {
    /**
     * 站点代码
     */
    private String siteCode;
    /**
     * 头像id
     */
    private String avatarId;
    /**
     * 头像名称
     */
    private String avatarName;
    /**
     * 头像图片地址
     */
    private String avatarImageUrl;
    /**
     * 启用禁用状态
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
