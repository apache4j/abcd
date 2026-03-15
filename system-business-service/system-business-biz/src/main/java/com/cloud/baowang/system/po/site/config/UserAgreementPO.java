package com.cloud.baowang.system.po.site.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TableName("user_agreement")
public class UserAgreementPO extends BasePO {
    private String id;
    private String siteCode;
    private Long createdTime; // 创建时间
    private Long updatedTime; // 更新时间
    private String updater; // 更新者
    private String optionType;//配置选项
    private Integer code;//排序标记
    private String optionValue;
    private String optionValueExtend;
    private String telegram;
    private String skype;
}
