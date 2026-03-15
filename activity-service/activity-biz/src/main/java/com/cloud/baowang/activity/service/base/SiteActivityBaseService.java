package com.cloud.baowang.activity.service.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityEligibilityEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.ActivityUserTypeEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortVO;
import com.cloud.baowang.activity.api.vo.category.AddActivityLabelSortVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2FloatIconVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityBaseV2VO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteCheckInRecordPO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.service.ActivityJobComponent;
import com.cloud.baowang.activity.service.SiteActivityCheckInRecordService;
import com.cloud.baowang.activity.service.SiteActivityLabsService;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.DeleteStateEnum;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @className: SiteActivityBaseService
 * @author: wade
 * @description: 活动基础服务
 * @date: 7/9/24 15:46
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityBaseService extends ServiceImpl<SiteActivityBaseRepository, SiteActivityBasePO> {

    private final I18nApi i18nApi;

    private final SiteActivityBaseRepository siteActivityBaseRepository;

    private final ActivityJobComponent activityJobComponent;

    private final SiteActivityLabsService siteActivityLabsService;

    private final SiteActivityCheckInRecordService checkInRecordService;
    /**
     * 活动列表
     */

    /**
     * 根据模板查询出已开启的活动配置
     */
    public SiteActivityBasePO getSiteActivityBasePO(String siteCode, String activityTemplate) {
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, RedisConstants.ACTIVITY_TEMPLATE);
        SiteActivityBasePO siteActivityBasePO = RedisUtil.getValue(key);
        if (ObjectUtil.isNotEmpty(siteActivityBasePO)) {
            return siteActivityBasePO;
        }

        siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteActivityBasePO::getSiteCode, siteCode));

        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }

        return siteActivityBasePO;
    }


    /**
     * 根据模板查询出已开启的活动配置
     */
    public List<SiteActivityBasePO> selectAllValid(String template) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBasePO.class);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据模板与siteCode查询出已开启的活动配置
     */
    public SiteActivityBasePO selectBySiteAndTem(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBasePO.class);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getDeleteFlag, 1);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 按照站点、活动模板获取活动列表
     *
     * @param template 活动模板
     * @param siteCode 站点编号
     * @return 活动列表
     */
    public List<SiteActivityBasePO> selectBySiteAndTemplate(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBasePO.class);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 按照站点、活动模板获取有效活动列表
     *
     * @param template 活动模板
     * @param siteCode 站点编号
     * @return 活动列表
     */
    public List<SiteActivityBasePO> selectValidBySiteAndTemplate(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBasePO.class);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectList(lambdaQueryWrapper);
    }


    private SiteActivityBasePO convertSiteActivityBasePO(ActivityBaseVO activityBaseVO) {

        List<Integer> activityEligibility = Lists.newArrayList();


        //每日竞赛不需要这个字段
        if (!ActivityTemplateEnum.DAILY_COMPETITION.getType().equals(activityBaseVO.getActivityTemplate())) {
            activityEligibility = activityBaseVO.getActivityEligibility();
            /*if (CollectionUtil.isEmpty(activityEligibility)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }*/
        }

        SiteActivityBasePO siteActivityBasePO = ConvertUtil.entityToModel(activityBaseVO, SiteActivityBasePO.class);
        siteActivityBasePO.setStatus(
                Objects.equals(activityBaseVO.getStatus(), EnableStatusEnum.ENABLE.getCode()) ?
                        EnableStatusEnum.ENABLE.getCode() :
                        EnableStatusEnum.DISABLE.getCode()
        );

        if (CollectionUtil.isNotEmpty(activityEligibility)) {
            Integer switchPhone = activityEligibility.contains(ActivityEligibilityEnum.PHONE.getValue()) ? EnableStatusEnum.ENABLE.getCode() : EnableStatusEnum.DISABLE.getCode();
            Integer switchEmail = activityEligibility.contains(ActivityEligibilityEnum.EMAIL.getValue()) ? EnableStatusEnum.ENABLE.getCode() : EnableStatusEnum.DISABLE.getCode();
            Integer switchIp = activityEligibility.contains(ActivityEligibilityEnum.IP.getValue()) ? EnableStatusEnum.ENABLE.getCode() : EnableStatusEnum.DISABLE.getCode();
            siteActivityBasePO.setSwitchPhone(switchPhone);
            siteActivityBasePO.setSwitchEmail(switchEmail);
            siteActivityBasePO.setSwitchIp(switchIp);
        } else {
            siteActivityBasePO.setSwitchPhone(EnableStatusEnum.DISABLE.getCode());
            siteActivityBasePO.setSwitchEmail(EnableStatusEnum.DISABLE.getCode());
            siteActivityBasePO.setSwitchIp(EnableStatusEnum.DISABLE.getCode());
        }

        String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_NAME.getCode());
        siteActivityBasePO.setActivityNameI18nCode(activityNameI18);
        if (CollectionUtil.isNotEmpty(activityBaseVO.getEntrancePictureI18nCodeList())) {
            String activityEntrancePicI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE.getCode());
            siteActivityBasePO.setEntrancePictureI18nCode(activityEntrancePicI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getEntrancePicturePcI18nCodeList())) {
            String activityEntrancePicPcI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE_PC.getCode());
            siteActivityBasePO.setEntrancePicturePcI18nCode(activityEntrancePicPcI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getHeadPictureI18nCodeList())) {
            String activityHeadPicI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_HEAD_PICTURE.getCode());
            siteActivityBasePO.setHeadPictureI18nCode(activityHeadPicI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getHeadPicturePcI18nCodeList())) {

            String activityHeadPicPcI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_HEAD_PICTURE_PC.getCode());
            siteActivityBasePO.setHeadPicturePcI18nCode(activityHeadPicPcI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getActivityRuleI18nCodeList())) {
            String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
            siteActivityBasePO.setActivityRuleI18nCode(activityRuleI18);
        }

        if (CollectionUtil.isNotEmpty(activityBaseVO.getActivityDescI18nCodeList())) {
            String activityDescI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_DESC.getCode());
            siteActivityBasePO.setActivityDescI18nCode(activityDescI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getActivityIntroduceI18nCodeList())) {
            // 活动简介
            String activityIntroduceI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_INTRODUCE.getCode());
            siteActivityBasePO.setActivityIntroduceI18nCode(activityIntroduceI18nCode);
        }

        siteActivityBasePO.setSiteCode(CurrReqUtils.getSiteCode());
        siteActivityBasePO.setActivityTemplate(activityBaseVO.getActivityTemplate());
        if (StringUtils.isBlank(siteActivityBasePO.getId())) {
            siteActivityBasePO.setCreatedTime(System.currentTimeMillis());
            siteActivityBasePO.setCreator(activityBaseVO.getOperator());
        }
        siteActivityBasePO.setUpdatedTime(System.currentTimeMillis());
        siteActivityBasePO.setUpdater(activityBaseVO.getOperator());
        // 如果是长期，结束时间为空
        if (activityBaseVO.getActivityDeadline() == ActivityDeadLineEnum.LONG_TERM.getType()) {
            siteActivityBasePO.setActivityEndTime(null);
        }
        if (ObjectUtil.isNotEmpty(activityBaseVO.getFloatIconAppI18nCodeList())) {
            String floatIconAppI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_FLOAT_ICON_APP.getCode());
            siteActivityBasePO.setFloatIconAppI18nCode(floatIconAppI18nCode);
        }
        if (ObjectUtil.isNotEmpty(activityBaseVO.getFloatIconPcI18nCodeList())) {
            String floatIconPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_FLOAT_ICON_APP.getCode());
            siteActivityBasePO.setFloatIconPcI18nCode(floatIconPcI18nCode);
        }
        return siteActivityBasePO;
    }


    public SiteActivityBasePO updateActivityBase(ActivityBaseVO activityBaseVO) {

        SiteActivityBasePO siteActivityBasePO = convertSiteActivityBasePO(activityBaseVO);
        if (baseMapper.updateById(siteActivityBasePO) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // 如果是长期，结束时间为空
        if (activityBaseVO.getActivityDeadline() == ActivityDeadLineEnum.LONG_TERM.getType()) {
            LambdaUpdateWrapper<SiteActivityBasePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SiteActivityBasePO::getActivityEndTime, null);
            updateWrapper.eq(SiteActivityBasePO::getId, activityBaseVO.getId());
            if (baseMapper.update(null, updateWrapper) <= 0) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        // 插入国际化信息
        /*Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                siteActivityBasePO.getActivityNameI18nCode(), activityBaseVO.getActivityNameI18nCodeList(),
                siteActivityBasePO.getEntrancePictureI18nCode(), activityBaseVO.getEntrancePictureI18nCodeList(),
                siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList(),
                siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList(),
                siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList(),
                siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList(),
                siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList(),
                siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList());*/
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();

        putIfNotNull(i18nData, siteActivityBasePO.getActivityNameI18nCode(), activityBaseVO.getActivityNameI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePictureI18nCode(), activityBaseVO.getEntrancePictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList());

        ResponseVO<Boolean> i18Bool = i18nApi.update(i18nData);

        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return siteActivityBasePO;

    }

    /**
     * 此处插入活动的基础信息方法
     *
     * @param activityBaseVO 参数
     * @return true=成功,false = 失败
     */
    public SiteActivityBasePO saveActivityBase(ActivityBaseVO activityBaseVO) {
        SiteActivityBasePO siteActivityBasePO = convertSiteActivityBasePO(activityBaseVO);
        siteActivityBasePO.setActivityNo(OrderUtil.getOrderNoNum("AT", 8));
        // 如果是长期的，结束时间
        if (baseMapper.insert(siteActivityBasePO) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        // 插入国际化信息
        /*Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(siteActivityBasePO.getActivityNameI18nCode(),
                activityBaseVO.getActivityNameI18nCodeList(),
                siteActivityBasePO.getEntrancePictureI18nCode(), activityBaseVO.getEntrancePictureI18nCodeList(),
                siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList(),
                siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList(),
                siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList(),
                siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList(),
                siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList(),
                siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList()
        );*/
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();

        putIfNotNull(i18nData, siteActivityBasePO.getActivityNameI18nCode(), activityBaseVO.getActivityNameI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePictureI18nCode(), activityBaseVO.getEntrancePictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList());

        putIfNotNull(i18nData, siteActivityBasePO.getFloatIconAppI18nCode(), activityBaseVO.getFloatIconAppI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getFloatIconPcI18nCode(), activityBaseVO.getFloatIconPcI18nCodeList());

        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);

        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return siteActivityBasePO;
    }

    private void putIfNotNull(Map<String, List<I18nMsgFrontVO>> map, String key, List<I18nMsgFrontVO> value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }

    public Page<ActivityBaseRespVO> siteActivityPageList(ActivityBaseReqVO requestVO) {
        Page<SiteActivityBasePO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        // 首先根据name在国际化查询
        if (ObjectUtil.isNotEmpty(requestVO.getActivityName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder().searchContent(requestVO.getActivityName()).bizKeyPrefix(I18MsgKeyEnum.ACTIVITY_NAME.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();
            if (CollectionUtil.isEmpty(activityNameList)) {
                return new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            } else {
                requestVO.setActivityNameCodeList(activityNameList);
            }
        }
        if (StringUtils.isNotBlank(requestVO.getUpdater())) {
            requestVO.setOperator(requestVO.getUpdater());
        }
        LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = SiteActivityBasePO.getQueryWrapper(requestVO);
        Page<SiteActivityBasePO> activityInfoPage = siteActivityBaseRepository.selectPage(respVOPage, queryWrapper);
        // 获取所有分类
        List<CodeValueVO> labelNames = siteActivityLabsService.getLabNameList(requestVO.getSiteCode());
        Map<String, String> labelNameMap = labelNames.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue, (v1, v2) -> v1));
        Page<ActivityBaseRespVO> resultPage = new Page<>();
        BeanUtils.copyProperties(activityInfoPage, resultPage);
        if (!CollectionUtils.isEmpty(activityInfoPage.getRecords())) {

            resultPage.setRecords(ConvertUtil.entityListToModelList(activityInfoPage.getRecords(), ActivityBaseRespVO.class));
            for (ActivityBaseRespVO record : resultPage.getRecords()) {
                record.setLabelName(labelNameMap.get(record.getLabelId()));

            }

        }
        return resultPage;
    }

    /**
     * B端
     * @param requestVO
     * @return
     */
    public List<ActivityBaseV2FloatIconVO> floatIconSortListToSite(ActivityBaseReqVO requestVO) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBasePO::getSiteCode, requestVO.getSiteCode());
        // 未开启的也要查出来
        // lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBasePO::getFloatIconShowFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.orderByDesc(SiteActivityBasePO::getFloatIconSort);
        List<SiteActivityBasePO> recordList = siteActivityBaseRepository.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(recordList)) {
            return Lists.newArrayList();
        }
        return ConvertUtil.entityListToModelList(recordList, ActivityBaseV2FloatIconVO.class);
    }

    /**
     * B端
     * @param requestVOList
     * @param floatIconShowNumber
     * @return
     */
    public ResponseVO<Boolean> floatIconSortListSave(List<ActivityBaseV2VO> requestVOList, Integer floatIconShowNumber) {
        List<SiteActivityBasePO> activityBases = Lists.newArrayList();
        List<ActivityBaseV2VO> list = requestVOList;
        for (int i = 0; i < list.size(); i++) {
            ActivityBaseV2VO bo = list.get(i);
            SiteActivityBasePO byId = siteActivityBaseRepository.selectById(bo.getId());
            byId.setFloatIconSort(bo.getFloatIconSort());
            byId.setUpdater(CurrReqUtils.getAccount());
            byId.setUpdatedTime(System.currentTimeMillis());
            activityBases.add(byId);
        }
        this.updateBatchById(activityBases);
        // 浮标展示数量
        String key = String.format(RedisConstants.ACTIVITY_FLOAT_ICON_SHOW_NUMBER, CurrReqUtils.getSiteCode()) ;
        if (null == floatIconShowNumber) {
            floatIconShowNumber = 0;
            RedisUtil.setValue(key, floatIconShowNumber);
        } else {
            RedisUtil.setValue(key, floatIconShowNumber);
        }
        return ResponseVO.success(true);
    }

    public Page<ActivityBasePartRespVO> activityPagePartList(ActivityBasePartReqVO requestVO) {
        Page<SiteActivityBasePO> reqPageVo = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        ActivityBaseReqVO vo = ActivityBaseReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, vo);
        vo.setShowStartTime(System.currentTimeMillis());
        vo.setShowEndTime(System.currentTimeMillis());
        vo.setStatus(EnableStatusEnum.ENABLE.getCode());
        LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = SiteActivityBasePO.getQueryWrapper(vo);
        // 过来出showFlag 不为0的，为空，或者是1的都可展示
        queryWrapper.and(q -> q
                .isNull(SiteActivityBasePO::getShowFlag)
                .or().eq(SiteActivityBasePO::getShowFlag, 1)
        );
        //
        // 新建一个 Wrapper，只复制查询条件，不复制排序
        /*LambdaQueryWrapper<SiteActivityBasePO> queryWrapperFinal = Wrappers.lambdaQuery();
        // 拷贝查询条件（MyBatis Plus 没提供 API，你需要从 getQueryWrapper() 返回前就不要加排序，或者重写 getQueryWrapper 不含排序）
        queryWrapperFinal.setEntity(queryWrapper.getEntity());
        queryWrapperFinal.setEntityClass(queryWrapper.getEntityClass());
        queryWrapperFinal.getExpression().getNormal().addAll(queryWrapper.getExpression().getNormal());

        queryWrapperFinal.orderByAsc(SiteActivityBasePO::getActivityDeadline);
        // 再次按照页签排序
        ResponseVO<List<AddActivityLabelSortVO>> sort = siteActivityLabsService.getSort(requestVO.getSiteCode());
        if(sort.isOk()&& CollUtil.isNotEmpty(sort.getData())){
            List<String> labelIds = sort.getData().stream().map(AddActivityLabelSortVO::getId).toList();
            // 推荐方式：in 过滤 + last 拼接 FIELD 排序
            // 生成安全的FIELD排序SQL（防止SQL注入）
            String fieldOrderSql = labelIds.stream()
                    .map(id -> "'" + id.replace("'", "''") + "'")
                    .collect(Collectors.joining(",", "ORDER BY FIELD(label_id, ", ")"));

            queryWrapperFinal.last(fieldOrderSql);
        }
        // 活动排序
        queryWrapperFinal.orderByAsc( SiteActivityBasePO::getSort);*/
        Page<SiteActivityBasePO> activityInfoPage = siteActivityBaseRepository.selectPage(reqPageVo, queryWrapper);


        IPage<ActivityBasePartRespVO> resultPage = activityInfoPage.convert(x -> {
            ActivityBasePartRespVO respVO = ActivityBasePartRespVO.builder().taskFlag(false).build();
            BeanUtils.copyProperties(x, respVO);
            /*if(ObjectUtil.equal(DeviceType.PC.getCode(),requestVO.getShowTerminal())){
                respVO.setHeadPictureI18nCode(x.getHeadPicturePcI18nCode());
                respVO.setEntrancePictureI18nCode(x.getEntrancePicturePcI18nCode());
            }*/
            // 判断活动是否在开启时间范围内
            long currentTime = System.currentTimeMillis();
            if (respVO.getActivityDeadline() == ActivityDeadLineEnum.LIMITED_TIME.getType()) {
                respVO.setEnable(currentTime >= respVO.getActivityStartTime() && currentTime <= respVO.getActivityEndTime());
            } else {
                respVO.setEnable(currentTime >= respVO.getActivityStartTime());
            }
            return respVO;
        });
        // 排序
        List<ActivityBasePartRespVO> records = resultPage.getRecords();
        // 再次按照页签排序
        ResponseVO<List<AddActivityLabelSortVO>> sort = siteActivityLabsService.getSort(requestVO.getSiteCode());
        List<ActivityBasePartRespVO> sortedList;
        if (sort.isOk() && CollUtil.isNotEmpty(sort.getData())) {
            List<String> labelIds = sort.getData().stream()
                    .map(AddActivityLabelSortVO::getId)
                    .toList();

            Comparator<ActivityBasePartRespVO> comparator = Comparator
                    .comparing(ActivityBasePartRespVO::getActivityDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(vos -> {
                        if (vos == null || vos.getLabelId() == null) {
                            return Integer.MAX_VALUE;
                        }
                        int index = labelIds.indexOf(vos.getLabelId());
                        return index == -1 ? Integer.MAX_VALUE : index;
                    })
                    .thenComparingInt(vos -> vos.getSort() == null ? Integer.MAX_VALUE : vos.getSort());

            sortedList = records.stream()
                    .filter(Objects::nonNull) // 防止 null 元素
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            Comparator<ActivityBasePartRespVO> comparator = Comparator
                    .comparing(ActivityBasePartRespVO::getActivityDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(vos -> vos.getSort() == null ? Integer.MAX_VALUE : vos.getSort());

            sortedList = records.stream()
                    .filter(Objects::nonNull)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        //DeviceType

        resultPage.setRecords(sortedList);
        return ConvertUtil.toConverPage(resultPage);
    }

    /**
     * C端
     * @param requestVO
     * @return
     */
    public List<ActivityBaseV2FloatIconVO> floatIconSortListToApp(ActivityBasePartReqVO requestVO) {
        LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBasePO::getSiteCode, requestVO.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBasePO::getFloatIconShowFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.orderByDesc(SiteActivityBasePO::getFloatIconSort);
        List<SiteActivityBasePO> recordList = siteActivityBaseRepository.selectList(lambdaQueryWrapper);
        // 获取前端要展示的浮标数量
        Integer floatIconShowNumber = RedisUtil.getValue(String.format(RedisConstants.ACTIVITY_FLOAT_ICON_SHOW_NUMBER, CurrReqUtils.getSiteCode()));
        if (null == floatIconShowNumber) {
            floatIconShowNumber = 0;
        }
        recordList = recordList.stream().limit(floatIconShowNumber).collect(Collectors.toList());
        if (CollUtil.isEmpty(recordList)) {
            return Lists.newArrayList();
        }
        return ConvertUtil.entityListToModelList(recordList, ActivityBaseV2FloatIconVO.class);
    }

    /**
     * 查询排序
     *
     * @return 返回查询过程
     */
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(String siteCode, String LabelId) {
        LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        queryWrapper.eq(SiteActivityBasePO::getLabelId, LabelId);
        queryWrapper.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        queryWrapper.orderByAsc(SiteActivityBasePO::getSort);
        List<SiteActivityBasePO> list = siteActivityBaseRepository.selectList(queryWrapper);
        try {
            List<ActivityBaseSortRespVO> resultList = ConvertUtil.convertListToList(list, new ActivityBaseSortRespVO());
            resultList.stream().forEach(e -> {

            });
            return ResponseVO.success(resultList);
        } catch (Exception e) {
            log.error("ConvertUtil.convertListToList error: {}", e.getMessage());
        }
        return ResponseVO.success(Collections.emptyList());
    }

    /**
     * 排序规则
     *
     * @return 返回排序结果
     */
    public ResponseVO<Boolean> activeTabSort(ActiveSortReqVO reqVO) {
        List<SiteActivityBasePO> activityBases = Lists.newArrayList();
        List<ActiveSortVO> list = reqVO.getSortVOS();
        for (int i = 0; i < list.size(); i++) {
            ActiveSortVO bo = list.get(i);
            SiteActivityBasePO byId = siteActivityBaseRepository.selectById(bo.getId());
            byId.setSort(bo.getSort());
            byId.setUpdater(reqVO.getAdminName());
            byId.setUpdatedTime(System.currentTimeMillis());
            activityBases.add(byId);
        }
        this.updateBatchById(activityBases);
        return ResponseVO.success(true);
    }

    /**
     * 删除
     *
     * @return 删除
     */
    public ResponseVO<Boolean> delete(ActiveBaseOnOffVO reqVO) {
        LambdaUpdateWrapper<SiteActivityBasePO> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(SiteActivityBasePO::getId, reqVO.getId());
        queryWrapper.eq(SiteActivityBasePO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.set(SiteActivityBasePO::getStatus, EnableStatusEnum.DISABLE.getCode());
        queryWrapper.set(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.DISABLE.getCode());
        siteActivityBaseRepository.update(null, queryWrapper);
        return ResponseVO.success(true);
    }

    /**
     * 启用|禁用
     *
     * @return 删除
     */
    public ResponseVO<Boolean> operateStatus(ActiveBaseOnOffVO reqVO) {
        if (ObjectUtil.isEmpty(reqVO.getStatus())) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        SiteActivityBasePO basePO = getById(reqVO.getId());
        if (basePO.getStatus().equals(reqVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_OPEN_ALREADY_TIME);
        }
        LambdaUpdateWrapper<SiteActivityBasePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityBasePO::getId, reqVO.getId());
        updateWrapper.eq(SiteActivityBasePO::getSiteCode, reqVO.getSiteCode());
        updateWrapper.set(SiteActivityBasePO::getStatus, reqVO.getStatus());
        updateWrapper.set(SiteActivityBasePO::getUpdater, reqVO.getOperator());
        updateWrapper.set(SiteActivityBasePO::getUpdatedTime, System.currentTimeMillis());
        if (reqVO.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            updateWrapper.set(SiteActivityBasePO::getForbidTime, null);
        } else {
            updateWrapper.set(SiteActivityBasePO::getForbidTime, System.currentTimeMillis());
        }
        siteActivityBaseRepository.update(null, updateWrapper);
        return ResponseVO.success(true);
    }


    public ResponseVO<Boolean> validateActivityBaseVO(ActivityBaseVO vo) {


        // 检查 activityNameI18nCodeList 不能为空且不能是空列表
        if (vo.getActivityNameI18nCodeList() == null || vo.getActivityNameI18nCodeList().isEmpty()) {
            // return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动名称-不能为空");
            for (I18nMsgFrontVO frontVO : vo.getActivityNameI18nCodeList()) {
                String msg = frontVO.getMessage();
                if (StringUtils.isBlank(msg) || msg.length() > 100) {
                    return ResponseVO.fail(ResultCode.PARAM_ERROR);
                }
            }
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        // 检查 labelId 活动分类不能为空
        if (StringUtils.isBlank(vo.getLabelId())) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动分类不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 activityDeadline 不能为空
        if (vo.getActivityDeadline() == null) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动时效不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 activityStartTime 和 activityEndTime 不能为空
        if (vo.getActivityStartTime() == null) {
            // return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动开始时间不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (ActivityDeadLineEnum.LIMITED_TIME.getType() == vo.getActivityDeadline() && vo.getActivityEndTime() == null) {
            // return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动结束时间不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        if (vo.getActivityStartTime() < System.currentTimeMillis()) {
            log.info("当前活动开始时间:{},小于现在时间:{},不允许创建", vo.getActivityStartTime(), System.currentTimeMillis());
            throw new BaowangDefaultException(ResultCode.BASE_ERROR_ACTIVITY);
        }
        // 如果是静态活动，且不展示，就不校验showTime
        boolean isStaticNotShowFlag = vo.getActivityTemplate().equals(ActivityTemplateEnum.STATIC.getType()) && vo.getShowFlag() == 0;
        // 检查展示开始时间和展示结束时间
        if (vo.getShowStartTime() == null && !isStaticNotShowFlag) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动展示开始时间不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        if (vo.getShowEndTime() == null && !isStaticNotShowFlag) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动展示结束时间不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查活动模板
        if (StringUtils.isBlank(vo.getActivityTemplate())) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动模板不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 washRatio 不能为空
        if (!ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.ASSIGN_DAY.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.CHECKIN.getType().equals(vo.getActivityTemplate())) {
            if (vo.getWashRatio() == null) {
                //return ResponseVO.fail(ResultCode.PARAM_ERROR, "洗码倍率不能为空");
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        // 检查 accountType 活动生效的账户类型
        if (vo.getAccountType() == null) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动生效的账户类型不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查展示终端
        if (StringUtils.isBlank(vo.getShowTerminal())) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动展示终端不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 entrancePictureI18nCodeList (入口图-移动端)不能为空
        if (vo.getEntrancePictureI18nCodeList() == null || vo.getEntrancePictureI18nCodeList().isEmpty()) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "入口图-移动端不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 entrancePicturePcI18nCodeList (入口图-PC端)不能为空
        if (vo.getEntrancePicturePcI18nCodeList() == null || vo.getEntrancePicturePcI18nCodeList().isEmpty()) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "入口图-PC端不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 headPictureI18nCodeList (活动头图-移动端)不能为空
        if (vo.getHeadPictureI18nCodeList() == null || vo.getHeadPictureI18nCodeList().isEmpty()) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动头图-移动端不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        // 检查 洗码倍率 不能为空，且大于0
        if (!ActivityTemplateEnum.FIRST_DEPOSIT.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.SECOND_DEPOSIT.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.ASSIGN_DAY.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateEnum.CHECKIN.getType().equals(vo.getActivityTemplate())) {

            if (vo.getWashRatio() == null || vo.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseVO.fail(ResultCode.WASH_RATIO_AMOUNT_OVER_ZERO_ERROR);
            }
        }
        // 检查 headPicturePcI18nCodeList (活动头图-PC端)不能为空
        if (vo.getHeadPicturePcI18nCodeList() == null || vo.getHeadPicturePcI18nCodeList().isEmpty()) {
            //return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动头图-PC端不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }


        // 所有校验通过，返回成功
        return ResponseVO.success(true);
    }


    public List<SiteActivityBasePO> queryActivityBaseList(ActivityBaseVO requestVO) {
        ActivityBaseReqVO reqVO = ActivityBaseReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, reqVO);

        String key = RedisConstants.getToSetSiteCodeKeyConstant(requestVO.getSiteCode(), RedisConstants.ACTIVITY_BASE_LIST);
        List<SiteActivityBasePO> list = RedisUtil.getList(key);
        if (CollectionUtil.isEmpty(list)) {
            list = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityBasePO.class).eq(SiteActivityBasePO::getSiteCode, requestVO.getSiteCode())
                    .eq(SiteActivityBasePO::getDeleteFlag, DeleteStateEnum.Normal.getType()));
            if (CollectionUtil.isNotEmpty(list)) {
                RedisUtil.setList(key, list, 60L, TimeUnit.MINUTES);
            }
        }


        if (CollectionUtil.isEmpty(list)) {
            return Lists.newArrayList();
        }


        if (ObjectUtil.isNotEmpty(requestVO.getId())) {
            list = list.stream().filter(x -> x.getId().equals(requestVO.getId())).toList();
        }

        if (CollectionUtil.isNotEmpty(requestVO.getIds())) {
            list = list.stream().filter(x -> requestVO.getIds().contains(x.getId())).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getActivityNo())) {
            list = list.stream().filter(x -> x.getActivityNo().equals(requestVO.getActivityNo())).toList();
        }

        if (CollectionUtil.isNotEmpty(requestVO.getActivityNoList())) {
            list = list.stream().filter(x -> requestVO.getActivityNoList().contains(x.getActivityNo())).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getLabelId())) {
            list = list.stream().filter(x -> x.getLabelId().equals(requestVO.getLabelId())).toList();
        }


        if (ObjectUtil.isNotEmpty(requestVO.getActivityTemplate())) {
            list = list.stream().filter(x -> x.getActivityTemplate().equals(requestVO.getActivityTemplate())).toList();
        }

        if (CollUtil.isNotEmpty(requestVO.getActivityTemplateList())) {
            list = list.stream().filter(x -> requestVO.getActivityTemplateList().contains(x.getActivityTemplate())).toList();
        }


        if (ObjectUtil.isNotEmpty(requestVO.getAccountType())) {
            list = list.stream().filter(x -> x.getAccountType().equals(requestVO.getAccountType())).toList();
        }
        // 遍历活动列表，根据开始和结束时间重新设置启用/禁用状态
        if (ObjectUtil.isNotEmpty(list)) {
            long now = System.currentTimeMillis(); // 当前时间只获取一次，避免重复调用

            for (SiteActivityBasePO activity : list) {
                // 如果活动是限时活动并且有结束时间
                if (ActivityDeadLineEnum.LIMITED_TIME.equals(activity.getActivityDeadline())
                        && ObjectUtil.isNotEmpty(activity.getActivityEndTime())) {

                    boolean isOngoing = activity.getActivityStartTime() <= now && activity.getActivityEndTime() > now;

                    // 根据当前时间判断活动是否启用
                    activity.setStatus(isOngoing
                            ? EnableStatusEnum.ENABLE.getCode()
                            : EnableStatusEnum.DISABLE.getCode());
                }
            }
        }



        // 派发操作不校验这个
        if (ObjectUtil.isNotEmpty(requestVO.getStatus()) && requestVO.isApplyFlag()) {
            list = list.stream().filter(x -> x.getStatus().equals(requestVO.getStatus())).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getOperator())) {
            list = list.stream().filter(x -> x.getUpdater().equals(requestVO.getOperator())).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getOperator())) {
            list = list.stream().filter(x -> x.getUpdater().equals(requestVO.getOperator())).toList();
        }


        if (ObjectUtil.isNotEmpty(requestVO.getShowStartTime())) {
            list = list.stream().filter(x -> x.getShowStartTime() <= requestVO.getShowStartTime()).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getShowEndTime())) {
            list = list.stream().filter(x -> x.getShowEndTime() > requestVO.getShowEndTime()).toList();
        }


        if (ObjectUtil.isNotEmpty(requestVO.getActivityStartTime())) {
            list = list.stream().filter(x -> x.getActivityStartTime() <= requestVO.getActivityStartTime()).toList();
        }

        //长期活动不需要校验时间,直接返回,非长期活动的需要校验结束时间
        if (ObjectUtil.isNotEmpty(requestVO.getActivityEndTime())) {
            list = list.stream().filter(x -> {
                if (x.getActivityDeadline().equals(ActivityDeadLineEnum.LONG_TERM.getType())) {
                    return true;
                }
                return x.getActivityEndTime() >= requestVO.getActivityEndTime();
            }).toList();
        }

        String requestedTerminal = requestVO.getShowTerminal();
        if (ObjectUtil.isNotEmpty(requestedTerminal)) {
            list = list.stream().filter(x -> {
                List<String> terminalList = Arrays.asList(x.getShowTerminal().split(","));
                return terminalList.contains(requestedTerminal);
            }).toList();
        }

        if (ObjectUtil.isNotEmpty(requestVO.getActivityDeadline())) {
            list = list.stream().filter(x -> x.getActivityDeadline().equals(requestVO.getActivityDeadline())).toList();
        }

        return list;
    }

    public ActivityBaseRespVO queryActivityById(String id, String siteCode) {
        SiteActivityBasePO siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getId, id)
                .eq(SiteActivityBasePO::getSiteCode, siteCode));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseRespVO respVO = new ActivityBaseRespVO();
        BeanUtils.copyProperties(siteActivityBasePO, respVO);
        return respVO;
    }


    /**
     * 执行过期活动定时任务清理，把base表过期后的状态修改为0
     */
    public void expiredActivity(SiteVO siteVO, ActivityTemplateEnum activityTemplateEnum) {
        log.info("执行过期活动定时任务清理:site:{},activity:{}", siteVO, activityTemplateEnum);
        LambdaQueryWrapper<SiteActivityBasePO> baseWrapper = Wrappers.lambdaQuery(SiteActivityBasePO.class).eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode()).eq(SiteActivityBasePO::getActivityTemplate, activityTemplateEnum.getType()).eq(SiteActivityBasePO::getSiteCode, siteVO.getSiteCode()).le(SiteActivityBasePO::getActivityEndTime, System.currentTimeMillis());
        List<SiteActivityBasePO> list = baseMapper.selectList(baseWrapper);
        for (SiteActivityBasePO activityBase : list) {
            // todo 添加禁用时间
            int count = baseMapper.update(SiteActivityBasePO.builder().status(EnableStatusEnum.DISABLE.getCode()).xxlJobId(null).forbidTime(System.currentTimeMillis()).build(),
                    Wrappers.lambdaQuery(SiteActivityBasePO.class).eq(SiteActivityBasePO::getId, activityBase.getId())
            );
            log.info("活动过期删除逻辑....,id:{},activityTemplateEnum:{},upActivity:{}", activityBase.getId(), activityBase.getActivityTemplate(), count);
            // 活动过期后 可能还会启用 时间也可能被修改 所以 不能删除job
            /*if (count > 0 && StringUtils.isNotBlank(activityBase.getXxlJobId())) {
                activityJobComponent.remove(List.of(activityBase.getXxlJobId()));
            }*/
            String key = RedisConstants.getToSetSiteCodeKeyConstant(siteVO.getSiteCode(), String.format(RedisConstants.ACTIVITY_CONFIG, activityBase.getId()));
            RedisUtil.deleteKey(key);
        }
    }


    public void updateJobId(String activityId, String xxlJobId) {
        LambdaUpdateWrapper<SiteActivityBasePO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivityBasePO>();
        lambdaUpdateWrapper.set(SiteActivityBasePO::getXxlJobId, xxlJobId);
        lambdaUpdateWrapper.eq(SiteActivityBasePO::getId, activityId);
        this.update(lambdaUpdateWrapper);
    }

    public ActivityBaseRespVO queryActivityByActivityNo(String activityNo, String template, String siteCode) {
        SiteActivityBasePO siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityNo, activityNo)
                .eq(SiteActivityBasePO::getActivityTemplate, template)
                .eq(SiteActivityBasePO::getSiteCode, siteCode));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseRespVO respVO = new ActivityBaseRespVO();
        BeanUtils.copyProperties(siteActivityBasePO, respVO);
        return respVO;
    }

    public ResponseVO<CheckInBasePartRespVO> checkInActivityInfo(UserBaseReqVO reqVO) {
        ActivityBaseVO vo = new ActivityBaseVO();
        BeanUtils.copyProperties(reqVO, vo);
        vo.setShowStartTime(System.currentTimeMillis());
        vo.setShowEndTime(System.currentTimeMillis());
        vo.setStatus(EnableStatusEnum.ENABLE.getCode());
        vo.setActivityTemplate(ActivityTemplateEnum.CHECKIN.getType());
        vo.setSiteCode(reqVO.getSiteCode());
        vo.setShowTerminal(reqVO.getDeviceType().toString());
        List<SiteActivityBasePO> basePOS = queryActivityBaseList(vo);
        CheckInBasePartRespVO result = new CheckInBasePartRespVO();
        if (CollectionUtil.isNotEmpty(basePOS) && basePOS.size() == 1 && basePOS.get(0) != null) {
            BeanUtils.copyProperties(basePOS.get(0), result);
            // 判断活动是否在开启时间范围内
            long currentTime = System.currentTimeMillis();
            if (result.getActivityDeadline() == ActivityDeadLineEnum.LIMITED_TIME.getType()) {
                result.setEnable(currentTime >= result.getActivityStartTime() && currentTime <= result.getActivityEndTime());
            } else {
                result.setEnable(currentTime >= result.getActivityStartTime());
            }

            result.setIsForbidden(false);
        }
        result.setUserType(ActivityUserTypeEnum.ALL_USER.getCode());
        if (ObjectUtil.isEmpty(reqVO.getUserId())) {
            result.setCheckInStatus(false);
            // 设置领取状态都是4
            return ResponseVO.success(result);
        }
        // 判断是否获取
        long currentTime = System.currentTimeMillis();
        String timeZone = reqVO.getTimezone();

        Long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(currentTime, timeZone);
        Long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(currentTime, timeZone);
        List<SiteCheckInRecordPO> todayCheckInRecord = checkInRecordService.getTodayCheckInRecord(reqVO.getSiteCode(), reqVO.getUserId(), timeStart, timeEnd);
        if (CollectionUtil.isNotEmpty(todayCheckInRecord) && todayCheckInRecord.size() > 0) {
            result.setCheckInStatus(true);
        }
        return ResponseVO.success(result);

    }

    public ResponseVO<CheckInRecordRespVO> checkInRecord(UserBaseReqVO build) {
        return checkInRecordService.checkInRecord(build);


    }

    public ResponseVO<CheckInRewardResultRespVO> checkIn(UserBaseReqVO build) {
        return checkInRecordService.checkIn(build);
    }
}