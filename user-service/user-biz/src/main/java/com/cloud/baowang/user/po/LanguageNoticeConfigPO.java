package com.cloud.baowang.user.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;


/**
 * 场馆多语言游戏名称配置实体类
 * (适用于MybatisPlus;该文件自动生成，请勿修改)
 *
 * @author 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("language_notice_config")
public class LanguageNoticeConfigPO extends BasePO {


    /**
     * 业务code:title:代表标题,content:内容
     */
    private String code;

    /**
     * 关联关系ID
     */
    private String paramId;

    /**
     * 多语言名称
     */
    private String name;

    /**
     * 语言
     */
    private String language;


}