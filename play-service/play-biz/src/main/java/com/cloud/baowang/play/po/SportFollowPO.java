package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sport_follow")
public class SportFollowPO extends BasePO {

    /**
     * 用户账号
     */
    private String userId;

    /**
     * 盘口类型:1:冠军，2:赛事,3:赛事+球类
     */
    private String type;

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 三方ID
     */
    private String thirdId;

    /**
     * 球类ID
     */
    private Integer sportType;


    /**
     * 备注
     */
    private String remark;

}
