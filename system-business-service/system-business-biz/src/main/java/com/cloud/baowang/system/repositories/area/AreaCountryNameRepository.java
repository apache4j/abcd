package com.cloud.baowang.system.repositories.area;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.area.AreaCountryNamePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AreaCountryNameRepository extends BaseMapper<AreaCountryNamePO> {
    void deleteByCode(@Param("areaCode") String areaCode);
}
