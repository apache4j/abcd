package com.cloud.baowang.system.po.tutorial;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;


@Getter
@Setter
@TableName("tutorial_operation_record")
@Builder
public class TutorialOperationRecordPO {

    private String id;
    private String siteCode;

    private String siteName;

    private Long updateTime;

    private String changeCatalog;

    private String changeType;

    private String beforeChange;

    private String afterChange;

    private String operator;

    private String typeMark;

    private Integer beforeStatus;

    private Integer afterStatus;

}
