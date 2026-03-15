package com.cloud.baowang.system.po.tutorial;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tutorial_category")
public class TutorialCategoryPO {
    private String id;
    private String siteCode;
    private String siteName;
    private String nameCn;
    private String nameUs;
    private String nameBr;
    private String nameVn;
    private String imgKey;
    private Integer status;
    private String creator;
    private String operator;
    private Long createTime;
    private Long updateTime;
    private int sort;

}
