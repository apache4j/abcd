package com.cloud.baowang.user.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.LanguageEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.I18nMsgBindUtil;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.i18n.dto.I18NMessageDTO;
import com.cloud.baowang.user.api.vo.medal.MedalInfoNewReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoDetailRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoStatusReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoUpdateReqVO;
import com.cloud.baowang.user.po.MedalInfoPO;
import com.cloud.baowang.user.po.SiteMedalInfoPO;
import com.cloud.baowang.user.po.SiteMedalOperLogPO;
import com.cloud.baowang.user.repositories.MedalInfoRepository;
import com.cloud.baowang.user.repositories.SiteMedalInfoRepository;
import com.cloud.baowang.user.util.MinioFileService;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Desciption: 勋章信息
 * @Author: Ford
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteMedalInfoService extends ServiceImpl<SiteMedalInfoRepository, SiteMedalInfoPO> {

    private final MinioFileService minioFileService;

    private  final MedalInfoRepository medalInfoRepository;

    private final SiteMedalOperLogService siteMedalOperLogService;

    private final I18nApi i18nApi;

    private final SystemParamApi systemParamApi;

    private final MedalRewardConfigService medalRewardConfigService;

    public ResponseVO<Page<SiteMedalInfoRespVO>> selectPage(SiteMedalInfoReqVO siteMedalInfoReqVO) {
        Page<SiteMedalInfoPO> page = new Page<SiteMedalInfoPO>(siteMedalInfoReqVO.getPageNumber(), siteMedalInfoReqVO.getPageSize());
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteMedalInfoReqVO.getSiteCode());
        if(StringUtils.hasText(siteMedalInfoReqVO.getMedalName())){
            lqw.like(SiteMedalInfoPO::getMedalName, siteMedalInfoReqVO.getMedalName());
        }
        if(StringUtils.hasText(siteMedalInfoReqVO.getMedalCode())){
            lqw.eq(SiteMedalInfoPO::getMedalCode, siteMedalInfoReqVO.getMedalCode());
        }
        if(siteMedalInfoReqVO.getStatus()!=null){
            lqw.eq(SiteMedalInfoPO::getStatus, siteMedalInfoReqVO.getStatus());
        }
        if(!StringUtils.hasText(siteMedalInfoReqVO.getOrderField())&&!StringUtils.hasText(siteMedalInfoReqVO.getOrderType())){
            lqw.orderByAsc(SiteMedalInfoPO::getSortOrder);
        }
        if("updatedTime".equals(siteMedalInfoReqVO.getOrderField())){
            if("asc".equals(siteMedalInfoReqVO.getOrderType())){
                lqw.orderByAsc(SiteMedalInfoPO::getUpdatedTime);
            }
            if("desc".equals(siteMedalInfoReqVO.getOrderType())){
                lqw.orderByDesc(SiteMedalInfoPO::getUpdatedTime);
            }
        }
        IPage<SiteMedalInfoPO> siteMedalInfoIPage =  this.baseMapper.selectPage(page,lqw);
        Page<SiteMedalInfoRespVO> siteMedalInfoRespVOPage=new Page<SiteMedalInfoRespVO>(siteMedalInfoReqVO.getPageNumber(), siteMedalInfoReqVO.getPageSize());
        siteMedalInfoRespVOPage.setTotal(siteMedalInfoIPage.getTotal());
        siteMedalInfoRespVOPage.setPages(siteMedalInfoIPage.getPages());
        List<SiteMedalInfoRespVO> resultLists= Lists.newArrayList();
        for(SiteMedalInfoPO siteMedalInfoPO:siteMedalInfoIPage.getRecords()){
            SiteMedalInfoRespVO siteMedalInfoRespVO=new SiteMedalInfoRespVO();
            BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoRespVO);
            siteMedalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getActivatedPic()));
            siteMedalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getInactivatedPic()));
            siteMedalInfoRespVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            siteMedalInfoRespVO.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
            resultLists.add(siteMedalInfoRespVO);
        }
        siteMedalInfoRespVOPage.setRecords(resultLists);
        return ResponseVO.success(siteMedalInfoRespVOPage);
    }


    public ResponseVO<List<SiteMedalInfoRespVO>> selectBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteCode);
        List<SiteMedalInfoPO> siteMedalInfoPOs=this.baseMapper.selectList(lqw);
        List<SiteMedalInfoRespVO> resultLists= Lists.newArrayList();
        for(SiteMedalInfoPO siteMedalInfoPO:siteMedalInfoPOs){
            SiteMedalInfoRespVO siteMedalInfoRespVO=new SiteMedalInfoRespVO();
            BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoRespVO);
            siteMedalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getActivatedPic()));
            siteMedalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getInactivatedPic()));
            siteMedalInfoRespVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            siteMedalInfoRespVO.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
            resultLists.add(siteMedalInfoRespVO);
        }
        return ResponseVO.success(resultLists);
    }
    public ResponseVO<List<SiteMedalInfoRespVO>> selectValidBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteCode);
        lqw.eq(SiteMedalInfoPO::getStatus,EnableStatusEnum.ENABLE.getCode());
        List<SiteMedalInfoPO> siteMedalInfoPOs=this.baseMapper.selectList(lqw);
        List<SiteMedalInfoRespVO> resultLists= Lists.newArrayList();
        for(SiteMedalInfoPO siteMedalInfoPO:siteMedalInfoPOs){
            SiteMedalInfoRespVO siteMedalInfoRespVO=new SiteMedalInfoRespVO();
            BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoRespVO);
            siteMedalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getActivatedPic()));
            siteMedalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getInactivatedPic()));
            siteMedalInfoRespVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            siteMedalInfoRespVO.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
            resultLists.add(siteMedalInfoRespVO);
        }
        return ResponseVO.success(resultLists);
    }



    public ResponseVO<List<SiteMedalInfoRespVO>> listAllBySort(String siteCode) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteCode);
        lqw.orderByAsc(SiteMedalInfoPO::getSortOrder);
        List<SiteMedalInfoPO> siteMedalInfoPOs=this.baseMapper.selectList(lqw);
        List<SiteMedalInfoRespVO> resultLists= Lists.newArrayList();
        for(SiteMedalInfoPO siteMedalInfoPO:siteMedalInfoPOs){
            SiteMedalInfoRespVO siteMedalInfoRespVO=new SiteMedalInfoRespVO();
            BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoRespVO);
            siteMedalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getActivatedPic()));
            siteMedalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getInactivatedPic()));
            siteMedalInfoRespVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            siteMedalInfoRespVO.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
            resultLists.add(siteMedalInfoRespVO);
        }
        resultLists.sort(Comparator.comparingInt(SiteMedalInfoRespVO::getSortOrder));
        return ResponseVO.success(resultLists);
    }


    public ResponseVO<SiteMedalInfoRespVO> selectByCond(SiteMedalInfoCondReqVO siteMedalInfoCondReqVO) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteMedalInfoCondReqVO.getSiteCode());
        lqw.eq(SiteMedalInfoPO::getMedalCode,siteMedalInfoCondReqVO.getMedalCode());
        SiteMedalInfoPO siteMedalInfoPO=this.baseMapper.selectOne(lqw);
        if(siteMedalInfoPO!=null){
            SiteMedalInfoRespVO siteMedalInfoRespVO=new SiteMedalInfoRespVO();
            BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoRespVO);
            siteMedalInfoRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getActivatedPic()));
            siteMedalInfoRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoRespVO.getInactivatedPic()));
            return ResponseVO.success(siteMedalInfoRespVO);
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }


    public ResponseVO<Void> insert(MedalInfoNewReqVO medalInfoNewReqVO) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getMedalName, medalInfoNewReqVO.getMedalName());
        SiteMedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld==null){
            SiteMedalInfoPO medalInfo=new SiteMedalInfoPO();
            BeanUtils.copyProperties(medalInfoNewReqVO,medalInfo);
            medalInfo.setStatus(EnableStatusEnum.ENABLE.getCode());
            medalInfo.setCreator(medalInfoNewReqVO.getOperatorUserNo());
            medalInfo.setUpdater(medalInfoNewReqVO.getOperatorUserNo());
            medalInfo.setCreatedTime(System.currentTimeMillis());
            medalInfo.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.insert(medalInfo);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_IS_EXIST);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> updateByInfo(SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getId, siteMedalInfoUpdateReqVO.getId());
        SiteMedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld!=null){
            SiteMedalInfoPO medalInfoNew=new SiteMedalInfoPO();
            BeanUtils.copyProperties(medalInfoOld,medalInfoNew);
            BeanUtils.copyProperties(siteMedalInfoUpdateReqVO,medalInfoNew);
            medalInfoNew.setUpdater(siteMedalInfoUpdateReqVO.getOperatorUserNo());
            medalInfoNew.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.updateById(medalInfoNew);
            // 勋章变更记录
            SiteMedalOperLogPO siteMedalOperLogPOCommon=new SiteMedalOperLogPO();
            siteMedalOperLogPOCommon.setSiteMedalId(medalInfoOld.getId());
            siteMedalOperLogPOCommon.setMedalCode(medalInfoOld.getMedalCode());
            siteMedalOperLogPOCommon.setMedalName(medalInfoOld.getMedalName());
            siteMedalOperLogPOCommon.setSiteCode(medalInfoOld.getSiteCode());
            siteMedalOperLogPOCommon.setOperTime(System.currentTimeMillis());
            siteMedalOperLogPOCommon.setCreator(siteMedalInfoUpdateReqVO.getOperatorUserNo());
            siteMedalOperLogPOCommon.setCreatedTime(System.currentTimeMillis());

            List<SiteMedalOperLogPO> siteMedalOperLogPOS=Lists.newArrayList();
            Map<String,String> operationI18CodeMap = systemParamApi.getSystemParamMap(CommonConstant.MEDAL_OPERATION).getData();
            checkChangeField(medalInfoOld,medalInfoNew,siteMedalOperLogPOCommon,siteMedalOperLogPOS,operationI18CodeMap);
            checkMultiLangName(siteMedalInfoUpdateReqVO,siteMedalOperLogPOCommon,siteMedalOperLogPOS,operationI18CodeMap);
            checkMultiLangDesc(siteMedalInfoUpdateReqVO,siteMedalOperLogPOCommon,siteMedalOperLogPOS,operationI18CodeMap);
            if(!CollectionUtils.isEmpty(siteMedalOperLogPOS)){
                siteMedalOperLogService.saveBatch(siteMedalOperLogPOS);
            }

            Map<String,List<I18nMsgFrontVO>> i18nMsgFrontMap=Maps.newHashMap();
            i18nMsgFrontMap.put(siteMedalInfoUpdateReqVO.getMedalNameI18(),siteMedalInfoUpdateReqVO.getMedalNameI18List());
            i18nMsgFrontMap.put(siteMedalInfoUpdateReqVO.getMedalDescI18(),siteMedalInfoUpdateReqVO.getMedalDescI18List());
            i18nApi.update(i18nMsgFrontMap);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    /**
     * 解锁条件说明
     * @param siteMedalInfoUpdateReqVO 修改参数
     * @param siteMedalOperLogPOCommon 操作日志
     * @param siteMedalOperLogPOS 操作记录
     * @param operationI18CodeMap  操作项I18N
     */
    private void checkMultiLangDesc(SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO,SiteMedalOperLogPO siteMedalOperLogPOCommon,List<SiteMedalOperLogPO> siteMedalOperLogPOS, Map<String,String> operationI18CodeMap) {
        ResponseVO<List<I18NMessageDTO>>  i18nApiMessageDtoResp=i18nApi.getMessageByKey(siteMedalInfoUpdateReqVO.getMedalDescI18());
        if(i18nApiMessageDtoResp.isOk()){
            List<I18NMessageDTO> i18NMessageDTOSOld=i18nApiMessageDtoResp.getData();
            for(I18nMsgFrontVO i18nMsgFrontVO:siteMedalInfoUpdateReqVO.getMedalDescI18List()){
                Optional<I18NMessageDTO> i18NMessageDTOOptional=i18NMessageDTOSOld.stream().filter(o->o.getLanguage().equals(i18nMsgFrontVO.getLanguage())).findFirst();
                if(i18NMessageDTOOptional.isPresent()){
                    I18NMessageDTO i18NMessageDTO=i18NMessageDTOOptional.get();
                    if(!i18NMessageDTO.getMessage().equals(i18nMsgFrontVO.getMessage())){
                        SiteMedalOperLogPO siteMedalOperCompare=new SiteMedalOperLogPO();
                        String id=siteMedalOperCompare.getId();
                        BeanUtils.copyProperties(siteMedalOperLogPOCommon,siteMedalOperCompare);
                        siteMedalOperCompare.setId(id);
                        siteMedalOperCompare.setOperItem(SiteMedalInfoPO.Fields.medalDesc);
                       // MedalOperationEnum medalOperationEnum=MedalOperationEnum.parseByFieldCode(SiteMedalInfoPO.Fields.medalDesc);
                        siteMedalOperCompare.setOperItemI18(operationI18CodeMap.get(SiteMedalInfoPO.Fields.medalDesc));
                        siteMedalOperCompare.setOperBefore(i18NMessageDTO.getMessage());
                        siteMedalOperCompare.setOperAfter(i18nMsgFrontVO.getMessage());
                        siteMedalOperLogPOS.add(siteMedalOperCompare);
                    }
                }
            }
        }

    }


    /**
     * 勋章说明变更
     * @param siteMedalInfoUpdateReqVO
     * @param siteMedalOperLogPOCommon
     * @param siteMedalOperLogPOS
     * @param operationI18CodeMap
     */
    private void checkMultiLangName(SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO,SiteMedalOperLogPO siteMedalOperLogPOCommon,List<SiteMedalOperLogPO> siteMedalOperLogPOS, Map<String,String> operationI18CodeMap) {
       //当前修改的多语言
        ResponseVO<List<I18NMessageDTO>>  i18nApiMessageDtoResp=i18nApi.getMessageByKey(siteMedalInfoUpdateReqVO.getMedalNameI18());
        if(!i18nApiMessageDtoResp.isOk()) {
            return;
        }

        //所有勋章多语言
        ResponseVO<List<I18NMessageDTO>>  i18nApiMessageLikeDtoResp=i18nApi.getMessageLikeKey(I18MsgKeyEnum.MEDAL_NAME.getCode());
        LambdaQueryWrapper<SiteMedalInfoPO> lambdaQueryWrapper =new LambdaQueryWrapper<SiteMedalInfoPO>();
        lambdaQueryWrapper.eq(SiteMedalInfoPO::getSiteCode,siteMedalInfoUpdateReqVO.getSiteCode());
        List<SiteMedalInfoPO> siteMedalInfoPOS=this.baseMapper.selectList(lambdaQueryWrapper);
        List<String> medalNameI18Lists=siteMedalInfoPOS.stream().map(SiteMedalInfoPO::getMedalNameI18).toList();


        List<I18NMessageDTO> i18nApiMessageLikeDtos =i18nApiMessageLikeDtoResp.getData();
        i18nApiMessageLikeDtos=i18nApiMessageLikeDtos.stream().filter(o->!o.getMessageKey().equals(siteMedalInfoUpdateReqVO.getMedalNameI18())).toList();
        //存在当前站点中
        i18nApiMessageLikeDtos=i18nApiMessageLikeDtos.stream().filter(o->{
            if(medalNameI18Lists.stream().filter(t->t.equals(o.getMessageKey())).findFirst().isPresent()){
                return true;
            }else {
                return false;
            }
        }).toList();

        List<I18NMessageDTO> i18NMessageDTOSOld=i18nApiMessageDtoResp.getData();
        for(I18nMsgFrontVO i18nMsgFrontVO:siteMedalInfoUpdateReqVO.getMedalNameI18List()){
            //同语言下存在相同勋章名称
            if(i18nApiMessageLikeDtos.stream().filter(o->o.getMessage().equals(i18nMsgFrontVO.getMessage())&&o.getLanguage().equals(i18nMsgFrontVO.getLanguage())).findFirst().isPresent()){
                log.info("相同语言下存在重复勋章名称:{}",i18nMsgFrontVO.getMessage());
                throw  new BaowangDefaultException(ResultCode.MEDAL_HAS_EXISTS);
            }

            Optional<I18NMessageDTO> i18NMessageDTOOptional=i18NMessageDTOSOld.stream().filter(o->o.getLanguage().equals(i18nMsgFrontVO.getLanguage())).findFirst();
            if(i18NMessageDTOOptional.isPresent()){
                I18NMessageDTO i18NMessageDTO=i18NMessageDTOOptional.get();
                if(!i18NMessageDTO.getMessage().equals(i18nMsgFrontVO.getMessage())){
                    SiteMedalOperLogPO siteMedalOperCompare=new SiteMedalOperLogPO();
                    String id=siteMedalOperCompare.getId();
                    BeanUtils.copyProperties(siteMedalOperLogPOCommon,siteMedalOperCompare);
                    siteMedalOperCompare.setId(id);
                    siteMedalOperCompare.setOperItem(SiteMedalInfoPO.Fields.medalName);
                   // MedalOperationEnum medalOperationEnum=MedalOperationEnum.parseByFieldCode(SiteMedalInfoPO.Fields.medalName);
                    siteMedalOperCompare.setOperItemI18(operationI18CodeMap.get(SiteMedalInfoPO.Fields.medalName));
                    siteMedalOperCompare.setOperBefore(i18NMessageDTO.getMessage());
                    siteMedalOperCompare.setOperAfter(i18nMsgFrontVO.getMessage());
                    siteMedalOperLogPOS.add(siteMedalOperCompare);
                }
            }
        }
    }

    /**
     * 勋章变更记录
     * @param medalInfoOld 数据库信息
     * @param medalInfoNew 修改后信息
     * @param siteMedalOperLogPOCommon  公共参数
     * @param operationI18CodeMap 操作项多语言
     */
    private void checkChangeField(SiteMedalInfoPO medalInfoOld, SiteMedalInfoPO medalInfoNew,SiteMedalOperLogPO siteMedalOperLogPOCommon, List<SiteMedalOperLogPO> siteMedalOperLogPOS, Map<String,String> operationI18CodeMap) {
        JSONObject beforeJson= JSONObject.parseObject(JSON.toJSONString(medalInfoOld));
        JSONObject afterJson=JSONObject.parseObject(JSON.toJSONString(medalInfoNew));
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.condNum1,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.condNum2,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.typingMultiple,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.rewardAmount,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.activatedPic,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,SiteMedalInfoPO.Fields.inactivatedPic,operationI18CodeMap);
    }

    /**
     * 字段对比 是否修改
     * @param siteMedalOperLogPOS 对比后的集合
     * @param siteMedalOperLogPO 公共参数
     * @param beforeJson 变化前数据
     * @param afterJson 变化后数据
     * @param fieldCode 变化字段
     */
    private void checkFiled( List<SiteMedalOperLogPO> siteMedalOperLogPOS,SiteMedalOperLogPO siteMedalOperLogPO,JSONObject beforeJson, JSONObject afterJson, String fieldCode,Map<String,String> operationI18CodeMap) {
        String beforeVal=beforeJson.getString(fieldCode);
        String afterVal=afterJson.getString(fieldCode);
        if(fieldCode.equalsIgnoreCase(SiteMedalInfoPO.Fields.activatedPic)||fieldCode.equalsIgnoreCase(SiteMedalInfoPO.Fields.inactivatedPic)){
            if(StringUtils.hasText(beforeVal)){
                beforeVal=minioFileService.getFileUrlByKey(beforeVal);
            }
            if(StringUtils.hasText(afterVal)){
                afterVal=minioFileService.getFileUrlByKey(afterVal);
            }
        }
        if(fieldCode.equalsIgnoreCase(SiteMedalInfoPO.Fields.rewardAmount)||fieldCode.equalsIgnoreCase(SiteMedalInfoPO.Fields.typingMultiple)){
            if(StringUtils.hasText(beforeVal)){
                beforeVal=new BigDecimal(beforeVal).setScale(2, RoundingMode.CEILING).toString();
            }
            if(StringUtils.hasText(afterVal)){
                afterVal=new BigDecimal(afterVal).setScale(2, RoundingMode.CEILING).toString();
            }
        }
        if(!ObjectUtils.nullSafeEquals(beforeVal,afterVal)){
            SiteMedalOperLogPO siteMedalOperCompare=new SiteMedalOperLogPO();
            String id=siteMedalOperCompare.getId();
            BeanUtils.copyProperties(siteMedalOperLogPO,siteMedalOperCompare);
            siteMedalOperCompare.setId(id);
            siteMedalOperCompare.setOperItem(fieldCode);
           // MedalOperationEnum medalOperationEnum=MedalOperationEnum.parseByFieldCode(fieldCode);
            siteMedalOperCompare.setOperItemI18(operationI18CodeMap.get(fieldCode));
            siteMedalOperCompare.setOperBefore(beforeVal);
            siteMedalOperCompare.setOperAfter(afterVal);
            siteMedalOperLogPOS.add(siteMedalOperCompare);
        }
    }

    /**
     * 启用禁用
     * @param siteMedalInfoStatusReqVO
     * @return
     */
    @Transactional
    public ResponseVO<Void> enableOrDisable(SiteMedalInfoStatusReqVO siteMedalInfoStatusReqVO) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getId, siteMedalInfoStatusReqVO.getId());
        SiteMedalInfoPO medalInfoOld= this.baseMapper.selectOne(lqw);
        if(medalInfoOld!=null){
            Integer beforeStatus=medalInfoOld.getStatus();
            Integer afterStatus;
            if(Objects.equals(EnableStatusEnum.ENABLE.getCode(), medalInfoOld.getStatus())){
                afterStatus=EnableStatusEnum.DISABLE.getCode();
            }else {
                afterStatus=EnableStatusEnum.ENABLE.getCode();
            }
            medalInfoOld.setStatus(afterStatus);
            medalInfoOld.setUpdatedTime(System.currentTimeMillis());
            medalInfoOld.setUpdater(siteMedalInfoStatusReqVO.getOperatorUserNo());
            this.baseMapper.updateById(medalInfoOld);


            SiteMedalOperLogPO siteMedalOperLogPO=new SiteMedalOperLogPO();
            siteMedalOperLogPO.setSiteMedalId(medalInfoOld.getId());
            siteMedalOperLogPO.setMedalCode(medalInfoOld.getMedalCode());
            siteMedalOperLogPO.setMedalName(medalInfoOld.getMedalName());
            siteMedalOperLogPO.setSiteCode(medalInfoOld.getSiteCode());
            siteMedalOperLogPO.setOperItem(SiteMedalInfoPO.Fields.status);
            siteMedalOperLogPO.setOperBefore(beforeStatus.toString());
            siteMedalOperLogPO.setOperAfter(afterStatus.toString());
            siteMedalOperLogPO.setOperTime(System.currentTimeMillis());
            siteMedalOperLogPO.setCreator(siteMedalInfoStatusReqVO.getOperatorUserNo());
            siteMedalOperLogPO.setCreatedTime(System.currentTimeMillis());
            siteMedalOperLogService.recordOperLog(siteMedalOperLogPO);
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
    }

    /**
     * 勋章初始化
     * @param siteCode 站点
     * @return
     */
    public ResponseVO<Boolean> init(String siteCode) {
        LambdaQueryWrapper<SiteMedalInfoPO> lqw = new LambdaQueryWrapper<SiteMedalInfoPO>();
        lqw.eq(SiteMedalInfoPO::getSiteCode,siteCode);
        Long countNum = this.baseMapper.selectCount(lqw);
        if(countNum>=1){
            log.info("站点:{},数据已经初始化,无需操作",siteCode);
            return ResponseVO.success(Boolean.TRUE);
        }
        LambdaQueryWrapper<MedalInfoPO> lqwSys = new LambdaQueryWrapper<MedalInfoPO>();
        lqwSys.orderByAsc(MedalInfoPO::getSortOrder);
        List<MedalInfoPO> medalInfoPOS = medalInfoRepository.selectList(lqwSys);
        log.info("站点:{}开始对勋章进行初始化操作",siteCode);
        if(!CollectionUtils.isEmpty(medalInfoPOS)){
            List<SiteMedalInfoPO> siteMediaInfos=Lists.newArrayList();
            List<MedalInfoPO> medalInfoPoCns =  medalInfoPOS.stream().filter(o->o.getLanguageCode().equals(LanguageEnum.ZH_CN.getLang())).toList();
            medalInfoPoCns=medalInfoPoCns.stream().sorted(Comparator.comparingInt(MedalInfoPO::getSortOrder)).toList();
            Map<String, List<I18nMsgFrontVO>> i18Map =Maps.newHashMap();
            for(MedalInfoPO medalInfoPO:medalInfoPoCns){
                String medalNameI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MEDAL_NAME.getCode());
                String medalDescI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MEDAL_DESC.getCode());

                String medalUnlockI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MEDAL_UNLOCK.getCode());
                String medalCondLabel1I18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MEDAL_COND_LABEL1.getCode());
                String medalCondLabel2I18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.MEDAL_COND_LABEL2.getCode());


               SiteMedalInfoPO medalInfoPOSite=new   SiteMedalInfoPO();
               BeanUtils.copyProperties(medalInfoPO,medalInfoPOSite);
               medalInfoPOSite.setId(OrderUtil.createNumber(8));
               medalInfoPOSite.setParentId(Long.valueOf(medalInfoPO.getId()));
               medalInfoPOSite.setSiteCode(siteCode);
               medalInfoPOSite.setCreatedTime(System.currentTimeMillis());
               medalInfoPOSite.setUpdatedTime(System.currentTimeMillis());
               medalInfoPOSite.setCondLabel1(medalInfoPO.getCondLabel1());
               medalInfoPOSite.setCondLabel2(medalInfoPO.getCondLabel2());

                medalInfoPOSite.setMedalNameI18(medalNameI18Code);
                medalInfoPOSite.setMedalDescI18(medalDescI18Code);
                medalInfoPOSite.setUnlockCondNameI18(medalUnlockI18Code);
                if(StringUtils.hasText(medalInfoPOSite.getCondLabel1())){
                    medalInfoPOSite.setCondLabel1I18(medalCondLabel1I18Code);
                }
                if(StringUtils.hasText(medalInfoPOSite.getCondLabel2())){
                    medalInfoPOSite.setCondLabel2I18(medalCondLabel2I18Code);
                }
               siteMediaInfos.add(medalInfoPOSite);

               List<I18nMsgFrontVO> medalNameLists=Lists.newArrayList();
               List<I18nMsgFrontVO> medalDescLists=Lists.newArrayList();
               List<I18nMsgFrontVO> unlockLists=Lists.newArrayList();
               List<I18nMsgFrontVO> condLabel1List=Lists.newArrayList();
               List<I18nMsgFrontVO> condLabel2List=Lists.newArrayList();
               for(MedalInfoPO medalInfoPoAll:medalInfoPOS){
                   if(medalInfoPoAll.getMedalCode().equals(medalInfoPO.getMedalCode())){
                       I18nMsgFrontVO medalNameI18Msg=new I18nMsgFrontVO();
                       medalNameI18Msg.setMessageKey(medalNameI18Code) ;
                       medalNameI18Msg.setLanguage(medalInfoPoAll.getLanguageCode());
                       medalNameI18Msg.setMessage(medalInfoPoAll.getMedalName());
                       medalNameLists.add(medalNameI18Msg);

                       I18nMsgFrontVO medalDescI18Msg=new I18nMsgFrontVO();
                       medalDescI18Msg.setMessageKey(medalDescI18Code) ;
                       medalDescI18Msg.setLanguage(medalInfoPoAll.getLanguageCode());
                       medalDescI18Msg.setMessage(medalInfoPoAll.getMedalDesc());
                       medalDescLists.add(medalDescI18Msg);

                       I18nMsgFrontVO unlockCondNameI18Msg=new I18nMsgFrontVO();
                       unlockCondNameI18Msg.setMessageKey(medalUnlockI18Code) ;
                       unlockCondNameI18Msg.setLanguage(medalInfoPoAll.getLanguageCode());
                       unlockCondNameI18Msg.setMessage(medalInfoPoAll.getUnlockCondName());
                       unlockLists.add(unlockCondNameI18Msg);
                       if(StringUtils.hasText(medalInfoPoAll.getCondLabel1())){
                            I18nMsgFrontVO condLabel1I18Msg=new I18nMsgFrontVO();
                            condLabel1I18Msg.setMessageKey(medalCondLabel1I18Code) ;
                            condLabel1I18Msg.setLanguage(medalInfoPoAll.getLanguageCode());
                            condLabel1I18Msg.setMessage(medalInfoPoAll.getCondLabel1());
                            condLabel1List.add(condLabel1I18Msg);
                       }

                       if(StringUtils.hasText(medalInfoPoAll.getCondLabel2())){
                           I18nMsgFrontVO condLabel2I18Msg=new I18nMsgFrontVO();
                           condLabel2I18Msg.setMessageKey(medalCondLabel2I18Code) ;
                           condLabel2I18Msg.setLanguage(medalInfoPoAll.getLanguageCode());
                           condLabel2I18Msg.setMessage(medalInfoPoAll.getCondLabel2());
                           condLabel2List.add(condLabel2I18Msg);
                       }
                   }
               }
                i18Map=I18nMsgBindUtil.bind(i18Map,medalNameI18Code,medalNameLists);
                i18Map=I18nMsgBindUtil.bind(i18Map,medalDescI18Code,medalDescLists);
                i18Map=I18nMsgBindUtil.bind(i18Map,medalUnlockI18Code,unlockLists);
                i18Map=I18nMsgBindUtil.bind(i18Map,medalCondLabel1I18Code,condLabel1List);
                i18Map=I18nMsgBindUtil.bind(i18Map,medalCondLabel2I18Code,condLabel2List);
           }
            this.saveBatch(siteMediaInfos);
            //勋章多语言翻译
            i18nApi.insert(i18Map);
        }
        //同时进行奖励配置初始化
        medalRewardConfigService.init(siteCode);
        log.info("站点:{},勋章第一次初始化完成",siteCode);
        return ResponseVO.success(Boolean.TRUE);
    }


    /**
     * 勋章详情
     * @param id 主键ID
     * @return
     */
    public ResponseVO<SiteMedalInfoDetailRespVO> info(String id) {
        SiteMedalInfoPO siteMedalInfoPO=this.baseMapper.selectById(id);
        if(siteMedalInfoPO==null){
            return ResponseVO.success();
        }
        SiteMedalInfoDetailRespVO siteMedalInfoDetailRespVO=new SiteMedalInfoDetailRespVO();
        BeanUtils.copyProperties(siteMedalInfoPO,siteMedalInfoDetailRespVO);
        siteMedalInfoDetailRespVO.setActivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoDetailRespVO.getActivatedPic()));
        siteMedalInfoDetailRespVO.setInactivatedPicUrl(minioFileService.getFileUrlByKey(siteMedalInfoDetailRespVO.getInactivatedPic()));
        siteMedalInfoDetailRespVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
        siteMedalInfoDetailRespVO.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
        return ResponseVO.success(siteMedalInfoDetailRespVO);
    }


}
