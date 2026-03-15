package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.ShortUrlChangeRecordQueryVO;
import com.cloud.baowang.agent.po.AgentInfoChangeRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 代理信息变更记录表 Mapper 接口
 * </p>
 *
 * @author awei
 * @since 2023-10-10
 */
@Mapper
public interface AgentInfoChangeRecordRepository extends BaseMapper<AgentInfoChangeRecordPO> {
    List<ShortUrlChangeRecordPageVO> shortRecordPageList(@Param("vo") ShortUrlChangeRecordQueryVO vo);
}
