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
@TableName("tutorial_class")
public class TutorialClassPO {
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
    private String categoryName;
    private String categoryId;
    private int sort;

}
