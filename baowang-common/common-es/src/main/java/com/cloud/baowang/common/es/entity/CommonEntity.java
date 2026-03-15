package com.cloud.baowang.common.es.entity;


/**
 * @Author: sheldon
 * @Date: 3/18/24 11:21 上午
 */
public interface CommonEntity {

    Long getCreateTime();

    void setCreateTime(Long createTime);

    Long getModifyTime();

    void setModifyTime(Long modifyTime);

    Integer getVersion();

    void setVersion(Integer version);
}
