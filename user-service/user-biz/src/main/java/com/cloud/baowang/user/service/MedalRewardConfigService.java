package com.cloud.baowang.user.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigBatchUpdateReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigRespVO;
import com.cloud.baowang.user.api.vo.medal.MedalRewardConfigUpdateReqVO;
import com.cloud.baowang.user.po.MedalRewardConfigPO;
import com.cloud.baowang.user.po.SiteMedalInfoPO;
import com.cloud.baowang.user.po.SiteMedalOperLogPO;
import com.cloud.baowang.user.repositories.MedalRewardConfigRepository;
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

import static com.cloud.baowang.common.core.constants.CommonConstant.ADMIN_CENTER_SITE_CODE;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MedalRewardConfigService extends ServiceImpl<MedalRewardConfigRepository, MedalRewardConfigPO> {

    private final SiteMedalOperLogService siteMedalOperLogService;

    private final SystemParamApi systemParamApi;

    public ResponseVO<Page<MedalRewardConfigRespVO>> listPage(MedalRewardConfigReqVO medalRewardConfigReqVO) {
        Page<MedalRewardConfigPO> page = new Page<MedalRewardConfigPO>(medalRewardConfigReqVO.getPageNumber(), medalRewardConfigReqVO.getPageSize());
        LambdaQueryWrapper<MedalRewardConfigPO> lqw = new LambdaQueryWrapper<MedalRewardConfigPO>();
        if(StringUtils.hasText(medalRewardConfigReqVO.getSiteCode())){
            lqw.eq(MedalRewardConfigPO::getSiteCode,medalRewardConfigReqVO.getSiteCode());
        }
        lqw.orderByAsc(MedalRewardConfigPO::getRewardNo);
        IPage<MedalRewardConfigPO> medalRewardConfigPOPage =  this.baseMapper.selectPage(page,lqw);
        Page<MedalRewardConfigRespVO> medalRewardConfigRespVOPage=new Page<MedalRewardConfigRespVO>(medalRewardConfigReqVO.getPageNumber(), medalRewardConfigReqVO.getPageSize());
        medalRewardConfigRespVOPage.setTotal(medalRewardConfigPOPage.getTotal());
        medalRewardConfigRespVOPage.setPages(medalRewardConfigPOPage.getPages());
        List<MedalRewardConfigRespVO> resultLists= Lists.newArrayList();
        for(MedalRewardConfigPO medalRewardConfigPO:medalRewardConfigPOPage.getRecords()){
            MedalRewardConfigRespVO medalRewardConfigRespVO=new MedalRewardConfigRespVO();
            BeanUtils.copyProperties(medalRewardConfigPO,medalRewardConfigRespVO);
            medalRewardConfigRespVO.setPlatformCurrency(CurrReqUtils.getPlatCurrencySymbol());
            resultLists.add(medalRewardConfigRespVO);
        }
        medalRewardConfigRespVOPage.setRecords(resultLists);
        return ResponseVO.success(medalRewardConfigRespVOPage);
    }

    public ResponseVO<Boolean> init(String siteCode) {
        LambdaQueryWrapper<MedalRewardConfigPO> lqw = new LambdaQueryWrapper<MedalRewardConfigPO>();
        lqw.eq(MedalRewardConfigPO::getSiteCode,siteCode);
        Long countNum = this.baseMapper.selectCount(lqw);
        if(countNum>=1){
            log.info("站点:{},勋章奖励配置数据已经初始化",siteCode);
            return ResponseVO.success(Boolean.TRUE);
        }
        LambdaQueryWrapper<MedalRewardConfigPO> lqwSys = new LambdaQueryWrapper<MedalRewardConfigPO>();
        lqwSys.eq(MedalRewardConfigPO::getSiteCode,ADMIN_CENTER_SITE_CODE);
        lqwSys.orderByAsc(MedalRewardConfigPO::getId);
        List<MedalRewardConfigPO> medalRewardConfigPOS = this.baseMapper.selectList(lqwSys);
        if(!CollectionUtils.isEmpty(medalRewardConfigPOS)){
            List<MedalRewardConfigPO> medalRewardConfigPOArrayList= Lists.newArrayList();
            for(MedalRewardConfigPO medalRewardConfigPO:medalRewardConfigPOS){
                MedalRewardConfigPO medalRewardConfigPOSite=new   MedalRewardConfigPO();
                BeanUtils.copyProperties(medalRewardConfigPO,medalRewardConfigPOSite);
                medalRewardConfigPOSite.setId(OrderUtil.createNumber(8));
                medalRewardConfigPOSite.setSiteCode(siteCode);
                medalRewardConfigPOSite.setRewardNo(medalRewardConfigPO.getRewardNo());
                medalRewardConfigPOSite.setStatus(EnableStatusEnum.ENABLE.getCode());
                medalRewardConfigPOSite.setCreatedTime(System.currentTimeMillis());
                medalRewardConfigPOSite.setUpdatedTime(System.currentTimeMillis());
                medalRewardConfigPOArrayList.add(medalRewardConfigPOSite);
            }
            this.saveBatch(medalRewardConfigPOArrayList);
        }
        log.info("站点:{},勋章奖励配置数据初始化完成",siteCode);
        return ResponseVO.success(Boolean.TRUE);

    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> batchSave(MedalRewardConfigBatchUpdateReqVO medalRewardConfigBatchUpdateReqVO) {
        List<MedalRewardConfigPO> medalRewardConfigPOArrayList= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(medalRewardConfigBatchUpdateReqVO.getMedalRewardConfigUpdateReqVOList())){
            List<MedalRewardConfigUpdateReqVO> medalRewardConfigUpdateReqVOList=medalRewardConfigBatchUpdateReqVO.getMedalRewardConfigUpdateReqVOList().stream().sorted(Comparator.comparingInt(MedalRewardConfigUpdateReqVO::getRewardNo)).toList();
            Integer unlockMedalNumBefore=0;
            for(MedalRewardConfigUpdateReqVO medalRewardConfigUpdateReqVO:medalRewardConfigUpdateReqVOList){
                if(medalRewardConfigUpdateReqVO.getUnlockMedalNum()<unlockMedalNumBefore){
                    log.info("请按照顺序填写解锁数量,后面的数量不能小于前面的解锁数量");
                    throw new BaowangDefaultException(ResultCode.MEDAL_UNLOCK_NUM_ORDER);
                }
                MedalRewardConfigPO medalRewardConfigPOSite=new   MedalRewardConfigPO();
                BeanUtils.copyProperties(medalRewardConfigUpdateReqVO,medalRewardConfigPOSite);
                medalRewardConfigPOSite.setId(null);
                medalRewardConfigPOSite.setSiteCode(medalRewardConfigBatchUpdateReqVO.getSiteCode());
                medalRewardConfigPOSite.setRewardNo(medalRewardConfigUpdateReqVO.getRewardNo());
                medalRewardConfigPOSite.setUnlockMedalNum(medalRewardConfigUpdateReqVO.getUnlockMedalNum());
                medalRewardConfigPOSite.setStatus(EnableStatusEnum.ENABLE.getCode());
                medalRewardConfigPOSite.setCreator(medalRewardConfigBatchUpdateReqVO.getOperatorUserNo());
                medalRewardConfigPOSite.setUpdater(medalRewardConfigBatchUpdateReqVO.getOperatorUserNo());
                medalRewardConfigPOSite.setCreatedTime(System.currentTimeMillis());
                medalRewardConfigPOSite.setUpdatedTime(System.currentTimeMillis());
                medalRewardConfigPOArrayList.add(medalRewardConfigPOSite);
                unlockMedalNumBefore=medalRewardConfigUpdateReqVO.getUnlockMedalNum();

                LambdaQueryWrapper<MedalRewardConfigPO> lambdaQueryWrapper=new LambdaQueryWrapper<MedalRewardConfigPO>();
                lambdaQueryWrapper.eq(MedalRewardConfigPO::getRewardNo,medalRewardConfigPOSite.getRewardNo());
                lambdaQueryWrapper.eq(MedalRewardConfigPO::getSiteCode,medalRewardConfigPOSite.getSiteCode());
                MedalRewardConfigPO  medalRewardConfigPODb=this.baseMapper.selectOne(lambdaQueryWrapper);


                LambdaUpdateWrapper<MedalRewardConfigPO> lambdaUpdateWrapper=new LambdaUpdateWrapper<MedalRewardConfigPO>();
                lambdaUpdateWrapper.set(MedalRewardConfigPO::getUnlockMedalNum,medalRewardConfigPOSite.getUnlockMedalNum());;
                lambdaUpdateWrapper.set(MedalRewardConfigPO::getRewardAmount,medalRewardConfigPOSite.getRewardAmount());
                lambdaUpdateWrapper.set(MedalRewardConfigPO::getTypingMultiple,medalRewardConfigPOSite.getTypingMultiple());
                lambdaUpdateWrapper.set(MedalRewardConfigPO::getStatus,EnableStatusEnum.ENABLE.getCode());
                lambdaUpdateWrapper.set(MedalRewardConfigPO::getUpdatedTime,medalRewardConfigPOSite.getUpdatedTime());
                lambdaUpdateWrapper.eq(MedalRewardConfigPO::getRewardNo,medalRewardConfigPOSite.getRewardNo());
                lambdaUpdateWrapper.eq(MedalRewardConfigPO::getSiteCode,medalRewardConfigPOSite.getSiteCode());
                this.update(lambdaUpdateWrapper);

                //todo 变更记录
                // 勋章变更记录
                SiteMedalOperLogPO siteMedalOperLogPOCommon=new SiteMedalOperLogPO();
               // siteMedalOperLogPOCommon.setSiteMedalId(medalRewardConfigPODb.getId());
               // siteMedalOperLogPOCommon.setMedalCode(medalRewardConfigPODb.getMedalCode());
               // siteMedalOperLogPOCommon.setMedalName(medalRewardConfigPODb.getMedalName());
                siteMedalOperLogPOCommon.setSiteCode(medalRewardConfigPODb.getSiteCode());
                siteMedalOperLogPOCommon.setOperTime(System.currentTimeMillis());
                siteMedalOperLogPOCommon.setCreator(medalRewardConfigBatchUpdateReqVO.getOperatorUserNo());
                siteMedalOperLogPOCommon.setCreatedTime(System.currentTimeMillis());

                List<SiteMedalOperLogPO> siteMedalOperLogPOS=Lists.newArrayList();
                Map<String,String> operationI18CodeMap = systemParamApi.getSystemParamMap(CommonConstant.MEDAL_OPERATION).getData();
                checkChangeField(medalRewardConfigPODb,medalRewardConfigPOSite,siteMedalOperLogPOCommon,siteMedalOperLogPOS,operationI18CodeMap);
                if(!CollectionUtils.isEmpty(siteMedalOperLogPOS)){
                    siteMedalOperLogService.saveBatch(siteMedalOperLogPOS);
                }
            }
        }
        return ResponseVO.success();
    }
    /**
     * 勋章变更记录
     * @param medalRewardConfigPOOld 数据库信息
     * @param medalRewardConfigPONew 修改后信息
     * @param siteMedalOperLogPOCommon  公共参数
     * @param operationI18CodeMap 操作项多语言
     */
    private void checkChangeField(MedalRewardConfigPO medalRewardConfigPOOld, MedalRewardConfigPO medalRewardConfigPONew, SiteMedalOperLogPO siteMedalOperLogPOCommon, List<SiteMedalOperLogPO> siteMedalOperLogPOS, Map<String,String> operationI18CodeMap) {
        JSONObject beforeJson= JSONObject.parseObject(JSON.toJSONString(medalRewardConfigPOOld));
        JSONObject afterJson=JSONObject.parseObject(JSON.toJSONString(medalRewardConfigPONew));

        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,MedalRewardConfigPO.Fields.unlockMedalNum,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,MedalRewardConfigPO.Fields.rewardAmount,operationI18CodeMap);
        checkFiled(siteMedalOperLogPOS,siteMedalOperLogPOCommon,beforeJson,afterJson,MedalRewardConfigPO.Fields.typingMultiple,operationI18CodeMap);
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
        String rewardConfig=CommonConstant.REWARD_CONFIG;
        if(fieldCode.equalsIgnoreCase(MedalRewardConfigPO.Fields.rewardAmount)||fieldCode.equalsIgnoreCase(MedalRewardConfigPO.Fields.typingMultiple)){
            if(StringUtils.hasText(beforeVal)){
                beforeVal=new BigDecimal(beforeVal).setScale(2, RoundingMode.CEILING).toString();
            }
            if(StringUtils.hasText(afterVal)){
                afterVal=new BigDecimal(afterVal).setScale(2, RoundingMode.CEILING).toString();
            }
        }
        String operatorItem=rewardConfig.concat(fieldCode);
        if(!ObjectUtils.nullSafeEquals(beforeVal,afterVal)){
            SiteMedalOperLogPO siteMedalOperCompare=new SiteMedalOperLogPO();
            String id=siteMedalOperCompare.getId();
            BeanUtils.copyProperties(siteMedalOperLogPO,siteMedalOperCompare);
            siteMedalOperCompare.setId(id);
            siteMedalOperCompare.setOperItem(operatorItem);
            siteMedalOperCompare.setOperItemI18(operationI18CodeMap.get(operatorItem));
            siteMedalOperCompare.setOperBefore(beforeVal);
            siteMedalOperCompare.setOperAfter(afterVal);
            siteMedalOperLogPOS.add(siteMedalOperCompare);
        }
    }

    public List<MedalRewardConfigPO> listAll(String siteCode) {
        LambdaQueryWrapper<MedalRewardConfigPO> lqw = new LambdaQueryWrapper<MedalRewardConfigPO>();
        lqw.eq(MedalRewardConfigPO::getSiteCode,siteCode);
        lqw.orderByAsc(MedalRewardConfigPO::getRewardNo);
        return this.baseMapper.selectList(lqw);
    }

    public List<MedalRewardConfigRespVO> listAllVo(String siteCode) {
        LambdaQueryWrapper<MedalRewardConfigPO> lqw = new LambdaQueryWrapper<MedalRewardConfigPO>();
        lqw.eq(MedalRewardConfigPO::getSiteCode,siteCode);
        lqw.orderByAsc(MedalRewardConfigPO::getRewardNo);
         List<MedalRewardConfigPO> medalRewardConfigPOS=this.baseMapper.selectList(lqw);
        List<MedalRewardConfigRespVO> resultLists= Lists.newArrayList();
        for(MedalRewardConfigPO medalRewardConfigPO:medalRewardConfigPOS){
            MedalRewardConfigRespVO medalRewardConfigRespVO=new MedalRewardConfigRespVO();
            BeanUtils.copyProperties(medalRewardConfigPO,medalRewardConfigRespVO);
            resultLists.add(medalRewardConfigRespVO);
        }
        return resultLists;
    }
}
