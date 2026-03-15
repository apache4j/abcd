package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.ActivityBaseReqVO;
import com.cloud.baowang.activity.api.vo.category.*;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityLabsPO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.SiteActivityLabsRepository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 站点-活动分类配置相关service
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class SiteActivityLabsService extends ServiceImpl<SiteActivityLabsRepository, SiteActivityLabsPO> {
    private final SiteActivityLabsRepository repository;
    private final I18nApi i18nApi;

    private final SiteActivityBaseRepository siteActivityBaseRepository;
    private final SiteActivityBaseV2Repository siteActivityBaseV2Repository;

    public ResponseVO<Page<SiteActivityLabsVO>> pageQuery(SiteActivityLabPageQueryVO pageQueryVo) {
        try {
            Page<SiteActivityLabsPO> page = new Page<>(pageQueryVo.getPageNumber(), pageQueryVo.getPageSize());
            LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
            Integer state = pageQueryVo.getStatus();
            String siteCode = pageQueryVo.getSiteCode();

            if (StringUtils.isNotBlank(siteCode)) {
                query.eq(SiteActivityLabsPO::getSiteCode, siteCode);
            }
            String labNameI18Code = pageQueryVo.getLabNameI18Code();

            if (StringUtils.isNotBlank(labNameI18Code)) {
                query.eq(SiteActivityLabsPO::getLabNameI18Code, labNameI18Code);
            }
            String creator = pageQueryVo.getCreator();
            if (StringUtils.isNotBlank(creator)) {
                query.eq(SiteActivityLabsPO::getCreator, creator);
            }
            String recentOperator = pageQueryVo.getRecentOperator();
            if (StringUtils.isNotBlank(recentOperator)) {
                query.eq(SiteActivityLabsPO::getUpdater, recentOperator);
            }

            if (state != null) {
                query.eq(SiteActivityLabsPO::getStatus, state);
            }
            if (StrUtil.equals("lastOperationTime", pageQueryVo.getOrderField())) {
                if ("asc".equals(pageQueryVo.getOrderType())) {
                    query.orderByAsc(SiteActivityLabsPO::getUpdatedTime);
                } else if ("desc".equals(pageQueryVo.getOrderType())) {
                    query.orderByDesc(SiteActivityLabsPO::getUpdatedTime);
                }
            }
            if (StrUtil.equals("creationTime", pageQueryVo.getOrderField())) {
                if ("asc".equals(pageQueryVo.getOrderType())) {
                    query.orderByAsc(SiteActivityLabsPO::getCreatedTime);
                } else if ("desc".equals(pageQueryVo.getOrderType())) {
                    query.orderByDesc(SiteActivityLabsPO::getCreatedTime);
                }
            }

            query.orderByDesc(SiteActivityLabsPO::getUpdatedTime);
            page = repository.selectPage(page, query);

            IPage<SiteActivityLabsVO> convert = page.convert(item -> BeanUtil.copyProperties(item, SiteActivityLabsVO.class));
            return ResponseVO.success(ConvertUtil.toConverPage(convert));
        } catch (Exception e) {
            log.error("分页查询分类列表失败，原因:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前站点已配置全部启用状态的活动分类
     *
     * @param siteCode 站点code
     * @return 所有已启用的活动分类
     */
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryList(String siteCode) {
        try {
            LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
            query.eq(SiteActivityLabsPO::getSiteCode, siteCode)
                    .eq(SiteActivityLabsPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            query.orderByAsc(SiteActivityLabsPO::getSort);
            List<SiteActivityLabsPO> pos = repository.selectList(query);
            // 产看各个页签下有无活动
            ActivityBaseReqVO requestVO = new ActivityBaseReqVO();
            requestVO.setSiteCode(siteCode);
            requestVO.setStatus(EnableStatusEnum.ENABLE.getCode());
            requestVO.setShowEndTime(System.currentTimeMillis());
            requestVO.setShowStartTime(System.currentTimeMillis());
            LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = SiteActivityBasePO.getQueryWrapper(requestVO);
            List<SiteActivityBasePO> siteActivityBasePOS = siteActivityBaseRepository.selectList(queryWrapper);
            // 提前提取出所有存在的 labelId，放入 Set 中加快判断效率
            Set<String> validLabelIds = siteActivityBasePOS.stream()
                    .map(SiteActivityBasePO::getLabelId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            // 筛选 pos 中 labelId 存在于 validLabelIds 的记录
            List<SiteActivityLabsPO> list = pos.stream()
                    .filter(e -> validLabelIds.contains(e.getId()))
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(list)) {
                return ResponseVO.success(BeanUtil.copyToList(list, SiteActivityLabsVO.class));
            }
            return ResponseVO.success(new ArrayList<>());
        } catch (Exception e) {
            log.error("获取站点所属活动分类失败:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前站点已配置全部启用状态的活动分类
     *
     * @param siteCode 站点code
     * @return 所有已启用的活动分类
     */
    public ResponseVO<List<SiteActivityLabsVO>> siteQueryListV2(String siteCode) {
        try {
            LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
            query.eq(SiteActivityLabsPO::getSiteCode, siteCode)
                    .eq(SiteActivityLabsPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            query.orderByAsc(SiteActivityLabsPO::getSort);
            List<SiteActivityLabsPO> pos = repository.selectList(query);
            // 产看各个页签下有无活动
            ActivityBaseReqVO requestVO = new ActivityBaseReqVO();
            requestVO.setSiteCode(siteCode);
            requestVO.setStatus(EnableStatusEnum.ENABLE.getCode());
            requestVO.setShowEndTime(System.currentTimeMillis());
            requestVO.setShowStartTime(System.currentTimeMillis());
            LambdaQueryWrapper<SiteActivityBaseV2PO> queryWrapper = SiteActivityBaseV2PO.getQueryWrapper(requestVO);
            List<SiteActivityBaseV2PO> siteActivityBasePOS = siteActivityBaseV2Repository.selectList(queryWrapper);
            // 提前提取出所有存在的 labelId，放入 Set 中加快判断效率
            Set<String> validLabelIds = siteActivityBasePOS.stream()
                    .map(SiteActivityBaseV2PO::getLabelId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet());
            // 筛选 pos 中 labelId 存在于 validLabelIds 的记录
            List<SiteActivityLabsPO> list = pos.stream()
                    .filter(e -> validLabelIds.contains(e.getId()))
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(list)) {
                return ResponseVO.success(BeanUtil.copyToList(list, SiteActivityLabsVO.class));
            }
            return ResponseVO.success(new ArrayList<>());
        } catch (Exception e) {
            log.error("获取站点所属活动V2分类失败:{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void validateI18nDuplicate(SiteActivityLabRequestVO requestVo,String id) {
        // 查询表该站点下所有的中文code
        // 查询该站点下所有标签的国际化 code
        LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityLabsPO::getSiteCode, requestVo.getSiteCode());
        query.ne(ObjectUtil.isNotNull(id),SiteActivityLabsPO::getId, id);
        List<SiteActivityLabsPO> list = repository.selectList(query);
        // 创建的进行校验，修改不校验
        if (CollectionUtil.isNotEmpty(list)  && ObjectUtil.isNull(id)) {
            if(list.size() > 9){
                throw new BaowangDefaultException(ResultCode.NAME_ALREADY_RECORD_OVER_10);
            }
        }
        // 提取 i18n code 列表
        List<String> labNameI18CodeList = list.stream()
                .map(SiteActivityLabsPO::getLabNameI18Code)
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(labNameI18CodeList)) {
            // 获取已有标签的中文翻译
            ResponseVO<Map<String, String>> messageInKey = i18nApi.getMessageInKey(labNameI18CodeList, LanguageEnum.ZH_CN.getLang());
            if (messageInKey.isOk()) {
                Map<String, String> data = messageInKey.getData();

                // 获取当前请求的中文名称
                Optional<I18nMsgFrontVO> zhCnOpt = requestVo.getLabNameI18List().stream()
                        .filter(e -> StrUtil.equals(e.getLanguage(), LanguageEnum.ZH_CN.getLang()))
                        .findFirst();

                if (zhCnOpt.isPresent()) {
                    String name = zhCnOpt.get().getMessage();

                    // 判断已有中文名称中是否存在同名
                    boolean duplicate = data.values().stream().anyMatch(e -> StrUtil.equals(e, name));
                    if (duplicate) {
                        throw new BaowangDefaultException(ResultCode.NAME_ALREADY_RECORD);
                    }
                }
            }
        }

    }

    /**
     * 新增活动页签
     *
     * @param requestVo
     * @param creator
     * @return
     */
    public ResponseVO<Boolean> addActivityLab(SiteActivityLabRequestVO requestVo, String creator) {
        // 校验中文是否重复
        validateI18nDuplicate(requestVo,null);
        //i18nApi.getMessageInKey()
        String i18nDynamicKey = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_LAB_NAME.getCode());
        // 插入国际化信息(更新方法是删除后新增，这里每次直接调用update吧)
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                i18nDynamicKey, requestVo.getLabNameI18List());
        i18nApi.update(i18nData);

        SiteActivityLabsPO po = new SiteActivityLabsPO();
        BeanUtil.copyProperties(requestVo, po);
        po.setLabNameI18Code(i18nDynamicKey);
        po.setCreator(creator);
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdater(creator);
        po.setUpdatedTime(System.currentTimeMillis());
        po.setStatus(EnableStatusEnum.DISABLE.getCode());
        repository.insert(po);
        return ResponseVO.success();
    }

    /**
     * 修改活动页签
     *
     * @param requestVo
     * @param creator
     * @return
     */
    public ResponseVO<Boolean> updActivityLab(SiteActivityLabRequestVO requestVo, String creator) {
        //
        validateI18nDuplicate(requestVo,requestVo.getId());
        // 插入国际化信息(更新方法是删除后新增，这里每次直接调用update吧)
        String labNameI18Code = requestVo.getLabNameI18Code();
        if (!labNameI18Code.contains("BIZ_ACTIVITY_LAB_NAME_")) {
            String i18nDynamicKey = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_LAB_NAME.getCode());
            requestVo.setLabNameI18Code(i18nDynamicKey);
        }
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                requestVo.getLabNameI18Code(), requestVo.getLabNameI18List());
        i18nApi.update(i18nData);
        SiteActivityLabsPO po = BeanUtil.copyProperties(requestVo, SiteActivityLabsPO.class);
        po.setUpdater(creator);
        po.setUpdatedTime(System.currentTimeMillis());
        this.updateById(po);
        return ResponseVO.success();
    }

    /**
     * 删除
     */
    public ResponseVO<Boolean> deleteById(Long id) {
        // 校验是否可以删除
        LambdaQueryWrapper<SiteActivityBasePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityBasePO::getLabelId, id);
       // query.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        query.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        Long count = siteActivityBaseRepository.selectCount(query);
        if (count > 0) {
            return ResponseVO.fail(ResultCode.PLEASE_REMOVE_ACTIVITY);
        }
        this.removeById(id);
        return ResponseVO.success();
    }

    public ResponseVO<SiteActivityLabsVO> detail(String id, String siteCode) {
        LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityLabsPO::getId, id).eq(SiteActivityLabsPO::getSiteCode, siteCode);
        SiteActivityLabsPO po = repository.selectOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SiteActivityLabsVO vo = BeanUtil.copyProperties(po, SiteActivityLabsVO.class);
        vo.setLabName(po.getLabNameI18Code());
        return ResponseVO.success(vo);
    }

    public ResponseVO<Boolean> enDisAbleLab(String id, Integer status) {
        if (!EnableStatusEnum.DISABLE.getCode().equals(status) &&
                !EnableStatusEnum.ENABLE.getCode().equals(status)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        SiteActivityLabsPO po = repository.selectById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        po.setStatus(status);
        po.setUpdatedTime(System.currentTimeMillis());
        repository.updateById(po);
        return ResponseVO.success();
    }

    public List<CodeValueVO> getLabNameList(String siteCode) {
        LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityLabsPO::getSiteCode, siteCode);
        List<SiteActivityLabsPO> siteActivityLabsPOS = repository.selectList(query);
        if (CollectionUtils.isEmpty(siteActivityLabsPOS)) {
            return Lists.newArrayList();
        }
        List<CodeValueVO> resultList = Lists.newArrayList();
        for (SiteActivityLabsPO siteActivityLabsPO : siteActivityLabsPOS) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(siteActivityLabsPO.getId());
            codeValueVO.setValue(siteActivityLabsPO.getLabNameI18Code());
            codeValueVO.setType(siteActivityLabsPO.getLabNameI18Code());
            resultList.add(codeValueVO);
        }
        return resultList;
    }

    public ResponseVO<List<AddActivityLabelSortVO>> getSort(String siteCode) {
        LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityLabsPO::getSiteCode, siteCode);
        query.orderByAsc(SiteActivityLabsPO::getSort);
        List<SiteActivityLabsPO> list = this.list(query);
        return ResponseVO.success(BeanUtil.copyToList(list, AddActivityLabelSortVO.class));
    }

    @Transactional
    public ResponseVO<Boolean> addSort(List<AddActivityLabelSortVO> labelSortVOS) {
        List<String> labelIds = labelSortVOS.stream().map(AddActivityLabelSortVO::getId).toList();
        LambdaQueryWrapper<SiteActivityLabsPO> query = Wrappers.lambdaQuery();
        query.in(SiteActivityLabsPO::getId, labelIds);
        List<SiteActivityLabsPO> list = this.list(query);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, Integer> sortMap = labelSortVOS.stream().collect(Collectors.toMap(AddActivityLabelSortVO::getId, AddActivityLabelSortVO::getSort));
            for (SiteActivityLabsPO siteActivityLabsPO : list) {
                if (sortMap.containsKey(siteActivityLabsPO.getId())) {
                    siteActivityLabsPO.setSort(sortMap.get(siteActivityLabsPO.getId()));
                }
            }
        }
        this.updateBatchById(list);
        return ResponseVO.success();
    }
}
