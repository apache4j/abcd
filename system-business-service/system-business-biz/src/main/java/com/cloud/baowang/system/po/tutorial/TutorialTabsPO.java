package com.cloud.baowang.system.po.tutorial;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tutorial_tabs")
public class TutorialTabsPO {
    private String id;
    private String siteCode;
    private String siteName;
    private String NameCn;
    private String NameUs;
    private String nameBr;
    private String nameVn;
    private String tutorialIcon;
    private Integer status;
    private String creator;
    private String operator;
    private Long createTime;
    private Long updateTime;
    private String categoryName;
    private Long categoryId;
    private String className;
    private Long classId;
    private int sort;


}
