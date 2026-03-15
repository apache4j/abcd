package com.cloud.baowang.system.po.versions;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("system_version_manager")
public class SystemVersionManagerPO extends BasePO {

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 平台类型--文案与deviceTerminal实际返回内容不一致,故使用新增的枚举类型
     * {@link com.cloud.baowang.system.api.enums.versions.VersionMobilePlatform}
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
     * 文件大小（字节）
     */
    private Long fileSize;
    /**
     * 文件地址
     */
    private String fileUrl;


    /**
     * 更新状态（使用 VersionUpdateStatus 枚举）
     * {@link com.cloud.baowang.system.api.enums.versions.VersionUpdateStatus}
     */
    private Integer versionUpdateStatus;

    /**
     * 更新描述
     */
    private String updateDescription;

}
