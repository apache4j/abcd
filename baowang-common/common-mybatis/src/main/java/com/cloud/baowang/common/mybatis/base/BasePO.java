package com.cloud.baowang.common.mybatis.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
@FieldNameConstants
public class BasePO implements Serializable {

   // @TableId(type = IdType.ASSIGN_ID)
    @TableId
    private String id;
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @TableField(fill = FieldFill.INSERT, value = "created_time")
    private Long createdTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updater;


    @TableField(fill = FieldFill.UPDATE, value = "updated_time")
    private Long updatedTime;

    public String getId(){
        if(!StringUtils.hasText(id)){
            this.id=SnowFlakeUtils.getSnowId();
        }
        return id;
    }
}
