package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.LockStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.properties.CommonProperties;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.vo.StatusVO;
import com.cloud.baowang.user.api.vo.user.BackContentText;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAddVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackDetailResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppResVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackAppPageReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackReplyReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteReqVO;
import com.cloud.baowang.user.api.vo.user.SiteUserFeedbackSiteRespVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.po.SiteUserFeedbackPO;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.repositories.SiteUserFeedbackRepository;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.github.xiaoymin.knife4j.core.conf.GlobalConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums.FEEDBACK_MAX_MESSAGES_IN_3_MINUTES;
import static io.prometheus.client.SampleNameFilter.stringToList;
import static java.util.stream.Collectors.toMap;


/**
 *
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteUserFeedbackService extends ServiceImpl<SiteUserFeedbackRepository, SiteUserFeedbackPO> {

    private final SiteUserFeedbackRepository siteUserFeedbackRepository;
    private final UserInfoRepository userInfoRepository;
    private final SiteUserLabelConfigService siteUserLabelConfigService;
    private final SiteVIPGradeService siteVIPGradeService;
    private final CommonProperties commonproperties;
    private final SystemDictConfigApi systemDictConfigApi;


    /**
     *留言限制
     * @param userAccount
     */

    public void checkUserNoteLimit(String userAccount) {
        String limitKey = String.format(RedisConstants.KEY_FEEDBACK_TIMES_LIMIT,CurrReqUtils.getSiteCode(),userAccount);
        Integer limitValue = RedisUtil.getValue(limitKey);
        SystemDictConfigRespVO configValue = systemDictConfigApi.getByCode(FEEDBACK_MAX_MESSAGES_IN_3_MINUTES.getCode(),CurrReqUtils.getSiteCode()).getData();
        if (limitValue != null && limitValue >= Integer.parseInt(configValue.getConfigParam())) {
            throw new BaowangDefaultException(configValue.getHintInfo());
        }

    }

    public void incrUserNoteLimit(String userAccount) {
        String limitKey = String.format(RedisConstants.KEY_FEEDBACK_TIMES_LIMIT,CurrReqUtils.getSiteCode(), userAccount);
        RedisUtil.incrAndExpirationFirst(limitKey,CommonConstant.business_one,CommonConstant.THREE_MINUTES_SECONDS);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> submit(SiteUserFeedbackAddVO siteUserFeedbackAddVO) {
        checkUserNoteLimit(CurrReqUtils.getOneId());
        String content = siteUserFeedbackAddVO.getContent().trim();
        String topFeedId = siteUserFeedbackAddVO.getFeedTopId();
        if (StringUtils.isEmpty(content) || content.length() > 500) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        String picUrls = siteUserFeedbackAddVO.getPicUrls();
        if (StringUtils.isNotEmpty(picUrls)) {
            String[] split = picUrls.split(",");
            if (split.length > 3) {
                log.error("picUrl size error");
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        if (StringUtils.isNotEmpty(siteUserFeedbackAddVO.getOrderId()) && siteUserFeedbackAddVO.getOrderId().length() > CommonConstant.business_thirty_two){
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        String userId = CurrReqUtils.getOneId();
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        UserInfoPO userInfo = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getUserAccount,userAccount).eq(UserInfoPO::getSiteCode,siteCode));
        String userLabelId = userInfo.getUserLabelId();
        SiteUserFeedbackPO siteUserFeedbackPO = BeanUtil.copyProperties(siteUserFeedbackAddVO, SiteUserFeedbackPO.class);
        siteUserFeedbackPO.setUserId(userId);
        siteUserFeedbackPO.setUserLabel(userLabelId);
        siteUserFeedbackPO.setUserAccount(userAccount);
        siteUserFeedbackPO.setSiteCode(siteCode);
        siteUserFeedbackPO.setPicUrls(picUrls);
        siteUserFeedbackPO.setSort(0);
        siteUserFeedbackPO.setIsRead(1);
        siteUserFeedbackPO.setUpdatedTime(System.currentTimeMillis());
        siteUserFeedbackPO.setVipGradeCode(userInfo.getVipGradeCode());
        SiteVIPGradePO gradePO = siteVIPGradeService.getOne(Wrappers.<SiteVIPGradePO>lambdaQuery().eq(SiteVIPGradePO::getSiteCode, siteCode).eq(SiteVIPGradePO::getVipGradeCode, userInfo.getVipGradeCode()));
        siteUserFeedbackPO.setVipGradeName(gradePO.getVipGradeName());

        if (StringUtils.isNotEmpty(topFeedId)) {
            SiteUserFeedbackPO maxSort = siteUserFeedbackRepository.selectOne(Wrappers.<SiteUserFeedbackPO>lambdaQuery().select(SiteUserFeedbackPO::getSort,SiteUserFeedbackPO::getType).eq(SiteUserFeedbackPO::getFeedId, topFeedId).orderByDesc(SiteUserFeedbackPO::getSort).last("limit 1"));
            siteUserFeedbackPO.setSort(maxSort.getSort() + 1);
            siteUserFeedbackPO.setFeedId(topFeedId);
            siteUserFeedbackPO.setType(maxSort.getType());
        }
        siteUserFeedbackRepository.insert(siteUserFeedbackPO);

        if (StringUtils.isEmpty(topFeedId)) {
            topFeedId = siteUserFeedbackPO.getId();
        }
        siteUserFeedbackRepository.update(null, Wrappers.<SiteUserFeedbackPO>lambdaUpdate().eq(SiteUserFeedbackPO::getId, topFeedId).eq(SiteUserFeedbackPO::getSort, 0).set(SiteUserFeedbackPO::getFeedId, siteUserFeedbackPO.getId()));
        incrUserNoteLimit(CurrReqUtils.getOneId());
        return ResponseVO.success(true);
    }

    public ResponseVO<Page<SiteUserFeedbackAppResVO>> userPageList(SiteUserFeedbackAppPageReqVO reqVO) {
        String userAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        Page<SiteUserFeedbackSiteRespVO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        SiteUserFeedbackSiteReqVO request = BeanUtil.toBean(reqVO, SiteUserFeedbackSiteReqVO.class);
        request.setUserAccount(userAccount);
        Page<SiteUserFeedbackSiteRespVO> pageResp = siteUserFeedbackRepository.feedbackList(page, request, siteCode);
        Page<SiteUserFeedbackAppResVO> res = new Page();
        BeanUtil.copyProperties(pageResp, res);
        List<SiteUserFeedbackSiteRespVO> records = pageResp.getRecords();
        if (records.isEmpty()) {
            return ResponseVO.success(res);
        }
        List<SiteUserFeedbackAppResVO> vos = BeanUtil.copyToList(records, SiteUserFeedbackAppResVO.class);
        List<String> feedIds = vos.stream().map(SiteUserFeedbackAppResVO::getFeedId).toList();
        List<SiteUserFeedbackPO> mainPos = siteUserFeedbackRepository.selectBatchIds(feedIds);
        Map<String, String> id2Content = mainPos.stream().collect(toMap(SiteUserFeedbackPO::getId, SiteUserFeedbackPO::getContent));
        vos.forEach(e -> {
            e.setId(e.getFeedId());
            e.setCreatedTime(e.getUpdatedTime());
            e.setContent(id2Content.get(e.getFeedId()));
            if(e.getBackTime() != null){
                e.setCreatedTime(e.getBackTime());
            }
            if (StringUtils.isNotEmpty(e.getPicUrls())) {
                e.setPicUrls(Arrays.stream(e.getPicUrls().split(",")).map(m -> commonproperties.getFileDomain() + m).collect(Collectors.joining(",")));
            }
        });
        res.setRecords(vos);

        return ResponseVO.success(res);
    }

    public ResponseVO<List<SiteUserFeedbackDetailResVO>> detail(IdVO idVO) {
        List<SiteUserFeedbackDetailResVO> res = Lists.newArrayList();
        String id = idVO.getId();
        SiteUserFeedbackPO siteUserFeedbackPO = siteUserFeedbackRepository.selectById(id);
        if (siteUserFeedbackPO == null) {
            return ResponseVO.success();
        }
        SiteUserFeedbackDetailResVO top = new SiteUserFeedbackDetailResVO();
        BeanUtil.copyProperties(siteUserFeedbackPO, top,"backContent");
        if (siteUserFeedbackPO.getBackContent() != null){
            List<BackContentText> backContent = JSONArray.parseArray(siteUserFeedbackPO.getBackContent(), BackContentText.class);
            top.setBackContent(backContent);
        }

        res.add(top);
        if (!siteUserFeedbackPO.getId().equals(siteUserFeedbackPO.getFeedId())) {
            List<SiteUserFeedbackPO> feeds = siteUserFeedbackRepository.selectList(Wrappers.<SiteUserFeedbackPO>lambdaQuery().eq(SiteUserFeedbackPO::getFeedId, id));
            if (CollectionUtil.isNotEmpty(feeds)) {
                for (SiteUserFeedbackPO feed : feeds) {
                    SiteUserFeedbackDetailResVO subFeed = new SiteUserFeedbackDetailResVO();
                    BeanUtil.copyProperties(feed, subFeed,"backContent");
                    if (feed.getBackContent() != null){
                        List<BackContentText> backContent = JSONArray.parseArray(feed.getBackContent(), BackContentText.class);
                        subFeed.setBackContent(backContent);
                    }
                    res.add(subFeed);
                }
            }
        }
        UserInfoPO userInfoVO = userInfoRepository.selectOne(Wrappers.<UserInfoPO>lambdaQuery().eq(UserInfoPO::getUserId, siteUserFeedbackPO.getUserId()));
        res.forEach(e -> {
            if (StringUtils.isNotEmpty(e.getPicUrls())) {
                e.setPicUrls(Arrays.stream(e.getPicUrls().split(",")).map(m -> commonproperties.getFileDomain() + m).collect(Collectors.joining(",")));
            }
            e.setUserAccount(userInfoVO.getUserAccount());
            e.setAvatarCode(userInfoVO.getAvatarCode());
            e.setAvatar(userInfoVO.getAvatar());
            if (CollectionUtil.isNotEmpty(e.getBackContent())){
                e.setLatestBackContent(e.getBackContent().get(e.getBackContent().size() - 1).getBackContent());
            }
            List<BackContentText> backContent = e.getBackContent();
            if(CollectionUtil.isNotEmpty(backContent)){
                backContent.sort(Comparator.comparing(BackContentText::getBackTime).reversed());
            }
        });
        res.sort(Comparator.comparing(SiteUserFeedbackDetailResVO::getSort).reversed());
        return ResponseVO.success(res);
    }

    public ResponseVO<Page<SiteUserFeedbackSiteRespVO>> feedbackList(SiteUserFeedbackSiteReqVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<SiteUserFeedbackSiteRespVO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteUserFeedbackSiteRespVO> result = siteUserFeedbackRepository.feedbackList(page, reqVO, siteCode);
        List<SiteUserFeedbackSiteRespVO> records = result.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            List<String> idList = records.stream().map(SiteUserFeedbackSiteRespVO::getUserLabel).filter(Objects::nonNull).toList();
            Map<String, String> id2Name = Maps.newHashMap();
            if (CollectionUtil.isNotEmpty(idList)) {
                List<String> ids = idList.stream().flatMap(e -> Arrays.stream(e.split(CommonConstant.COMMA))).toList();
                List<GetUserLabelByIdsVO> userLabelByIds = siteUserLabelConfigService.getUserLabelByIds(ids);
                id2Name.putAll(userLabelByIds.stream().collect(toMap(GetUserLabelByIdsVO::getId, GetUserLabelByIdsVO::getLabelName)));
            }
            List<String> feedIds = records.stream().filter(e -> e.getBackTime() == null).map(SiteUserFeedbackSiteRespVO::getFeedId).toList();
            Map<String,SiteUserFeedbackPO> mainId2PO = Maps.newHashMap();
            Map<String,SiteUserFeedbackPO> id2FeedPO = Maps.newHashMap();
            if (CollectionUtil.isNotEmpty(feedIds)){
                // 主id
                List<SiteUserFeedbackPO> pos = siteUserFeedbackRepository.selectBatchIds(feedIds);
                if(CollectionUtil.isNotEmpty(pos)){
                    mainId2PO.putAll(pos.stream().filter(e -> e.getBackTime() != null).collect(toMap(SiteUserFeedbackPO::getId, e -> e)));
                    id2FeedPO.putAll(mainId2PO);
                }
                // feedid
                List<SiteUserFeedbackPO> feedPos = siteUserFeedbackRepository.latestReply(feedIds);
                if (CollectionUtil.isNotEmpty(feedPos)){
                    Map<String, SiteUserFeedbackPO> id2PO = feedPos.stream().collect(toMap(SiteUserFeedbackPO::getFeedId, e -> e));
                    id2FeedPO.putAll(id2PO);
                }
            }
            for (SiteUserFeedbackSiteRespVO record : records) {
                if (StringUtils.isNotEmpty(record.getBackContent())){
                    record.setCurrentBack(1);
                }
                if (StringUtils.isNotEmpty(record.getPicUrls())) {

                    record.setPicUrls(Arrays.stream(record.getPicUrls().split(",")).map(m -> commonproperties.getFileDomain() + m).collect(Collectors.joining(",")));
                }
                if (mainId2PO.get(record.getFeedId()) != null){
                    record.setOrderId(mainId2PO.get(record.getFeedId()).getOrderId());
                }
                String backContent = record.getBackContent();
                if (backContent == null && id2FeedPO.get(record.getFeedId()) != null){
                    backContent = id2FeedPO.get(record.getFeedId()).getBackContent();
                }
                if (StringUtils.isNotEmpty(backContent)){
                    List<BackContentText> contentTexts = JSONArray.parseArray(backContent, BackContentText.class);
                    BackContentText contentText = contentTexts.get(contentTexts.size() - 1);
                    record.setBackContent(contentText.getBackContent());
                    record.setBackTime(contentText.getBackTime());
                    record.setBackAccount(contentText.getBackAccount());
                }
                if (StringUtils.isNotEmpty(record.getUserLabel())) {
                    List<String> recIds = stringToList(record.getUserLabel());
                    String labelNames = recIds.stream().map(e -> "#" + id2Name.get(e)).collect(Collectors.joining(CommonConstant.COMMA));
                    record.setUserLabel(labelNames);
                }
            }

            sort(reqVO, records, result);
        }
        return ResponseVO.success(result);
    }

    private void sort(SiteUserFeedbackSiteReqVO reqVO, List<SiteUserFeedbackSiteRespVO> records, Page<SiteUserFeedbackSiteRespVO> result) {
        if ("back_time".equals(reqVO.getOrderField()) && reqVO.getOrderType() != null){
            records.removeIf(Objects::isNull);
            Comparator<SiteUserFeedbackSiteRespVO> comparing = Comparator.comparing(SiteUserFeedbackSiteRespVO::getBackTime,Comparator.nullsFirst(Comparator.naturalOrder()));
            if (reqVO.getOrderType().equalsIgnoreCase("desc")){
                comparing = Comparator.comparing(SiteUserFeedbackSiteRespVO::getBackTime,Comparator.nullsLast(Comparator.reverseOrder()));
            }
            records = records.stream().sorted(comparing).toList();
            result.setRecords(records);
        }
    }

    public ResponseVO<SiteUserFeedbackSiteRespVO> lock(StatusListVO vo) {
        List<String> ids = vo.getId();
        // 获取参数
        List<SiteUserFeedbackPO> feedbackPOs = this.listByIds(ids);
        if (CollectionUtil.isEmpty(feedbackPOs)) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            feedbackPOs.forEach(e -> {
                StatusVO statusVO = new StatusVO();
                statusVO.setId(e.getId());
                statusVO.setStatus(vo.getStatus());
                lockOperate(statusVO);
            });
        } catch (Exception e) {
            log.error("会员意见反馈-锁单/解锁error,审核单号:{},操作人:{}", vo.getId(), CurrReqUtils.getAccount(), e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
        // 获取单条锁单最新数据状态
        if(ids.size() == 1){
            // 获取最新状态信息
            return ResponseVO.success(getLatestFeedback(ids.get(0)));
        }
        return ResponseVO.success();

    }

    private SiteUserFeedbackSiteRespVO getLatestFeedback(String id) {
        SiteUserFeedbackSiteRespVO respVO = new SiteUserFeedbackSiteRespVO();
        SiteUserFeedbackPO feedbackPO = siteUserFeedbackRepository.selectById(id);
        BeanUtil.copyProperties(feedbackPO,respVO);
        String backContent = feedbackPO.getBackContent();
        if (StringUtils.isEmpty(backContent)){
            // 最后回复
            SiteUserFeedbackPO backPO = siteUserFeedbackRepository.selectOne(Wrappers.<SiteUserFeedbackPO>lambdaQuery().gt(SiteUserFeedbackPO::getBackTime,0)
                    .and(e->e.eq(SiteUserFeedbackPO::getFeedId,feedbackPO.getFeedId()).or().eq(SiteUserFeedbackPO::getId,feedbackPO.getFeedId()))
                    .orderByDesc(SiteUserFeedbackPO::getBackTime)
                    .last("limit 1"));
            if (backPO != null){
                backContent = backPO.getBackContent();
            }
        }
        if (StringUtils.isEmpty(backContent)){
            return respVO;
        }
        List<BackContentText> subBackContent = JSONArray.parseArray(backContent, BackContentText.class);
        BackContentText contentText = subBackContent.get(subBackContent.size() - 1);
        respVO.setBackAccount(contentText.getBackAccount());
        respVO.setBackTime(contentText.getBackTime());
        respVO.setBackContent(contentText.getBackContent());
        if(StringUtils.isNotEmpty(respVO.getPicUrls())) {
            respVO.setPicUrls(Arrays.stream(respVO.getPicUrls().split(",")).map(m -> commonproperties.getFileDomain() + m).collect(Collectors.joining(",")));
        }
        return respVO;
    }

    private ResponseVO<Boolean> lockOperate(StatusVO vo) {
        boolean res = false;
        RLock fairLock = RedisUtil.getFairLock(RedisKeyTransUtil.getFeedBackLockKey(vo.getId()));
        String account = CurrReqUtils.getAccount();
        try {
            res = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                SiteUserFeedbackPO feedbackPO = this.getById(vo.getId());
                Integer myLockStatus;
                String locker;
                // 锁单状态 0未锁 1已锁
                if (LockStatusEnum.LOCK.getCode().equals(vo.getStatus())) {
                    // 开始锁单
                    if (LockStatusEnum.LOCK.getCode().equals(feedbackPO.getLockStatus())) {
                        return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
                    }
//                    if (feedbackPO.getBackContent() != null) {
//                        return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
//                    }
                    myLockStatus = LockStatusEnum.LOCK.getCode();
                    locker = account;
                } else {
                    // 开始解锁
                    myLockStatus = LockStatusEnum.UNLOCK.getCode();
                    locker = null;
                }

                LambdaUpdateWrapper<SiteUserFeedbackPO> lambdaUpdate = new LambdaUpdateWrapper<>();
                lambdaUpdate.eq(SiteUserFeedbackPO::getId, vo.getId())
                        .set(SiteUserFeedbackPO::getLockStatus, myLockStatus)
                        .set(SiteUserFeedbackPO::getLocker, locker);
                this.update(null, lambdaUpdate);
                return ResponseVO.success(true);
            }
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);

        } catch (Exception e) {
            log.error("会员意见反馈-锁单/解锁error,审核单号:{},操作人:{}", vo.getId(), account, e);
            throw (BaowangDefaultException) e;
        } finally {
            fairLock.unlock();
        }
    }

    public ResponseVO<Boolean> reply(SiteUserFeedbackReplyReqVO reqVO) {
        String account = CurrReqUtils.getAccount();
        List<String> ids = reqVO.getIds();
        if (CollectionUtil.isEmpty(ids) || StringUtils.isEmpty(reqVO.getContext().trim()) || reqVO.getContext().length() > 1000) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        List<SiteUserFeedbackPO> pos = siteUserFeedbackRepository.selectBatchIds(ids);
        if (CollectionUtil.isEmpty(pos)) {
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }
        pos.removeIf(e -> !account.equalsIgnoreCase(e.getLocker()));
        if (CollectionUtil.isEmpty(pos)) {
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }
        BackContentText contentText = new BackContentText();
        contentText.setBackAccount(account);
        contentText.setBackContent( reqVO.getContext());
        contentText.setBackTime(System.currentTimeMillis());
        List<BackContentText> contentTexts = Lists.newArrayList(contentText);
        siteUserFeedbackRepository.update(null, Wrappers.<SiteUserFeedbackPO>lambdaUpdate()
                .in(SiteUserFeedbackPO::getId, ids)
                .set(SiteUserFeedbackPO::getBackAccount, account)
                .set(SiteUserFeedbackPO::getLockStatus, null)
                .set(SiteUserFeedbackPO::getLocker, null)
                .set(SiteUserFeedbackPO::getBackContent, JSONObject.toJSONString(contentTexts))
                .set(SiteUserFeedbackPO::getBackTime, System.currentTimeMillis())
                .set(SiteUserFeedbackPO::getUpdatedTime, System.currentTimeMillis())
                .set(SiteUserFeedbackPO::getUpdater, account)
                .set(SiteUserFeedbackPO::getIsRead, 0)
        );
        return ResponseVO.success(true);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void read(IdVO idVO) {
        SiteUserFeedbackPO feedbackPO = siteUserFeedbackRepository.selectById(idVO.getId());
        if (Objects.isNull(feedbackPO)) {
            return;
        }
        feedbackPO.setIsRead(1);
        if (StringUtils.isNotEmpty(feedbackPO.getBackContent())){
            List<BackContentText> backContent = JSONArray.parseArray(feedbackPO.getBackContent(), BackContentText.class);
            backContent.forEach(e->e.setIsRead(1));
            feedbackPO.setBackContent(JSONObject.toJSONString(backContent));
        }
        siteUserFeedbackRepository.updateById(feedbackPO);
        List<SiteUserFeedbackPO> pos = siteUserFeedbackRepository.selectList(Wrappers.<SiteUserFeedbackPO>lambdaQuery().eq(SiteUserFeedbackPO::getFeedId, idVO.getId()).eq(SiteUserFeedbackPO::getIsRead, 0));
        if (CollectionUtil.isEmpty(pos)){
            return;
        }
        for (SiteUserFeedbackPO e : pos) {
            e.setIsRead(1);
            if (StringUtils.isNotEmpty(e.getBackContent() )){
                List<BackContentText> subBackContent = JSONArray.parseArray(e.getBackContent(), BackContentText.class);
                subBackContent.forEach(el->el.setIsRead(1));
                e.setBackContent(JSONObject.toJSONString(subBackContent));
            }
        }
        updateBatchById(pos);
    }

    public int getUnreadNums(String userId) {
        List<SiteUserFeedbackPO> pos = siteUserFeedbackRepository.selectList(Wrappers.<SiteUserFeedbackPO>lambdaQuery().select(SiteUserFeedbackPO::getBackContent).eq(SiteUserFeedbackPO::getUserId, userId).eq(SiteUserFeedbackPO::getIsRead, CommonConstant.business_zero));
        if(CollectionUtil.isEmpty(pos)){
            return 0;
        }
        pos.removeIf(e->e.getBackContent() == null);
        if(CollectionUtil.isEmpty(pos)){
            return 0;
        }
        int count  = 0;
        try {
            for (SiteUserFeedbackPO po : pos) {
                List<BackContentText> backContent = JSONArray.parseArray(po.getBackContent(), BackContentText.class);
                long total = backContent.stream().filter(e -> e.getIsRead() == 0).count();
                count += Math.toIntExact(total);
            }
        }catch (Exception e){
            log.error("获取未读反馈错误,{}",JSONObject.toJSONString(pos),e);
        }
        return count;
    }

    public ResponseVO<Boolean> replyAgain(SiteUserFeedbackReplyReqVO reqVO) {

        String account = CurrReqUtils.getAccount();
        String id = reqVO.getId();
        if (id == null || StringUtils.isEmpty(reqVO.getContext().trim()) || reqVO.getContext().length() > 1000) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        SiteUserFeedbackPO po = siteUserFeedbackRepository.selectById(id);
        if (Objects.isNull(po)) {
            return ResponseVO.fail(ResultCode.ORDER_NOT_EXIST);
        }
        if (StringUtils.isEmpty(po.getBackContent())) {
            return ResponseVO.fail(ResultCode.PARAM_NOT_VALID);
        }
        BackContentText contentText = new BackContentText();
        contentText.setBackAccount(account);
        contentText.setBackContent( reqVO.getContext());
        contentText.setBackTime(System.currentTimeMillis());
        List<BackContentText> backContent = JSONArray.parseArray(po.getBackContent(), BackContentText.class);
        backContent.add(contentText);
        siteUserFeedbackRepository.update(null, Wrappers.<SiteUserFeedbackPO>lambdaUpdate()
                .eq(SiteUserFeedbackPO::getId, po.getId())
                .set(SiteUserFeedbackPO::getBackAccount, account)
                .set(SiteUserFeedbackPO::getLockStatus, null)
                .set(SiteUserFeedbackPO::getLocker, null)
                .set(SiteUserFeedbackPO::getBackContent, JSONObject.toJSONString(backContent))
                .set(SiteUserFeedbackPO::getBackTime, System.currentTimeMillis())
                .set(SiteUserFeedbackPO::getUpdatedTime, System.currentTimeMillis())
                .set(SiteUserFeedbackPO::getUpdater, account)
                .set(SiteUserFeedbackPO::getIsRead, 0)
        );
        return ResponseVO.success(true);
    }

    public Boolean del(String id) {
        siteUserFeedbackRepository.deleteById(id);
        siteUserFeedbackRepository.delete(Wrappers.<SiteUserFeedbackPO>lambdaQuery().eq(SiteUserFeedbackPO::getFeedId, id));
        return true;
    }

    public Boolean batchDel(String[] ids) {
        if (null == ids || ids.length == 0) {
            log.warn("批量删除反馈意见传入ids 为空");
            return true;
        }
        List<String> idList = Arrays.stream(ids)
                .filter(id -> id != null && !id.trim().isEmpty())
                .map(String::trim)
                .toList();
        if (CollectionUtils.isEmpty(idList)) {
            log.warn("批量删除反馈意见传入ids 数据为空");
            return true;
        }
        siteUserFeedbackRepository.deleteBatchIds(idList);
        siteUserFeedbackRepository.delete(Wrappers.<SiteUserFeedbackPO>lambdaQuery().in(SiteUserFeedbackPO::getFeedId, idList));
        return true;
    }
}
