package com.cloud.baowang.activity.service;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.activity.api.vo.base.ActiveSortVO;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.po.SiteTaskConfigNextPO;
import com.cloud.baowang.activity.po.SiteTaskConfigPO;
import com.cloud.baowang.activity.po.SiteTaskOverViewConfigPO;
import com.cloud.baowang.activity.repositories.SiteTaskConfigNextRepository;
import com.cloud.baowang.activity.repositories.SiteTaskConfigRepository;
import com.cloud.baowang.activity.repositories.SiteTaskOverViewConfigRepository;
import com.cloud.baowang.activity.utils.DataUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteTaskConfigService extends ServiceImpl<SiteTaskConfigRepository, SiteTaskConfigPO> {

    private final SiteTaskConfigRepository siteTaskConfigRepository;

    private final SiteTaskConfigNextRepository taskConfigNextRepository;

    private final I18nApi i18nApi;

    private final LanguageManagerApi languageManagerApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final SiteTaskOverViewConfigRepository overViewConfigRepository;


    public List<SiteTaskConfigResVO> taskConfigList(TaskConfigReqVO requestVO) {

        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, requestVO.getSiteCode());
        queryWrapper.eq(SiteTaskConfigPO::getTaskType, requestVO.getTaskType());
        queryWrapper.orderByAsc(SiteTaskConfigPO::getSort);
        List<SiteTaskConfigPO> records = siteTaskConfigRepository.selectList(queryWrapper);
        LambdaQueryWrapper<SiteTaskConfigNextPO> queryWrapperNext = new LambdaQueryWrapper<>();
        queryWrapperNext.eq(SiteTaskConfigNextPO::getSiteCode, requestVO.getSiteCode());
        queryWrapperNext.eq(SiteTaskConfigNextPO::getTaskType, requestVO.getTaskType());
        // 如果增加，如果缺少任务，则需要添加
        List<SiteTaskConfigPO> insert = new ArrayList<>();
        List<String> langCodes = languageManagerApi.list().getData().stream().map(LanguageManagerListVO::getCode).toList();
        for (TaskEnum subTask : TaskEnum.getTaskListByType(requestVO.getTaskType())) {
            String subTaskType = subTask.getSubTaskType();
            boolean match = records.stream().anyMatch(e -> subTaskType.equals(e.getSubTaskType()));
            if (!match) {
                SiteTaskConfigPO taskConfigPO = processInsert(requestVO, subTask, langCodes);
                insert.add(taskConfigPO);
                // 返回前端
                records.add(taskConfigPO);
            }
        }
        if (CollectionUtil.isNotEmpty(insert)) {
            this.saveBatch(insert);
        }

        List<SiteTaskConfigNextPO> recordNexts = taskConfigNextRepository.selectList(queryWrapperNext);

        List<SiteTaskConfigResVO> results = new ArrayList<>();
        // 查询 场馆列表
        Map<String, String> venueNameMap = playVenueInfoApi.getSiteVenueNameMap().getData();
        if (CollectionUtils.isNotEmpty(records)) {
            // 查询下一期的

            for (SiteTaskConfigPO po : records) {
                // 查询是否有下一期任务
                Optional<SiteTaskConfigNextPO> first = recordNexts.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), po.getSubTaskType())).findFirst();
                if (first.isPresent()) {
                    po.setTaskNameI18nCode(first.get().getTaskNameI18nCode());
                    po.setTaskDescriptionI18nCode(first.get().getTaskDescriptionI18nCode());
                    po.setTaskPictureI18nCode(first.get().getTaskPictureI18nCode());
                    po.setTaskDescI18nCode(first.get().getTaskDescI18nCode());
                    po.setTaskPicturePcI18nCode(first.get().getTaskPicturePcI18nCode());

                    po.setUpdater(first.get().getUpdater());
                    po.setUpdatedTime(first.get().getUpdatedTime());

                    po.setMinBetAmount(first.get().getMinBetAmount());
                    po.setRewardAmount(first.get().getRewardAmount());
                    po.setWashRatio(first.get().getWashRatio());
                    //po.setVenueType(first.get().getVenueType());
                    po.setVenueCode(first.get().getVenueCode());
                    po.setTaskConfigJson(first.get().getTaskConfigJson());

                }
                SiteTaskConfigResVO resVO = ConvertUtil.entityToModel(po, SiteTaskConfigResVO.class);
                // 解析
                if (StringUtils.isNotBlank(po.getTaskConfigJson())) {
                    resVO.setTaskConfigJson(JSON.parseArray(po.getTaskConfigJson(), TaskSubConfigReqVO.class));
                }
                // 根据任务类型和子任务类型获取任务名称并设置
                assert resVO != null;
                resVO.setSubTaskTypeName(TaskEnum.fromTask(po.getTaskType(), po.getSubTaskType()).getName());
                String venueCode = po.getVenueCode();
                if (StringUtils.isNotBlank(venueCode)) {
                    // 使用 StringJoiner 来处理分隔符和去掉最后的逗号
                    List<String> venueNames = new ArrayList<>();
                    List<String> venueCodes = new ArrayList<>();
                    // 按逗号拆分 venueCode 并映射为对应的名称
                    for (String str : venueCode.split(CommonConstant.COMMA)) {
                        String venueName = venueNameMap.get(str);
                        if (venueName != null) {
                            venueNames.add(venueName);
                            venueCodes.add(str);
                        }
                    }
                    // 设置拼接好的 venueNames
                    resVO.setVenueCodeName(venueNames);
                    resVO.setVenueCode(venueCodes);
                }
                results.add(resVO);
            }

        } else {
            // 初始化
            records = initTaskConfigRecord(requestVO);
            for (SiteTaskConfigPO po : records) {
                SiteTaskConfigResVO resVO = ConvertUtil.entityToModel(po, SiteTaskConfigResVO.class);
                if (StringUtils.isNotBlank(po.getTaskConfigJson())) {
                    resVO.setTaskConfigJson(JSON.parseArray(po.getTaskConfigJson(), TaskSubConfigReqVO.class));
                }
                assert resVO != null;
                //resVO.setSubTaskTypeName(TaskEnum.fromTask(po.getTaskType(), po.getSubTaskType()).getSubTaskType());
                results.add(resVO);
            }
        }

        for (SiteTaskConfigResVO siteTaskConfigPO : results) {
            if (!siteTaskConfigPO.getTaskType().equals(TaskEnum.NOVICE_WELCOME.getTaskType())) {
                List<TaskSubConfigReqVO> list = siteTaskConfigPO.getTaskConfigJson();
                if (CollectionUtil.isNotEmpty(list)) {

                    BigDecimal total = list.stream().map(TaskSubConfigReqVO::getRewardAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    siteTaskConfigPO.setRewardAmount(total);
                }
            }
        }
        return results;
    }

    private SiteTaskConfigPO processInsert(TaskConfigReqVO requestVO, TaskEnum taskEnum, List<String> langCodes) {
        SiteTaskConfigPO siteTaskConfigPO = new SiteTaskConfigPO();
        siteTaskConfigPO.setTaskType(requestVO.getTaskType());
        siteTaskConfigPO.setSubTaskType(taskEnum.getSubTaskType());
        siteTaskConfigPO.setCreatedTime(System.currentTimeMillis());
        siteTaskConfigPO.setCreator(requestVO.getOperator());
        siteTaskConfigPO.setUpdatedTime(System.currentTimeMillis());
        siteTaskConfigPO.setUpdater(requestVO.getOperator());
        siteTaskConfigPO.setSiteCode(requestVO.getSiteCode());
        siteTaskConfigPO.setStatus(EnableStatusEnum.DISABLE.getCode());
        siteTaskConfigPO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        String taskNameI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_NAME.getCode());
        String taskPictureI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE.getCode());
        String taskPicturePcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE_PC.getCode());
        String taskDescI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_DESCRIPTION.getCode());
        String taskDescriptionI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_DESC_DESCRIPTION.getCode());
        siteTaskConfigPO.setTaskNameI18nCode(taskNameI18nCode);
        siteTaskConfigPO.setTaskPictureI18nCode(taskPictureI18nCode);
        siteTaskConfigPO.setTaskPicturePcI18nCode(taskPicturePcI18nCode);
        siteTaskConfigPO.setTaskDescI18nCode(taskDescI18nCode);
        siteTaskConfigPO.setTaskDescriptionI18nCode(taskDescriptionI18nCode);

        //siteTaskConfigPO.setEffectiveStartTime(curr);
        //siteTaskConfigPO.setEffectiveEndTime(endTime);
        //siteTaskConfigPO.setLatest(CommonConstant.business_one);
        List<I18nMsgFrontVO> msgFrontVOS = new ArrayList<>();
        for (String lang : langCodes) {
            I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO();
            i18nMsgFrontVO.setLanguage(lang);
            i18nMsgFrontVO.setMessage("");
            msgFrontVOS.add(i18nMsgFrontVO);
        }
        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                taskNameI18nCode, msgFrontVOS,
                taskPictureI18nCode, msgFrontVOS,
                taskPicturePcI18nCode, msgFrontVOS,
                taskDescI18nCode, msgFrontVOS,
                taskDescriptionI18nCode, msgFrontVOS
        );


        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        return siteTaskConfigPO;

    }

    public List<SiteTaskConfigPO> initTaskConfigRecord(TaskConfigReqVO requestVO) {
        List<TaskEnum> taskEnumList = TaskEnum.getTaskListByType(requestVO.getTaskType());
        List<SiteTaskConfigPO> insert = new ArrayList<>();
        List<String> langCodes = languageManagerApi.list().getData().stream().map(LanguageManagerListVO::getCode).toList();
        for (TaskEnum taskEnum : taskEnumList) {
            SiteTaskConfigPO taskConfigPO = processInsert(requestVO, taskEnum, langCodes);
            insert.add(taskConfigPO);
        }
        this.saveBatch(insert);
        return insert;

    }


    public ResponseVO<Boolean> operateStatus(SiteTaskOnOffVO reqVO) {
        if (ObjectUtil.isEmpty(reqVO.getStatus())) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 只有启用才校验一些字段
        Boolean flag = reqVO.getStatus() == 1;
        // 查询
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getId, reqVO.getId());
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, reqVO.getSiteCode());
        SiteTaskConfigPO taskConfigPO = siteTaskConfigRepository.selectOne(queryWrapper);
        if (taskConfigPO == null) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (taskConfigPO.getStatus() == reqVO.getStatus()) {
            if (reqVO.getStatus() == 1) {
                return ResponseVO.fail(ResultCode.TASK_ALREADY_ENABLE);
            } else {
                return ResponseVO.fail(ResultCode.TASK_ALREADY_NO_ENABLE);
            }
        }

        //
        // 首先查询
        SiteTaskConfigPO record = this.getBaseMapper().selectById(reqVO.getId());
        // 每日每周任务
        if (!StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())
                && !StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                && !StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
            if (record.getMinBetAmount() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_MIN_BET_NULL);
            }
            // 添加游戏与场馆类别校验
            /*if (record.getVenueType() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_VENUE_TYPE_NULL);
            }*/
            /*if (record.getVenueCode() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_VENUE_CODE_NULL);
            }*/

        }
        // 新人任务最小投注设置未null
        if (StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())
                || StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                || StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
            record.setMinBetAmount(null);
        }
        if (record.getWashRatio() == null && flag) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
        }
        if (record.getWashRatio() != null && flag) {
            if (record.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
            }
        }
        if (record.getRewardAmount() == null && flag) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
        }
        /*if (flag) {
            if (record.getMinBetAmount() != null
                    && !StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                    && !StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
                if (record.getMinBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
                }
            }
            if (record.getRewardAmount() != null
                    && StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())) {
                if (record.getRewardAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
                }
            }
        }*/

        //
        LambdaUpdateWrapper<SiteTaskConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteTaskConfigPO::getId, reqVO.getId());
        updateWrapper.eq(SiteTaskConfigPO::getSiteCode, reqVO.getSiteCode());
        updateWrapper.eq(SiteTaskConfigPO::getSubTaskType, taskConfigPO.getSubTaskType());
        updateWrapper.set(SiteTaskConfigPO::getStatus, reqVO.getStatus());
        updateWrapper.set(SiteTaskConfigPO::getUpdater, reqVO.getOperator());
        updateWrapper.set(SiteTaskConfigPO::getUpdatedTime, System.currentTimeMillis());
        siteTaskConfigRepository.update(null, updateWrapper);
        return ResponseVO.success(true);
    }

    public List<SiteTaskConfigSortRespVO> getTaskTabSort(String siteCode, String taskType) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, siteCode);
        queryWrapper.eq(SiteTaskConfigPO::getTaskType, taskType);
        //queryWrapper.eq(SiteTaskConfigPO::getLatest, CommonConstant.business_one);
        queryWrapper.orderByAsc(SiteTaskConfigPO::getSort);
        List<SiteTaskConfigPO> list = siteTaskConfigRepository.selectList(queryWrapper);
        try {
            return ConvertUtil.convertListToList(list, new SiteTaskConfigSortRespVO());
        } catch (Exception e) {
            log.error("ConvertUtil.convertListToList error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public Boolean taskTabSort(SiteTasKConfigSortReqVO reqVO) {
        List<SiteTaskConfigPO> activityBases = Lists.newArrayList();
        List<ActiveSortVO> list = reqVO.getSortVOS();
        if (CollectionUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                ActiveSortVO bo = list.get(i);
                SiteTaskConfigPO byId = siteTaskConfigRepository.selectById(bo.getId());
                byId.setSort(bo.getSort());
                byId.setUpdater(reqVO.getOperator());
                byId.setUpdatedTime(System.currentTimeMillis());
                activityBases.add(byId);
            }
            this.updateBatchById(activityBases);

        }
        return true;
    }

    public void init() {
        String taskNameI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_NAME.getCode());
        String taskPictureI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE.getCode());
        String taskPicturePcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE_PC.getCode());
        String taskDescI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_DESCRIPTION.getCode());
        List<String> langCodes = languageManagerApi.list().getData().stream().map(LanguageManagerListVO::getCode).collect(Collectors.toList());
        List<I18nMsgFrontVO> msgFrontVOS = new ArrayList<>();
        for (String lang : langCodes) {
            I18nMsgFrontVO i18nMsgFrontVO = new I18nMsgFrontVO();
            i18nMsgFrontVO.setLanguage(lang);
            i18nMsgFrontVO.setMessage("");
        }

        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(taskNameI18nCode, msgFrontVOS, taskPictureI18nCode, msgFrontVOS, taskPicturePcI18nCode, msgFrontVOS, taskDescI18nCode, msgFrontVOS);
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

    }

    public SiteTaskConfigPO checkParam(SiteTaskConfigReqVO vo) {
        // 首先查询
        SiteTaskConfigPO record = this.getBaseMapper().selectById(vo.getId());
        // 每日每周任务
        /*if (!StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())
                && !StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                && !StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
            if (vo.getMinBetAmount() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_MIN_BET_NULL);
            }
            // 添加游戏与场馆类别校验
            if (vo.getVenueType() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_VENUE_TYPE_NULL);
            }
            if (vo.getVenueCode() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_PARAM_VENUE_CODE_NULL);
            }

        }*/
        // 新人任务最小投注设置未null
        /*if (StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())
                || StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                || StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
            vo.setMinBetAmount(null);
        }*/
        if (StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())) {
            vo.setTaskConfigJson(null);

        } else {
            vo.setRewardAmount(null);
        }
        if (vo.getWashRatio() == null) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
        }
        if (vo.getWashRatio() != null) {
            if (vo.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
            }
        }

        /*if (vo.getMinBetAmount() != null) {
            if (vo.getMinBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
            }
            if (vo.getMinBetAmount().stripTrailingZeros().scale() > 0) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }*/
        /*if (StringUtils.equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType(), record.getSubTaskType())
                || StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {

            vo.setRewardAmount(null);
        }*/
        // 新人的判断
        if (StringUtils.equals(TaskEnum.NOVICE_WELCOME.getTaskType(), record.getTaskType())) {
            if (vo.getRewardAmount() == null) {
                throw new BaowangDefaultException(ResultCode.TASK_REWARD_AMOUNT_OVER_ZERO_ERROR);
            }
            if (vo.getRewardAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaowangDefaultException(ResultCode.TASK_REWARD_AMOUNT_OVER_ZERO_ERROR);
            }
        }
        //存款任务与邀请任务参数校验 只要不是新人任务，都需要校验 todo
       /* if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(record.getSubTaskType())
                || StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {*/
        if (!TaskEnum.NOVICE_WELCOME.getTaskType().equals(record.getTaskType())) {
            List<TaskSubConfigReqVO> taskConfigJson = vo.getTaskConfigJson();
            if (CollectionUtil.isEmpty(taskConfigJson)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            // 排序
            taskConfigJson.sort(Comparator.comparing(TaskSubConfigReqVO::getStep));

            // 校验 每日存款任务
            if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(record.getSubTaskType())) {
                BigDecimal depositConfig = BigDecimal.ZERO;
                for (TaskSubConfigReqVO configReqVO : taskConfigJson) {
                    if (TaskEnum.DAILY_DEPOSIT.getTaskType().equals(record.getTaskType())) {
                        if (Objects.isNull(configReqVO) || Objects.isNull(configReqVO.getDepositAmount()) || configReqVO.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }
                    if (Objects.isNull(configReqVO.getStep())) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    BigDecimal tempDeposit = configReqVO.getDepositAmount();
                    if (tempDeposit.compareTo(depositConfig) < 0) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    depositConfig = tempDeposit;

                }
            } else if (StringUtils.equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType(), record.getSubTaskType())) {
                // 每周邀请任务
                Integer friendCount = 0;
                for (TaskSubConfigReqVO configReqVO : taskConfigJson) {
                    if (TaskEnum.WEEK_INVITE_FRIENDS.getTaskType().equals(record.getTaskType())) {
                        if (Objects.isNull(configReqVO) || Objects.isNull(configReqVO.getInviteFriendCount()) || configReqVO.getInviteFriendCount() <= 0) {
                            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                        }
                    }
                    if (Objects.isNull(configReqVO.getStep())) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    int tempCount = configReqVO.getInviteFriendCount();
                    if (tempCount < friendCount) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    friendCount = tempCount;
                }
            } else {
                BigDecimal minBetAmount = BigDecimal.ZERO;
                // 其他校验
                for (TaskSubConfigReqVO configReqVO : taskConfigJson) {

                    if (Objects.isNull(configReqVO) || Objects.isNull(configReqVO.getMinBetAmount()) || Objects.isNull(configReqVO.getRewardAmount())) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    if (Objects.isNull(configReqVO.getStep())) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    if (Objects.isNull(configReqVO) || Objects.isNull(configReqVO.getMinBetAmount()) || configReqVO.getMinBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    if (Objects.isNull(configReqVO) || Objects.isNull(configReqVO.getRewardAmount()) || configReqVO.getRewardAmount().compareTo(BigDecimal.ZERO) < 0) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }

                    // 下一步骤配置要大于上一级
                    if (configReqVO.getMinBetAmount().compareTo(minBetAmount) <= 0) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    minBetAmount = configReqVO.getMinBetAmount();

                }
            }

        }
        return record;
    }

    @Transactional
    public void save(SiteTaskConfigReqVO vo) {
        // 校验
        SiteTaskConfigPO record = checkParam(vo);
        String taskNameI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_NAME.getCode());
        String taskPictureI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE.getCode());
        String taskPicturePcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_PICTURE_PC.getCode());
        String taskDescI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_DESCRIPTION.getCode());
        String taskDescriptionI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.TASK_DESC_DESCRIPTION.getCode());


        if (ObjectUtil.isEmpty(record)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        long startTimeEff = 0L;
        if (TaskEnum.DAILY_BET.getTaskType().equals(record.getTaskType())) {
            startTimeEff = TimeZoneUtils.lastDayTimestamp(-1, vo.getTimeZone());
        } else if (TaskEnum.WEEK_BET.getTaskType().equals(record.getTaskType())) {
            startTimeEff = TimeZoneUtils.getStartOfNextWeekInTimeZone(System.currentTimeMillis(), vo.getTimeZone());
        } else {
            startTimeEff = System.currentTimeMillis();
        }
        // 是否需要删除i18（新人任务或者init的数据）
        boolean updateFlag = true;
        // 查看是否有下一期生效，如果有，则修改下一期生效的
        SiteTaskConfigNextPO siteTaskConfigNextPO = taskConfigNextRepository.selectOne(new LambdaQueryWrapper<SiteTaskConfigNextPO>()
                .eq(SiteTaskConfigNextPO::getSiteCode, vo.getSiteCode())
                .eq(SiteTaskConfigNextPO::getSubTaskType, record.getSubTaskType()));

        // 是否新增一条：（就是看有没有生效，如果生效了，就需要新增一条，如果没有生效，就在原来的基础上修改）

        if (!TaskEnum.NOVICE_WELCOME.getTaskType().equals(record.getTaskType())) {
            updateFlag = false;
            // 默认没有初始化，需要直接更新
            boolean initFlag = false;
            // 还需要判断如果是默认的，就是当初始化后，则不需要更新生成，直接更新到init数据
            if (record.getSubTaskType().equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType())
                    || record.getSubTaskType().equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType())) {
                if (StringUtils.isBlank(record.getTaskConfigJson())) {
                    initFlag = true;
                    updateFlag = true;
                }

            } else {
                if (record.getWashRatio() == null || record.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                    initFlag = true;
                    updateFlag = true;
                }
            }

            if (initFlag) {
                // 初始化需要立即更新，不需要下期生效
                LambdaUpdateWrapper<SiteTaskConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SiteTaskConfigPO::getId, vo.getId());
                updateWrapper.eq(SiteTaskConfigPO::getSiteCode, vo.getSiteCode());
                updateWrapper.eq(SiteTaskConfigPO::getTaskType, record.getTaskType());
                updateWrapper.eq(SiteTaskConfigPO::getSubTaskType, record.getSubTaskType());
                updateWrapper.set(StringUtils.isNotBlank(taskNameI18nCode), SiteTaskConfigPO::getTaskNameI18nCode, taskNameI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskPictureI18nCode), SiteTaskConfigPO::getTaskPictureI18nCode, taskPictureI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskPicturePcI18nCode), SiteTaskConfigPO::getTaskPicturePcI18nCode, taskPicturePcI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskDescI18nCode), SiteTaskConfigPO::getTaskDescI18nCode, taskDescI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskDescriptionI18nCode), SiteTaskConfigPO::getTaskDescriptionI18nCode, taskDescriptionI18nCode);
                updateWrapper.set(vo.getRewardAmount() != null, SiteTaskConfigPO::getRewardAmount, vo.getRewardAmount());
                updateWrapper.set(vo.getWashRatio() != null, SiteTaskConfigPO::getWashRatio, vo.getWashRatio());
                //updateWrapper.set(ObjectUtil.isNotEmpty(vo.getVenueType()), SiteTaskConfigPO::getVenueType, vo.getVenueType());
                updateWrapper.set(ObjectUtil.isNotEmpty(vo.getMinBetAmount()), SiteTaskConfigPO::getMinBetAmount, vo.getMinBetAmount());
                updateWrapper.set(ObjectUtil.isNotEmpty(vo.getVenueCode()), SiteTaskConfigPO::getVenueCode, DataUtils.listToString(vo.getVenueCode()));
                updateWrapper.set(SiteTaskConfigPO::getUpdater, vo.getOperator());
                updateWrapper.set(SiteTaskConfigPO::getUpdatedTime, System.currentTimeMillis());
                updateWrapper.set(SiteTaskConfigPO::getCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE);
                updateWrapper.set(SiteTaskConfigPO::getTaskConfigJson, JSONObject.toJSONString(vo.getTaskConfigJson()));
                siteTaskConfigRepository.update(null, updateWrapper);
            } else {
                // 后台修改任务图标、名称，前端要立即生效
                LambdaUpdateWrapper<SiteTaskConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SiteTaskConfigPO::getId, vo.getId());
                updateWrapper.eq(SiteTaskConfigPO::getSiteCode, vo.getSiteCode());
                updateWrapper.eq(SiteTaskConfigPO::getTaskType, record.getTaskType());
                updateWrapper.eq(SiteTaskConfigPO::getSubTaskType, record.getSubTaskType());
                updateWrapper.set(StringUtils.isNotBlank(taskNameI18nCode), SiteTaskConfigPO::getTaskNameI18nCode, taskNameI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskPictureI18nCode), SiteTaskConfigPO::getTaskPictureI18nCode, taskPictureI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskPicturePcI18nCode), SiteTaskConfigPO::getTaskPicturePcI18nCode, taskPicturePcI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskDescI18nCode), SiteTaskConfigPO::getTaskDescI18nCode, taskDescI18nCode);
                updateWrapper.set(StringUtils.isNotBlank(taskDescriptionI18nCode), SiteTaskConfigPO::getTaskDescriptionI18nCode, taskDescriptionI18nCode);
                updateWrapper.set(SiteTaskConfigPO::getUpdater, vo.getOperator());
                updateWrapper.set(SiteTaskConfigPO::getUpdatedTime, System.currentTimeMillis());
                updateWrapper.set(SiteTaskConfigPO::getCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE);
                //updateWrapper.set(SiteTaskConfigPO::getTaskConfigJson, JSONObject.toJSONString(vo.getTaskConfigJson()));
                siteTaskConfigRepository.update(null, updateWrapper);
                // 当初始化的时候，则第一次更新，直接在更新
                if (siteTaskConfigNextPO == null) {
                    // 是否生效, 如果没有生效，则直接更新
                    // 生效，则生成一个
                    SiteTaskConfigNextPO siteTaskConfigNextInSert = new SiteTaskConfigNextPO();
                    siteTaskConfigNextInSert.setTaskType(record.getTaskType());
                    siteTaskConfigNextInSert.setSubTaskType(record.getSubTaskType());
                    siteTaskConfigNextInSert.setCreatedTime(System.currentTimeMillis());
                    siteTaskConfigNextInSert.setCreator(vo.getOperator());
                    siteTaskConfigNextInSert.setUpdatedTime(System.currentTimeMillis());
                    siteTaskConfigNextInSert.setUpdater(vo.getOperator());
                    siteTaskConfigNextInSert.setSiteCode(vo.getSiteCode());
                    siteTaskConfigNextInSert.setStatus(record.getStatus());
                    siteTaskConfigNextInSert.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                    siteTaskConfigNextInSert.setTaskNameI18nCode(taskNameI18nCode);
                    siteTaskConfigNextInSert.setTaskPictureI18nCode(taskPictureI18nCode);
                    siteTaskConfigNextInSert.setTaskPicturePcI18nCode(taskPicturePcI18nCode);
                    siteTaskConfigNextInSert.setTaskDescI18nCode(taskDescI18nCode);
                    siteTaskConfigNextInSert.setTaskDescriptionI18nCode(taskDescriptionI18nCode);
                    siteTaskConfigNextInSert.setSort(record.getSort());
                    siteTaskConfigNextInSert.setEffectiveStartTime(startTimeEff);
                    siteTaskConfigNextInSert.setMinBetAmount(vo.getMinBetAmount());
                    siteTaskConfigNextInSert.setRewardAmount(vo.getRewardAmount());
                    siteTaskConfigNextInSert.setWashRatio(vo.getWashRatio());
                    siteTaskConfigNextInSert.setVenueType(vo.getVenueType());
                    siteTaskConfigNextInSert.setVenueCode(DataUtils.listToString(vo.getVenueCode()));
                    siteTaskConfigNextInSert.setTaskConfigJson(JSONObject.toJSONString(vo.getTaskConfigJson()));
                    taskConfigNextRepository.insert(siteTaskConfigNextInSert);

                } else {
                    LambdaUpdateWrapper<SiteTaskConfigNextPO> updateWrapperNext = new LambdaUpdateWrapper();
                    updateWrapperNext.eq(SiteTaskConfigNextPO::getId, siteTaskConfigNextPO.getId());
                    updateWrapperNext.set(StringUtils.isNotBlank(taskNameI18nCode), SiteTaskConfigNextPO::getTaskNameI18nCode, taskNameI18nCode);
                    updateWrapperNext.set(StringUtils.isNotBlank(taskPictureI18nCode), SiteTaskConfigNextPO::getTaskPictureI18nCode, taskPictureI18nCode);
                    updateWrapperNext.set(StringUtils.isNotBlank(taskPicturePcI18nCode), SiteTaskConfigNextPO::getTaskPicturePcI18nCode, taskPicturePcI18nCode);
                    updateWrapperNext.set(StringUtils.isNotBlank(taskDescI18nCode), SiteTaskConfigNextPO::getTaskDescI18nCode, taskDescI18nCode);
                    updateWrapperNext.set(StringUtils.isNotBlank(taskDescriptionI18nCode), SiteTaskConfigNextPO::getTaskDescriptionI18nCode, taskDescriptionI18nCode);
                    updateWrapperNext.set(vo.getRewardAmount() != null, SiteTaskConfigNextPO::getRewardAmount, vo.getRewardAmount());
                    updateWrapperNext.set(vo.getWashRatio() != null, SiteTaskConfigNextPO::getWashRatio, vo.getWashRatio());
                    updateWrapperNext.set(ObjectUtil.isNotEmpty(vo.getVenueType()), SiteTaskConfigNextPO::getVenueType, vo.getVenueType());
                    updateWrapperNext.set(ObjectUtil.isNotEmpty(vo.getMinBetAmount()), SiteTaskConfigNextPO::getMinBetAmount, vo.getMinBetAmount());
                    updateWrapperNext.set(ObjectUtil.isNotEmpty(vo.getVenueCode()), SiteTaskConfigNextPO::getVenueCode, DataUtils.listToString(vo.getVenueCode()));
                    updateWrapperNext.set(SiteTaskConfigNextPO::getUpdater, vo.getOperator());
                    updateWrapperNext.set(SiteTaskConfigNextPO::getUpdatedTime, System.currentTimeMillis());
                    updateWrapperNext.set(SiteTaskConfigNextPO::getCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE);
                    updateWrapperNext.set(SiteTaskConfigNextPO::getEffectiveStartTime, startTimeEff);
                    updateWrapperNext.set(SiteTaskConfigNextPO::getTaskConfigJson, JSONObject.toJSONString(vo.getTaskConfigJson()));
                    taskConfigNextRepository.update(null, updateWrapperNext);
                }
            }

        } else {
            // 新人任务直接更新
            LambdaUpdateWrapper<SiteTaskConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteTaskConfigPO::getId, vo.getId());
            updateWrapper.eq(SiteTaskConfigPO::getSiteCode, vo.getSiteCode());
            updateWrapper.eq(SiteTaskConfigPO::getTaskType, record.getTaskType());
            updateWrapper.eq(SiteTaskConfigPO::getSubTaskType, record.getSubTaskType());
            updateWrapper.set(StringUtils.isNotBlank(taskNameI18nCode), SiteTaskConfigPO::getTaskNameI18nCode, taskNameI18nCode);
            updateWrapper.set(StringUtils.isNotBlank(taskPictureI18nCode), SiteTaskConfigPO::getTaskPictureI18nCode, taskPictureI18nCode);
            updateWrapper.set(StringUtils.isNotBlank(taskPicturePcI18nCode), SiteTaskConfigPO::getTaskPicturePcI18nCode, taskPicturePcI18nCode);
            updateWrapper.set(StringUtils.isNotBlank(taskDescI18nCode), SiteTaskConfigPO::getTaskDescI18nCode, taskDescI18nCode);
            updateWrapper.set(StringUtils.isNotBlank(taskDescriptionI18nCode), SiteTaskConfigPO::getTaskDescriptionI18nCode, taskDescriptionI18nCode);
            updateWrapper.set(vo.getRewardAmount() != null, SiteTaskConfigPO::getRewardAmount, vo.getRewardAmount());
            updateWrapper.set(vo.getWashRatio() != null, SiteTaskConfigPO::getWashRatio, vo.getWashRatio());
            //updateWrapper.set(ObjectUtil.isNotEmpty(vo.getVenueType()), SiteTaskConfigPO::getVenueType, vo.getVenueType());
            updateWrapper.set(ObjectUtil.isNotEmpty(vo.getMinBetAmount()), SiteTaskConfigPO::getMinBetAmount, vo.getMinBetAmount());
            updateWrapper.set(ObjectUtil.isNotEmpty(vo.getVenueCode()), SiteTaskConfigPO::getVenueCode, DataUtils.listToString(vo.getVenueCode()));
            updateWrapper.set(SiteTaskConfigPO::getUpdater, vo.getOperator());
            updateWrapper.set(SiteTaskConfigPO::getUpdatedTime, System.currentTimeMillis());
            updateWrapper.set(SiteTaskConfigPO::getCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE);
            siteTaskConfigRepository.update(null, updateWrapper);
        }
        // 更改原来的
        // 修改当期的。
        // 更新历史数据，修改任务名称
        // 插入国际化信息
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(taskNameI18nCode, vo.getTaskNameI18nCodeList(),
                taskPictureI18nCode, vo.getTaskPictureI18nCodeList(),
                taskPicturePcI18nCode, vo.getTaskPicturePcI18nCodeList(),
                taskDescI18nCode, vo.getTaskDescI18nCodeList(),
                taskDescriptionI18nCode, vo.getTaskDescriptionI18nCodeList());
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        //删除,如果是新人任务，则因为实时生效，则修改 都需要删除
        List<String> i18nDelList = processI18nCodes(record);
        if (CollectionUtil.isNotEmpty(i18nDelList)) {
            i18nApi.deleteBatchByMsgKey(i18nDelList);
        }


    }

    // 判断字符串是否为空并将非空的添加到列表中
    private void addI18nCodeIfNotBlank(String code, List<String> i18nDels) {
        if (StringUtils.isNotBlank(code)) {
            i18nDels.add(code);
        }
    }

    /**
     * 删除的i18key
     */
    public List<String> processI18nCodes(SiteTaskConfigPO record) {
        List<String> i18nDelList = new ArrayList<>();

        // 将各个多语言代码添加到列表中
        addI18nCodeIfNotBlank(record.getTaskNameI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getTaskPictureI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getTaskPicturePcI18nCode(), i18nDelList);
        addI18nCodeIfNotBlank(record.getTaskDescI18nCode(), i18nDelList);

        return i18nDelList;
    }

    /**
     * 检查给定站点是否存在启用状态的任务配置。
     *
     * @param siteCode 站点代码，用于筛选任务配置
     * @return 如果未找到任何任务配置，则返回 true；否则返回 false
     */
    public Boolean hasTaskConfig(String siteCode) {
        // 查询任务配置，筛选条件为站点代码和启用状态
        List<SiteTaskConfigPO> taskConfigPOS = siteTaskConfigRepository.selectList(new LambdaQueryWrapper<SiteTaskConfigPO>().eq(SiteTaskConfigPO::getSiteCode, siteCode).eq(SiteTaskConfigPO::getStatus, EnableStatusEnum.ENABLE));
        // 如果任务配置列表为空，返回 true；否则返回 false
        return CollectionUtil.isEmpty(taskConfigPOS);
    }

    public List<ReportSiteTaskConfigResVO> taskAllList(TaskConfigReqVO taskConfigReqVO) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SiteTaskConfigPO::getId, SiteTaskConfigPO::getTaskNameI18nCode);
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, taskConfigReqVO.getSiteCode());
        List<SiteTaskConfigPO> taskConfigPOS = this.baseMapper.selectList(queryWrapper);
        /*for (SiteTaskConfigPO siteTaskConfigPO : taskConfigPOS) {
            if (siteTaskConfigPO.getSubTaskType().equals(TaskEnum.DAILY_DEPOSIT.getSubTaskType())
                    || siteTaskConfigPO.getSubTaskType().equals(TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType())) {
                String json = siteTaskConfigPO.getTaskConfigJson();
                if (StringUtils.isNotBlank(json)) {
                    List<TaskSubConfigReqVO> list = JSON.parseArray(json, TaskSubConfigReqVO.class);
                    BigDecimal total = list.stream().map(TaskSubConfigReqVO::getRewardAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                    siteTaskConfigPO.setRewardAmount(total);
                }
            }
        }*/
        return ConvertUtil.entityListToModelList(taskConfigPOS, ReportSiteTaskConfigResVO.class);
    }

    public List<String> taskAllListByTaskName(ReportTaskConfigReqVO vo) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SiteTaskConfigPO::getId);
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, vo.getSiteCode());
        queryWrapper.in(SiteTaskConfigPO::getTaskNameI18nCode, vo.getTaskNameI18nCodes());
        List<SiteTaskConfigPO> taskConfigPOS = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(taskConfigPOS)) {
            return new ArrayList<>();
        }
        return taskConfigPOS.stream().map(SiteTaskConfigPO::getId).collect(Collectors.toList());
    }

    public SiteTaskConfigResVO taskDetail(TaskConfigDetailReqVO reqVO) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getId, reqVO.getId());
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, reqVO.getSiteCode());
        SiteTaskConfigPO taskConfigPO = this.baseMapper.selectOne(queryWrapper);
        if (taskConfigPO == null) {
            return new SiteTaskConfigResVO();
        }
        LambdaQueryWrapper<SiteTaskConfigNextPO> queryWrapperNext = new LambdaQueryWrapper<>();
        queryWrapperNext.eq(SiteTaskConfigNextPO::getSiteCode, reqVO.getSiteCode());
        queryWrapperNext.eq(SiteTaskConfigNextPO::getSubTaskType, taskConfigPO.getSubTaskType());
        SiteTaskConfigNextPO siteTaskConfigNextPO = taskConfigNextRepository.selectOne(queryWrapperNext);
        if (siteTaskConfigNextPO != null) {
            taskConfigPO.setTaskNameI18nCode(siteTaskConfigNextPO.getTaskNameI18nCode());
            taskConfigPO.setTaskDescriptionI18nCode(siteTaskConfigNextPO.getTaskDescriptionI18nCode());
            taskConfigPO.setTaskPictureI18nCode(siteTaskConfigNextPO.getTaskPictureI18nCode());
            taskConfigPO.setTaskDescI18nCode(siteTaskConfigNextPO.getTaskDescI18nCode());
            taskConfigPO.setTaskPicturePcI18nCode(siteTaskConfigNextPO.getTaskPicturePcI18nCode());

            taskConfigPO.setUpdater(siteTaskConfigNextPO.getUpdater());
            taskConfigPO.setUpdatedTime(siteTaskConfigNextPO.getUpdatedTime());

            taskConfigPO.setMinBetAmount(siteTaskConfigNextPO.getMinBetAmount());
            taskConfigPO.setRewardAmount(siteTaskConfigNextPO.getRewardAmount());
            taskConfigPO.setWashRatio(siteTaskConfigNextPO.getWashRatio());
            //taskConfigPO.setVenueType(siteTaskConfigNextPO.getVenueType());
            taskConfigPO.setVenueCode(siteTaskConfigNextPO.getVenueCode());
            taskConfigPO.setTaskConfigJson(siteTaskConfigNextPO.getTaskConfigJson());
        }

        return ConvertUtil.entityToModel(taskConfigPO, SiteTaskConfigResVO.class);
    }

    /**
     * 下一期生效定时任务
     */
    public void updateEffective() {
        LambdaQueryWrapper<SiteTaskConfigNextPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(SiteTaskConfigNextPO::getEffectiveStartTime, System.currentTimeMillis());
        // 更新下一期
        List<SiteTaskConfigNextPO> siteTaskConfigNextPOS = taskConfigNextRepository.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(siteTaskConfigNextPOS)) {
            for (SiteTaskConfigNextPO record : siteTaskConfigNextPOS) {
                // 查找
                LambdaQueryWrapper<SiteTaskConfigPO> queryWrapperOne = new LambdaQueryWrapper<>();
                queryWrapperOne.eq(SiteTaskConfigPO::getSiteCode, record.getSiteCode());
                queryWrapperOne.eq(SiteTaskConfigPO::getSubTaskType, record.getSubTaskType());
                SiteTaskConfigPO taskConfigPO = this.baseMapper.selectOne(queryWrapperOne);
                log.info("定时任务更新任务配置数据：siteCode:{},任务类型:{}", record.getSiteCode(), record.getSubTaskType());
                if (taskConfigPO == null) {
                    log.error("定时任务更新任务配置状态查询不到数据：siteCode:{},任务类型:{}", record.getSiteCode(), record.getSubTaskType());
                    continue;
                }
                // 更新表，删除下一次
                LambdaUpdateWrapper<SiteTaskConfigPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SiteTaskConfigPO::getId, taskConfigPO.getId());
                updateWrapper.eq(SiteTaskConfigPO::getSiteCode, record.getSiteCode());
                updateWrapper.eq(SiteTaskConfigPO::getTaskType, record.getTaskType());
                updateWrapper.eq(SiteTaskConfigPO::getSubTaskType, record.getSubTaskType());
                updateWrapper.set(SiteTaskConfigPO::getTaskNameI18nCode, record.getTaskNameI18nCode());
                updateWrapper.set(SiteTaskConfigPO::getTaskPictureI18nCode, record.getTaskPictureI18nCode());
                updateWrapper.set(SiteTaskConfigPO::getTaskPicturePcI18nCode, record.getTaskPicturePcI18nCode());
                updateWrapper.set(SiteTaskConfigPO::getTaskDescI18nCode, record.getTaskDescI18nCode());
                updateWrapper.set(SiteTaskConfigPO::getTaskDescriptionI18nCode, record.getTaskDescriptionI18nCode());
                updateWrapper.set(SiteTaskConfigPO::getRewardAmount, record.getRewardAmount());
                updateWrapper.set(SiteTaskConfigPO::getWashRatio, record.getWashRatio());
                //updateWrapper.set(SiteTaskConfigPO::getVenueType, record.getVenueType());
                updateWrapper.set(SiteTaskConfigPO::getMinBetAmount, record.getMinBetAmount());
                updateWrapper.set(SiteTaskConfigPO::getVenueCode, record.getVenueCode());
                updateWrapper.set(SiteTaskConfigPO::getUpdater, record.getUpdater());
                updateWrapper.set(SiteTaskConfigPO::getUpdatedTime, record.getUpdatedTime());
                updateWrapper.set(SiteTaskConfigPO::getCurrencyCode, CommonConstant.PLAT_CURRENCY_CODE);
                updateWrapper.set(SiteTaskConfigPO::getTaskConfigJson, record.getTaskConfigJson());
                siteTaskConfigRepository.update(null, updateWrapper);
                // 删除
                taskConfigNextRepository.deleteById(record.getId());
                // 删除之前的配置的i8N 只有已经更新了图标，名称等国际化，这些不需要再次清理
                /*List<String> i18nDelList = processI18nCodes(taskConfigPO);
                if (CollectionUtil.isNotEmpty(i18nDelList)) {
                    i18nApi.deleteBatchByMsgKey(i18nDelList);
                }*/
            }
        }


    }

    public SiteTaskOverViewConfigResVO taskOverViewConfig(TaskConfigOverViewReqVO taskConfigReqVO) {
        LambdaQueryWrapper<SiteTaskOverViewConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskOverViewConfigPO::getSiteCode, taskConfigReqVO.getSiteCode());
        SiteTaskOverViewConfigPO siteTaskOverViewConfigPO = overViewConfigRepository.selectOne(queryWrapper);
        if (siteTaskOverViewConfigPO == null) {
            return SiteTaskOverViewConfigResVO.builder().expandStatus(1).build();
        } else {
            return ConvertUtil.entityToModel(siteTaskOverViewConfigPO, SiteTaskOverViewConfigResVO.class);
        }
    }

    public boolean updateTaskOverViewConfig(TaskConfigOverViewReqVO taskConfigReqVO) {
        LambdaQueryWrapper<SiteTaskOverViewConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskOverViewConfigPO::getSiteCode, taskConfigReqVO.getSiteCode());
        SiteTaskOverViewConfigPO siteTaskOverViewConfigPO = overViewConfigRepository.selectOne(queryWrapper);
        if (siteTaskOverViewConfigPO == null) {
            siteTaskOverViewConfigPO = new SiteTaskOverViewConfigPO();
            siteTaskOverViewConfigPO.setSiteCode(taskConfigReqVO.getSiteCode());
            siteTaskOverViewConfigPO.setExpandStatus(taskConfigReqVO.getExpandStatus());
            siteTaskOverViewConfigPO.setUpdater(taskConfigReqVO.getOperator());
            siteTaskOverViewConfigPO.setUpdatedTime(System.currentTimeMillis());
            siteTaskOverViewConfigPO.setCreatedTime(System.currentTimeMillis());
            siteTaskOverViewConfigPO.setCreator(taskConfigReqVO.getOperator());
            overViewConfigRepository.insert(siteTaskOverViewConfigPO);
        } else {
            siteTaskOverViewConfigPO.setExpandStatus(taskConfigReqVO.getExpandStatus());
            siteTaskOverViewConfigPO.setUpdater(taskConfigReqVO.getOperator());
            siteTaskOverViewConfigPO.setUpdatedTime(System.currentTimeMillis());
            overViewConfigRepository.updateById(siteTaskOverViewConfigPO);
        }
        return true;
    }

    /**
     *
     *
     *
     *
     */
    public boolean validityPeriod(TaskConfigOverViewReqVO taskConfigReqVO) {
        LambdaQueryWrapper<SiteTaskOverViewConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskOverViewConfigPO::getSiteCode, taskConfigReqVO.getSiteCode());
        SiteTaskOverViewConfigPO siteTaskOverViewConfigPO = overViewConfigRepository.selectOne(queryWrapper);
        if (siteTaskOverViewConfigPO == null) {
            siteTaskOverViewConfigPO = new SiteTaskOverViewConfigPO();
            siteTaskOverViewConfigPO.setSiteCode(taskConfigReqVO.getSiteCode());
            siteTaskOverViewConfigPO.setExpandStatus(taskConfigReqVO.getExpandStatus());
            siteTaskOverViewConfigPO.setUpdater(taskConfigReqVO.getOperator());
            siteTaskOverViewConfigPO.setUpdatedTime(System.currentTimeMillis());
            siteTaskOverViewConfigPO.setCreatedTime(System.currentTimeMillis());
            siteTaskOverViewConfigPO.setCreator(taskConfigReqVO.getOperator());
            overViewConfigRepository.insert(siteTaskOverViewConfigPO);
        } else {
            siteTaskOverViewConfigPO.setExpandStatus(taskConfigReqVO.getExpandStatus());
            siteTaskOverViewConfigPO.setUpdater(taskConfigReqVO.getOperator());
            siteTaskOverViewConfigPO.setUpdatedTime(System.currentTimeMillis());
            overViewConfigRepository.updateById(siteTaskOverViewConfigPO);
        }
        return true;
    }


}
