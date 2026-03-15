package com.cloud.baowang.system.service.tutorial;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
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
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentRspVO;
import com.cloud.baowang.system.po.tutorial.*;
import com.cloud.baowang.system.repositories.site.tutorial.TutorialContentRepository;
import com.cloud.baowang.system.util.TutorialOperationRecordUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TutorialContentService extends ServiceImpl<TutorialContentRepository, TutorialContentPO> {

    @Autowired
    private TutorialContentRepository repository;
    @Autowired
    private TutorialOperationRecordService operationRecordService;

    @Autowired
    private I18nApi i18nApi;

    private TutorialCategoryService getCategoryService() {
        return SpringUtils.getBean(TutorialCategoryService.class);
    }

    private TutorialClassService getClassService() {
        return SpringUtils.getBean(TutorialClassService.class);
    }

    private TutorialTabsService getTabsService() {
        return SpringUtils.getBean(TutorialTabsService.class);
    }


    public Page<TutorialContentRspVO> listPage(TutorialContentResVO vo) {

        Page<TutorialContentPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<TutorialContentPO> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(vo.getSiteCode()), TutorialContentPO::getSiteCode, vo.getSiteCode());
        query.eq(StringUtils.isNotBlank(vo.getCategoryId()), TutorialContentPO::getCategoryId, vo.getCategoryId());
        query.eq(StringUtils.isNotBlank(vo.getClassId()), TutorialContentPO::getClassId, vo.getClassId());
        query.eq(StringUtils.isNotBlank(vo.getTabsId()), TutorialContentPO::getTabsId, vo.getTabsId());
        query.eq(StringUtils.isNotBlank(vo.getContentId()), TutorialContentPO::getId, vo.getContentId());

        if (vo.getStatus() != null) {
            query.eq(TutorialContentPO::getStatus, vo.getStatus());
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && "createTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialContentPO::getCreateTime);
        }else if (StringUtils.isNotBlank(vo.getOrderField()) && "updateTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialContentPO::getUpdateTime);
        }else {
            //query.orderByDesc(TutorialContentPO::getStatus);
            query.orderByDesc(TutorialContentPO::getUpdateTime);
        }
        page = repository.selectPage(page, query);

        return ConvertUtil.toConverPage(page.convert(item -> {
            TutorialContentRspVO rspVO = BeanUtil.copyProperties(item, TutorialContentRspVO.class);
            rspVO.setNameCn(item.getNameCn());
            //List<I18NMessageDTO> i18nData = i18nApi.getMessageByKey(item.getNameCn()).getData();
            List<I18NMessageDTO> i18nData = buildI18nDataRsp(item.getNameCn(),item.getContentDetailCn());
            rspVO.setI18nMessages(i18nData);
            return rspVO;
        }));
    }

    public List<I18NMessageDTO> buildI18nDataRsp(String nameCn,String nameDetailCn){
        List<I18NMessageDTO> i18nData = i18nApi.getMessageByKey(nameCn).getData();
        List<I18NMessageDTO> detailData = i18nApi.getMessageByKey(nameDetailCn).getData();
        for (I18NMessageDTO i18nMessageDTO : i18nData) {
            for (I18NMessageDTO detailMessageDTO : detailData) {
                if (i18nMessageDTO.getLanguage().equals(detailMessageDTO.getLanguage())) {
                    i18nMessageDTO.setMessageKey(detailMessageDTO.getMessage());
                }
            }
        }
        return i18nData;
    }
    /**
     * 获取教程内容下拉列表
     *
     * @return
     */
    public List<CodeValueVO> getContentDownBox(TutorialDownBoxResVo resVo) {
        LambdaQueryWrapper<TutorialContentPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialContentPO::getSiteCode, resVo.getSiteCode());
        query.eq(StringUtils.isNotBlank(resVo.getCategoryId()), TutorialContentPO::getCategoryId, resVo.getCategoryId());
        query.eq(StringUtils.isNotBlank(resVo.getClassId()), TutorialContentPO::getClassId, resVo.getClassId());
        query.eq(StringUtils.isNotBlank(resVo.getTabsId()), TutorialContentPO::getTabsId, resVo.getTabsId());
        if (resVo.getPosition() == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getStatus, resVo.getPosition());
        }
        query.orderByAsc(TutorialContentPO::getSort);
        List<TutorialContentPO> resTemp = repository.selectList(query);
        List<CodeValueVO> result = new ArrayList<>();
        if (!resTemp.isEmpty()) {
            resTemp.forEach(item -> {

                result.add(CodeValueVO.builder().code(item.getId()).value(item.getNameCn()).type(item.getNameCn()).build());

            });
        }
        return result;

    }

    /**
     * 排序
     *
     * @param sourceList
     * @param siteCode
     * @return
     */
    public ResponseVO<Boolean> sort(List<TutorialCategoryRspVO> sourceList, String siteCode) {
        List<TutorialContentPO> targetList = Lists.newArrayList();
        for (int i = 0; i < sourceList.size(); i++) {
            String contentId = sourceList.get(i).getId();
            LambdaQueryWrapper<TutorialContentPO> query = new LambdaQueryWrapper<>();
            query.eq(TutorialContentPO::getSiteCode, siteCode);
            query.eq(TutorialContentPO::getId, contentId);
            TutorialContentPO contentPO = repository.selectOne(query);
            if (contentPO == null) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            contentPO.setSort(i + 1);
            contentPO.setOperator(CurrReqUtils.getAccount());
            contentPO.setUpdateTime(System.currentTimeMillis());
            targetList.add(contentPO);
        }
        this.updateBatchById(targetList);
        return ResponseVO.success(true);
    }

    public boolean add(TutorialContentAddVO vo) {
        LambdaQueryWrapper<TutorialContentPO> query = Wrappers.lambdaQuery();
        //查询同一大类,同一个分类下,是否存在
        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        TutorialClassPO classPO = getClassService().getById(vo.getClassId());
        TutorialTabsPO tabsPO = getTabsService().getById(vo.getTabsId());
        if (categoryPO == null || classPO == null || tabsPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //查询当前分类是否属于当前大类
        if (!classPO.getCategoryId().equals(String.valueOf(vo.getCategoryId()))) {
            throw new BaowangDefaultException(ResultCode.CLASS_NOT_BELONG_CATEGORY);
        }
        if (!tabsPO.getClassId().equals(vo.getClassId())) {
            throw new BaowangDefaultException(ResultCode.TBAS_NOT_BELONG_CLASS);
        }
        query.eq(TutorialContentPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialContentPO::getSiteCode, vo.getSiteCode());
        query.eq(TutorialContentPO::getClassId, vo.getClassId());
        query.eq(TutorialContentPO::getTabsId, vo.getTabsId());
        List<TutorialContentPO> classPOList = repository.selectList(query);
        if (!classPOList.isEmpty()){
            List<String> contentNameList = classPOList.stream().map(TutorialContentPO::getNameCn).toList();
            List<I18NMessageDTO> i18nContentData = i18nApi.getMessageByKeyList(contentNameList).getData();
            checkNameRules(vo, false, i18nContentData);

            List<String> contentDetailList = classPOList.stream().map(TutorialContentPO::getContentDetailCn).toList();
            List<I18NMessageDTO> i18nDetailList = i18nApi.getMessageByKeyList(contentDetailList).getData();
            checkDetailRules(vo,false, i18nDetailList);
        }

        String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CONTENT);
        String contentI18nName = RedisKeyTransUtil.getI18nDynamicKey(i18nMessageKey);
        i18nApi.insert(Map.of(contentI18nName, vo.getI18nMessages()));

        String detailI18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CONTENT_DETAIL);
        String detailI18nName = RedisKeyTransUtil.getI18nDynamicKey(detailI18nMessageKey);
        List<I18nMsgFrontVO> detailI18nList = new ArrayList<>();
        List<I18nMsgFrontVO> i18nMessagesTemp = vo.getI18nMessages();
        i18nMessagesTemp.forEach(item -> {
            detailI18nList.add(I18nMsgFrontVO.builder().language(item.getLanguage()).message(item.getMessageKey()).build());
        });
        i18nApi.insert(Map.of(detailI18nName, detailI18nList));

        TutorialContentPO po = BeanUtil.copyProperties(vo, TutorialContentPO.class);
        long cTime = System.currentTimeMillis();
        po.setStatus(CommonConstant.business_zero);
        po.setCreator(CurrReqUtils.getAccount());
        po.setOperator(CurrReqUtils.getAccount());
        po.setCreateTime(cTime);
        po.setUpdateTime(cTime);

        po.setCategoryId(Long.valueOf(categoryPO.getId()));
        po.setCategoryName(categoryPO.getNameCn());

        po.setClassId(Long.valueOf(classPO.getId()));
        po.setClassName(classPO.getNameCn());

        po.setTabsId(Long.valueOf(tabsPO.getId()));
        po.setTabsName(tabsPO.getNameCn());

        po.setNameCn(contentI18nName);
        po.setContentDetailCn(detailI18nName);
        repository.insert(po);
        return true;
    }


    public boolean edit(TutorialContentAddVO vo) {
        TutorialContentPO prePO = repository.selectById(vo.getId());
        if (prePO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        TutorialClassPO classPO = getClassService().getById(vo.getClassId());
        TutorialTabsPO tabsPO = getTabsService().getById(vo.getTabsId());
        if (categoryPO == null || classPO == null || tabsPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //查询当前分类是否属于当前大类
        if (!classPO.getCategoryId().equals(String.valueOf(vo.getCategoryId()))) {
            throw new BaowangDefaultException(ResultCode.CLASS_NOT_BELONG_CATEGORY);
        }
        if (!tabsPO.getClassId().equals(vo.getClassId())) {
            throw new BaowangDefaultException(ResultCode.TBAS_NOT_BELONG_CLASS);
        }
        LambdaQueryWrapper<TutorialContentPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialContentPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialContentPO::getSiteCode, vo.getSiteCode());
        query.eq(TutorialContentPO::getClassId, vo.getClassId());
        query.eq(TutorialContentPO::getTabsId, vo.getTabsId());
        query.ne(TutorialContentPO::getId, vo.getId());
        List<TutorialContentPO> classPOList = repository.selectList(query);
        if (!classPOList.isEmpty()) {
            List<String> classNameList = classPOList.stream().map(TutorialContentPO::getNameCn).toList();
            List<I18NMessageDTO> oldI18nList = i18nApi.getMessageByKeyList(classNameList).getData();
            checkNameRules(vo, true, oldI18nList);

            List<String> contentDetailList = classPOList.stream().map(TutorialContentPO::getContentDetailCn).toList();
            List<I18NMessageDTO> i18nDetailList = i18nApi.getMessageByKeyList(contentDetailList).getData();
            checkDetailRules(vo,true, i18nDetailList);
        }

        List<I18nMsgFrontVO> i18nNewList = vo.getI18nMessages();
        List<I18NMessageDTO> oldI18nVo = i18nApi.getMessageByKey(prePO.getNameCn()).getData();

        List<I18nMsgFrontVO> detailI18nList = new ArrayList<>();
        List<I18nMsgFrontVO> i18nMessagesTemp = vo.getI18nMessages();
        i18nMessagesTemp.forEach(item -> {
            detailI18nList.add(I18nMsgFrontVO.builder().language(item.getLanguage()).message(item.getMessageKey()).build());
        });
        List<I18NMessageDTO> oldI18nDetailVo = i18nApi.getMessageByKey(prePO.getContentDetailCn()).getData();
        long cTime = System.currentTimeMillis();
        TutorialContentPO po = BeanUtil.copyProperties(vo, TutorialContentPO.class);
        po.setOperator(CurrReqUtils.getAccount());
        po.setUpdateTime(cTime);
        po.setCategoryId(Long.valueOf(categoryPO.getId()));
        po.setCategoryName(categoryPO.getNameCn());
        po.setClassId(Long.valueOf(classPO.getId()));
        po.setClassName(classPO.getNameCn());
        po.setTabsId(Long.valueOf(tabsPO.getId()));
        po.setTabsName(tabsPO.getNameCn());
        repository.updateById(po);
        //变更记录
        List<TutorialOperationRecordPO> recordList = Lists.newArrayList();
        for (I18nMsgFrontVO i18nVo : i18nNewList) {
            //名称变更记录
            for (I18NMessageDTO i18nDto : oldI18nVo) {
                if (i18nVo.getLanguage().equals(i18nDto.getLanguage())) {
                    if (!i18nVo.getMessage().equals(i18nDto.getMessage())) {
                        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                                .buildOperationRecord(vo.getSiteCode(), null, i18nDto.getMessage(), i18nVo.getMessage(), 3, 6, cTime, null,1);
                        recordList.add(recordPO);
                    }
                }
            }
            //富文本变更记录
            for (I18NMessageDTO i18nDto : oldI18nDetailVo) {
                if (i18nVo.getLanguage().equals(i18nDto.getLanguage())) {
                    if (!i18nVo.getMessageKey().equals(i18nDto.getMessage())) {
                        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                                .buildOperationRecord(vo.getSiteCode(), null, i18nDto.getMessage(), i18nVo.getMessageKey(), 3, 6, cTime, null,4);
                        recordList.add(recordPO);
                    }
                }
            }
        }
        i18nApi.update(Map.of(prePO.getNameCn(), vo.getI18nMessages()));
        i18nApi.update(Map.of(prePO.getContentDetailCn(), detailI18nList));

        operationRecordService.asyncAddChangeRecord(recordList);
        return true;
    }


    public Boolean enableAndDisAble(TutorialContentRspVO vo) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus()) && !EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //查询大类,分类是否启用
        if (!getTabsService().checkTabsStatus(String.valueOf(vo.getTabsId()))) {
            throw new BaowangDefaultException(ResultCode.TABS_IS_DISABLED);
        }
        if (!getClassService().checkClassStatus(vo.getClassId())) {
            throw new BaowangDefaultException(ResultCode.CLASS_IS_DISABLED);
        }
        if (!getCategoryService().checkCategoryStatus(vo.getCategoryId())) {
            throw new BaowangDefaultException(ResultCode.CATEGORY_IS_DISABLED);
        }
        TutorialContentPO po = BeanUtil.copyProperties(vo, TutorialContentPO.class);
        long updateTime = System.currentTimeMillis();
        po.setUpdateTime(updateTime);
        po.setOperator(CurrReqUtils.getAccount());
        repository.updateById(po);

        TutorialOperationRecordPO recordInfo = TutorialOperationRecordUtil
                .buildOperationRecord(CurrReqUtils.getSiteCode(), vo.getSiteName(), null, null, 3, 4, updateTime, vo.getStatus(),2);
        operationRecordService.asyncAddChangeRecord(List.of(recordInfo));
        return true;
    }

    public Boolean del(String id) {
        TutorialContentPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        repository.deleteById(id);
        i18nApi.deleteByMsgKey(po.getNameCn());
        return true;
    }

    //上级信息修改
    public void updateByPreLevel(String siteCode, String preLevelId, String curName, int categoryChange, int classChange) {
        LambdaQueryWrapper<TutorialContentPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialContentPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getCategoryId, preLevelId);
            List<TutorialContentPO> contentList = repository.selectList(query);
            if (!contentList.isEmpty()) {
                List<TutorialContentPO> list = contentList.stream().peek(e -> e.setCategoryName(curName)).toList();
                this.updateBatchById(list);
            }
        }
        if (classChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getClassId, preLevelId);
            List<TutorialContentPO> contentList = repository.selectList(query);
            if (!contentList.isEmpty()) {
                List<TutorialContentPO> list = contentList.stream().peek(e -> e.setClassName(curName)).toList();
                this.updateBatchById(list);
            }
        } else {
            query.eq(TutorialContentPO::getTabsId, preLevelId);
            List<TutorialContentPO> contentList = repository.selectList(query);
            if (!contentList.isEmpty()) {
                List<TutorialContentPO> list = contentList.stream().peek(e -> e.setTabsName(curName)).toList();
                this.updateBatchById(list);
            }
        }

    }

    //上级禁用
    public void updateByPreLevel(String siteCode, String preLevelId, int curStatus, int categoryChange, int classChange) {
        LambdaQueryWrapper<TutorialContentPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialContentPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getCategoryId, preLevelId);
        }else if (classChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getClassId, preLevelId);
        } else {
            query.eq(TutorialContentPO::getTabsId, preLevelId);
        }
        List<TutorialContentPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            List<TutorialContentPO> list = contentList.stream().peek(e -> e.setStatus(curStatus)).toList();
            this.updateBatchById(list);
        }
    }

    //上级删除
    public void updateByPreLevel(String siteCode, String preLevelId, int categoryChange, int classChange) {
        LambdaQueryWrapper<TutorialContentPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialContentPO::getSiteCode, siteCode);
        if (categoryChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getCategoryId, preLevelId);
        } else if (classChange == CommonConstant.business_one) {
            query.eq(TutorialContentPO::getClassId, preLevelId);
        } else {
            query.eq(TutorialContentPO::getTabsId, preLevelId);
        }
        List<TutorialContentPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            repository.deleteBatchIds(contentList);
        }
    }

    public List<TutorialClientShowVO> showContent(TutorialDownBoxResVo vo) {

        LambdaQueryWrapper<TutorialContentPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialContentPO::getSiteCode, vo.getSiteCode());
        query.eq(TutorialContentPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialContentPO::getClassId, vo.getClassId());
        query.eq(TutorialContentPO::getTabsId, vo.getTabsId());
        query.eq(TutorialContentPO::getStatus, CommonConstant.business_one);
        query.orderByAsc(TutorialContentPO::getSort);

        query.orderByAsc(TutorialContentPO::getSort).orderByDesc(TutorialContentPO::getUpdateTime);
        List<TutorialContentPO> resTemp = repository.selectList(query);
        List<TutorialClientShowVO> resVOList = new ArrayList<>();
        if (!resTemp.isEmpty()) {
            resTemp.forEach(item -> {
                resVOList.add(TutorialClientShowVO.builder().id(item.getId())
                        .name(item.getNameCn())
                        .value(item.getContentDetailCn())
                        .build());

            });
        }
        return resVOList;
    }

    private void checkNameRules(TutorialContentAddVO vo, boolean isEdit, List<I18NMessageDTO> i18ListTemp) {
        List<I18NMessageDTO> i18List;
        if (!isEdit) {
            String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CONTENT);
            i18List = i18nApi.getMessageLikeKey(i18nMessageKey).getData();
        } else {
            i18List = i18ListTemp;
        }

        List<I18nMsgFrontVO> i18nVoList = vo.getI18nMessages();

        for (I18nMsgFrontVO i18nVo : i18nVoList) {
            for (I18NMessageDTO dto : i18List) {
                if (i18nVo.getLanguage().equals(dto.getLanguage())) {
                   if (i18nVo.getMessage().equals(dto.getMessage())) {
                       throw new BaowangDefaultException(ResultCode.NAME_ALREADY_EXIST);

                   }
                }
            }
        }
    }
    private void  checkDetailRules(TutorialContentAddVO vo,boolean isEdit, List<I18NMessageDTO> i18ListTemp){
        List<I18NMessageDTO> i18List;
        if (!isEdit) {
            String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CONTENT_DETAIL);
            i18List = i18nApi.getMessageLikeKey(i18nMessageKey).getData();
        } else {
            i18List = i18ListTemp;
        }
        List<I18nMsgFrontVO> i18nVoList = vo.getI18nMessages();
        for (I18nMsgFrontVO i18nVo : i18nVoList) {
            for (I18NMessageDTO dto : i18List) {
                if (i18nVo.getLanguage().equals(dto.getLanguage())) {
                    if (i18nVo.getMessageKey().equals(dto.getMessage())) {
                        throw new BaowangDefaultException(ResultCode.NAME_ALREADY_EXIST + CommonConstant.COLON + i18nVo.getMessage());
                    }
                }
            }
        }
    }

    public boolean checkDataExist() {
        LambdaQueryWrapper<TutorialContentPO> query = new LambdaQueryWrapper<>();
        String siteCode = CurrReqUtils.getSiteCode();
        if (!StringUtils.isNotBlank(siteCode)) {
            return false;
        }
        query.eq(TutorialContentPO::getSiteCode, siteCode);
        List<TutorialContentPO> tutorialContentPOS = repository.selectList(query);
        return !tutorialContentPOS.isEmpty();
    }

    public String getI18nMessageKey(I18MsgKeyEnum key){
        return String.format(key.getCode(), CurrReqUtils.getSiteCode());
    }
}
