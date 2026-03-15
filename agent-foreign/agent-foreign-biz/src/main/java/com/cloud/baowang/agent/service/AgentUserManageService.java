package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.vo.agent.AgentBetOrderResVO;
import com.cloud.baowang.play.api.vo.agent.AgentUserDetailOrderRecordReqVO;
import com.cloud.baowang.play.api.vo.agent.AgentUserOrderRecordReqVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailParam;
import com.cloud.baowang.user.api.vo.UserDetails.SelectUserDetailResponseVO;
import com.cloud.baowang.user.api.vo.user.SubordinateUserListParam;
import com.cloud.baowang.user.api.vo.user.SubordinateUserListResponseVO;
import com.cloud.baowang.user.api.vo.user.request.EditRemarkParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author: fangfei
 * @createTime: 2024/06/17 22:40
 * @description: 会员管理服务
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentUserManageService {
    private final UserInfoApi userInfoApi;
    private final OrderRecordApi orderRecordApi;


    public ResponseVO<SelectUserDetailResponseVO> selectUserDetail(SelectUserDetailParam vo) {
        return userInfoApi.selectUserDetail(vo);
    }

    public ResponseVO<Page<SubordinateUserListResponseVO>> subordinateUserList(SubordinateUserListParam vo) {
        return userInfoApi.subordinateUserList(vo);
    }

    public ResponseVO<AgentBetOrderResVO> getAgentClientOrder(AgentUserDetailOrderRecordReqVO req) {
        AgentUserOrderRecordReqVO reqVO = new AgentUserOrderRecordReqVO();
        BeanUtil.copyProperties(req,reqVO);
        reqVO.setAgentIds(Collections.singletonList(req.getAgentId()));
        return orderRecordApi.getAgentClientOrder(reqVO);
    }

    public ResponseVO<?> agentEditRemark(EditRemarkParam vo) {
        return userInfoApi.agentEditRemark(vo);
    }
}
