package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentNoticeTypeEnum;
import com.cloud.baowang.agent.api.vo.AgentSystemMessageConfigVO;
import com.cloud.baowang.agent.api.vo.AgentUserLanguageVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.DeleteStateEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.NotificationTypeEnum;
import com.cloud.baowang.user.api.enums.ReadStateEnum;
import com.cloud.baowang.user.api.vo.notice.agent.*;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.constant.UserNoticeTargetConstant;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.repositories.AgentNoticeConfigRepository;
import com.cloud.baowang.user.repositories.UserNoticeConfigRepository;
import com.cloud.baowang.user.repositories.UserNoticeTargetRepository;
import com.cloud.baowang.user.util.SystemMessageConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AgentNoticeService extends ServiceImpl<UserNoticeTargetRepository, UserNoticeTargetPO> {


    private final AgentNoticeConfigRepository agentNoticeConfigRepository;


    private final UserNoticeTargetRepository userNoticeTargetRepository;

    private final UserNoticeConfigRepository userNoticeConfigRepository;

    private final UserNoticeTargetService userNoticeTargetService;
    private final UserInfoApi userInfoApi;

    private final AgentInfoApi agentInfoApi;


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
     * 获取代理消息的列表
     */
    public AgentNoticeRespBO getAgentNoticeList(AgentNoticeVO agentNoticeVO,Boolean isAgent) {
        Integer noticeTypeVo = agentNoticeVO.getNoticeType();
        Integer deviceTerminal = CurrReqUtils.getReqDeviceType();

        String userAccount = CurrReqUtils.getAccount();
        if (noticeTypeVo == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_TYPE_IS_NULL);
        }
        if (deviceTerminal == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_DEVICE_TERMINAL_IS_NULL);
        }

        if (!StringUtils.isNotBlank(userAccount)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        AgentNoticeRespBO result = new AgentNoticeRespBO();
        agentNoticeVO.setUserAccount(CurrReqUtils.getAccount());
        agentNoticeVO.setUserId(CurrReqUtils.getOneId());
        agentNoticeVO.setDeviceTerminal(deviceTerminal.toString());

        agentNoticeVO.setPlatform(isAgent?CommonConstant.business_two:CommonConstant.business_three);
        agentNoticeVO.setCurrentTime(System.currentTimeMillis());
        Page<UserNoticeRespVO> page = new Page<>(agentNoticeVO.getPageNumber(), agentNoticeVO.getPageSize());
        page = agentNoticeConfigRepository.getAgentNoticeListNew(page, agentNoticeVO);
        page = convertSystemMessage(page);
        result.setNoticeList(page);
        result.setUnReadNoticeTotal(getAgentNoticeUnreadTotal(agentNoticeVO, NotificationTypeEnum.NOTIFICATION.getCode()));
        result.setUnReadAnnounceTotal(getAgentNoticeUnreadTotal(agentNoticeVO, NotificationTypeEnum.ANNOUNCEMENT.getCode()));
        return result;
    }


    public Page<UserNoticeRespVO> convertSystemMessage(Page<UserNoticeRespVO> page) {
        List<UserNoticeRespVO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return page;
        }
        log.info(" records 翻译前 : "+records);
        records.forEach(record -> {
            if (record.getNoticeType().intValue() == CommonConstant.business_four && StringUtils.isNotBlank(record.getSystemMessageCode())) {
                AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
                languageVO.setUserId(CurrReqUtils.getOneId());
                languageVO.setUserType(CommonConstant.business_one_str);
                languageVO.setMessageType(SystemMessageEnum.nameOfCode(record.getSystemMessageCode()));
                AgentSystemMessageConfigVO messageConfigVO = agentInfoApi.getAgentLanguage(languageVO);
                String title = messageConfigVO.getTitle();
                String titleConvertValue = record.getTitleConvertValue();
                String content = messageConfigVO.getContent();
                String contentConvertValue = record.getContentConvertValue();
                record.setNoticeTitleI18nCode(title);
                record.setMessageContentI18nCode(content);
                if (StringUtils.isNotBlank(titleConvertValue)) {
                    String titleResult = SystemMessageConvertUtil.convertSystemMessage(title, titleConvertValue);
                    record.setNoticeTitleI18nCode(titleResult);
                }
                if (StringUtils.isNotBlank(contentConvertValue)) {
                    String contentResult = SystemMessageConvertUtil.convertSystemMessage(content, contentConvertValue);
                    record.setMessageContentI18nCode(contentResult);
                }
            }else {
                String title = record.getNoticeTitleI18nCode();
                String content = record.getMessageContentI18nCode();
                if (StringUtils.isNotBlank(title)) {
                    record.setNoticeTitleI18nCode(I18nMessageUtil.getI18NMessage(title));
                }
                if (StringUtils.isNotBlank(content)) {
                    record.setMessageContentI18nCode(I18nMessageUtil.getI18NMessage(content));
                }

            }
        });
        log.info(" records 翻译后 : "+records);
        page.setRecords(records);
        return page;
    }

    /**
     * 跑马灯
     *
     * @param agentNoticeReqVO
     * @return
     */
    public ResponseVO<List<UserNoticeRespVO>> getAgentNoticeHeadList(UserNoticeHeadReqVO agentNoticeReqVO) {
        agentNoticeReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
        agentNoticeReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        List<UserNoticeRespVO> userNoticeList = agentNoticeConfigRepository.getAgentHeadNoticeList(agentNoticeReqVO);
        List<UserNoticeRespVO> result = convertI18nMessage(userNoticeList);
        return ResponseVO.success(result);
    }

    /**
     * 弹窗
     */


    public List<UserNoticeRespVO> getForceAgentNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        userNoticeHeadReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
        userNoticeHeadReqVO.setCurrentTime(System.currentTimeMillis());
        List<UserNoticeRespVO> result = agentNoticeConfigRepository.getForceAgentNoticeHeadList(userNoticeHeadReqVO);
        return convertI18nMessage(result);
    }

    public List<UserNoticeRespVO> convertI18nMessage(List<UserNoticeRespVO> result){
        result.forEach(record -> {
            String title = record.getNoticeTitleI18nCode();
            String content = record.getMessageContentI18nCode();
            if (StringUtils.isNotBlank(title)) {
                record.setNoticeTitleI18nCode(I18nMessageUtil.getI18NMessage(title));
            }
            if (StringUtils.isNotBlank(content)) {
                record.setMessageContentI18nCode(I18nMessageUtil.getI18NMessage(content));
            }
        });
        return result;
    }


    public UserNoticeRespVO getAgentNoticeNotReadFirst(AgentNoticeVO agentNoticeVO) {
        //查询消息列表
        agentNoticeVO.setCurrentTime(System.currentTimeMillis());
        UserNoticeRespVO agentNoticeFirst = agentNoticeConfigRepository.getAgentNoticeFirst(agentNoticeVO);
        if (agentNoticeFirst != null){
            if ( CommonConstant.business_four == agentNoticeFirst.getNoticeType().intValue()) {
                AgentUserLanguageVO languageVO = new AgentUserLanguageVO();
                languageVO.setUserId(CurrReqUtils.getOneId());
                languageVO.setUserType(CommonConstant.business_zero_str);

                languageVO.setMessageType(SystemMessageEnum.nameOfCode(agentNoticeFirst.getSystemMessageCode()));
                AgentSystemMessageConfigVO messageConfigVO = agentInfoApi.getAgentLanguage(languageVO);
                String title = messageConfigVO.getTitle();
                String titleConvertValue = agentNoticeFirst.getTitleConvertValue();
                String content = messageConfigVO.getContent();
                String contentConvertValue = agentNoticeFirst.getContentConvertValue();
                agentNoticeFirst.setNoticeTitleI18nCode(title);
                agentNoticeFirst.setMessageContentI18nCode(content);
                if(StringUtils.isNotBlank(titleConvertValue)  ){
                    String titleResult = SystemMessageConvertUtil.convertSystemMessage(title, titleConvertValue);
                    agentNoticeFirst.setNoticeTitleI18nCode(titleResult);
                }
                if (StringUtils.isNotBlank(contentConvertValue)) {
                    String contentResult = SystemMessageConvertUtil.convertSystemMessage(content, contentConvertValue);
                    agentNoticeFirst.setMessageContentI18nCode(contentResult);
                }
                agentNoticeFirst.setNoticeType(NotificationTypeEnum.NOTIFICATION.getCode());

            }else {
                String title = agentNoticeFirst.getNoticeTitleI18nCode();
                String content = agentNoticeFirst.getMessageContentI18nCode();
                if (StringUtils.isNotBlank(title)) {
                    agentNoticeFirst.setNoticeTitleI18nCode(I18nMessageUtil.getI18NMessage(title));
                }
                if (StringUtils.isNotBlank(content)) {
                    agentNoticeFirst.setMessageContentI18nCode(I18nMessageUtil.getI18NMessage(content));
                }
            }
        }

        return agentNoticeFirst;

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
        //agentNoticePO.setTargetType(CommonConstant.business_five);
        agentNoticePO.setUserId(agentNoticeReq.getAgentAccount());
        agentNoticePO.setReadState(ReadStateEnum.UnRead.getType());
        agentNoticePO.setDeleteState(DeleteStateEnum.Normal.getType());
        //agentNoticePO.setTargetType(CommonConstant.business_five);
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
    public Long getAgentNoticeUnreadTotal(AgentNoticeVO vo, int noticeType) {
        //查询未读数量。通知类型(1=公告、2=活动、3=通知,4系统消息)
        LambdaQueryWrapper<UserNoticeTargetPO> query = new LambdaQueryWrapper<>();
        query.eq(UserNoticeTargetPO::getUserId, vo.getUserId());
        query.eq(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.UNREAD);
        query.eq(UserNoticeTargetPO::getRevokeState, UserNoticeTargetConstant.NORMAL);
        query.eq(UserNoticeTargetPO::getPlatform, CommonConstant.business_two);
        if (noticeType == CommonConstant.business_one) {
            query.eq(UserNoticeTargetPO::getNoticeType, CommonConstant.business_one);
        } else if (noticeType == CommonConstant.business_three) {
            query.in(UserNoticeTargetPO::getNoticeType, NotificationTypeEnum.NOTIFICATION.getCode(), NotificationTypeEnum.SYSTEM_MESSAGE.getCode());
        }


        return userNoticeTargetService.count(query);
    }


    /**
     * 标记已读通知（代理）/删除 /
     */
    public void setReadState(AgentNoticeSetReadStateReqVO agentNoticeSetReadStateReqVO) {
        String agentAccount = agentNoticeSetReadStateReqVO.getAgentAccount();
        UserNoticeTargetPO updateTarget = userNoticeTargetRepository.selectById(agentNoticeSetReadStateReqVO.getTargetId());
        if (updateTarget == null) {
            log.error("查询UserNoticeConfig表的数据，通知target={}不存在", agentNoticeSetReadStateReqVO.getTargetId());
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_IS_NOT_EXIST);
        }
        LambdaUpdateWrapper<UserNoticeTargetPO> systemUpdateWrapper = new LambdaUpdateWrapper();
        systemUpdateWrapper.eq(UserNoticeTargetPO::getUserId, agentNoticeSetReadStateReqVO.getAgentId());
        systemUpdateWrapper.eq(UserNoticeTargetPO::getId, agentNoticeSetReadStateReqVO.getTargetId());
        // 根据是已经读取，还是删除
        if (CommonConstant.business_one == agentNoticeSetReadStateReqVO.getOperatorStatus()) {
            systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.READ);
        } else {
            systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.READ);
            systemUpdateWrapper.set(UserNoticeTargetPO::getDeleteState, UserNoticeTargetConstant.DELETE);
        }
        int ii = userNoticeTargetRepository.update(null, systemUpdateWrapper);
        if (ii <= 0) {
            log.error(String.format("修改UserNoticeTarget表的数据失败，用户userAccount={}", agentAccount));
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_UPDATE_IS_FAIL);
        }


    }


    /**
     * （代理）批量标记已读通知/删除
     */
    public void setReadStateMore(AgentNoticeSetReadStateMoreReqVO reqVO,boolean isAgent) {
        String userId = CurrReqUtils.getOneId();
//        String userId = "8824083";
        reqVO.setCurrentTime(System.currentTimeMillis());

        LambdaUpdateWrapper<UserNoticeTargetPO> updateWrapper = new LambdaUpdateWrapper<>();
        if (CommonConstant.business_three.equals(reqVO.getNoticeType())) {
            updateWrapper.in(UserNoticeTargetPO::getNoticeType, List.of(CommonConstant.business_three, CommonConstant.business_four));
        } else {
            updateWrapper.eq(UserNoticeTargetPO::getNoticeType, reqVO.getNoticeType());
        }
        List<String> ids = reqVO.getNoticeList().stream().map(AgentNoticeDTO::getTargetId).toList();
        updateWrapper.eq(UserNoticeTargetPO::getPlatform, isAgent?CommonConstant.business_two:CommonConstant.business_three);
            if (reqVO.getOperatorStatus().intValue() == CommonConstant.business_one) {
            updateWrapper.eq(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.UNREAD);
            updateWrapper.eq(UserNoticeTargetPO::getUserId, userId);
            if (reqVO.getIsAllOperator()!=1){
                updateWrapper.in(UserNoticeTargetPO::getId, ids);
            }
            updateWrapper.set(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.READ);
            userNoticeTargetService.update(null, updateWrapper);
        } else if (reqVO.getOperatorStatus().intValue() == CommonConstant.business_two) {
            updateWrapper.eq(UserNoticeTargetPO::getDeleteState, UserNoticeTargetConstant.NORMAL);
            updateWrapper.eq(UserNoticeTargetPO::getUserId, userId);
            updateWrapper.in(UserNoticeTargetPO::getId, ids);
            updateWrapper.set(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.READ);
            updateWrapper.set(UserNoticeTargetPO::getDeleteState, UserNoticeTargetConstant.DELETE);
            userNoticeTargetService.update(null, updateWrapper);
        }
    }

}
