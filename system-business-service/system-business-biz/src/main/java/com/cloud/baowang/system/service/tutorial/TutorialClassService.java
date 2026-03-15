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
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassRspVO;
import com.cloud.baowang.system.po.tutorial.TutorialCategoryPO;
import com.cloud.baowang.system.po.tutorial.TutorialClassPO;
import com.cloud.baowang.system.po.tutorial.TutorialOperationRecordPO;
import com.cloud.baowang.system.repositories.site.tutorial.TutorialClassRepository;
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
public class TutorialClassService extends ServiceImpl<TutorialClassRepository, TutorialClassPO> {


    private final MinioFileService fileService;
    private final TutorialClassRepository repository;
    private final TutorialOperationRecordService operationRecordService;

    private final TutorialTabsService tabsService;
    private final TutorialContentService contentService;
    private final I18nApi i18nApi;


    public TutorialCategoryService getCategoryService() {
        return SpringUtils.getBean(TutorialCategoryService.class);
    }


    public Page<TutorialClassRspVO> listPage(TutorialClassResVO vo) {

        Page<TutorialClassPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<TutorialClassPO> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(vo.getSiteCode()), TutorialClassPO::getSiteCode, vo.getSiteCode());
        query.eq(StringUtils.isNotBlank(vo.getCategoryId()), TutorialClassPO::getCategoryId, vo.getCategoryId());
        query.eq(StringUtils.isNotBlank(vo.getClassId()), TutorialClassPO::getId, vo.getClassId());
        if (vo.getStatus() != null) {
            query.eq(TutorialClassPO::getStatus, vo.getStatus());
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && "createTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialClassPO::getCreateTime);
        }else if (StringUtils.isNotBlank(vo.getOrderField()) && "updateTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialClassPO::getUpdateTime);
        }else {
            //query.orderByDesc(TutorialClassPO::getStatus);
            query.orderByDesc(TutorialClassPO::getUpdateTime);
        }
        page = repository.selectPage(page, query);

        String minioDomain = fileService.getMinioDomain();
        return ConvertUtil.toConverPage(page.convert(item -> {
            TutorialClassRspVO rspVO = BeanUtil.copyProperties(item, TutorialClassRspVO.class);
            rspVO.setImgKeyUrl(minioDomain + "/" + rspVO.getImgKey());
            List<I18NMessageDTO> i18nData = i18nApi.getMessageByKey(item.getNameCn()).getData();
            rspVO.setI18nMessages(i18nData);
            return rspVO;
        }));
    }

    /**
     * 获取教程分类下拉列表
     *
     * @return
     */
    public List<CodeValueVO> getClassDownBox(TutorialDownBoxResVo resVo) {
        LambdaQueryWrapper<TutorialClassPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialClassPO::getSiteCode, resVo.getSiteCode());
        query.eq(StringUtils.isNotBlank(resVo.getCategoryId()), TutorialClassPO::getCategoryId, resVo.getCategoryId());
        if (resVo.getPosition() == CommonConstant.business_one) {
            query.eq(TutorialClassPO::getStatus, resVo.getPosition());
        }
        query.orderByAsc(TutorialClassPO::getSort);
        List<TutorialClassPO> resTemp = repository.selectList(query);
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
        List<TutorialClassPO> targetList = Lists.newArrayList();
        for (int i = 0; i < sourceList.size(); i++) {
            String classId = sourceList.get(i).getId();
            LambdaQueryWrapper<TutorialClassPO> query = new LambdaQueryWrapper<>();
            query.eq(TutorialClassPO::getSiteCode, siteCode);
            query.eq(TutorialClassPO::getId, classId);
            TutorialClassPO classPO = repository.selectOne(query);
            if (classPO == null) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            classPO.setSort(i + 1);
            classPO.setOperator(CurrReqUtils.getAccount());
            classPO.setUpdateTime(System.currentTimeMillis());
            targetList.add(classPO);
        }
        this.updateBatchById(targetList);
        return ResponseVO.success(true);
    }

    private void checkNameRules(TutorialClassAddVO vo, boolean isEdit, List<I18NMessageDTO> i18ListTemp) {
        List<I18NMessageDTO> i18List;
        if (!isEdit) {
            String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CLASS);
            i18List = i18nApi.getMessageLikeKey(i18nMessageKey).getData();
        } else {
            i18List = i18ListTemp;
        }

        List<I18nMsgFrontVO> i18nVoList = vo.getI18nMessages();

        for (I18nMsgFrontVO i18nVo : i18nVoList) {
            for (I18NMessageDTO dto : i18List) {
                if (i18nVo.getMessage().equals(dto.getMessage())) {
                    throw new BaowangDefaultException(ResultCode.NAME_ALREADY_EXIST);
                }
            }
        }
    }

    public boolean add(TutorialClassAddVO vo) {
        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        if (categoryPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        checkNameRules(vo, false, null);

        LambdaQueryWrapper<TutorialClassPO> query = Wrappers.lambdaQuery();
        //查询同一大类下,是否存在

        query.eq(TutorialClassPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialClassPO::getSiteCode, vo.getSiteCode());
        List<TutorialClassPO> classPOList = repository.selectList(query);
        List<String> classNameList = classPOList.stream().map(TutorialClassPO::getNameCn).toList();
        if (!classNameList.isEmpty()){
            List<I18NMessageDTO> i18nClassData = i18nApi.getMessageByKeyList(classNameList).getData();
            checkNameRules(vo, false, i18nClassData);
        }

        TutorialClassPO po = BeanUtil.copyProperties(vo, TutorialClassPO.class);
        String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CLASS);
        String classI18nName = RedisKeyTransUtil.getI18nDynamicKey(i18nMessageKey);
        i18nApi.insert(Map.of(classI18nName, vo.getI18nMessages()));

        long cTime = System.currentTimeMillis();
        po.setSiteCode(vo.getSiteCode());
        po.setStatus(CommonConstant.business_zero);
        po.setCreator(CurrReqUtils.getAccount());
        po.setOperator(CurrReqUtils.getAccount());
        po.setCreateTime(cTime);
        po.setUpdateTime(cTime);
        po.setNameCn(classI18nName);
        po.setCategoryId(vo.getCategoryId());
        po.setCategoryName(categoryPO.getNameCn());
        repository.insert(po);

        return true;
    }


    public boolean edit(TutorialClassAddVO vo) {
        TutorialClassPO oldPO = repository.selectById(vo.getId());
        if (oldPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        TutorialCategoryPO categoryPO = getCategoryService().getById(vo.getCategoryId());
        if (categoryPO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaQueryWrapper<TutorialClassPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialClassPO::getCategoryId, vo.getCategoryId());
        query.eq(TutorialClassPO::getSiteCode, vo.getSiteCode());
        query.ne(TutorialClassPO::getId, vo.getId());
        List<TutorialClassPO> classPOList = repository.selectList(query);
        if (!classPOList.isEmpty()) {
            List<String> classNameList = classPOList.stream().map(TutorialClassPO::getNameCn).toList();
            List<I18NMessageDTO> oldI18nList = i18nApi.getMessageByKeyList(classNameList).getData();
            checkNameRules(vo, true, oldI18nList);
        }
        List<I18NMessageDTO> oldI18nVo = i18nApi.getMessageByKey(oldPO.getNameCn()).getData();
        List<I18nMsgFrontVO> i18nNewList = vo.getI18nMessages();

        long cTime = System.currentTimeMillis();
        TutorialClassPO po = BeanUtil.copyProperties(vo, TutorialClassPO.class);
        po.setOperator(CurrReqUtils.getAccount());
        po.setUpdateTime(cTime);
        po.setCategoryId(vo.getCategoryId());
        po.setCategoryName(categoryPO.getNameCn());
        repository.updateById(po);
        //变更记录
        List<TutorialOperationRecordPO> recordList = Lists.newArrayList();
        for (I18nMsgFrontVO i18nVo : i18nNewList) {
            for (I18NMessageDTO i18nDto : oldI18nVo) {
                if (i18nVo.getLanguage().equals(i18nDto.getLanguage())) {
                    if (!i18nVo.getMessage().equals(i18nDto.getMessage())) {
                        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                                .buildOperationRecord(vo.getSiteCode(), null, i18nDto.getMessage(), i18nVo.getMessage(), 2, 0, cTime, null,1);
                        recordList.add(recordPO);
                    }
                }
            }
        }
        i18nApi.update(Map.of(oldPO.getNameCn(), vo.getI18nMessages()));
        if (!vo.getImgKey().equals(oldPO.getImgKey())) {
            String minioDomain = fileService.getMinioDomain();
            String oldImgUrl = minioDomain+"/"+oldPO.getImgKey();
            String curImgUrl = minioDomain+"/"+vo.getImgKey();
            TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                    .buildOperationRecord(vo.getSiteCode(), null, oldImgUrl, curImgUrl, 2, 2, cTime, null,3);
            recordList.add(recordPO);
        }
        operationRecordService.asyncAddChangeRecord(recordList);
        return true;
    }


    public Boolean enableAndDisAble(TutorialClassRspVO vo) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus()) && !EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (!getCategoryService().checkCategoryStatus(vo.getCategoryId())) {
            throw new BaowangDefaultException(ResultCode.CATEGORY_IS_DISABLED);
        }
        TutorialClassPO po = BeanUtil.copyProperties(vo, TutorialClassPO.class);
        long updateTime = System.currentTimeMillis();
        po.setUpdateTime(updateTime);
        po.setOperator(CurrReqUtils.getAccount());
        repository.updateById(po);
        if (EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            //禁用 页签+内容
            tabsService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus(), 0);
            contentService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus(), 0, 1);
        }

        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil.buildOperationRecord(vo.getSiteCode(), vo.getSiteName(), null, null, 2, 4, updateTime, vo.getStatus(),2);
        operationRecordService.asyncAddChangeRecord(List.of(recordPO));
        return true;
    }


    public Boolean del(String id) {
        TutorialClassPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        // 标签/内容 删除
        contentService.updateByPreLevel(po.getSiteCode(), po.getId(), 0, 1);
        tabsService.updateByPreLevel(po.getSiteCode(), po.getId(), 0);
        repository.deleteById(id);
        i18nApi.deleteByMsgKey(po.getNameCn());
        return true;
    }

    public Boolean checkClassStatus(String id) {
        TutorialClassPO classPO = repository.selectById(id);
        if (classPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        return classPO.getStatus() == 1;
    }

    //上级信息修改
    public void updateByPreLevel(String siteCode, String preLevelId, String curName) {
        LambdaQueryWrapper<TutorialClassPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialClassPO::getSiteCode, siteCode);
        query.eq(TutorialClassPO::getCategoryId, preLevelId);
        List<TutorialClassPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            List<TutorialClassPO> list = contentList.stream().peek(e -> e.setCategoryName(curName)).toList();
            this.updateBatchById(list);
        }


    }

    //上级禁用
    public void updateByPreLevel(String siteCode, String preLevelId, int curStatus) {
        LambdaQueryWrapper<TutorialClassPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialClassPO::getSiteCode, siteCode);
        query.eq(TutorialClassPO::getCategoryId, preLevelId);

        List<TutorialClassPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            List<TutorialClassPO> list = contentList.stream().peek(e -> e.setStatus(curStatus)).toList();
            this.updateBatchById(list);
        }
    }

    //上级删除
    public void updateByPreLevel(String siteCode, String preLevelId) {
        LambdaQueryWrapper<TutorialClassPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialClassPO::getSiteCode, siteCode);
        query.eq(TutorialClassPO::getCategoryId, preLevelId);
        List<TutorialClassPO> contentList = repository.selectList(query);
        if (!contentList.isEmpty()) {
            repository.deleteBatchIds(contentList);
        }
    }

    public List<TutorialClientShowVO> getClassList(String id) {
        LambdaQueryWrapper<TutorialClassPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialClassPO::getSiteCode, CurrReqUtils.getSiteCode());
        query.eq(TutorialClassPO::getCategoryId, id);
        query.eq(TutorialClassPO::getStatus, CommonConstant.business_one);
        query.orderByAsc(TutorialClassPO::getSort);
        List<TutorialClassPO> resTemp = repository.selectList(query);
        String minioDomain = fileService.getMinioDomain();
        List<TutorialClientShowVO> result = new ArrayList<>();
        if (!resTemp.isEmpty()) {
            resTemp.forEach(item -> {

                result.add(TutorialClientShowVO.builder().id(item.getId()).name(item.getNameCn()).icon(minioDomain + "/" + item.getImgKey()).build());

            });
        }
        return result;
    }
    public String getI18nMessageKey(I18MsgKeyEnum key){
        return String.format(key.getCode(), CurrReqUtils.getSiteCode());
    }

}
