package com.cloud.baowang.system.po.site;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_seo")
public class SiteSeoPO extends BasePO implements Serializable {

    /**
     *  站点CODE
     */
    private String siteCode;

    /**
     *  标题
     */
    private String title;

    /**
     *  网站摘要
     */
    private String meta;

    /**
     *  语言
     */
    private String lang;


}
