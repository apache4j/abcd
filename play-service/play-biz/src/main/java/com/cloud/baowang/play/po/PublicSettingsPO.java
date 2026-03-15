package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("public_settings")
public class PublicSettingsPO extends BasePO {


    /**
     * 用户账号
     */
    private String userId;


     /**
     * 配置标记
     */
    private String type;


    /**
     * 值
     */
    private String value;

    /**
     * 备注
     */
    private String remark;

}
