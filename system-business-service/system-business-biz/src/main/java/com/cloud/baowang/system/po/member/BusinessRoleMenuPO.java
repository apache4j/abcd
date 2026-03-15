package com.cloud.baowang.system.po.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author qiqi
 */
@Data
@TableName("business_role_menu")
public class BusinessRoleMenuPO {

   // @TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;

    /**
     * 角色ID
     */
    private String roleId;

    /**
     * 菜单ID
     */
    private String menuId;


    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id= SnowFlakeUtils.getSnowId();
        }
        return id;
    }

}
