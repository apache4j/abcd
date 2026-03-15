package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 8/5/23 4:45 PM
 * @Version 1.0
 */
@Data
@TableName("site_vip_change_record")
public class VIPChangePO extends BasePO implements Serializable {

    private String changeType;

    private String operationType;

    private String beforeChange;

    private String afterChange;

    private String userAccount;

    private String accountType;

    private String userLabel;

    private String controlRank;

    private String accountStatus;

    private String operator;

    private String siteCode;

    private Long changeTime;
}
