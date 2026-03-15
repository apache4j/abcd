package com.cloud.baowang.system.service.site.change;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.enums.SitClounmDefaultEnum;
import com.cloud.baowang.system.api.enums.SiteChangeTypeEnum;
import com.cloud.baowang.system.api.enums.SiteOptionStatusEnum;
import com.cloud.baowang.system.api.enums.SiteOptionTypeEnum;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.exchange.SystemCurrencyInfoRespVO;
import com.cloud.baowang.system.api.vo.language.SiteLanguageVO;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import com.cloud.baowang.system.api.vo.site.SiteBasicChangeVO;
import com.cloud.baowang.system.api.vo.site.SiteBasicVO;
import com.cloud.baowang.system.api.vo.site.SiteConfigVO;
import com.cloud.baowang.system.api.vo.site.SiteInfoChangeRequestVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordVO;
import com.cloud.baowang.system.po.SystemParamPO;
import com.cloud.baowang.system.po.site.SiteInfoChangeRecordPO;
import com.cloud.baowang.system.repositories.site.change.SiteInfoChangeRecordRepository;
import com.cloud.baowang.system.service.SystemParamService;
import com.cloud.baowang.system.service.exchange.SystemCurrencyInfoService;
import com.cloud.baowang.system.service.language.LanguageManagerService;
import com.cloud.baowang.system.service.operations.SkinInfoService;
import com.cloud.baowang.system.util.JsonComparator;
import com.cloud.baowang.system.util.JsonDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mufan
 * @Date 2025.04.04
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteInfoChangeRecordService extends ServiceImpl<SiteInfoChangeRecordRepository, SiteInfoChangeRecordPO> {
    private final SiteInfoChangeRecordRepository siteInfoChangeRecordRepository;
    private final SystemParamService systemParamService;
    private final LanguageManagerService languageManagerService;
    private final SystemCurrencyInfoService systemCurrencyInfoService;

    public Page<SiteInfoChangeRecordVO> querySiteInfoChangeRecord(SiteInfoChangeRequestVO siteInfoChangeRequestVO) {
        Page<SiteInfoChangeRecordPO> page = new Page<>(siteInfoChangeRequestVO.getPageNumber(), siteInfoChangeRequestVO.getPageSize());
        LambdaQueryWrapper<SiteInfoChangeRecordPO> lqw = initQueryData(siteInfoChangeRequestVO);
        Page<SiteInfoChangeRecordPO> siteAdminPOPage = siteInfoChangeRecordRepository.selectPage(page, lqw);
        Page<SiteInfoChangeRecordVO> siteAdminPageVOPage = new Page<>();
        BeanUtils.copyProperties(siteAdminPOPage, siteAdminPageVOPage);
        List<SiteInfoChangeRecordVO> list = siteAdminPOPage.getRecords().stream().map(record -> {
            SiteInfoChangeRecordVO siteAdminPageVO = new SiteInfoChangeRecordVO();
            BeanUtils.copyProperties(record, siteAdminPageVO);
            Gson gson = new Gson();
            if (StringUtils.isNotBlank(record.getChangeAfter())){
                List<JsonDifference> jsonDifferences = gson.fromJson(record.getChangeAfter(), new TypeToken<List<JsonDifference>>(){}.getType());
                siteAdminPageVO.setChangeAfter(jsonDifferences);
            }
            siteAdminPageVO.setOptionStatusStr(SiteOptionStatusEnum.fromCode(record.getOptionStatus()).getname());
            siteAdminPageVO.setOptionTypeStr(SiteOptionTypeEnum.fromCode(record.getOptionType()).getname());
            return siteAdminPageVO;
        }).toList();
        siteAdminPageVOPage.setRecords(list);
        return siteAdminPageVOPage;
    }

    private LambdaQueryWrapper<SiteInfoChangeRecordPO> initQueryData(SiteInfoChangeRequestVO siteInfoChangeRequestVO) {
        LambdaQueryWrapper<SiteInfoChangeRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(siteInfoChangeRequestVO.getOptionName()), SiteInfoChangeRecordPO::getOptionName, siteInfoChangeRequestVO.getOptionName());
        lqw.like(StringUtils.isNotBlank(siteInfoChangeRequestVO.getOptionCode()), SiteInfoChangeRecordPO::getOptionCode, siteInfoChangeRequestVO.getOptionCode());
        lqw.like(StringUtils.isNotBlank(siteInfoChangeRequestVO.getOptionModelName()), SiteInfoChangeRecordPO::getOptionModelName, siteInfoChangeRequestVO.getOptionModelName());
        lqw.eq(ObjectUtil.isNotNull(siteInfoChangeRequestVO.getOptionType()), SiteInfoChangeRecordPO::getOptionType, siteInfoChangeRequestVO.getOptionType());
        lqw.eq(ObjectUtil.isNotNull(siteInfoChangeRequestVO.getOptionStatus()), SiteInfoChangeRecordPO::getOptionStatus, siteInfoChangeRequestVO.getOptionStatus());
        lqw.eq(StringUtils.isNotBlank(siteInfoChangeRequestVO.getLoginIp()), SiteInfoChangeRecordPO::getLoginIp, siteInfoChangeRequestVO.getLoginIp());
        lqw.eq(StringUtils.isNotBlank(siteInfoChangeRequestVO.getOperator()), SiteInfoChangeRecordPO::getCreator, siteInfoChangeRequestVO.getOperator());
        lqw.ge(ObjectUtil.isNotNull(siteInfoChangeRequestVO.getStartTime()), SiteInfoChangeRecordPO::getCreatedTime, siteInfoChangeRequestVO.getStartTime());
        lqw.le(ObjectUtil.isNotNull(siteInfoChangeRequestVO.getEndTime()), SiteInfoChangeRecordPO::getCreatedTime, siteInfoChangeRequestVO.getEndTime());
        lqw.orderByDesc(SiteInfoChangeRecordPO::getCreatedTime);
        return lqw;
    }

    @Async
    public void addSiteInfoChangeRequestVO(SiteInfoChangeRecordReqVO siteInfoChangeRecordReqVO) {
        log.info("异步方法线程: " + Thread.currentThread().getName());
        SiteInfoChangeRecordPO po =new SiteInfoChangeRecordPO();
        BeanUtils.copyProperties(siteInfoChangeRecordReqVO, po);
        po.setUpdater(po.getCreator());
        Long time=System.currentTimeMillis();
        po.setCreatedTime(time);
        po.setUpdatedTime(time);
        if (siteInfoChangeRecordReqVO.getOptionStatus().equals(SiteOptionStatusEnum.fail.getCode())){
            siteInfoChangeRecordRepository.insert(po);
        }else{
            List<JsonDifference> differences =new ArrayList<>();
            siteInfoChangeRecordReqVO.getData().forEach(e ->{
                List<JsonDifference>  body = null;
                try {
                    body = JsonComparator.compareJson(e.getChangeBeforeObj(),e.getChangeAfterObj());
                } catch (Exception ex) {
                    throw new BaowangDefaultException("系统错误");
                }
                    body  = Optional.ofNullable(body)
                            .map(list -> list.stream()
                                    .map(data ->{
                                        String clounmName= e.getColumnNameMap().get(data.getPath());
                                        if (StringUtils.isNotBlank(clounmName)){
                                            data.setPathName(clounmName);
                                            data.setChangeType(e.getChangeType());
                                            return data;
                                        }else{
                                            return null;
                                        }
                                    })
                                    .collect(Collectors.toList()))
                            .orElse(List.of());
                differences.addAll(body);
            });
            if (CollectionUtils.isNotEmpty(differences)){
                Gson gson = new Gson();
                String json = gson.toJson(differences);
                po.setChangeAfter(json);
                siteInfoChangeRecordRepository.insert(po);
            }
        }
        log.info("站点:{}站点修改记录异步",siteInfoChangeRecordReqVO);
    }

    public SiteInfoChangeRecordReqVO initSiteInfoChangeRecordReqVO(String optionCode,String optionName,String optionModelName,String loginIp,
                                                                   List<SiteInfoChangeBodyVO> data,
                                                                    Integer optionType,Integer optionStatus,String creator){
        SiteInfoChangeRecordReqVO sd=new SiteInfoChangeRecordReqVO();
        if (StringUtils.isNotBlank(optionCode)){
            sd.setOptionCode(optionCode);
        }
        if (StringUtils.isNotBlank(optionName)){
            sd.setOptionName(optionName);
        }
        if (StringUtils.isNotBlank(optionModelName)){
            sd.setOptionModelName(optionModelName);
        }
        if (StringUtils.isNotBlank(loginIp)){
            sd.setLoginIp(loginIp);
        }
        if (CollectionUtils.isNotEmpty(data)){
            sd.setData(data);
        }
        if (optionType !=null){
            sd.setOptionType(optionType);
        }
        if (optionStatus !=null){
            sd.setOptionStatus(optionStatus);
        }
        if (StringUtils.isNotBlank(creator)){
            sd.setCreator(creator);
        }
        return sd;
    }

    /**
     * 站点基础配置转换成chang需要的内容
     * @param basicVO
     * @return
     */
    public SiteBasicChangeVO getSiteBase(SiteBasicVO basicVO){
        SiteBasicChangeVO basicChangeVO = new SiteBasicChangeVO();
        BeanUtils.copyProperties(basicVO,basicChangeVO);
        SystemParamPO systemParamPO=systemParamService.getSystemParamByTypeAndCode(CommonConstant.SITE_TYPE,basicChangeVO.getSiteType());
        SystemParamPO commissionPlanPO=systemParamService.getSystemParamByTypeAndCode(CommonConstant.COMMISSION_PLAN,basicChangeVO.getCommissionPlan());

        if (systemParamPO != null){
            basicChangeVO.setSiteType(systemParamPO.getValueDesc());
        }
        if (commissionPlanPO!=null){
            basicChangeVO.setCommissionPlan(commissionPlanPO.getValueDesc());
        }
        List<SiteLanguageVO> langlist=languageManagerService.getSiteLanguageDownBox(null).getData();
        Map<String, String> langlistMap = Optional.ofNullable(langlist)
                .map(s -> s.stream().collect(Collectors.toMap(SiteLanguageVO::getCode, SiteLanguageVO::getName))).orElse(Maps.newHashMap());
        List<String> lang=new ArrayList();
        basicChangeVO.getLanguage().forEach(e ->{
            lang.add(langlistMap.get(e));
        });
        basicChangeVO.setLanguage(lang);
        List<SystemCurrencyInfoRespVO> curlist= systemCurrencyInfoService.selectAll().getData();
        Map<String, String> curlistMap = Optional.ofNullable(curlist)
                .map(s -> s.stream().collect(Collectors.toMap(SystemCurrencyInfoRespVO::getCurrencyCode, SystemCurrencyInfoRespVO::getCurrencyNameI18))).orElse(Maps.newHashMap());
        List<String> cur=new ArrayList();
        basicChangeVO.getCurrency().forEach(e ->{
            cur.add(I18nMessageUtil.getI18NMessageInAdvice(curlistMap.get(e)));
        });
        basicChangeVO.setCurrency(cur);
        return basicChangeVO;
    }
    
    public List<JsonDifferenceVO> getJsonDifferenceList(SiteInfoChangeBodyVO siteInfoChangeBodyVO) {
        List<JsonDifference> differences =new ArrayList<>();
        List<JsonDifferenceVO> dataValues =new ArrayList<>();
        try {
            differences = JsonComparator.compareJson(siteInfoChangeBodyVO.getChangeBeforeObj(),siteInfoChangeBodyVO.getChangeAfterObj());
        } catch (Exception ex) {
            throw new BaowangDefaultException("系统错误");
        }
        if (siteInfoChangeBodyVO.getChangeType().equals(SiteChangeTypeEnum.VenueAuthor.getname())||
                siteInfoChangeBodyVO.getChangeType().equals(SiteChangeTypeEnum.RechargeAuthor.getname())||
                siteInfoChangeBodyVO.getChangeType().equals(SiteChangeTypeEnum.withdrawAuthor.getname())){
            dataValues  = Optional.ofNullable(differences)
                    .map(list -> list.stream()
                            .map(data ->{
                                JsonDifferenceVO vodata= new JsonDifferenceVO();
                                Object newValue=data.getNewValue();
                                Object oldValue=data.getOldValue();
                                if (Objects.nonNull(newValue)){
                                    Map<String,Object> newvalue=new HashMap<>();
                                    newvalue.put(data.getPath(),newValue);
                                    vodata.setNewValue(newvalue);
                                }
                                if (Objects.nonNull(oldValue)){
                                    Map<String,Object> old=new HashMap<>();
                                    old.put(data.getPath(),oldValue);
                                    vodata.setOldValue(old);
                                }
                                vodata.setPath(data.getPath());
                                vodata.setPathName(siteInfoChangeBodyVO.getColumnNameMap().get(SitClounmDefaultEnum.baseClounm.getCode()));
                                vodata.setChangeType(siteInfoChangeBodyVO.getChangeType());
                                return vodata;
                            })
                            .collect(Collectors.toList()))
                    .orElse(List.of()); // 如
        }else{
            dataValues  = Optional.ofNullable(differences)
                    .map(list -> list.stream()
                            .map(data ->{
                                String clounmName= siteInfoChangeBodyVO.getColumnNameMap().get(data.getPath());
                                if (StringUtils.isNotBlank(clounmName)){
                                    JsonDifferenceVO vodata= new JsonDifferenceVO();
                                    data.setPathName(clounmName);
                                    data.setChangeType(siteInfoChangeBodyVO.getChangeType());
                                    vodata.setPath(data.getPath());;
                                    vodata.setOldValue(data.getOldValue());;
                                    vodata.setNewValue(data.getNewValue());
                                    vodata.setPathName(data.getPathName());
                                    vodata.setChangeType(data.getChangeType());
                                    return vodata;
                                }else{
                                    return null;
                                }
                            })
                            .collect(Collectors.toList()))
                    .orElse(List.of()); // 如
        }
        return dataValues;
    }


    /**
     * 为特殊的存款和提款单独的转换
     * @param siteInfoChangeBodyVO
     * @return
     */
    public List<JsonDifferenceVO> getJsonDifferenceListForRecharger(SiteInfoChangeBodyVO siteInfoChangeBodyVO) {
        List<JsonDifference> differences =new ArrayList<>();
        List<JsonDifferenceVO> dataValues =new ArrayList<>();
        try {
            differences = JsonComparator.compareJson(siteInfoChangeBodyVO.getChangeBeforeObj(),siteInfoChangeBodyVO.getChangeAfterObj());
        } catch (Exception ex) {
            throw new BaowangDefaultException("系统错误");
        }
        dataValues  = Optional.ofNullable(differences)
                    .map(list -> list.stream()
                            .map(data ->{
                                JsonDifferenceVO vodata= new JsonDifferenceVO();
                                Object newValue=data.getNewValue();
                                Object oldValue=data.getOldValue();
                                if (Objects.nonNull(newValue)){
                                   String curreny= siteInfoChangeBodyVO.getColumnNameMap().get(data.getPath());
                                    curreny=curreny+":{"+data.getPath()+":"+newValue.toString()+"}";
                                    vodata.setNewValue(curreny);
                                }
                                if (Objects.nonNull(oldValue)){
                                    String curreny= siteInfoChangeBodyVO.getColumnNameMap().get(data.getPath());
                                    curreny=curreny+":{"+data.getPath()+":"+oldValue.toString()+"}";
                                    vodata.setOldValue(curreny);
                                }
                                vodata.setPath(data.getPath());
                                vodata.setPathName(siteInfoChangeBodyVO.getColumnNameMap().get(SitClounmDefaultEnum.baseClounm.getCode()));
                                vodata.setChangeType(siteInfoChangeBodyVO.getChangeType());
                                return vodata;
                            })
                            .collect(Collectors.toList()))
                    .orElse(List.of()); // 如
        return dataValues;
    }





    @Async
    public void addJsonDifferenceList(SiteInfoChangeRecordListReqVO vo) {
        log.info("异步方法线程: " + Thread.currentThread().getName());
        SiteInfoChangeRecordPO po =new SiteInfoChangeRecordPO();
        BeanUtils.copyProperties(vo, po);
        po.setUpdater(po.getCreator());
        Long time=System.currentTimeMillis();
        po.setCreatedTime(time);
        po.setUpdatedTime(time);
        List<JsonDifferenceVO> data=vo.getData();
        if (CollectionUtils.isNotEmpty(data)){
            Gson gson = new Gson();
            String json = gson.toJson(data);
            po.setChangeAfter(json);
            siteInfoChangeRecordRepository.insert(po);
        }
    }
}
