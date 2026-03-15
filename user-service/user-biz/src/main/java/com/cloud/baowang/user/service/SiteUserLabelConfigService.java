package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.userlabel.GetAllUserLabelVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserLabelByIdsVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserPageByLabelIdRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserPageByLabelIdVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelAddRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageResponseVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelDelRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelEditRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelIdReqVO;
import com.cloud.baowang.user.po.SiteUserLabelConfigPO;
import com.cloud.baowang.user.po.SiteUserLabelConfigRecordPO;
import com.cloud.baowang.user.po.UserAccountUpdateReviewPO;
import com.cloud.baowang.user.repositories.SiteUserLabelConfigRepository;
import com.cloud.baowang.user.repositories.UserAccountUpdateReviewRepository;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 会员标签配置 服务类
 *
 * @author wade
 * @since 2023-05-04 10:00:00
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SiteUserLabelConfigService extends ServiceImpl<SiteUserLabelConfigRepository, SiteUserLabelConfigPO> {
    private final SiteUserLabelConfigRepository siteUserLabelConfigRepository;
    private final SystemParamApi systemParamApi;
    private final SiteUserLabelConfigRecordService siteUserLabelConfigRecordService;
    private final UserInfoRepository userInfoRepository;
    private final UserInfoService userInfoService;
    private final UserAccountUpdateReviewRepository userAccountUpdateReviewRepository;


    public List<GetAllUserLabelVO> getAllUserLabel(String siteCode) {
        List<SiteUserLabelConfigPO> list = this.lambdaQuery()
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .eq(SiteUserLabelConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .list();
        return ConvertUtil.entityListToModelList(list, GetAllUserLabelVO.class);
    }

    public List<GetAllUserLabelVO> getAllUserLabelBySiteCode(String siteCode) {
        List<SiteUserLabelConfigPO> list = this.lambdaQuery()
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .eq(SiteUserLabelConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteUserLabelConfigPO::getDeleted, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteUserLabelConfigPO::getSiteCode, siteCode)
                .list();
        return ConvertUtil.entityListToModelList(list, GetAllUserLabelVO.class);
    }

    public Boolean existsLabelId(String ids, String labelId) {
        return this.lambdaQuery()
                .in(SiteUserLabelConfigPO::getId, ids)
                .eq(SiteUserLabelConfigPO::getLabelId,labelId)
                .count()>=1?Boolean.TRUE:Boolean.FALSE;
    }

    /**
     * 使用的，去掉禁止的
     *
     * @param siteCode
     * @return
     */
    public List<GetAllUserLabelVO> getAllEnableUserLabelBySiteCode(String siteCode) {
        List<SiteUserLabelConfigPO> list = this.lambdaQuery()
                .eq(SiteUserLabelConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteUserLabelConfigPO::getDeleted, EnableStatusEnum.ENABLE.getCode())
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .list();
        return ConvertUtil.entityListToModelList(list, GetAllUserLabelVO.class);
    }

    /**
     * 查询的历史，包括被删除与禁用的，也包括官方的
     *
     * @param siteCode
     * @return
     */
    public List<GetAllUserLabelVO> getAllEnableUserLabelBySiteCodeForHis(String siteCode) {
        List<SiteUserLabelConfigPO> list = this.lambdaQuery()
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .list();
        return ConvertUtil.entityListToModelList(list, GetAllUserLabelVO.class);
    }


    public GetAllUserLabelVO getByLabelId(IdVO idVO) {
        return ConvertUtil.entityToModel(this.getAllUserLabel(idVO.getSiteCode()), GetAllUserLabelVO.class);
    }


    public GetUserLabelByIdsVO getInfoById(Long id) {
        SiteUserLabelConfigPO siteUserLabelConfigPO = this.getById(id);
        if (null == siteUserLabelConfigPO ) {
            return null;
        }
        GetUserLabelByIdsVO getUserLabelByIdsVO=new GetUserLabelByIdsVO();
        BeanUtils.copyProperties(siteUserLabelConfigPO,getUserLabelByIdsVO);
        return getUserLabelByIdsVO;
    }


    public SiteUserLabelConfigPO getByLabelName(String labelName, String siteCode) {
        return this.lambdaQuery()
                .eq(SiteUserLabelConfigPO::getLabelName, labelName)
                .eq(SiteUserLabelConfigPO::getStatus, CommonConstant.business_one)
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .eq(SiteUserLabelConfigPO::getSiteCode, CurrReqUtils.getSiteCode())
                .one();
    }


    public List<GetUserLabelByIdsVO> getUserLabelByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        List<SiteUserLabelConfigPO> list = this.lambdaQuery()
                .in(SiteUserLabelConfigPO::getId, ids)
                .list();
        return ConvertUtil.entityListToModelList(list, GetUserLabelByIdsVO.class);
    }

    public SiteUserLabelConfigPO getUserLabelById(String id) {
        SiteUserLabelConfigPO byId = this.getById(id);
        if (null == byId || CommonConstant.business_zero.equals(byId.getStatus())) {
            return null;
        }
        return byId;
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> addLabel(UserLabelAddRequestVO vo) {
        String labelName = vo.getLabelName();
        String labelDescribe = vo.getLabelDescribe();

        // 标签名称 是否已经存在
        if (ObjectUtils.isNotNull(this.getByLabelName(labelName, vo.getSiteCode()))) {
            return ResponseVO.fail(ResultCode.LABEL_ERROR);
        }
        // 保存到会员标签配置表
        SiteUserLabelConfigPO siteUserLabelConfigPO = new SiteUserLabelConfigPO();
        siteUserLabelConfigPO.setLabelName(labelName);
        siteUserLabelConfigPO.setLabelDescribe(labelDescribe);
        siteUserLabelConfigPO.setStatus(Integer.valueOf(vo.getStatus()));
        siteUserLabelConfigPO.setColor(vo.getColor());
        siteUserLabelConfigPO.setCreateName(vo.getOperator());
        siteUserLabelConfigPO.setCustomizeStatus(Integer.parseInt(YesOrNoEnum.NO.getCode()));
        siteUserLabelConfigPO.setLastOperator(vo.getOperator());
        siteUserLabelConfigPO.setSiteCode(vo.getSiteCode());

        // labelId 自增
        SiteUserLabelConfigPO maxLabelId = siteUserLabelConfigRepository.selectOne(Wrappers.<SiteUserLabelConfigPO>lambdaQuery()
                .select(SiteUserLabelConfigPO::getLabelId)
                .eq(SiteUserLabelConfigPO::getSiteCode, vo.getSiteCode())
                .orderByDesc(SiteUserLabelConfigPO::getLabelId).last("limit 1"));
        String labelId = "0";
        if (maxLabelId != null) {
            labelId = maxLabelId.getLabelId();
        }
        labelId = String.format("%06d", Integer.parseInt(labelId) + 1);

        siteUserLabelConfigPO.setLabelId(labelId);
        this.save(siteUserLabelConfigPO);

        // 标签名称记录
        SiteUserLabelConfigRecordPO siteUserLabelConfigRecordPO = new SiteUserLabelConfigRecordPO();
        siteUserLabelConfigRecordPO.setSiteUserLabelConfigId(siteUserLabelConfigPO.getId());
        siteUserLabelConfigRecordPO.setLabelName(labelName);
        siteUserLabelConfigRecordPO.setLastOperator(vo.getOperator());
        siteUserLabelConfigRecordPO.setAfterChange(StrUtil.isEmpty(labelName) ? null : labelName);
        // 获取所有请求
        List<CodeValueVO> respDTOS = systemParamApi.getSystemParamByType(CommonConstant.USER_LABEL_CHANGE_TYPE).getData();
        siteUserLabelConfigRecordPO.setChangeType(respDTOS.get(0).getCode());
        siteUserLabelConfigRecordPO.setSiteCode(vo.getSiteCode());
        siteUserLabelConfigRecordService.save(siteUserLabelConfigRecordPO);

        // 标签描述记录
        SiteUserLabelConfigRecordPO userLabelConfigRecord = new SiteUserLabelConfigRecordPO();
        userLabelConfigRecord.setSiteUserLabelConfigId(siteUserLabelConfigPO.getId());
        userLabelConfigRecord.setLabelName(labelName);
        userLabelConfigRecord.setLastOperator(vo.getOperator());
        userLabelConfigRecord.setAfterChange(StrUtil.isEmpty(siteUserLabelConfigPO.getLabelDescribe()) ? null : siteUserLabelConfigPO.getLabelDescribe());
        userLabelConfigRecord.setChangeType(respDTOS.get(1).getCode());
        userLabelConfigRecord.setSiteCode(vo.getSiteCode());
        siteUserLabelConfigRecordService.save(userLabelConfigRecord);

        return ResponseVO.success();
    }


    public Page<UserLabelConfigPageResponseVO> getLabelConfigPage(UserLabelConfigPageRequestVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        Page<UserLabelConfigPageResponseVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserLabelConfigPageResponseVO> result = siteUserLabelConfigRepository.getLabelConfigPage(page, vo, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE));
        // 获取所查询id
        List<String> userLabelIds = result.getRecords().stream().map(UserLabelConfigPageResponseVO::getId).toList();
        List<UserLabelConfigVO> userLabelConfigVOS = new ArrayList<>(16);
        Map<String, Long> labelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userLabelIds)) {
            userLabelConfigVOS = userInfoRepository.getUserLabelConfigListList(userLabelIds, siteCode);
            if (CollectionUtil.isNotEmpty(userLabelConfigVOS)) {
                List<UserLabelConfigVO> userLabelConfigs = Lists.newArrayList();
                for (UserLabelConfigVO userLabelConfigVO : userLabelConfigVOS) {
                    String[] split = userLabelConfigVO.getUserLabelId().split(CommonConstant.COMMA);
                    if (split.length == 1) {
                        userLabelConfigs.add(userLabelConfigVO);
                        continue;
                    }
                    for (String s : split) {
                        UserLabelConfigVO userLabelConfig = new UserLabelConfigVO();
                        userLabelConfig.setUserLabelId(s);
                        userLabelConfig.setUserAccount(userLabelConfigVO.getUserAccount());
                        userLabelConfigs.add(userLabelConfig);
                    }
                }
                labelMap = userLabelConfigs.stream().collect(Collectors.groupingBy(UserLabelConfigVO::getUserLabelId, Collectors.counting()));
            }
        }
        Map<String, Long> finalLabelMap = labelMap;
        result.getRecords().forEach(e -> {
            e.setLabelCount(String.valueOf(finalLabelMap.getOrDefault(e.getId(), 0L)));
        });

        return result;
    }


    public ResponseVO<?> editLabel(UserLabelEditRequestVO vo) {
        String labelName = vo.getLabelName();
        String labelDescribe = vo.getLabelDescribe();
        String status = vo.getStatus();
        String color = vo.getColor();
        String id = vo.getId();

        // 标签配置记录
        SiteUserLabelConfigPO siteUserLabelConfigPO = this.getById(id);
        // 检验标签是否是定制，如果是定制，则不允许修改
        if (ObjectUtils.isNotNull(this.getByLabelName(labelName, vo.getSiteCode())) && Objects.equals(siteUserLabelConfigPO.getCustomizeStatus(), Integer.parseInt(YesOrNoEnum.YES.getCode()))) {
            return ResponseVO.fail(ResultCode.LABEL_EDIT_ERROR);
        }
        // 标签名称 是否已经存在
        if (ObjectUtils.isNotNull(this.getByLabelName(labelName, vo.getSiteCode())) && !Objects.equals(siteUserLabelConfigPO.getLabelName(), labelName)) {
            return ResponseVO.fail(ResultCode.LABEL_ERROR);
        }

        LambdaUpdateWrapper<SiteUserLabelConfigPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(SiteUserLabelConfigPO::getId, id)
                .set(SiteUserLabelConfigPO::getLabelName, labelName)
                .set(SiteUserLabelConfigPO::getLabelDescribe, labelDescribe)
                .set(SiteUserLabelConfigPO::getLastOperator, vo.getOperator())
                .set(SiteUserLabelConfigPO::getUpdater, vo.getOperator())
                .set(SiteUserLabelConfigPO::getStatus, status)
                .set(SiteUserLabelConfigPO::getColor, color)
                .set(SiteUserLabelConfigPO::getUpdatedTime, System.currentTimeMillis());
        //更新标签配置表
        this.update(null, lambdaUpdateWrapper);
        // 标签停用，清除用户标签信息
        if (status.equals(CommonConstant.business_zero.toString())) {
            List<String> userIds = userInfoRepository.getAllUserIdByLabelConfig(vo.getSiteCode(), id);
            if (CollectionUtil.isNotEmpty(userIds)) {
                userInfoService.deleteUserLabel(vo.getSiteCode(), userIds, id);
            }
        }

        if (!Objects.equals(labelName, siteUserLabelConfigPO.getLabelName())) {
            SiteUserLabelConfigRecordPO siteUserLabelConfigRecordPO = new SiteUserLabelConfigRecordPO();
            siteUserLabelConfigRecordPO.setSiteUserLabelConfigId(id);
            siteUserLabelConfigRecordPO.setLabelName(labelName);
            siteUserLabelConfigRecordPO.setLastOperator(vo.getOperator());

            siteUserLabelConfigRecordPO.setBeforeChange(siteUserLabelConfigPO.getLabelName());
            siteUserLabelConfigRecordPO.setAfterChange(labelName);
            siteUserLabelConfigRecordPO.setChangeType(this.systemParamApi.getSystemParamByType(CommonConstant.USER_LABEL_CHANGE_TYPE).getData().get(0).getCode());
            siteUserLabelConfigRecordPO.setSiteCode(vo.getSiteCode());
            siteUserLabelConfigRecordService.save(siteUserLabelConfigRecordPO);
        }
        if (!Objects.equals(labelDescribe, siteUserLabelConfigPO.getLabelDescribe())) {
            SiteUserLabelConfigRecordPO siteUserLabelConfigRecordPO = new SiteUserLabelConfigRecordPO();
            siteUserLabelConfigRecordPO.setSiteUserLabelConfigId(id);
            siteUserLabelConfigRecordPO.setLabelName(labelName);
            siteUserLabelConfigRecordPO.setLastOperator(vo.getOperator());

            siteUserLabelConfigRecordPO.setBeforeChange(siteUserLabelConfigPO.getLabelDescribe());
            siteUserLabelConfigRecordPO.setAfterChange(labelDescribe);
            siteUserLabelConfigRecordPO.setSiteCode(vo.getSiteCode());
            siteUserLabelConfigRecordPO.setChangeType(this.systemParamApi.getSystemParamByType(CommonConstant.USER_LABEL_CHANGE_TYPE).getData().get(1).getCode());
            siteUserLabelConfigRecordService.save(siteUserLabelConfigRecordPO);
        }
        return ResponseVO.success();
    }


    public ResponseVO<?> delLabel(UserLabelDelRequestVO vo) {
        String siteCode = vo.getSiteCode();
        List<String> userIds = userInfoRepository.getAllUserIdByLabelConfig(siteCode, vo.getId());
        if (CollectionUtil.isNotEmpty(userIds)) {
            userInfoService.deleteUserLabel(siteCode, userIds, vo.getId());
        }


        Long pendingCount = userAccountUpdateReviewRepository
                .selectCount(new LambdaQueryWrapper<UserAccountUpdateReviewPO>()
                        .eq(UserAccountUpdateReviewPO::getReviewApplicationType, CommonConstant.business_three)
                        .eq(UserAccountUpdateReviewPO::getReviewOperation, CommonConstant.business_one)
                        .eq(UserAccountUpdateReviewPO::getAfterModification, vo.getId()));
        if (pendingCount > 0) {
            return ResponseVO.fail(ResultCode.USER_LABEL_DEL_ERROR);
        }

        SiteUserLabelConfigPO byId = this.getById(vo.getId());
        // 检验标签是否是定制，如果是定制，则不允许修改
        if (ObjectUtils.isNotNull(byId.getLabelName()) && Objects.equals(byId.getCustomizeStatus(), CommonConstant.business_one)) {
            return ResponseVO.fail(ResultCode.LABEL_EDIT_ERROR);
        }
        byId.setDeleted(EnableStatusEnum.DISABLE.getCode());
        byId.setUpdater(vo.getOperator());
        byId.setUpdatedTime(System.currentTimeMillis());
        byId.setLastOperator(vo.getOperator());
        this.updateById(byId);

        SiteUserLabelConfigRecordPO siteUserLabelConfigRecordPO = new SiteUserLabelConfigRecordPO();
        siteUserLabelConfigRecordPO.setSiteUserLabelConfigId(vo.getId());
        siteUserLabelConfigRecordPO.setLabelName(byId.getLabelName());
        siteUserLabelConfigRecordPO.setCreator(vo.getOperator());
        siteUserLabelConfigRecordPO.setUpdater(vo.getOperator());
        siteUserLabelConfigRecordPO.setLastOperator(vo.getOperator());
        siteUserLabelConfigRecordPO.setCreatedTime(System.currentTimeMillis());
        siteUserLabelConfigRecordPO.setUpdatedTime(System.currentTimeMillis());
        siteUserLabelConfigRecordPO.setSiteCode(siteCode);
        siteUserLabelConfigRecordPO.setChangeType(this.systemParamApi.getSystemParamByType(CommonConstant.USER_LABEL_CHANGE_TYPE).getData().get(2).getCode());
        siteUserLabelConfigRecordService.save(siteUserLabelConfigRecordPO);

        return ResponseVO.success();
    }


    public Page<GetUserPageByLabelIdVO> getUserPageByLabelId(GetUserPageByLabelIdRequestVO vo) {
        Page<UserLabelConfigVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserLabelConfigVO> pageList = userInfoRepository.getUserLabelConfig(page, vo.getLabelId(), vo.getSiteCode());
        Map<String, String> accountTypeNameMap = systemParamApi.getSystemParamMap(CommonConstant.USER_ACCOUNT_TYPE).getData();
        List<GetUserPageByLabelIdVO> result = pageList.getRecords().stream().map(item -> {
            GetUserPageByLabelIdVO data = ConvertUtil.entityToModel(item, GetUserPageByLabelIdVO.class);
            // 账号类型
            if (data != null && StrUtil.isNotEmpty(data.getAccountType())) {
                String accountTypeName = accountTypeNameMap.get(data.getAccountType());
                data.setAccountType(accountTypeName);
            }
            return data;
        }).toList();

        Page<GetUserPageByLabelIdVO> pageResult = new Page<>(vo.getPageNumber(), vo.getPageSize(), page.getTotal());
        pageResult.setRecords(result);

        return pageResult;
    }

    public ResponseVO<Long> getTotalCount(UserLabelConfigPageRequestVO reqVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        Long count = siteUserLabelConfigRepository.selectCount(Wrappers.<SiteUserLabelConfigPO>lambdaQuery()
                .in(SiteUserLabelConfigPO::getSiteCode, Lists.newArrayList(siteCode, CommonConstant.ADMIN_CENTER_SITE_CODE))
                .eq(StringUtils.isNotBlank(reqVO.getStatus()), SiteUserLabelConfigPO::getStatus, reqVO.getStatus())
                .eq(StringUtils.isNoneBlank(reqVO.getLabelId()), SiteUserLabelConfigPO::getLabelId, reqVO.getLabelId())
                .eq(StringUtils.isNoneBlank(reqVO.getLabelName()), SiteUserLabelConfigPO::getLabelName, reqVO.getLabelName())
                .eq(SiteUserLabelConfigPO::getDeleted, EnableStatusEnum.ENABLE.getCode()));
        return ResponseVO.success(count);
    }

    public List<GetUserLabelByIdsVO> getUserLabel(UserLabelIdReqVO userLabelIdReqVO) {
        if (StringUtils.isBlank(userLabelIdReqVO.getLabelIds())) {
            GetUserLabelByIdsVO getUserLabelByIdsVO = new GetUserLabelByIdsVO();
            return Lists.newArrayList(getUserLabelByIdsVO);
        }
        if (StringUtils.isBlank(userLabelIdReqVO.getSiteCode())) {
            GetUserLabelByIdsVO getUserLabelByIdsVO = new GetUserLabelByIdsVO();
            return Lists.newArrayList(getUserLabelByIdsVO);
        }
        List<String> ids = Arrays.asList(userLabelIdReqVO.getLabelIds().split(CommonConstant.COMMA));
        LambdaQueryWrapper<SiteUserLabelConfigPO> query = Wrappers.lambdaQuery();
        query.in(SiteUserLabelConfigPO::getSiteCode, List.of(userLabelIdReqVO.getSiteCode(), CommonConstant.ADMIN_CENTER_SITE_CODE)).in(SiteUserLabelConfigPO::getId, ids);
        List<SiteUserLabelConfigPO> list = this.list(query);
        return BeanUtil.copyToList(list, GetUserLabelByIdsVO.class);
    }


}
