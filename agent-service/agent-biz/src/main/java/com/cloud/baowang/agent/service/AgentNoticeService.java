package com.cloud.baowang.agent.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentNoticeTypeEnum;
import com.cloud.baowang.agent.po.AgentNoticePO;
import com.cloud.baowang.agent.po.UserNoticeConfigPO;
import com.cloud.baowang.agent.po.UserNoticeTargetPO;
import com.cloud.baowang.agent.repositories.UserNoticeConfigRepository;
import com.cloud.baowang.agent.repositories.UserNoticeTargetRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.constant.AgentNoticeTargetConstant;
import com.cloud.baowang.common.core.enums.DeleteStateEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.NotificationTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.user.api.enums.ReadStateEnum;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeDTO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeReq;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeRespBO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateMoreReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeSetReadStateReqVO;
import com.cloud.baowang.user.api.vo.notice.agent.AgentNoticeVO;
import com.cloud.baowang.user.api.vo.notice.agent.SaveAgentNoticeReq;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeHeadRspVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class AgentNoticeService extends ServiceImpl<UserNoticeTargetRepository, UserNoticeTargetPO> {


    @Autowired
    private UserNoticeConfigRepository userNoticeConfigRepository;

    @Autowired
    private UserNoticeTargetRepository userNoticeTargetRepository;


    /**
     * 获取代理的通知Tab字典
     */
    public HashMap<String, Object> getAngentNoticeTabList() {
        LinkedList<Map<String, Object>> agentNoticeTypeList = AgentNoticeTypeEnum.toList();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("agentNoticeTypeList", agentNoticeTypeList);
        return resultMap;
    }

    /**
     * 插入（批量）代理消息--代码删除
     */
    public Boolean saveAgentNotices(SaveAgentNoticeReq saveAgentNoticeReq) {
        List<AgentNoticePO> agentNoticePOS = ConvertUtil.entityListToModelList(saveAgentNoticeReq.getAgentNoticeReqList(), AgentNoticePO.class);
        return true;
    }


    /**
     * 获取代理消息的列表
     */
    public AgentNoticeRespBO getAgentNoticeList(AgentNoticeVO agentNoticeVO) {
        Integer noticeTypeVo = agentNoticeVO.getNoticeType();
        String agentAccount = agentNoticeVO.getUserAccount();
        if (AgentNoticeTypeEnum.isNotExist(noticeTypeVo)) {
            throw new BaowangDefaultException("代理通知类型是不存在");
        }
        if (StringUtils.isBlank(agentAccount)) {
            throw new BaowangDefaultException("登录用户为空");
        }
        //查询消息列表
        Page page = new Page<>(agentNoticeVO.getPageNumber(), agentNoticeVO.getPageSize());
        agentNoticeVO.setCurrentTime(System.currentTimeMillis());
        Page<UserNoticeRespVO> pageResult = new Page<>();
        // 未读的消息，是属于配置的公告
        pageResult = userNoticeConfigRepository.getUserNoticeList(page, agentNoticeVO);
        List<UserNoticeRespVO> list = pageResult.getRecords().stream().map(record -> {
            UserNoticeRespVO responseVO = new UserNoticeRespVO();
            BeanUtils.copyProperties(record, responseVO);
//                if (null != record.getCreatedTime()) {
//                    String createdTimeStr = DateUtil.format(new Date(record.getCreatedTime()), DatePattern.NORM_DATETIME_PATTERN);
//                    responseVO.setCreatedTimeStr(createdTimeStr);
//                }
            return responseVO;
        }).toList();
        pageResult.setRecords(list);
        // 特定的，包括已经读取的+系统消息
        /*if (noticeTypeVo == AgentNoticeTypeEnum.AgentNotice.getType()) {
            pageResult = userNoticeConfigRepository.getAgentNoticeList(page, agentNoticeVO);
        }*/


        //查询未读数量。通知类型(1=公告、2=活动、3=通知)
        /*Integer[] noticeTypeArr = {
                AgentNoticeTypeEnum.PublicNotice.getType(),
                AgentNoticeTypeEnum.AgentNotice.getType()
        };
        List<AgentNoticeUnreadRespVO> unreadCountList = new ArrayList<>();
        for (Integer noticeType : noticeTypeArr) {
            //全部会员
            Integer unreadCount1 = userNoticeConfigRepository.getUnreadCountForUserNotice1(noticeType, agentNoticeVO.getAgentAccount());
            //Integer unreadCount3 = userNoticeConfigRepository.getUnreadCountForUserNotice3(noticeType, agentNoticeVO.getAgentAccount(), agentNoticeVO.getDeviceTerminal());
            Integer unreadCount2 = userNoticeConfigRepository.getUnreadCountForUserNotice2(noticeType, agentNoticeVO.getAgentAccount());
            if (noticeType == AgentNoticeTypeEnum.AgentNotice.getType()) {
                // 特定代理账号
                Integer unreadCount = userNoticeConfigRepository.getUnreadCountForAgentNotice(AgentNoticeTypeEnum.AgentNotice.getType(), agentNoticeVO.getAgentAccount());
                // 特定会员未读消息
                // Integer unreadCount = agentNoticeRepository.getUnreadCountForAgentNotice(AgentNoticeTypeEnum.AgentNotice.getType(), agentNoticeVO.getAgentAccount());
                unreadCount2 = unreadCount2 + unreadCount;
            }
            AgentNoticeUnreadRespVO agentNoticeUnreadRespVO = new AgentNoticeUnreadRespVO();
            agentNoticeUnreadRespVO.setNoticeType(noticeType);
            agentNoticeUnreadRespVO.setUnreadCount(unreadCount1 + unreadCount2);
            unreadCountList.add(agentNoticeUnreadRespVO);
        }*/
        /*AgentNoticeUnreadRespVO agentNoticeUnreadVO = new AgentNoticeUnreadRespVO();
        agentNoticeUnreadVO.setNoticeType(AgentNoticeTypeEnum.AgentNotice.getType());
        Integer unreadCount = agentNoticeRepository.getUnreadCountForAgentNotice(AgentNoticeTypeEnum.AgentNotice.getType(), agentNoticeVO.getAgentAccount());
        agentNoticeUnreadVO.setUnreadCount(unreadCount);
        unreadCountList.add(agentNoticeUnreadVO);*/


        //构建响应对象
        AgentNoticeRespBO agentNoticeRespBO = new AgentNoticeRespBO();
        agentNoticeRespBO.setNoticeList(pageResult);
        /*agentNoticeRespBO.setUnreadCountList(unreadCountList);
        agentNoticeRespBO.setUnreadTotal(0);*/


        /*for (AgentNoticeUnreadRespVO itemVo : unreadCountList) {
            int unread = agentNoticeRespBO.getUnreadTotal() + itemVo.getUnreadCount();
            agentNoticeRespBO.setUnreadTotal(unread);
        }*/

        return agentNoticeRespBO;
    }


    /**
     * 获取代理消息的列表 AgentNoticeTypeEnum
     */
    public UserNoticeRespVO getAgentNoticeNotReadFirst(AgentNoticeVO agentNoticeVO) {
        //agentNoticeVO.setNoticeType(AgentNoticeTypeEnum.AgentNotice.getType());
        Integer noticeTypeVo = agentNoticeVO.getNoticeType();
        //String deviceTerminal = agentNoticeVO.getDeviceTerminal();
        String agentAccount = agentNoticeVO.getUserAccount();
        /*if (AgentNoticeTypeEnum.isNotExist(noticeTypeVo)) {
            throw new BaowangDefaultException("代理通知类型是不存在");
        }*/
        if (StringUtils.isBlank(agentAccount)) {
            throw new BaowangDefaultException("登录用户为空");
        }
        //查询消息列表
        Page page = new Page<>(agentNoticeVO.getPageNumber(), agentNoticeVO.getPageSize());
        agentNoticeVO.setCurrentTime(System.currentTimeMillis());
        Page<UserNoticeRespVO> pageResult = new Page<>();
        // 未读的消息，是属于配置的公告
        //agentNoticeVO.setNoticeType(2);
        UserNoticeRespVO responseVo = userNoticeConfigRepository.getAgentNoticeFirst(agentNoticeVO);
        return responseVo;
    }


    /**
     * 添加代理通知 --
     */
    public void addAgentNotice(AgentNoticeReq agentNoticeReq) {
        String noticeTitle = agentNoticeReq.getNoticeTitle();
        String messageContent = agentNoticeReq.getMessageContent();
        String agentAccount = agentNoticeReq.getAgentAccount();
        String agentAccountId = agentNoticeReq.getAgentAccountId();
        if (StringUtils.isBlank(noticeTitle)) {
            throw new BaowangDefaultException("代理通知的标题是空");
        }
        if (StringUtils.isBlank(messageContent)) {
            throw new BaowangDefaultException("代理通知的内容是空");
        }
        noticeTitle = noticeTitle.trim();
        agentNoticeReq.setNoticeTitle(noticeTitle);
        if (noticeTitle.length() > 100) {
            throw new BaowangDefaultException("代理通知的标题最大100个字符");
        }
        messageContent = messageContent.trim();
        agentNoticeReq.setMessageContent(messageContent);
        if (messageContent.length() > 300) {
            throw new BaowangDefaultException("代理通知的内容最大300个字符");
        }
        Integer noticeType = agentNoticeReq.getNoticeType();


        //构建代理通知的对象
        UserNoticeTargetPO agentNoticePO = new UserNoticeTargetPO();
        BeanUtils.copyProperties(agentNoticeReq, agentNoticePO);
        agentNoticePO.setNoticeType(AgentNoticeTypeEnum.AgentNotice.getType());
        // 代理消息，特定会员，数据库字段是发送对象(1 全体会员 2 特定会员 3 终端 4 全部代理 5 特定代理)
        agentNoticePO.setTargetType(CommonConstant.business_five);
        agentNoticePO.setUserAccount(agentNoticeReq.getAgentAccount());
        agentNoticePO.setReadState(ReadStateEnum.UnRead.getType());
        agentNoticePO.setDeleteState(DeleteStateEnum.Normal.getType());
        agentNoticePO.setTargetType(CommonConstant.business_five);
        agentNoticePO.setCreator(agentNoticeReq.getAgentAccountId());
        agentNoticePO.setCreatedTime(System.currentTimeMillis());
        agentNoticePO.setUpdater(agentNoticeReq.getAgentAccountId());
        agentNoticePO.setUpdatedTime(System.currentTimeMillis());
        int ii = userNoticeTargetRepository.insert(agentNoticePO);
        if (ii <= 0) {
            log.error("添加代理通知发生失败");
            throw new BaowangDefaultException(ResultCode.INSERT_ERROR);
        }
    }


    /**
     * 获取代理的未读通知总数量
     */
//    public AgentNoticeRespBO getAgentNoticeUnreadTotal(AgentNoticeVO agentNoticeVO) {
//        //查询未读数量。通知类型(1=公告、2=活动、3=通知)
//        Integer[] noticeTypeArr = {
//                AgentNoticeTypeEnum.PublicNotice.getType(),
//                AgentNoticeTypeEnum.AgentNotice.getType(),
//        };
//        List<AgentNoticeUnreadRespVO> unreadCountList = new ArrayList<>();
//        for (Integer noticeType : noticeTypeArr) {
//            // 全体会员
//            Integer unreadCount1 = userNoticeConfigRepository.getUnreadCountForUserNotice1(noticeType, agentNoticeVO.getUserAccount(), System.currentTimeMillis());
//            //Integer unreadCount3 = userNoticeConfigRepository.getUnreadCountForUserNotice3(noticeType, agentNoticeVO.getAgentAccount(), agentNoticeVO.getDeviceTerminal());
//            // 特定会员
//            Integer unreadCount2 = userNoticeConfigRepository.getUnreadCountForUserNotice2(noticeType, agentNoticeVO.getUserAccount(), System.currentTimeMillis());
//            /*if (noticeType == 2) {
//                // 当时通知，需要添加系统消息未读
//                Integer unreadCount = userNoticeConfigRepository.getUnreadCountForAgentNotice(AgentNoticeTypeEnum.AgentNotice.getType(), agentNoticeVO.getAgentAccount());
//                unreadCount2 = unreadCount2 + unreadCount;
//            }*/
//            AgentNoticeUnreadRespVO agentNoticeUnreadRespVO = new AgentNoticeUnreadRespVO();
//            agentNoticeUnreadRespVO.setNoticeType(noticeType);
//            agentNoticeUnreadRespVO.setUnreadCount(unreadCount1 + unreadCount2);
//            unreadCountList.add(agentNoticeUnreadRespVO);
//        }
//        AgentNoticeUnreadRespVO agentNoticeUnreadVO = new AgentNoticeUnreadRespVO();
//        /*agentNoticeUnreadVO.setNoticeType(AgentNoticeTypeEnum.AgentNotice.getType());
//        Integer unreadCount = agentNoticeRepository.getUnreadCountForAgentNotice(AgentNoticeTypeEnum.AgentNotice.getType(), agentNoticeVO.getAgentAccount());
//        agentNoticeUnreadVO.setUnreadCount(unreadCount);
//        unreadCountList.add(agentNoticeUnreadVO);*/
//
//
//        //构建响应对象
//        AgentNoticeRespBO agentNoticeRespBO = new AgentNoticeRespBO();
//        agentNoticeRespBO.setNoticeList(null);
//        agentNoticeRespBO.setUnreadCountList(unreadCountList);
//        agentNoticeRespBO.setUnreadTotal(0);
//        for (AgentNoticeUnreadRespVO itemVo : unreadCountList) {
//            int unread = agentNoticeRespBO.getUnreadTotal() + itemVo.getUnreadCount();
//            agentNoticeRespBO.setUnreadTotal(unread);
//        }
//
//        return agentNoticeRespBO;
//    }

    public UserNoticeHeadRspVO getForceAgentNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        UserNoticeHeadRspVO rspVO = new UserNoticeHeadRspVO();
        userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
        userNoticeHeadReqVO.setCurrentTime(System.currentTimeMillis());
        List<UserNoticeRespVO> userNoticeList = userNoticeConfigRepository.getForceAgentNoticeHeadList(userNoticeHeadReqVO);
        rspVO.setUserNoticeList(userNoticeList);
        return rspVO;
    }


    /**
     * 标记已读通知（代理）/删除 /
     */
//    public void setReadState(AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
//        // 这个是
//        String noticeId = agentNoticeSetReadStateReqVO.getNoticeId();
//        String agentAccount = agentNoticeSetReadStateReqVO.getAgentAccount();
//        // List<Long> noticeIdList = agentNoticeSetReadStateReqVO.getNoticeIdList();
//        /*Integer noticeType = agentNoticeSetReadStateReqVO.getNoticeType();
//        if (AgentNoticeTypeEnum.isNotExist(noticeType)) {
//            throw new BaowangDefaultException("代理通知类型是不存在");
//        }*/
//        UserNoticeTargetPO updateTarget = userNoticeTargetRepository.selectById(agentNoticeSetReadStateReqVO.getTargetId());
//        UserNoticeConfigPO userNoticeConfigPO = userNoticeConfigRepository.selectById(noticeId);
//        if (updateTarget == null && userNoticeConfigPO == null) {
//            log.error("查询UserNoticeConfig表的数据，通知noticeId={},target={}不存在", noticeId, agentNoticeSetReadStateReqVO.getTargetId());
//            throw new BaowangDefaultException(ResultCode.USER_NOTICE_IS_NOT_EXIST);
//        }
//        // 系统消息或者特定消息
//        if (updateTarget != null) {
//            // 系统消息，没有关联noticeConfig表，因此查询为null。查询target表，更改读取状态
//            LambdaUpdateWrapper<UserNoticeTargetPO> systemUpdateWrapper = new LambdaUpdateWrapper();
//            systemUpdateWrapper.eq(UserNoticeTargetPO::getUserAccount, agentAccount);
//            systemUpdateWrapper.eq(UserNoticeTargetPO::getId, agentNoticeSetReadStateReqVO.getTargetId());
//            // 根据是已经读取，还是删除
//            if (CommonConstant.business_one == agentNoticeSetReadStateReqVO.getOperatorStatus()) {
//                systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, AgentNoticeTargetConstant.READ);
//            } else {
//                //`delete_state` int DEFAULT NULL COMMENT '删除状态: 1=删除、2=正常',
//                systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, AgentNoticeTargetConstant.READ);
//                systemUpdateWrapper.set(UserNoticeTargetPO::getDeleteState, AgentNoticeTargetConstant.DELETE);
//            }
//            int ii = userNoticeTargetRepository.update(null, systemUpdateWrapper);
//            if (ii <= 0) {
//                log.error(String.format("修改UserNoticeTarget表的数据失败，用户userAccount={}, 通知noticeId={}", agentAccount, noticeId));
//                throw new BaowangDefaultException(ResultCode.USER_NOTICE_UPDATE_IS_FAIL);
//            }
//            return;
//
//        }
//        // 应该不会走
//        if (updateTarget != null) {
//            Integer readState = updateTarget.getReadState();
//            if (readState == AgentNoticeTargetConstant.UNREAD) {
//                if (userNoticeConfigPO.getStatus() == AgentNoticeTargetConstant.REVOCATION) {
//                    log.error("通知noticeId={}已撤销", noticeId);
//                    throw new BaowangDefaultException(ResultCode.USER_NOTICE_IS_REVOCATION);
//                }
//            }
//        }
//        // 系统公告之类，没有在target表存，需要插入已经读取
//        LambdaQueryWrapper<UserNoticeTargetPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(UserNoticeTargetPO::getNoticeId, noticeId);
//        lambdaQueryWrapper.eq(UserNoticeTargetPO::getUserAccount, agentAccount);
//        UserNoticeTargetPO agentNoticePO = userNoticeTargetRepository.selectOne(lambdaQueryWrapper);
//        if (agentNoticePO == null) {
//            // 插入已读
//            UserNoticeTargetPO targetPO = new UserNoticeTargetPO();
//            targetPO.setNoticeId(noticeId);
//            targetPO.setNoticeType(userNoticeConfigPO.getNoticeType());
//            //发送目标：1=插入已读、2=特定会员
//            targetPO.setTargetType(AgentNoticeTargetConstant.INSERT_READ);
//            targetPO.setUserAccount(agentAccount);
//
//            //撤销状态，1=未撤销、2=撤销
//            targetPO.setRevokeState(AgentNoticeTargetConstant.NOT_REVOKED);
//            //删除状态: 1=删除、2=正常'
//            if (CommonConstant.business_one == agentNoticeSetReadStateReqVO.getOperatorStatus()) {
//                //阅读状态，0=未读、1=已读
//                targetPO.setReadState(AgentNoticeTargetConstant.READ);
//                targetPO.setDeleteState(AgentNoticeTargetConstant.NORMAL);
//            } else {
//                //阅读状态，0=未读、1=已读
//                targetPO.setReadState(AgentNoticeTargetConstant.READ);
//                targetPO.setDeleteState(AgentNoticeTargetConstant.DELETE);
//            }
//
//            targetPO.setCreatedTime(System.currentTimeMillis());
//            int tt = userNoticeTargetRepository.insert(targetPO);
//            if (tt <= 0) {
//                log.error(String.format("往UserNoticeTarget表的添加数据失败"));
//                throw new BaowangDefaultException(ResultCode.USER_NOTICE_ADD_IS_FAIL);
//            }
//        } else {
//            log.info("通知已读了");
//        }
//
//    }


    /**
     * （代理）批量标记已读通知/删除
     */
    public void setReadStateMore(AgentNoticeSetReadStateMoreReqVO moreReqVO) {
        Integer isAllOperator = moreReqVO.getIsAllOperator();
        List<String> noticeIds = new ArrayList<>(16);
        List<String> targetIds = new ArrayList<>(16);
        // 全部，且删除的时候， 只能删除消息，不能删除公告
        if (isAllOperator == CommonConstant.business_one) {
            // 所有的进行读取
            AgentNoticeVO agentNoticeVO = new AgentNoticeVO();
            if (moreReqVO.getOperatorStatus() == CommonConstant.business_one) {
                //  读取
                agentNoticeVO.setNoticeType(moreReqVO.getNoticeType());
            }else{
                // 删除
                agentNoticeVO.setNoticeType(CommonConstant.business_two);
            }
            agentNoticeVO.setUserAccount(moreReqVO.getAgentAccount());
            List<UserNoticeRespVO> list = userNoticeConfigRepository.getUserAllNotReadNoticeList(agentNoticeVO);
            if (list != null && !list.isEmpty()) {

                targetIds = list.stream().filter(dto -> dto.getTargetId() != null)
                        .map(UserNoticeRespVO::getTargetId).collect(Collectors.toList());
            }


        } else {
            List<AgentNoticeDTO> agentNoticeDTOS = moreReqVO.getNoticeList();
            targetIds = agentNoticeDTOS.stream().filter(dto -> dto.getTargetId() != null)
                    .map(AgentNoticeDTO::getTargetId)
                    .distinct()
                    .collect(Collectors.toList());

            noticeIds = agentNoticeDTOS.stream().filter(dto -> dto.getTargetId() == null)
                    .map(AgentNoticeDTO::getNoticeId)
                    .distinct()
                    .collect(Collectors.toList());
        }


        String agentAccount = moreReqVO.getAgentAccount();
        Integer noticeType = moreReqVO.getNoticeType();
        if (AgentNoticeTypeEnum.isNotExist(noticeType)) {
            throw new BaowangDefaultException("代理通知类型是不存在");
        }
        //
        // 系统消息或者特定消息
        if (targetIds != null && !targetIds.isEmpty()) {
            // 系统消息，没有关联noticeConfig表，因此查询为null。查询target表，更改读取状态
            LambdaUpdateWrapper<UserNoticeTargetPO> systemUpdateWrapper = new LambdaUpdateWrapper();
            systemUpdateWrapper.eq(UserNoticeTargetPO::getUserAccount, agentAccount);
            systemUpdateWrapper.in(UserNoticeTargetPO::getId, targetIds);
            // 根据是已经读取，还是删除
            if (CommonConstant.business_one == moreReqVO.getOperatorStatus()) {
                systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, AgentNoticeTargetConstant.READ);
            } else {
                //`delete_state` int DEFAULT NULL COMMENT '删除状态: 1=删除、2=正常',
                systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, AgentNoticeTargetConstant.READ);
                systemUpdateWrapper.set(UserNoticeTargetPO::getDeleteState, AgentNoticeTargetConstant.DELETE);
            }
            int ii = userNoticeTargetRepository.update(null, systemUpdateWrapper);
            if (ii < 0) {
                log.error(String.format("修改agentNotice表的数据失败，用户agentAccount={}, 通知targetIds={}", agentAccount, JSONObject.toJSONString(targetIds)));
                throw new BaowangDefaultException(ResultCode.USER_NOTICE_UPDATE_IS_FAIL);
            }
        }
        if (noticeIds != null && !noticeIds.isEmpty()) {
            List<UserNoticeTargetPO> inserts = new ArrayList<>(16);
            for (int i = 0; i < noticeIds.size(); i++) {
                String noticeId = noticeIds.get(i);
                // 系统公告之类，没有在target表存，需要插入已经读取
                // 插入已读
                UserNoticeConfigPO userNoticeConfigPO = userNoticeConfigRepository.selectById(noticeId);
                // 系统公告之类，没有在target表存，需要插入已经读取
                LambdaQueryWrapper<UserNoticeTargetPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserNoticeTargetPO::getNoticeId, noticeId);
                lambdaQueryWrapper.eq(UserNoticeTargetPO::getUserAccount, agentAccount);
                lambdaQueryWrapper.last(CommonConstant.query_limit);
                UserNoticeTargetPO agentNoticePO = userNoticeTargetRepository.selectOne(lambdaQueryWrapper);
                if (agentNoticePO != null) {
                    continue;
                }
                UserNoticeTargetPO targetPO = new UserNoticeTargetPO();
                targetPO.setNoticeId(noticeId);
                targetPO.setNoticeType(userNoticeConfigPO.getNoticeType());
                //发送目标：1=插入已读、2=特定会员
                targetPO.setTargetType(AgentNoticeTargetConstant.INSERT_READ);
                targetPO.setUserAccount(agentAccount);

                //撤销状态，1=未撤销、2=撤销
                targetPO.setRevokeState(AgentNoticeTargetConstant.NOT_REVOKED);
                //删除状态: 1=删除、2=正常'
                if (CommonConstant.business_two == moreReqVO.getOperatorStatus()) {
                    //阅读状态，0=未读、1=已读
                    targetPO.setReadState(AgentNoticeTargetConstant.READ);
                    targetPO.setDeleteState(CommonConstant.business_two);
                } else {
                    //删除，0=未读、1=已读
                    targetPO.setReadState(AgentNoticeTargetConstant.READ);
                    targetPO.setDeleteState(CommonConstant.business_one);
                }

                targetPO.setCreatedTime(System.currentTimeMillis());
                inserts.add(targetPO);
            }
            this.saveBatch(inserts);
        }


    }

    /**
     * 标记删除通知 - 跟已读取合并 -删除
     */
    public void deleteAgentNotice(AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {

    }


}
