package com.cloud.baowang.system.repositories.exchange;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoReqVO;
import com.cloud.baowang.system.po.exchange.SystemCurrencyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemCurrencyInfoRepository extends BaseMapper<SystemCurrencyInfo> {

    IPage<SystemCurrencyInfo> listPage(@Param("page") Page<SystemCurrencyInfo> page, @Param("vo") SystemCurrencyInfoReqVO vo);
}
