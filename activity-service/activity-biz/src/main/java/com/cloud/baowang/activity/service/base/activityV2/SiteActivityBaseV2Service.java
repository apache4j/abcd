package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityEligibilityEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveSortVO;
import com.cloud.baowang.activity.api.vo.category.AddActivityLabelSortVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.activity.service.ActivityJobComponent;
import com.cloud.baowang.activity.service.SiteActivityCheckInRecordService;
import com.cloud.baowang.activity.service.SiteActivityLabsService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.*;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.operations.DomainQueryVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
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
public class SiteActivityBaseV2Service extends ServiceImpl<SiteActivityBaseV2Repository, SiteActivityBaseV2PO> {

    private final I18nApi i18nApi;

    private final SiteActivityBaseV2Repository siteActivityBaseV2Repository;

    private final ActivityJobComponent activityJobComponent;

    private final SiteActivityLabsService siteActivityLabsService;

    private final SiteActivityCheckInRecordService checkInRecordService;
    private final DomainInfoApi domainInfoApi;
    /**
     * 活动列表
     */

    /**
     * 根据模板查询出已开启的活动配置
     */
    public SiteActivityBaseV2PO getSiteActivityBasePO(String siteCode, String activityTemplate) {
        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, RedisConstants.ACTIVITY_TEMPLATE);
        if (RedisUtil.isKeyExist(key)) {
            return RedisUtil.getValue(key);
        }
        SiteActivityBaseV2PO siteActivityBaseV2PO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteActivityBaseV2PO::getSiteCode, siteCode));

        if (ObjectUtil.isEmpty(siteActivityBaseV2PO)) {
            return null;
        }

        return siteActivityBaseV2PO;
    }


    /**
     * 根据模板查询出已开启的活动配置
     */
    public List<SiteActivityBaseV2PO> selectAllValid(String template) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBaseV2PO.class);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据模板与siteCode查询出已开启的活动配置
     */
    public SiteActivityBaseV2PO selectBySiteAndTem(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBaseV2PO.class);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getDeleteFlag, 1);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectOne(lambdaQueryWrapper);
    }

    /**
     * 按照站点、活动模板获取活动列表
     *
     * @param template 活动模板
     * @param siteCode 站点编号
     * @return 活动列表
     */
    public List<SiteActivityBaseV2PO> selectBySiteAndTemplate(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBaseV2PO.class);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, siteCode);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 按照站点、活动模板获取有效活动列表
     *
     * @param template 活动模板
     * @param siteCode 站点编号
     * @return 活动列表
     */
    public List<SiteActivityBaseV2PO> selectValidBySiteAndTemplate(String siteCode, String template) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = Wrappers.lambdaQuery(SiteActivityBaseV2PO.class);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getActivityTemplate, template);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        return baseMapper.selectList(lambdaQueryWrapper);
    }


    private SiteActivityBaseV2PO convertSiteActivityBasePO(ActivityBaseV2VO activityBaseVO) {

        List<Integer> activityEligibility = activityBaseVO.getActivityEligibility();

        SiteActivityBaseV2PO siteActivityBasePO = ConvertUtil.entityToModel(activityBaseVO, SiteActivityBaseV2PO.class);
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
        if (StrUtil.isBlank(siteActivityBasePO.getActivityNameI18nCode())) {
            String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_NAME.getCode());
            siteActivityBasePO.setActivityNameI18nCode(activityNameI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getEntrancePictureI18nCodeList())) {
            String activityEntrancePicI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE.getCode());
            siteActivityBasePO.setEntrancePictureI18nCode(activityEntrancePicI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getEntrancePictureBlackI18nCodeList())) {
            String activityEntrancePicBlackI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE_BLACK.getCode());
            siteActivityBasePO.setEntrancePictureBlackI18nCode(activityEntrancePicBlackI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getEntrancePicturePcBlackI18nCodeList())) {
            String activityEntrancePicPcBlackI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_ENTRANCE_PICTURE_PC_BLACK.getCode());
            siteActivityBasePO.setEntrancePicturePcBlackI18nCode(activityEntrancePicPcBlackI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getHeadPictureBlackI18nCodeList())) {
            String activityHeadPicI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_HEAD_PICTURE_BLACK.getCode());
            siteActivityBasePO.setHeadPictureBlackI18nCode(activityHeadPicI18);
        }
        if (CollectionUtil.isNotEmpty(activityBaseVO.getHeadPicturePcBlackI18nCodeList())) {
            String activityHeadPicPcI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_HEAD_PICTURE_PC_BLACK.getCode());
            siteActivityBasePO.setHeadPicturePcBlackI18nCode(activityHeadPicPcI18);
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

        if (CollectionUtil.isNotEmpty(activityBaseVO.getPicShowupPcI18nCodeList())) {
            // 未登录展示图PC
            String showUpPicPcI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_SHOW_UP_PIC_PC.getCode());
            siteActivityBasePO.setPicShowupPcI18nCode(showUpPicPcI18Code);
        }

        if (CollectionUtil.isNotEmpty(activityBaseVO.getPicShowupAppI18nCodeList())) {
            // 未登录展示图APP
            String showUpPicAppI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_SHOW_UP_PIC_APP.getCode());
            siteActivityBasePO.setPicShowupAppI18nCode(showUpPicAppI18Code);
        }
        if (ObjectUtil.isNotEmpty(activityBaseVO.getRecommendTerminalsPicI18nCodeList())) {
            String recommendTerminalsPicI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_RECOMMEND_TERMINALS_PIC.getCode());
            siteActivityBasePO.setRecommendTerminalsPicI18nCode(recommendTerminalsPicI18nCode);
        }
        if (ObjectUtil.isNotEmpty(activityBaseVO.getRecommendTerminalsPicPcI18nCodeList())) {
            String recommendTerminalsPicPcI18nCode = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_RECOMMEND_TERMINALS_PIC_PC.getCode());
            siteActivityBasePO.setRecommendTerminalsPicPcI18nCode(recommendTerminalsPicPcI18nCode);
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
        if (Objects.equals(activityBaseVO.getActivityDeadline(), ActivityDeadLineEnum.LONG_TERM.getType())) {
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


    public SiteActivityBaseV2PO updateActivityBase(ActivityBaseV2VO activityBaseVO) {

        SiteActivityBaseV2PO siteActivityBasePO = convertSiteActivityBasePO(activityBaseVO);
        if (siteActivityBaseV2Repository.updateById(siteActivityBasePO) <= 0) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        // 如果是长期，结束时间为空
        if (Objects.equals(activityBaseVO.getActivityDeadline(), ActivityDeadLineEnum.LONG_TERM.getType())) {
            LambdaUpdateWrapper<SiteActivityBaseV2PO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SiteActivityBaseV2PO::getActivityEndTime, null);
            updateWrapper.eq(SiteActivityBaseV2PO::getId, activityBaseVO.getId());
            if (siteActivityBaseV2Repository.update(null, updateWrapper) <= 0) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        putIfNotNull(i18nData, siteActivityBasePO.getActivityNameI18nCode(), activityBaseVO.getActivityNameI18nCodeList());

        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePictureI18nCode(), activityBaseVO.getEntrancePictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePictureBlackI18nCode(), activityBaseVO.getEntrancePictureBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcBlackI18nCode(), activityBaseVO.getEntrancePicturePcBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureBlackI18nCode(), activityBaseVO.getHeadPictureBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcBlackI18nCode(), activityBaseVO.getHeadPicturePcBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList());
        //未登录图
        putIfNotNull(i18nData, siteActivityBasePO.getPicShowupPcI18nCode(), activityBaseVO.getPicShowupPcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getPicShowupAppI18nCode(), activityBaseVO.getPicShowupAppI18nCodeList());

        putIfNotNull(i18nData, siteActivityBasePO.getRecommendTerminalsPicI18nCode(), activityBaseVO.getRecommendTerminalsPicI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getRecommendTerminalsPicPcI18nCode(), activityBaseVO.getRecommendTerminalsPicPcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getFloatIconAppI18nCode(), activityBaseVO.getFloatIconAppI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getFloatIconPcI18nCode(), activityBaseVO.getFloatIconPcI18nCodeList());

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
    public SiteActivityBaseV2PO saveActivityBase(ActivityBaseV2VO activityBaseVO) {
        SiteActivityBaseV2PO siteActivityBasePO = convertSiteActivityBasePO(activityBaseVO);
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
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePictureBlackI18nCode(), activityBaseVO.getEntrancePictureBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcI18nCode(), activityBaseVO.getEntrancePicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getEntrancePicturePcBlackI18nCode(), activityBaseVO.getEntrancePicturePcBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureI18nCode(), activityBaseVO.getHeadPictureI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPictureBlackI18nCode(), activityBaseVO.getHeadPictureBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcI18nCode(), activityBaseVO.getHeadPicturePcI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getHeadPicturePcBlackI18nCode(), activityBaseVO.getHeadPicturePcBlackI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityRuleI18nCode(), activityBaseVO.getActivityRuleI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityDescI18nCode(), activityBaseVO.getActivityDescI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getActivityIntroduceI18nCode(), activityBaseVO.getActivityIntroduceI18nCodeList());

        putIfNotNull(i18nData, siteActivityBasePO.getPicShowupAppI18nCode(), activityBaseVO.getPicShowupAppI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getPicShowupPcI18nCode(), activityBaseVO.getPicShowupPcI18nCodeList());

        putIfNotNull(i18nData, siteActivityBasePO.getRecommendTerminalsPicI18nCode(), activityBaseVO.getRecommendTerminalsPicI18nCodeList());
        putIfNotNull(i18nData, siteActivityBasePO.getRecommendTerminalsPicPcI18nCode(), activityBaseVO.getRecommendTerminalsPicPcI18nCodeList());
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

    public Page<ActivityBaseV2RespVO> siteActivityPageList(ActivityBaseReqVO requestVO) {
        Page<SiteActivityBaseV2PO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
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
        LambdaQueryWrapper<SiteActivityBaseV2PO> queryWrapper = SiteActivityBaseV2PO.getQueryWrapper(requestVO);
        Page<SiteActivityBaseV2PO> activityInfoPage = siteActivityBaseV2Repository.selectPage(respVOPage, queryWrapper);
        // 获取所有分类
        List<CodeValueVO> labelNames = siteActivityLabsService.getLabNameList(requestVO.getSiteCode());
        Map<String, String> labelNameMap = labelNames.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue, (v1, v2) -> v1));
        Page<ActivityBaseV2RespVO> resultPage = new Page<>();
        BeanUtils.copyProperties(activityInfoPage, resultPage);
        if (!CollectionUtils.isEmpty(activityInfoPage.getRecords())) {

            resultPage.setRecords(ConvertUtil.entityListToModelList(activityInfoPage.getRecords(), ActivityBaseV2RespVO.class));
            for (ActivityBaseV2RespVO record : resultPage.getRecords()) {
                record.setLabelName(labelNameMap.get(record.getLabelId()));
                ActivityTemplateV2Enum activityTemplateEnum = ActivityTemplateV2Enum.nameOfCode(record.getActivityTemplate());
                if (activityTemplateEnum == ActivityTemplateV2Enum.NEW_HAND
                        || activityTemplateEnum == ActivityTemplateV2Enum.CONTEST_PAYOUT_V2
                ){
                    record.setWashRatio(null);
                }

            }

        }
        return resultPage;
    }

    /**
     * B端
     * @param requestVO
     * @return
     */
    public ResponseVO<ActivityBaseV2FloatIconRespVO>  floatIconSortListToSite(ActivityBaseReqVO requestVO) {
        ActivityBaseV2FloatIconRespVO respVO = new ActivityBaseV2FloatIconRespVO();
        Integer floatIconShowNumber = RedisUtil.getValue(String.format(RedisConstants.ACTIVITY_FLOAT_ICON_SHOW_NUMBER, CurrReqUtils.getSiteCode()));
        if (null == floatIconShowNumber) {
            floatIconShowNumber = 0;
        }
        respVO.setFloatIconShowNumber(floatIconShowNumber);
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, requestVO.getSiteCode());
        // 未开启的也要查出来
        // lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getFloatIconShowFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.orderByDesc(SiteActivityBaseV2PO::getFloatIconSort);
        List<SiteActivityBaseV2PO> recordList = siteActivityBaseV2Repository.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(recordList)) {
            recordList = Lists.newArrayList();
        }
        respVO.setActivityBaseV2FloatIconVOList(ConvertUtil.entityListToModelList(recordList, ActivityBaseV2FloatIconVO.class));
        return ResponseVO.success(respVO);
    }

    /**
     * B端
     * @param requestVOList
     * @param floatIconShowNumber
     * @return
     */
    public ResponseVO<Boolean> floatIconSortListSave(List<ActivityBaseV2VO> requestVOList, Integer floatIconShowNumber) {
        List<SiteActivityBaseV2PO> activityBases = Lists.newArrayList();
        List<ActivityBaseV2VO> list = requestVOList;
        for (int i = 0; i < list.size(); i++) {
            ActivityBaseV2VO bo = list.get(i);
            SiteActivityBaseV2PO byId = siteActivityBaseV2Repository.selectById(bo.getId());
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



    public Page<ActivityBasePartV2RespVO> activityPagePartList(ActivityBasePartReqVO requestVO) {
        Page<SiteActivityBaseV2PO> reqPageVo = new Page<>(requestVO.getPageNumber(), 1000);
        ActivityBaseReqVO vo = ActivityBaseReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, vo);
        vo.setShowStartTime(System.currentTimeMillis());
        vo.setShowEndTime(System.currentTimeMillis());
        vo.setStatus(EnableStatusEnum.ENABLE.getCode());
        vo.setRecommendTerminals(requestVO.getRecommendTerminals());

        LambdaQueryWrapper<SiteActivityBaseV2PO> queryWrapper = SiteActivityBaseV2PO.getQueryWrapper(vo);
        // 过来出showFlag 不为0的，为空，或者是1的都可展示
        queryWrapper.and(q -> q
                .isNull(SiteActivityBaseV2PO::getShowFlag)
                .or().eq(SiteActivityBaseV2PO::getShowFlag, 1)
        );
        Page<SiteActivityBaseV2PO> activityInfoPage = siteActivityBaseV2Repository.selectPage(reqPageVo, queryWrapper);


        IPage<ActivityBasePartV2RespVO> resultPage = activityInfoPage.convert(x -> {
            ActivityBasePartV2RespVO respVO = ActivityBasePartV2RespVO.builder().taskFlag(false).build();
            BeanUtils.copyProperties(x, respVO);
            // 判断活动是否在开启时间范围内
            long currentTime = System.currentTimeMillis();
            if (Objects.equals(respVO.getActivityDeadline(), ActivityDeadLineEnum.LIMITED_TIME.getType())) {
                respVO.setEnable(currentTime >= respVO.getActivityStartTime() && currentTime <= respVO.getActivityEndTime());
            } else {
                respVO.setEnable(currentTime >= respVO.getActivityStartTime());
            }
            return respVO;
        });
        // 排序
        List<ActivityBasePartV2RespVO> records = resultPage.getRecords();
        // 再次按照页签排序
        ResponseVO<List<AddActivityLabelSortVO>> sort = siteActivityLabsService.getSort(requestVO.getSiteCode());
        List<ActivityBasePartV2RespVO> sortedList;
        if (sort.isOk() && CollUtil.isNotEmpty(sort.getData())) {
            List<String> labelIds = sort.getData().stream()
                    .map(AddActivityLabelSortVO::getId)
                    .toList();
            sortedList = records.stream().peek(vos -> {
                int index = labelIds.indexOf(vos.getLabelId());
                if (index == -1 ){
                    vos.setAllSort(Integer.MAX_VALUE);
                }else {
                    vos.setAllSort(index);
                }
            }).sorted(Comparator.comparing(ActivityBasePartV2RespVO::getAllSort)).toList();
        } else {
            Comparator<ActivityBasePartV2RespVO> comparator = Comparator
                    .comparing(ActivityBasePartV2RespVO::getActivityDeadline, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingInt(vos -> vos.getSort() == null ? Integer.MAX_VALUE : vos.getSort());

            sortedList = records.stream()
                    .filter(Objects::nonNull)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }
        //DeviceType

        sortedList.forEach(activityBasePartV2RespVO -> {
            Long activityEndTime = activityBasePartV2RespVO.getActivityEndTime();

            Integer activityDeadline = activityBasePartV2RespVO.getActivityDeadline();
            Long activityStartTime = activityBasePartV2RespVO.getActivityStartTime();

            if (activityEndTime!=null){
                activityBasePartV2RespVO.setActivityTimeRemaining((activityEndTime-System.currentTimeMillis())/1000);
            }

            if (activityDeadline==1 && System.currentTimeMillis() > activityStartTime){
                activityBasePartV2RespVO.setOpenStatus(1);
            }
            if (activityDeadline==0 && (System.currentTimeMillis() >= activityStartTime) ){
                if (System.currentTimeMillis() <= activityEndTime){
                    activityBasePartV2RespVO.setOpenStatus(1);
                }else {
                    activityBasePartV2RespVO.setOpenStatus(2);
                }
            }

            //设置H5 URl
            String h5Domain = getH5Domain();
            String h5DomainUrl = getH5DomainUrl(h5Domain, activityBasePartV2RespVO.getActivityTemplate(), activityBasePartV2RespVO.getId());
            activityBasePartV2RespVO.setH5ActivityUrl(h5DomainUrl);

        });

        resultPage.setRecords(sortedList);
        return ConvertUtil.toConverPage(resultPage);
    }

    /**
     * C端
     * @param requestVO
     * @return
     */
    public List<ActivityBaseV2FloatIconVO> floatIconSortListToApp(ActivityBasePartReqVO requestVO) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, requestVO.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.eq(SiteActivityBaseV2PO::getFloatIconShowFlag, EnableStatusEnum.ENABLE.getCode());
        lambdaQueryWrapper.orderByDesc(SiteActivityBaseV2PO::getFloatIconSort);
        List<SiteActivityBaseV2PO> recordList = siteActivityBaseV2Repository.selectList(lambdaQueryWrapper);
        final String showTerminal = requestVO.getShowTerminal();
        long currentTimeMillis = System.currentTimeMillis();
        recordList = recordList.stream()
                // 判断当前设备类型
                .filter(x -> StrUtil.contains(x.getShowTerminal(), showTerminal))
                // 判断展示时间
                .filter(y ->
                        DateUtil.date(currentTimeMillis).isBefore(DateUtil.date(y.getShowEndTime())) &&
                                DateUtil.date(currentTimeMillis).isAfter(DateUtil.date(y.getShowStartTime())))
                // 活动结束也是不展示
                .filter(z -> DateUtil.date(currentTimeMillis).isBefore(DateUtil.date(z.getActivityEndTime())))
                .collect(Collectors.toList());

        // 获取前端要展示的浮标数量
        Integer floatIconShowNumber = RedisUtil.getValue(String.format(RedisConstants.ACTIVITY_FLOAT_ICON_SHOW_NUMBER, CurrReqUtils.getSiteCode()));
        if (null == floatIconShowNumber) {
            floatIconShowNumber = 0;
        }
        recordList = recordList.stream().limit(floatIconShowNumber).collect(Collectors.toList());
        if (CollUtil.isEmpty(recordList)) {
            return Lists.newArrayList();
        }else {
            String h5Domain = getH5Domain();
            List<ActivityBaseV2FloatIconVO> baseV2FloatIconVOS = BeanUtil.copyToList(recordList, ActivityBaseV2FloatIconVO.class);
            for (ActivityBaseV2FloatIconVO vo : baseV2FloatIconVOS) {
                vo.setH5ActivityUrl(getH5DomainUrl(h5Domain, vo.getActivityTemplate(), vo.getId()));
            }
            return baseV2FloatIconVOS;
        }
    }

    public String getH5Domain(){
        DomainQueryVO domainQueryVO = new DomainQueryVO();
        domainQueryVO.setDomainType(DomainInfoTypeEnum.H5_PAGE.getType());
        domainQueryVO.setSiteCode(CurrReqUtils.getSiteCode());
        DomainVO domainByType = domainInfoApi.getDomainByType(domainQueryVO);
        if (domainByType != null && StrUtil.isNotEmpty(domainByType.getDomainAddr())) {
            String url;
            if (domainByType.getDomainAddr().contains("http")) {
                url = domainByType.getDomainAddr();
            } else {
                url = "https://" + domainByType.getDomainAddr();
            }
            return url;
        }
        return "";
    }

    public String getH5DomainUrl(String domain,  String activityTemplate, String activityId){
        return  domain + "/activity/list/" + activityTemplate + "/" + activityId;
    }


    /**
     * 查询排序
     *
     * @return 返回查询过程
     */
    public ResponseVO<List<ActivityBaseSortRespVO>> getActiveTabSort(String siteCode, String LabelId) {
        LambdaQueryWrapper<SiteActivityBaseV2PO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, siteCode);
        queryWrapper.eq(SiteActivityBaseV2PO::getLabelId, LabelId);
        queryWrapper.eq(SiteActivityBaseV2PO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        queryWrapper.orderByAsc(SiteActivityBaseV2PO::getSort);
        List<SiteActivityBaseV2PO> list = siteActivityBaseV2Repository.selectList(queryWrapper);
        try {
            List<ActivityBaseSortRespVO> resultList = ConvertUtil.convertListToList(list, new ActivityBaseSortRespVO());
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
        List<SiteActivityBaseV2PO> activityBases = Lists.newArrayList();
        List<ActiveSortVO> list = reqVO.getSortVOS();
        for (int i = 0; i < list.size(); i++) {
            ActiveSortVO bo = list.get(i);
            SiteActivityBaseV2PO byId = siteActivityBaseV2Repository.selectById(bo.getId());
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
        LambdaUpdateWrapper<SiteActivityBaseV2PO> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(SiteActivityBaseV2PO::getId, reqVO.getId());
        queryWrapper.eq(SiteActivityBaseV2PO::getSiteCode, reqVO.getSiteCode());
        queryWrapper.set(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.DISABLE.getCode());
        queryWrapper.set(SiteActivityBaseV2PO::getDeleteFlag, EnableStatusEnum.DISABLE.getCode());
        siteActivityBaseV2Repository.update(null, queryWrapper);
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
        SiteActivityBaseV2PO basePO = getById(reqVO.getId());
        if (basePO.getStatus().equals(reqVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_OPEN_ALREADY_TIME);
        }
        LambdaUpdateWrapper<SiteActivityBaseV2PO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityBaseV2PO::getId, reqVO.getId());
        updateWrapper.eq(SiteActivityBaseV2PO::getSiteCode, reqVO.getSiteCode());
        updateWrapper.set(SiteActivityBaseV2PO::getStatus, reqVO.getStatus());
        updateWrapper.set(SiteActivityBaseV2PO::getUpdater, reqVO.getOperator());
        updateWrapper.set(SiteActivityBaseV2PO::getUpdatedTime, System.currentTimeMillis());
        if (reqVO.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            updateWrapper.set(SiteActivityBaseV2PO::getForbidTime, null);
        } else {
            updateWrapper.set(SiteActivityBaseV2PO::getForbidTime, System.currentTimeMillis());
        }
        siteActivityBaseV2Repository.update(null, updateWrapper);
        return ResponseVO.success(true);
    }


    public ResponseVO<Boolean> validateActivityBaseVO(ActivityBaseV2VO vo) {

        // 检查 activityNameI18nCodeList 不能为空且不能是空列表
        if (vo.getActivityNameI18nCodeList() == null || vo.getActivityNameI18nCodeList().isEmpty()) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        } else {
            // return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动名称-不能为空");
            for (I18nMsgFrontVO frontVO : vo.getActivityNameI18nCodeList()) {
                String msg = frontVO.getMessage();
                if (StringUtils.isBlank(msg) || msg.length() > 100) {
                    return ResponseVO.fail(ResultCode.PARAM_ERROR);
                }
            }
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
        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), vo.getActivityDeadline()) && vo.getActivityEndTime() == null) {
            // return ResponseVO.fail(ResultCode.PARAM_ERROR, "活动结束时间不能为空");
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        //NOTE 只有活动状态非打开才会校验时间，已经开始的活动不校验
        if (!StatusEnum.OPEN.getCode().equals(vo.getStatus()) && vo.getActivityStartTime() < System.currentTimeMillis()) {
            log.info("当前活动开始时间:{},小于现在时间:{},不允许创建", vo.getActivityStartTime(), System.currentTimeMillis());
            throw new BaowangDefaultException(ResultCode.BASE_ERROR_ACTIVITY);
        }
        // 如果是静态活动，且不展示，就不校验showTime
        boolean isStaticNotShowFlag = vo.getActivityTemplate().equals(ActivityTemplateV2Enum.STATIC_V2.getType()) && vo.getShowFlag() == 0;
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
        if (!ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateV2Enum.ASSIGN_DAY_V2.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateV2Enum.NEW_HAND.getType().equals(vo.getActivityTemplate())
                && !ActivityTemplateV2Enum.CONTEST_PAYOUT_V2.getType().equals(vo.getActivityTemplate())) {

            if (vo.getWashRatio() == null || vo.getWashRatio().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("洗码倍率不能小于0");
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


    public List<SiteActivityBaseV2PO> queryActivityBaseList(ActivityBaseVO requestVO) {
        ActivityBaseReqVO reqVO = ActivityBaseReqVO.builder().build();
        BeanUtils.copyProperties(requestVO, reqVO);

        String key = RedisConstants.getToSetSiteCodeKeyConstant(requestVO.getSiteCode(), RedisConstants.ACTIVITY_BASE_V2_LIST);
        List<SiteActivityBaseV2PO> list = RedisUtil.getList(key);
        if (CollectionUtil.isEmpty(list)) {
            list = baseMapper.selectList(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class).eq(SiteActivityBaseV2PO::getSiteCode, requestVO.getSiteCode())
                    .eq(SiteActivityBaseV2PO::getDeleteFlag, DeleteStateEnum.Normal.getType()));
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
            list = list.stream().filter(x -> ObjectUtil.equals(requestVO.getActivityTemplate(), x.getActivityTemplate())).toList();
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

            for (SiteActivityBaseV2PO activity : list) {
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
        SiteActivityBaseV2PO siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getId, id)
                .eq(SiteActivityBaseV2PO::getSiteCode, siteCode));
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
    public void expiredActivity(SiteVO siteVO, ActivityTemplateV2Enum activityTemplateEnum) {
        log.info("执行过期活动定时任务清理:site:{},activity:{}", siteVO, activityTemplateEnum);
        LambdaQueryWrapper<SiteActivityBaseV2PO> baseWrapper = Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteActivityBaseV2PO::getActivityTemplate, activityTemplateEnum.getType())
                .eq(SiteActivityBaseV2PO::getSiteCode, siteVO.getSiteCode())
                .le(SiteActivityBaseV2PO::getActivityEndTime, System.currentTimeMillis());
        List<SiteActivityBaseV2PO> list = baseMapper.selectList(baseWrapper);
        for (SiteActivityBaseV2PO activityBase : list) {
            // todo 添加禁用时间
            int count = baseMapper.update(SiteActivityBaseV2PO.builder().status(EnableStatusEnum.DISABLE.getCode()).xxlJobId(null).forbidTime(System.currentTimeMillis()).build(),
                    Wrappers.lambdaQuery(SiteActivityBaseV2PO.class).eq(SiteActivityBaseV2PO::getId, activityBase.getId())
            );
            log.info("活动过期删除逻辑....,id:{},activityTemplateEnum:{},upActivity:{}", activityBase.getId(), activityBase.getActivityTemplate(), count);
            // 活动过期后 可能还会启用 时间也可能被修改 所以 不能删除job
            /*if (count > 0 && StringUtils.isNotBlank(activityBase.getXxlJobId())) {
                activityJobComponent.remove(List.of(activityBase.getXxlJobId()));
            }*/
            String key = RedisConstants.getToSetSiteCodeKeyConstant(siteVO.getSiteCode(), String.format(RedisConstants.ACTIVITY_CONFIG_V2, activityBase.getId()));
            RedisUtil.deleteKey(key);
        }
    }


    public void updateJobId(String activityId, String xxlJobId) {
        LambdaUpdateWrapper<SiteActivityBaseV2PO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SiteActivityBaseV2PO::getXxlJobId, xxlJobId);
        lambdaUpdateWrapper.eq(SiteActivityBaseV2PO::getId, activityId);
        this.update(lambdaUpdateWrapper);
    }

    public ActivityBaseRespVO queryActivityByActivityNo(String activityNo, String template, String siteCode) {
        SiteActivityBaseV2PO siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getActivityNo, activityNo)
                .eq(SiteActivityBaseV2PO::getActivityTemplate, template)
                .eq(SiteActivityBaseV2PO::getSiteCode, siteCode));
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseRespVO respVO = new ActivityBaseRespVO();
        BeanUtils.copyProperties(siteActivityBasePO, respVO);
        return respVO;
    }

    public ActivityBaseV2AppRespVO recommended() {

        SiteActivityBaseV2PO siteActivityBasePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteActivityBaseV2PO.class)
                .eq(SiteActivityBaseV2PO::getRecommended, true)
                .eq(SiteActivityBaseV2PO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteActivityBaseV2PO::getDeleteFlag, CommonConstant.business_one)
                .eq(SiteActivityBaseV2PO::getStatus, CommonConstant.business_one)
                .last(" limit 1 ")
        );
        if (ObjectUtil.isEmpty(siteActivityBasePO)) {
            return null;
        }
        ActivityBaseV2AppRespVO respVO = new ActivityBaseV2AppRespVO();
        BeanUtils.copyProperties(siteActivityBasePO, respVO);

        String h5DomainUrl = getH5DomainUrl(getH5Domain(), respVO.getActivityTemplate(), respVO.getId());
        respVO.setH5ActivityUrl(h5DomainUrl);

        return respVO;
    }

}