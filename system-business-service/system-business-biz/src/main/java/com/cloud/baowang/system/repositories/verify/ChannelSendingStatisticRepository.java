package com.cloud.baowang.system.repositories.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.ChannelSendingStatisticPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChannelSendingStatisticRepository extends BaseMapper<ChannelSendingStatisticPO> {

    Page<ChannelSendingStatisticVO> queryChannelStatistic(@Param("page") Page<ChannelSendingStatisticPO> page, @Param("vo") ChannelSendStatisticQueryVO queryVO);

    Long queryChannelStatisticCount(@Param("vo") ChannelSendStatisticQueryVO queryVO);

    Page<ChannelSendDetailsRspVO> getChannelSendDetails(@Param("page") Page<ChannelSendingStatisticPO> page, @Param("vo") SiteInfoVO queryVO) ;

    long queryChannelSendCount(@Param("vo") SiteInfoVO queryVO);

    Page<ChannelSendingStatisticVO> queryChannelEmailStatistic(@Param("page") Page<ChannelSendingStatisticPO> page, @Param("vo") ChannelSendStatisticQueryVO queryVO);
}
