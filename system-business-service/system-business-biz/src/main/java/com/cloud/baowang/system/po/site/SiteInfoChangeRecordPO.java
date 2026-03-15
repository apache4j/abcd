package com.cloud.baowang.system.po.site;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : mufan
 * @Date : 2025/4/7 11:16
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_info_change_record")
public class SiteInfoChangeRecordPO extends BasePO implements Serializable {

    /**
     * 操作对象名称，对应站点名称
     */
    private String optionCode;
    /**
     * 操作对象code，对应站点编码
     */
    private String optionName;
    /**
     * 操作模块名称，对应站点列表
     */
    private String optionModelName;
    /**
     * 操作类型 0:新增、1:修改,2:删除
     */
    private Integer optionType;
    /**
     * 状态(0:失败,1:成功) SiteOptionStatusEnum
     */
    private Integer optionStatus;

    /**
     * 登入ip
     */
    private String loginIp;

    /**
     * 变更后的状态描述json保存
     */
    private String changeAfter;

}
