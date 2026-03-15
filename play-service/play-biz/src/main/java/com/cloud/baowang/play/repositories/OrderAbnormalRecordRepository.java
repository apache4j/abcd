package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.play.api.vo.AbnormalOrder.OrderAbnormalRecordAdminResVO;
import com.cloud.baowang.play.po.OrderAbnormalRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 异常注单 Mapper 接口
 *
 */
@Mapper
public interface OrderAbnormalRecordRepository extends BaseMapper<OrderAbnormalRecordPO> {
    Page<OrderAbnormalRecordPO> getPage(Page<OrderAbnormalRecordPO> page, @Param("vo") OrderAbnormalRecordAdminResVO vo);
    Long getTotalCount(@Param("vo") OrderAbnormalRecordAdminResVO vo);
}
