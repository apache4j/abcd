package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * 图片管理表
 */
@Data
@TableName("agent_image")
public class AgentImagePO extends BasePO implements Serializable {

    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 图片
     */
    private String imageName;
    /**
     * 图片尺寸
     */
    private String imageSize;
    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 图片类型: 1=综合、2=体育、3=真人、4=电竞、5=彩票、6=棋牌、7=活动
     * {@link com.cloud.baowang.agent.api.enums.AgentImageTypeEnum}
     */
    private Integer imageType;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * {@link com.cloud.baowang.common.core.enums.EnableStatusEnum}
     */
    private Integer status;
    /**
     * 图片备注
     */
    private String remark;

}
