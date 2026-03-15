package com.cloud.baowang.system.service.tutorial;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsRspVO;
import com.cloud.baowang.system.po.tutorial.*;
import com.cloud.baowang.system.repositories.site.tutorial.TutorialTabsRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.cloud.baowang.system.util.TutorialOperationRecordUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TutorialTabsService extends ServiceImpl<TutorialTabsRepository, TutorialTabsPO> {


    private final MinioFileService fileService;

    private final TutorialTabsRepository repository;

    private final TutorialOperationRecordService operationRecordService;

    private final TutorialContentService tutorialContentService;

    private final I18nApi i18nApi;

    public TutorialCategoryService getCategoryService() {
        return SpringUtils.getBean(TutorialCategoryService.class);
    }

    public TutorialClassService getClassService() {
        return SpringUtils.getBean(TutorialClassService.class);
    }

    public Page<TutorialTabsRspVO> listPage(TutorialTabsResVO vo) {

        Page<TutorialTabsPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<TutorialTabsPO> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(vo.getSiteCode()), TutorialTabsPO::getSiteCode, vo.getSiteCode());
        query.eq(StringUtils.isNotBlank(vo.getCategoryId()), TutorialTabsPO::getCategoryId, vo.getCategoryId());
        query.eq(StringUtils.isNotBlank(vo.getClassId()), TutorialTabsPO::getClassId, vo.getClassId());
        query.eq(StringUtils.isNotBlank(vo.getTabsId()), TutorialTabsPO::getId, vo.getTabsId());
        if (vo.getStatus() != null) {
            query.eq(TutorialTabsPO::getStatus, vo.getStatus());
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && "createTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialTabsPO::getCreateTime);
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && "updateTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialTabsPO::getUpdateTime);
        }else {
            //query.orderByDesc(TutorialTabsPO::getStatus);
            query.orderByDesc(TutorialTabsPO::getUpdateTime);
        }
        page = repository.selectPage(page, query);

        return ConvertUtil.toConverPage(page.convert(item -> {
            TutorialTabsRspVO rspVO = BeanUtil.copyProperties(item, TutorialTabsRspVO.class);
            rspVO.setNameCn(item.getNameCn());
            List<I18NMessageDTO> i18nData = i18nApi.getMessageByKey(item.getNameCn()).getData();
            rspVO.setI18nMessages(i18nData);
            return rspVO;
        }));
    }

    /**
     * 获取教程页签下拉列表
     *
     * @return
     */
    public List<CodeValueVO> getTabsDownBox(TutorialDownBoxResVo resVo) {
        LambdaQueryWrapper<TutorialTabsPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialTabsPO::getSiteCode, resVo.getSiteCode());
        query.eq(StringUtils.isNotBlank(resVo.getCategoryId()), TutorialTabsPO::getCategoryId, resVo.getCategoryId());
        query.eq(StringUtils.isNotBlank(resVo.getClassId()), TutorialTabsPO::getClassId, resVo.getClassId());

        if (resVo.getPosition() == CommonConstant.business_one) {
            query.eq(TutorialTabsPO::getStatus, resVo.getPosition());
        }
        query.orderByAsc(TutorialTabsPO::getSort);
        List<TutorialTabsPO> resTemp = repository.selectList(query);
        List<CodeValueVO> result = new ArrayList<>();
        if (!resTemp.isEmpty()) {
            resTemp.forEach(item -> {
                result.add(CodeValueVO.builder().code(item.getId()).value(item.getNameCn()).type(item.getNameCn()).build());
            });
        }
        return result;

    }

    /**
     * \
     * 排序
     *
     * @param sourceList
     * @param siteCode
     * @return
     */
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        List<TutorialTabsPO> targetList = Lists.newArrayList();
        for (int i = 0; i < sourceList.size(); i++) {
            String tabsId = sourceList.get(i).getId();
            LambdaQueryWrapper<TutorialTabsPO> query = new LambdaQueryWrapper<>();
            query.eq(TutorialTabsPO::getSiteCode, siteCode);
            query.eq(TutorialTabsPO::getId, tabsId);
            TutorialTabsPO tabsPO = repository.selectOne(query);
            if (tabsPO == null) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            tabsPO.setSort(i + 1);
            tabsPO.setOperator(CurrReqUtils.getAccount());
            tabsPO.setUpdateTime(System.currentTimeMillis());
            targetList.add(tabsPO);
        }
        this.updateBatchById(targetList);
        return ResponseVO.success(true);
    }

    public boolean add(TutorialTabsAddVO vo) {

        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        TutorialClassPO classPO = getClassService().getById(vo.getClassId());
        if (categoryPO == null || classPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (!classPO.getCategoryId().equals(String.valueOf(vo.getCategoryId()))) {
            throw new BaowangDefaultException(ResultCode.CLASS_NOT_BELONG_CATEGORY);
        }
        LambdaQueryWrapper<TutorialTabsPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialTabsPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialTabsPO::getSiteCode, vo.getSiteCode());
        query.eq(TutorialTabsPO::getClassId, vo.getClassId());
        List<TutorialTabsPO> classPOList = repository.selectList(query);
        List<String> tabsNameList = classPOList.stream().map(TutorialTabsPO::getNameCn).toList();
        if (!tabsNameList.isEmpty()) {
            List<I18NMessageDTO> i18nClassData = i18nApi.getMessageByKeyList(tabsNameList).getData();
            checkNameRules(vo, false, i18nClassData);
        }
        String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_TABS);
        String tabsI18nName = RedisKeyTransUtil.getI18nDynamicKey(i18nMessageKey);
        i18nApi.insert(Map.of(tabsI18nName, vo.getI18nMessages()));

        TutorialTabsPO po = BeanUtil.copyProperties(vo, TutorialTabsPO.class);
        long cTime = System.currentTimeMillis();
        po.setStatus(CommonConstant.business_zero);
        po.setCreator(CurrReqUtils.getAccount());
        po.setOperator(CurrReqUtils.getAccount());
        po.setCreateTime(cTime);
        po.setUpdateTime(cTime);
        po.setNameCn(tabsI18nName);
        po.setCategoryId(vo.getCategoryId());
        po.setCategoryName(categoryPO.getNameCn());
        po.setClassId(vo.getClassId());
        po.setClassName(classPO.getNameCn());
        repository.insert(po);
        return true;
    }

    public boolean edit(TutorialTabsAddVO vo) {
        TutorialTabsPO oldPO = repository.selectById(vo.getId());
        if (oldPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        TutorialClassPO classPO = getClassService().getById(vo.getClassId());
        if (categoryPO == null || classPO == null ) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (!classPO.getCategoryId().equals(String.valueOf(vo.getCategoryId()))) {
            throw new BaowangDefaultException(ResultCode.CLASS_NOT_BELONG_CATEGORY);
        }
        LambdaQueryWrapper<TutorialTabsPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialTabsPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialTabsPO::getSiteCode, vo.getSiteCode());
        query.ne(TutorialTabsPO::getId, vo.getId());
        List<TutorialTabsPO> classPOList = repository.selectList(query);
        if (!classPOList.isEmpty()){
            List<String> classNameList = classPOList.stream().map(TutorialTabsPO::getNameCn).toList();
            List<I18NMessageDTO> oldI18nList = i18nApi.getMessageByKeyList(classNameList).getData();
            checkNameRules(vo, true, oldI18nList);
        }

        List<I18nMsgFrontVO> i18nNewList = vo.getI18nMessages();
        List<I18NMessageDTO> oldI18nVo = i18nApi.getMessageByKey(oldPO.getNameCn()).getData();

        long cTime = System.currentTimeMillis();
        TutorialTabsPO po = BeanUtil.copyProperties(vo, TutorialTabsPO.class);
        po.setOperator(CurrReqUtils.getAccount());
        po.setUpdateTime(cTime);
        po.setCategoryId(vo.getCategoryId());
        po.setCategoryName(categoryPO.getNameCn());
        po.setClassId(vo.getClassId());
        po.setClassName(classPO.getNameCn());
        repository.updateById(po);
        //变更记录
        List<TutorialOperationRecordPO> recordList = Lists.newArrayList();
        for (I18nMsgFrontVO i18nVo : i18nNewList) {
            for (I18NMessageDTO i18nDto : oldI18nVo) {
                if (i18nVo.getLanguage().equals(i18nDto.getLanguage())) {
                    if (!i18nVo.getMessage().equals(i18nDto.getMessage())) {
                        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                                .buildOperationRecord(vo.getSiteCode(), null, i18nDto.getMessage(), i18nVo.getMessage(), 1, 1, cTime, null,2);
                        recordList.add(recordPO);
                    }
                }
            }
        }
        //更新i18n_message
        i18nApi.update(Map.of(oldPO.getNameCn(), vo.getI18nMessages()));

        operationRecordService.asyncAddChangeRecord(recordList);
        return true;
    }


    public Boolean enableAndDisAble(TutorialTabsRspVO vo) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus()) && !EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus())) {
            //查询大类,分类是否启用
            if (!getClassService().checkClassStatus(vo.getClassId())) {
                throw new BaowangDefaultException(ResultCode.CLASS_IS_DISABLED);
            }
            if (!getCategoryService().checkCategoryStatus(vo.getCategoryId())) {
                throw new BaowangDefaultException(ResultCode.CATEGORY_IS_DISABLED);
            }
        }
        TutorialTabsPO po = BeanUtil.copyProperties(vo, TutorialTabsPO.class);
        long updateTime = System.currentTimeMillis();
        po.setUpdateTime(updateTime);
        po.setOperator(CurrReqUtils.getAccount());
        repository.updateById(po);
        // 下级分类禁用
        if (EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            tutorialContentService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus(), 0, 0);
        }

        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                .buildOperationRecord(vo.getSiteCode(), vo.getSiteName(), null, null, 1, 5, updateTime, vo.getStatus(),2);
        operationRecordService.asyncAddChangeRecord(List.of(recordPO));
        return true;
    }

    public Boolean del(String id) {
        TutorialTabsPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        tutorialContentService.updateByPreLevel(po.getSiteCode(), po.getId(), 0, 0);
        repository.deleteById(id);
        i18nApi.deleteByMsgKey(po.getNameCn());
        return true;
    }

    public Boolean checkTabsStatus(String id) {
        TutorialTabsPO tabsPO = repository.selectById(id);
        if (tabsPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        return tabsPO.getStatus() == 1;
    }

    //上级信息修改
    public void updateByPreLevel(String siteCode, String preLevelId, String curName, int categoryChange) {
        LambdaQueryWrapper<TutorialTabsPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialTabsPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialTabsPO::getCategoryId, preLevelId);
            List<TutorialTabsPO> contentList = repository.selectList(query);
            if (!contentList.isEmpty()) {
                List<TutorialTabsPO> list = contentList.stream().peek(e -> e.setCategoryName(curName)).toList();
                this.updateBatchById(list);
            }
        } else {
            query.eq(TutorialTabsPO::getClassId, preLevelId);
            List<TutorialTabsPO> contentList = repository.selectList(query);
            if (!contentList.isEmpty()) {
                List<TutorialTabsPO> list = contentList.stream().peek(e -> e.setClassName(curName)).toList();
                this.updateBatchById(list);
            }
        }

    }

    //上级禁用
    public void updateByPreLevel(String siteCode, String preLevelId, int curStatus, int categoryChange) {
        LambdaQueryWrapper<TutorialTabsPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialTabsPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialTabsPO::getCategoryId, preLevelId);
        } else {
            query.eq(TutorialTabsPO::getClassId, preLevelId);
        }
        List<TutorialTabsPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            List<TutorialTabsPO> list = contentList.stream().peek(e -> e.setStatus(curStatus)).toList();
            this.updateBatchById(list);
        }
    }

    //上级删除
    public void updateByPreLevel(String siteCode, String preLevelId, int categoryChange) {
        LambdaQueryWrapper<TutorialTabsPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialTabsPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialTabsPO::getCategoryId, preLevelId);
        } else {
            query.eq(TutorialTabsPO::getClassId, preLevelId);

        }
        List<TutorialTabsPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            repository.deleteBatchIds(contentList);

        }
    }

    public List<TutorialClientShowVO> getTabsList(TutorialDownBoxResVo resVo) {
        LambdaQueryWrapper<TutorialTabsPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialTabsPO::getSiteCode, CurrReqUtils.getSiteCode());
        query.eq(TutorialTabsPO::getCategoryId, resVo.getCategoryId());
        query.eq(TutorialTabsPO::getClassId, resVo.getClassId());
        query.eq(TutorialTabsPO::getStatus, CommonConstant.business_one);
        query.orderByAsc(TutorialTabsPO::getSort);
        List<TutorialTabsPO> resTemp = repository.selectList(query);
        List<TutorialClientShowVO> result = new ArrayList<>();
        if (!resTemp.isEmpty()) {
            resTemp.forEach(item -> {
                    result.add(TutorialClientShowVO.builder().id(item.getId()).name(item.getNameCn()).build());
            });
        }
        return result;

    }

    private void checkNameRules(TutorialTabsAddVO vo, boolean isEdit, List<I18NMessageDTO> i18ListTemp) {
        List<I18NMessageDTO> i18List;
        if (!isEdit) {
            String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_TABS);
            i18List = i18nApi.getMessageLikeKey(i18nMessageKey).getData();
        } else {
            i18List = i18ListTemp;
        }

        List<I18nMsgFrontVO> i18nVoList = vo.getI18nMessages();

        for (I18nMsgFrontVO i18nVo : i18nVoList) {
            for (I18NMessageDTO dto : i18List) {
                if (i18nVo.getMessage().equals(dto.getMessage())) {
                    throw new BaowangDefaultException(ResultCode.NAME_ALREADY_EXIST );
                }
            }
        }
    }

    public String getI18nMessageKey(I18MsgKeyEnum key){
        return String.format(key.getCode(), CurrReqUtils.getSiteCode());
    }

}

