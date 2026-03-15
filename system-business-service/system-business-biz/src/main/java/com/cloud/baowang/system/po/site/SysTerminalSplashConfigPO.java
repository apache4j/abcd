package com.cloud.baowang.system.po.site;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * 闪屏页实体类，表示一个终端设备的闪屏页配置信息
 * 使用 MyBatis Plus 和 Lombok 注解简化代码
 */
@Data
@NoArgsConstructor  // 无参构造方法
@AllArgsConstructor // 全参构造方法
@TableName("sys_terminal_splash_config")  // 指定与数据库表映射
public class SysTerminalSplashConfigPO extends BasePO implements Serializable {


    /**
     * 闪屏页的名称
     */
    private String name;

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 显示终端  3-IOS_APP, 5-Andriod_APP
     */
    private String terminal;
    /**
     * 状态（1:启用；0:禁用）
     */
    private Integer status;

    /**
     * 闪屏图图片i18
     */
    private String bannerUrl;

    /**
     *  时效（0:LimitTime，1:Permanent）
     */
    private String validityPeriod;

    /**
     * 闪屏页的开始时间（当时效为LimitTime时才有效）
     */
    private Long startTime;

    /**
     * 闪屏页的结束时间（当时效为LimitTime时才有效）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED, insertStrategy = FieldStrategy.IGNORED)
    private Long endTime;

    /**
     * 闪屏页显示时长，以秒为单位
     */
    private Integer displayDuration;


}
