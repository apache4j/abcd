package com.cloud.baowang.user.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("site_new_user_guide_step_record")
public class SiteNewUserGuideStepRecordPO extends SiteBasePO implements Serializable {

    private static final long serialVersionUID = 1L;




    /**
     * 用户ID（唯一）
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount;

    /**
     * 完成步骤
     */
    private Integer step;


}
