package com.cloud.baowang.agent.repositories;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoModifyReviewPageVO;
import com.cloud.baowang.agent.po.AgentInfoModifyReviewPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * <p>
 * 代理账户修改审核表 Mapper 接口
 * </p>
 *
 * @author awei
 * @since 2023-10-10
 */
@Mapper
public interface AgentInfoModifyReviewRepository extends BaseMapper<AgentInfoModifyReviewPO> {

    Page<AgentInfoModifyReviewPageVO> pageList(Page<AgentInfoModifyReviewPO> page, @Param("vo") AgentInfoModifyReviewPageQueryVO param);

    Long selectUseCount(@Param("siteCode") String siteCode,
                        @Param("code") Integer code,
                        @Param("reviewStatusArr") ArrayList<Integer> reviewStatusArr,
                        @Param("afterValue") String afterValue);
}
