package com.cloud.baowang.common.es.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author: sheldon
 * @Date: 3/18/24 10:28 上午
 */

@Data
@Slf4j
public class BaseEntityAssign implements CommonEntity, Serializable {
    //ID
    @JsonSerialize(
            using = ToStringSerializer.class
    )
    //创建时间
    private Long createTime;
    private Long modifyTime;
    //版本号
    @Version
    private Integer version;

    //是否删除： true 删除 false 未删除
    //@TableLogic
    //private Boolean deleted;

}
