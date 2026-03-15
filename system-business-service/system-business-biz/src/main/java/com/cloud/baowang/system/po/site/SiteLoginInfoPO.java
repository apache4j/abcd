package com.cloud.baowang.system.po.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.Data;
import org.springframework.util.StringUtils;


/**
 * @author qiqi
 */
@Data
@TableName("site_login_info")
public class SiteLoginInfoPO {

    //@TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;


    private String userName;

    /**
     * 状态 0成功 1失败
     */
    private Integer status;

    /**
     * 地址
     */
    private String ipaddr;

    /**
     * 描述
     */
    private String msg;

    /**
     * 访问时间
     */
    private Long accessTime;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 终端设备号
     **/
    private String deviceCode;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 站点codes
     */
    @TableField(value = "site_code")
    private String siteCode;

    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id= SnowFlakeUtils.getSnowId();
        }
        return id;
    }
}
