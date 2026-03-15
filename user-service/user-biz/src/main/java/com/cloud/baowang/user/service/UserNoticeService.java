package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.SystemMessageEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.NotificationTypeEnum;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import com.cloud.baowang.user.api.vo.UserSystemMessageConfigVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeRespVO;
import com.cloud.baowang.user.api.vo.notice.user.reponse.UserNoticeUnreadNumRspVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeHeadReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeSetReadStateReqVO;
import com.cloud.baowang.user.api.vo.notice.user.request.UserNoticeTargetAddVO;
import com.cloud.baowang.user.constant.UserNoticeTargetConstant;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserNoticeTargetPO;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.UserNoticeConfigRepository;
import com.cloud.baowang.user.repositories.UserNoticeTargetRepository;
import com.cloud.baowang.user.util.SystemMessageConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserNoticeService extends ServiceImpl<UserInfoRepository, UserInfoPO> {


    private final UserNoticeConfigRepository userNoticeConfigRepository;

    private final UserNoticeTargetRepository userNoticeTargetRepository;

    private final UserInfoRepository userInfoRepository;
    private final UserNoticeTargetService userNoticeTargetService;

    private final SiteApi siteApi;

    public List<UserNoticeRespVO> getUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        String optionKey = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_MARQUEE_CONTENT_CACHE);
        List<UserNoticeRespVO> list = RedisUtil.getList(optionKey);
        if (CollectionUtil.isNotEmpty(list)) {
            return convertI18nMessage(list);
        }

        userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
        userNoticeHeadReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        List<UserNoticeRespVO> result = userNoticeConfigRepository.getUserHeadNoticeListNew(userNoticeHeadReqVO);
        RedisUtil.setList(optionKey, result);
        return convertI18nMessage(result);
    }

    public List<UserNoticeRespVO> getForceUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
        //String optionKey = RedisConstants.getToSetSiteCodeKeyConstant(CurrReqUtils.getOneId(),RedisConstants.KEY_FORCED_POPUP_CACHE);
//        List<UserNoticeRespVO> list = RedisUtil.getList(optionKey);
//        if (CollectionUtil.isNotEmpty(list)) {
//            return list;
//        }
        userNoticeHeadReqVO.setSiteCode(CurrReqUtils.getSiteCode());
        userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
        userNoticeHeadReqVO.setCurrentTime(System.currentTimeMillis());
        userNoticeHeadReqVO.setUserId(CurrReqUtils.getOneId());
        List<UserNoticeRespVO> result = userNoticeConfigRepository.getForceUserNoticeHeadListNew(userNoticeHeadReqVO);
        return convertI18nMessage(result);
    }

    public List<UserNoticeRespVO> convertI18nMessage(List<UserNoticeRespVO> result) {
        List<UserNoticeRespVO> rsp = Lists.newArrayList();
        result.forEach(record -> {
            String title = record.getNoticeTitleI18nCode();
            String content = record.getMessageContentI18nCode();
            if (StringUtils.isNotBlank(title)) {
                record.setNoticeTitleI18nCode(I18nMessageUtil.getI18NMessage(title));
            }
            if (StringUtils.isNotBlank(content)) {
                record.setMessageContentI18nCode(I18nMessageUtil.getI18NMessage(content));
            }
            rsp.add(record);
        });
        return rsp;
    }
    /**
     * public UserNoticeHeadRspVO getUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
     *         UserNoticeHeadRspVO rspVO = new UserNoticeHeadRspVO();
     *         userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
     *         userNoticeHeadReqVO.setCurrentTime(System.currentTimeMillis());
     *         List<UserNoticeRespVO> userNoticeList = userNoticeConfigRepository.getUserHeadNoticeList(userNoticeHeadReqVO);
     *         rspVO.setUserNoticeList(userNoticeList);
     *
     *         //查询未读数量。通知类型(1=公告、2=活动、3=通知)
     *         Integer[] noticeTypeArr = {NotificationTypeEnum.ANNOUNCEMENT.getCode(), NotificationTypeEnum.EVENTS.getCode(), NotificationTypeEnum.NOTIFICATION.getCode()};
     *         int unreadTotal = 0;
     *         for (Integer noticeType : noticeTypeArr) {
     *             Integer unreadCount = userNoticeConfigRepository.getUnreadCountForUserNotice(noticeType, userNoticeHeadReqVO.getUserAccount(), userNoticeHeadReqVO.getDeviceTerminal(), System.currentTimeMillis());
     *             // 仅限于notifyType=3的时候，需要把系统消息也展示，且归属于通知
     *             Integer unreadCount4 = 0;
     *             if (noticeType == NotificationTypeEnum.NOTIFICATION.getCode()) {
     *                 unreadCount4 = userNoticeConfigRepository.getUnreadCountForUserNotice4(NotificationTypeEnum.SYSTEM_MESSAGE.getCode(), userNoticeHeadReqVO.getUserAccount());
     *             }
     *             unreadTotal = unreadTotal + unreadCount + unreadCount4;
     *         }
     *         rspVO.setUnreadTotal(unreadTotal);
     *         return rspVO;
     *     }
     */

    /**
     *  public UserNoticeHeadRspVO getForceUserNoticeHeadList(UserNoticeHeadReqVO userNoticeHeadReqVO) {
     *         UserNoticeHeadRspVO rspVO = new UserNoticeHeadRspVO();
     *         userNoticeHeadReqVO.setNoticeType(NotificationTypeEnum.ANNOUNCEMENT.getCode());
     *         userNoticeHeadReqVO.setCurrentTime(System.currentTimeMillis());
     *         List<UserNoticeRespVO> userNoticeList = userNoticeConfigRepository.getForceUserNoticeHeadList(userNoticeHeadReqVO);
     *         rspVO.setUserNoticeList(userNoticeList);
     *         return rspVO;
     *     }
     */

    /**
     * 获取用户通知列表
     */
    public ResponseVO<Page<UserNoticeRespVO>> getUserNoticeList(UserNoticeReqVO userNoticeReqVO) {
        Integer noticeTypeVo = userNoticeReqVO.getNoticeType();
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
        userNoticeReqVO.setUserAccount(CurrReqUtils.getAccount());
        userNoticeReqVO.setUserId(CurrReqUtils.getOneId());
        userNoticeReqVO.setDeviceTerminal(deviceTerminal.toString());

        userNoticeReqVO.setCurrentTime(System.currentTimeMillis());
        Page<UserNoticeRespVO> page = new Page<>(userNoticeReqVO.getPageNumber(), userNoticeReqVO.getPageSize());
        page = userNoticeConfigRepository.getUserNoticeListNew(page, userNoticeReqVO);
        page = convertSystemMessage(page);
        return ResponseVO.success(page);
    }

    public UserInfoApi getSystemI18nMessage() {
        return SpringUtils.getBean(UserInfoApi.class);
    }

    public Page<UserNoticeRespVO> convertSystemMessage(Page<UserNoticeRespVO> page) {
        List<UserNoticeRespVO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return page;
        }
        records.forEach(record -> {
            if (record.getNoticeType().intValue() == CommonConstant.business_four && StringUtils.isNotBlank(record.getSystemMessageCode())) {
                UserLanguageVO languageVO = new UserLanguageVO();
                languageVO.setUserId(CurrReqUtils.getOneId());
                languageVO.setUserType(CommonConstant.business_zero_str);

                languageVO.setMessageType(SystemMessageEnum.nameOfCode(record.getSystemMessageCode()));
                UserSystemMessageConfigVO messageConfigVO = getSystemI18nMessage().getUserLanguage(languageVO);
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
                log.info(" 系统消息,翻译后 : "+record +" SystemMessageConfigVO "+messageConfigVO);

            } else {
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
        page.setRecords(records);
        return page;
    }


    public int getUserUnReadNoticeNums() {
        String userId = CurrReqUtils.getOneId();
        Integer deviceTerminal = CurrReqUtils.getReqDeviceType();
        return userNoticeConfigRepository.getUnreadNoticeNums(userId, deviceTerminal);
    }


    /**
     * 标记已读通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void setReadState(UserNoticeSetReadStateReqVO userNoticeSetReadStateReqVO) {
        String userAccount = userNoticeSetReadStateReqVO.getUserAccount();
        UserNoticeTargetPO updateTarget = userNoticeTargetRepository.selectById(userNoticeSetReadStateReqVO.getTargetId());
        if (updateTarget == null) {
            log.error("查询UserNoticeConfig表的数据，通知target={}不存在", userNoticeSetReadStateReqVO.getTargetId());
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_IS_NOT_EXIST);
        }
        LambdaUpdateWrapper<UserNoticeTargetPO> systemUpdateWrapper = new LambdaUpdateWrapper();
        systemUpdateWrapper.eq(UserNoticeTargetPO::getUserId, userNoticeSetReadStateReqVO.getUserId());
        systemUpdateWrapper.eq(UserNoticeTargetPO::getId, userNoticeSetReadStateReqVO.getTargetId());
        if (userNoticeSetReadStateReqVO.getStatus() == 1) {
            systemUpdateWrapper.set(UserNoticeTargetPO::getReadState, UserNoticeTargetConstant.READ);
        } else if (userNoticeSetReadStateReqVO.getStatus() == 2) {
            systemUpdateWrapper.set(UserNoticeTargetPO::getDeleteState, UserNoticeTargetConstant.DELETE);
        }
        int ii = userNoticeTargetRepository.update(null, systemUpdateWrapper);
        if (ii <= 0) {
            log.error(String.format("修改UserNoticeTarget表的数据失败，用户userAccount={}", userAccount));
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_UPDATE_IS_FAIL);
        }

    }

    /**
     * 标记已读通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void setReadStateAll(UserNoticeReqVO userNoticeReqVO) {
        updateUserNoticeStatus(userNoticeReqVO, false);
    }

    public void setDelStateAll(UserNoticeReqVO userNoticeReqVO) {

        try {
            String siteCode = CurrReqUtils.getSiteCode();
            ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(siteCode);
            if (!responseVO.isOk()){
                throw new BaowangDefaultException(ResultCode.UPDATE_ERROR);
            }
            SiteVO siteVO = responseVO.getData();
            Integer handicapMode = siteVO.getHandicapMode();
            if (handicapMode == null || handicapMode == 0) {
                //国际盘
                Long count = getUserUnreadNoticeNums(userNoticeReqVO);
                if (count > 0) {
                    throw new BaowangDefaultException(ResultCode.UPDATE_ERROR);
                }
            }
            //直接更新已读
            updateUserNoticeStatus(userNoticeReqVO, true);
        } catch (Exception e) {
            log.error("UserNoticeService.updateUserNoticeStatus " + e.getMessage());
        }
    }


    public void updateUserNoticeStatus(UserNoticeReqVO userNoticeReqVO, boolean isDel) {
        String userId = CurrReqUtils.getOneId();
        Integer noticeTypeVo = userNoticeReqVO.getNoticeType();
        Integer deviceTerminal = CurrReqUtils.getReqDeviceType();
        if (noticeTypeVo == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_TYPE_IS_NULL);
        }
        if (deviceTerminal == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_DEVICE_TERMINAL_IS_NULL);
        }
        if (!StringUtils.isNotBlank(userId)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        userNoticeReqVO.setIsDelete(isDel ? 0 : 1);
        userNoticeReqVO.setUserId(userId);
        userNoticeReqVO.setDeviceTerminal(deviceTerminal.toString());
        userNoticeConfigRepository.updateUserNoticeStatus(userNoticeReqVO);
    }

    public Long getUserUnreadNoticeNums(UserNoticeReqVO userNoticeReqVO) {
        String userId = CurrReqUtils.getOneId();
        Integer noticeTypeVo = userNoticeReqVO.getNoticeType();
        Integer deviceTerminal = CurrReqUtils.getReqDeviceType();
        if (noticeTypeVo == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_TYPE_IS_NULL);
        }
        if (deviceTerminal == null) {
            throw new BaowangDefaultException(ResultCode.USER_NOTICE_DEVICE_TERMINAL_IS_NULL);
        }
        if (!StringUtils.isNotBlank(userId)) {
            throw new BaowangDefaultException(ResultCode.LOGIN_EXPIRE);
        }
        userNoticeReqVO.setUserId(userId);
        userNoticeReqVO.setDeviceTerminal(deviceTerminal.toString());
        return userNoticeConfigRepository.getUserUnreadNoticeNums(userNoticeReqVO);
    }

    public void userNoticeTargetAdd(UserNoticeTargetAddVO userNoticeTargetAddVO) {
        UserNoticeTargetPO userNoticeTargetPO = ConvertUtil.entityToModel(userNoticeTargetAddVO, UserNoticeTargetPO.class);
        userNoticeTargetService.save(userNoticeTargetPO);
    }

    public UserNoticeUnreadNumRspVO getUserNoticeUnreadNum() {
        UserNoticeUnreadNumRspVO result = new UserNoticeUnreadNumRspVO();
        String userId = CurrReqUtils.getOneId();
        Integer deviceTerminal = CurrReqUtils.getReqDeviceType();
        int notice = userNoticeConfigRepository.getUnreadNoticeNumSplit(userId, deviceTerminal);
        int activity = userNoticeConfigRepository.getUnreadActivityNumSplit(userId, deviceTerminal);
        log.info( " notice - "+notice + " activity - "+activity);
        result.setUnreadNoticeNum(notice);
        result.setUnreadActivityNum(activity);
        return result;
    }


}
