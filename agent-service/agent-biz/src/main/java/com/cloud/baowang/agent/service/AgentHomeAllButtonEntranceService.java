package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.BaseReqVO;
import com.cloud.baowang.agent.po.AgentHomeAllButtonEntrancePO;
import com.cloud.baowang.agent.repositories.AgentHomeAllButtonEntranceRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 代理客户端-快捷入口(全部功能) 服务类
 * </p>
 *
 * @author kimi
 * @since 2023-10-10
 */
@Slf4j
@Service
public class AgentHomeAllButtonEntranceService extends ServiceImpl<AgentHomeAllButtonEntranceRepository, AgentHomeAllButtonEntrancePO> {

    public String getAllButtonEntranceByType(Integer pcOrH5) {
        List<AgentHomeAllButtonEntrancePO> list =
                this.lambdaQuery().eq(AgentHomeAllButtonEntrancePO::getPcOrH5, pcOrH5).list();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        list = list.subList(0, 9);
        return JSONObject.toJSONString(list);
    }

    public ResponseVO<Boolean> init(BaseReqVO baseReqVO) {
        String siteCode=baseReqVO.getSiteCode();
        Long countNum=this.lambdaQuery().eq(AgentHomeAllButtonEntrancePO::getSiteCode, siteCode).count();
        if(countNum<=0){
            List<AgentHomeAllButtonEntrancePO> list =this.lambdaQuery().eq(AgentHomeAllButtonEntrancePO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE).list();
            for(AgentHomeAllButtonEntrancePO agentHomeAllButtonEntrancePO:list){
                agentHomeAllButtonEntrancePO.setId(null);
                agentHomeAllButtonEntrancePO.setSiteCode(siteCode);
                agentHomeAllButtonEntrancePO.setCreator(baseReqVO.getAdminId());
                agentHomeAllButtonEntrancePO.setCreatedTime(System.currentTimeMillis());
                agentHomeAllButtonEntrancePO.setUpdater(baseReqVO.getAdminId());
                agentHomeAllButtonEntrancePO.setUpdatedTime(System.currentTimeMillis());
            }
            this.saveBatch(list);
        }
        return  ResponseVO.success(Boolean.TRUE);
    }
}
