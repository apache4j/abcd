package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@TableName("agent_image_record")
public class AgentImageRecordPO extends BasePO implements Serializable {

    private String siteCode;
    /**
     * 图片Id
     */
    private String imageName;
    /**
     * 变更类型
     */
    private Integer recordType;
    /**
     * 变更前
     */
    private String beforeText;
    /**
     * 变更后
     */
    private String afterText;
    /**
     * 备注
     */
    private String remark;

}
