package com.cloud.baowang.agent.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeDetailReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordDetailVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordVO;
import com.cloud.baowang.agent.po.AgentCoinRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 代理账变记录 Mapper 接口
 * </p>
 *
 * @author qiqi
 */
@Mapper
public interface AgentCoinRecordRepository extends BaseMapper<AgentCoinRecordPO> {

    /**
     * 代理PC,H5 账变明细
     * @param objectPage
     * @param vo
     * @return
     */
    Page<AgentCustomerCoinRecordVO> listAgentCustomerCoinRecord(@Param("page") Page<Object> objectPage, @Param("vo") AgentCoinChangeReqVO vo);

    /**
     * 代理PC,H5 账变明细详情
     * @param vo
     * @return
     */
    AgentCustomerCoinRecordDetailVO getCoinRecordDetail(@Param("vo") AgentCoinChangeDetailReqVO vo);


    /**
     * 按照条件全部汇总
     * @param ew
     * @return
     */
    AgentCoinRecordVO sumAllAgentCoinRecord(@Param(Constants.WRAPPER) LambdaQueryWrapper<AgentCoinRecordPO> ew);
}
