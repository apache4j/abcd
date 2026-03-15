package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 消息配置表
 * </p>
 *
 * @author 作者
 * @since 2024-10-31
 */
@Data
@Schema(description = "消息配置表")
@TableName("system_message_config")
public class SystemMessageConfigPO extends BasePO implements Serializable {



    @Schema(description = "用户类别（0:用户；1:代理）")
    private String userType;


    @Schema(description = "消息类型")
    private String messageType;

    @Schema(description = "标题")
    private String title;


    @Schema(description = "消息")
    private String message;


    @Schema(description = "说明")
    private String remark;



    @Schema(description = "语言")
    private String language;

}
