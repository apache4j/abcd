package com.cloud.baowang.system.po.versions;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("system_version_change_record")
public class SystemVersionChangeRecordPO extends BasePO {

    /**
     * 站点编码，标识所属站点
     */
    private String siteCode;
    /**
     * 站点名称
     */

    private String siteName;

    /**
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
     * 平台类型（使用 VersionMobilePlatform 枚举）
     */
    private Integer deviceTerminal;
    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 版本代码
     */
    private String versionCode;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 版本更新状态（使用 VersionUpdateStatus 枚举）
     * {@link com.cloud.baowang.system.api.enums.versions.VersionUpdateStatus}
     */
    private Integer versionUpdateStatus;
    /**
     * 变更前的状态描述
     */
    private Integer changeBefore;

    /**
     * 变更后的状态描述
     */
    private Integer changeAfter;
    /**
     * 变更前文件地址
     */
    private String beforeFile;
    /**
     * 变更后文件地址
     */
    private String afterFile;

}
