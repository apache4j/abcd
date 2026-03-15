package com.cloud.baowang.system.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "i18n_message")
public class I18NMessagePO extends BasePO {
    @TableField(value = "message_type")
    private String messageType;
    @TableField(value = "message_key")
    private String messageKey;
    private String language;
    private String message;
}
