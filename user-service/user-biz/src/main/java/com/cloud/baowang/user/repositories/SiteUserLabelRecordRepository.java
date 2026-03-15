package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.SiteUserLabelRecordsPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SiteUserLabelRecordRepository extends BaseMapper<SiteUserLabelRecordsPO> {
}
