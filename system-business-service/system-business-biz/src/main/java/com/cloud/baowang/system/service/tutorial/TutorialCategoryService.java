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
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryQueryVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.po.tutorial.TutorialCategoryPO;
import com.cloud.baowang.system.po.tutorial.TutorialOperationRecordPO;
import com.cloud.baowang.system.po.tutorial.TutorialTabsPO;
import com.cloud.baowang.system.repositories.site.tutorial.TutorialCategoryRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import com.cloud.baowang.system.util.TutorialOperationRecordUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TutorialCategoryService extends ServiceImpl<TutorialCategoryRepository, TutorialCategoryPO> {

    private final MinioFileService fileService;
    private final TutorialCategoryRepository repository;
    private final TutorialOperationRecordService operationRecordService;
    private final TutorialClassService classService;
    private final TutorialTabsService tabsService;
    private final TutorialContentService contentService;
    private final I18nApi i18nApi;


    public Page<TutorialCategoryRspVO> listPage(TutorialCategoryQueryVO vo) {

        Page<TutorialCategoryPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<TutorialCategoryPO> query = new LambdaQueryWrapper<>();
        query.eq(StringUtils.isNotBlank(vo.getSiteCode()), TutorialCategoryPO::getSiteCode, vo.getSiteCode());
        query.eq(StringUtils.isNotBlank(vo.getCategoryId()), TutorialCategoryPO::getId, vo.getCategoryId());
        if (vo.getStatus() != null) {
            query.eq(TutorialCategoryPO::getStatus, vo.getStatus());
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && "createTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialCategoryPO::getCreateTime);
        }else if (StringUtils.isNotBlank(vo.getOrderField()) && "updateTime".equals(vo.getOrderField())) {
            query.orderBy(StringUtils.isNotBlank(vo.getOrderField()), Sort.Direction.ASC.name().equalsIgnoreCase(vo.getOrderType()), TutorialCategoryPO::getUpdateTime);
        }else {
            //query.orderByDesc(TutorialCategoryPO::getStatus);
            query.orderByDesc(TutorialCategoryPO::getUpdateTime);
        }
        page = repository.selectPage(page, query);

        String minioDomain = fileService.getMinioDomain();
        return ConvertUtil.toConverPage(page.convert(item -> {
            TutorialCategoryRspVO rspVO = BeanUtil.copyProperties(item, TutorialCategoryRspVO.class);
            rspVO.setImgKeyUrl(minioDomain + "/" + rspVO.getImgKey());
            List<I18NMessageDTO> i18nData = i18nApi.getMessageByKey(item.getNameCn()).getData();
            rspVO.setI18nMessages(i18nData);
            return rspVO;
        }));
    }

    /**
     * 获取教程大类下拉列表
     *
     * @return
     */
    public List<CodeValueVO> getCategoryDownBox(TutorialDownBoxResVo resVo) {
        LambdaQueryWrapper<TutorialCategoryPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialCategoryPO::getSiteCode, resVo.getSiteCode());
        if (resVo.getPosition() == CommonConstant.business_one) {
            query.eq(TutorialCategoryPO::getStatus, resVo.getPosition());
        }
        query.orderByAsc(TutorialCategoryPO::getSort);
        List<TutorialCategoryPO> resTemp = repository.selectList(query);
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
        List<TutorialCategoryPO> targetList = Lists.newArrayList();
        for (int i = 0; i < sourceList.size(); i++) {
            String categoryId = sourceList.get(i).getId();
            LambdaQueryWrapper<TutorialCategoryPO> query = new LambdaQueryWrapper<>();
            query.eq(TutorialCategoryPO::getId, categoryId);
            query.eq(TutorialCategoryPO::getSiteCode, siteCode);
            TutorialCategoryPO categoryPO = repository.selectOne(query);
            if (categoryPO == null) {
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            categoryPO.setSort(i + 1);
            categoryPO.setOperator(CurrReqUtils.getAccount());
            categoryPO.setUpdateTime(System.currentTimeMillis());
            targetList.add(categoryPO);
        }
        this.updateBatchById(targetList);
        return ResponseVO.success(true);
    }

    public boolean add(TutorialCategoryAddVO vo) {
        TutorialCategoryPO po = BeanUtil.copyProperties(vo, TutorialCategoryPO.class);
        //检查名称重复
        checkNameRules(vo, false, null);
        String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CATEGORY);
        String categoryName = RedisKeyTransUtil.getI18nDynamicKey(i18nMessageKey);
        i18nApi.insert(Map.of(categoryName, vo.getI18nMessages()));
        long cTime = System.currentTimeMillis();
        po.setNameCn(categoryName);
        po.setStatus(CommonConstant.business_zero);
        po.setCreator(CurrReqUtils.getAccount());
        po.setOperator(CurrReqUtils.getAccount());
        po.setCreateTime(cTime);
        po.setUpdateTime(cTime);
        repository.insert(po);
        return true;
    }


    public boolean edit(TutorialCategoryAddVO vo) {
        TutorialCategoryPO oldPO = repository.selectById(vo.getId());
        if (oldPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        LambdaQueryWrapper<TutorialCategoryPO> query = Wrappers.lambdaQuery();
        query.eq(TutorialCategoryPO::getSiteCode, vo.getSiteCode());
        query.ne(TutorialCategoryPO::getId, vo.getId());
        List<TutorialCategoryPO> categoryPOList = repository.selectList(query);
        if (!categoryPOList.isEmpty()) {
            List<String> classNameList = categoryPOList.stream().map(TutorialCategoryPO::getNameCn).toList();
            List<I18NMessageDTO> oldI18nList = i18nApi.getMessageByKeyList(classNameList).getData();
            checkNameRules(vo, true, oldI18nList);
        }
//        List<I18NMessageDTO> i18nList = i18nApi.getMessageLikeKey(I18MsgKeyEnum.TUTORIAL_CATEGORY.getCode()).getData();
//        List<I18NMessageDTO> oldI18nVerifyList = i18nList.stream().filter(item -> item.getMessageKey().equals(oldPO.getNameCn())).toList();
//
//        checkNameRules(vo, true, oldI18nVerifyList);
//        ResponseVO<List<I18NMessageDTO>> i18List =  i18nApi.getMessageByKey(oldPO.getNameCn());
//        List<String> i18nNewList = vo.getI18nMessages().stream().map(I18nMsgFrontVO::getMessage).toList();
//        List<String> i18nOldList = i18List.getData().stream().map(I18NMessageDTO::getMessage).toList();


        long cTime = System.currentTimeMillis();
        TutorialCategoryPO po = BeanUtil.copyProperties(vo, TutorialCategoryPO.class);
        po.setOperator(CurrReqUtils.getAccount());
        po.setUpdateTime(cTime);
        repository.updateById(po);
        List<I18NMessageDTO> oldI18nList = i18nApi.getMessageByKey(oldPO.getNameCn()).getData();
        List<I18nMsgFrontVO> i18nNewList = vo.getI18nMessages();
        List<TutorialOperationRecordPO> recordList = Lists.newArrayList();
        for (I18nMsgFrontVO i18nVo : i18nNewList) {
            for (I18NMessageDTO i18nDto: oldI18nList){
                if (i18nVo.getLanguage().equals(i18nDto.getLanguage())){
                    if (!i18nVo.getMessage().equals(i18nDto.getMessage())){
                        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                                .buildOperationRecord(vo.getSiteCode(), null, i18nDto.getMessage(), i18nVo.getMessage(), 0, 0, cTime, null,1);
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
                    .buildOperationRecord(vo.getSiteCode(), null, oldImgUrl, curImgUrl, 0, 2, cTime, null,3);
            recordList.add(recordPO);
        }
        operationRecordService.asyncAddChangeRecord(recordList);

        return true;
    }

    private void checkNameRules(TutorialCategoryAddVO vo, boolean isEdit, List<I18NMessageDTO> i18ListTemp) {
        List<I18NMessageDTO> i18List;
        if (!isEdit) {
            String i18nMessageKey = getI18nMessageKey(I18MsgKeyEnum.TUTORIAL_CATEGORY);
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


    public Boolean enableAndDisAble(TutorialCategoryRspVO vo) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(vo.getStatus()) && !EnableStatusEnum.DISABLE.getCode().equals(vo.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        TutorialCategoryPO po = BeanUtil.copyProperties(vo, TutorialCategoryPO.class);
        long updateTime = System.currentTimeMillis();
        po.setUpdateTime(updateTime);
        po.setOperator(CurrReqUtils.getAccount());
        repository.updateById(po);
        // 涉及分类标签内容更改
        classService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus());
        tabsService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus(), 1);
        contentService.updateByPreLevel(vo.getSiteCode(), vo.getId(), vo.getStatus(), 1, 0);

        TutorialOperationRecordPO recordPO = TutorialOperationRecordUtil
                .buildOperationRecord(vo.getSiteCode(), vo.getSiteName(), null, null, 0, 4, updateTime, vo.getStatus(),2);
        operationRecordService.asyncAddChangeRecord(List.of(recordPO));
        return true;
    }

    public Boolean checkCategoryStatus(String id) {
        TutorialCategoryPO categoryPO = repository.selectById(id);
        if (categoryPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        return categoryPO.getStatus() == 1;
    }

    public Boolean del(String id) {
        TutorialCategoryPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        // 涉及分类标签内容更改
        classService.updateByPreLevel(po.getSiteCode(), po.getId());
        tabsService.updateByPreLevel(po.getSiteCode(), po.getId(), 1);
        contentService.updateByPreLevel(po.getSiteCode(), po.getId(), 1, 0);
        repository.deleteById(id);
        i18nApi.deleteByMsgKey(po.getNameCn());

        return true;
    }


    //查询站点启用大类
    public List<TutorialClientShowVO> getCategoryList() {
        //todo
        LambdaQueryWrapper<TutorialCategoryPO> query = new LambdaQueryWrapper<>();
        query.eq(TutorialCategoryPO::getSiteCode, CurrReqUtils.getSiteCode());
        query.eq(TutorialCategoryPO::getStatus, 1);
        query.orderByAsc(TutorialCategoryPO::getSort);
        List<TutorialCategoryPO> resTemp = repository.selectList(query);
        List<TutorialClientShowVO> result = new ArrayList<>();
        String minioDomain = fileService.getMinioDomain();
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
