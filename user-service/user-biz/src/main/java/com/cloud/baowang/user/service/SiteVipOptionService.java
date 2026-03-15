package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.vo.vip.SiteVipFeeRateVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionCurrencyConfigVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionVO;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.user.po.SiteVIPGradePO;
import com.cloud.baowang.user.po.SiteVipOptionCurrencyConfigPO;
import com.cloud.baowang.user.po.SiteVipOptionPO;
import com.cloud.baowang.user.po.VIPGradePO;
import com.cloud.baowang.user.repositories.SiteVipOptionRepository;
import com.cloud.baowang.user.repositories.VIPGradeRepository;
import com.cloud.baowang.user.util.MinioFileService;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigApi;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Desciption: mufan
 * @Date: 2024/7/27 15:22
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteVipOptionService extends ServiceImpl<SiteVipOptionRepository, SiteVipOptionPO> {
    private SiteVipOptionCurrencyConfigService siteVipOptionCurrencyConfigService;
    private VIPGradeRepository vipGradeRepository;
    private MinioFileService minioFileService;
    private UserWithdrawConfigApi userWithdrawConfigApi;

    public boolean initVip(String siteCode, List<String> currency){
        currency.forEach(e ->{
            List<SiteVipOptionVO> vo= getList(siteCode,e);
            if (CollectionUtil.isEmpty(vo)){
                this.batchSave( siteCode, e);
                clearData(siteCode,e);
            }
        });

        return true;
    }

    public List<SiteVipOptionVO> getList(String siteCode,String currency){
        String key= CacheConstants.SITE_CN_VIP_CONFIG+siteCode+":"+currency;
        String minioDomain = minioFileService.getMinioDomain();
        List<SiteVipOptionVO> redisValue =null;
        String jsonList = RedisUtil.getValue(key);
        if (StringUtils.isNotBlank(jsonList)) {
            return JSON.parseArray(jsonList, SiteVipOptionVO.class);
        }
        redisValue =new ArrayList<>();
        List<SiteVipOptionPO> values= this.getBaseMapper().selectList(Wrappers.<SiteVipOptionPO>lambdaQuery()
                .eq(SiteVipOptionPO::getSiteCode, siteCode)
                .eq(SiteVipOptionPO::getCurrencyCode,currency).orderByAsc(SiteVipOptionPO::getVipGradeCode));
       if (CollectionUtil.isNotEmpty(values)){
          List<String> ids = values.stream().map(SiteVipOptionPO::getId).collect(Collectors.toList());
          Map<String, List<SiteVipOptionCurrencyConfigVO>> maps= siteVipOptionCurrencyConfigService.getSiteVipOptionCurrencyConfigPOs(ids);
          redisValue = values.stream().map(po -> {
               SiteVipOptionVO data = new SiteVipOptionVO();
               BeanUtils.copyProperties(po, data);
               if (StringUtils.isNotBlank(data.getVipIcon())) {
                  data.setVipIconImage(minioDomain + "/" + data.getVipIcon());
               }
               data.setCurrencyConfigVOs(maps.get(po.getId()));
               return data;
           }).collect(Collectors.toList());
           RedisUtil.setValue(key, JSON.toJSONString(redisValue));
       }
        return redisValue;
    }


    public void batchSave(String siteCode,String currency){
        List<VIPGradeVO> data= this.getInitVIPGrade();
        long timestampMillis = System.currentTimeMillis();
        List<SiteVipOptionPO> insertData= data.stream().map(po -> {
            SiteVipOptionPO poa=new SiteVipOptionPO();
            poa.setCreatedTime(timestampMillis);
            poa.setCreator(CurrReqUtils.getAccount());
            poa.setCurrencyCode(currency);
            poa.setSiteCode(siteCode);
            poa.setVipGradeCode(po.getVipGradeCode());
            poa.setVipGradeName(po.getVipGradeName());
            return poa;
        }).collect(Collectors.toList());
        this.saveBatch(insertData);
    }

    public List<VIPGradeVO> getInitVIPGrade(){
        List<VIPGradeVO> list =null;
        String key=CacheConstants.SITE_CN_VIP_CONFIG+"init";
        String jsonList = RedisUtil.getValue(key);
        if (StringUtils.isNotBlank(jsonList)) {
           return JSON.parseArray(jsonList, VIPGradeVO.class);
        }
        if (CollUtil.isEmpty(list)) {
           List<VIPGradePO> data = vipGradeRepository.selectList(new LambdaQueryWrapper<VIPGradePO>()
                            .le(VIPGradePO::getVipGradeCode,11).orderByAsc(VIPGradePO::getVipGradeCode));
           list = data.stream().map(po -> {
                VIPGradeVO vo = new VIPGradeVO();
                BeanUtils.copyProperties(po, vo);
                return vo;
            }).collect(Collectors.toList());
            RedisUtil.setValue(key, JSON.toJSONString(list));
        }
        return list;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> updateSiteVipOptionVO(SiteVipOptionVO vo){
        SiteVipOptionPO check= this.getById(vo.getId());
        if (Objects.isNull(check)){
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        SiteVipOptionPO po =new SiteVipOptionPO();
        BeanUtils.copyProperties(vo, po);
        siteVipOptionCurrencyConfigService.batchSave(vo.getId(),vo.getCurrencyConfigVOs());
        LambdaUpdateWrapper<SiteVipOptionPO> update=Wrappers.lambdaUpdate();
        update.eq(SiteVipOptionPO::getId,po.getId());
        if (po.getWeekBonusType()==1){
            po.setWeekBonusAmountMultiple(BigDecimal.ZERO);
            update.set(SiteVipOptionPO::getWeekBonusAmountMultiple,null);
        }else{
            po.setWeekBonusAmountTotal(BigDecimal.ZERO);
            update.set(SiteVipOptionPO::getWeekBonusAmountTotal,null);
        }
        this.updateById(po);
        this.update(update);
         //初始化提款配置相关信息
        UserWithdrawConfigAddOrUpdateVO data=new UserWithdrawConfigAddOrUpdateVO();
        data.setCurrencyCode(vo.getCurrencyCode());
        data.setSiteCode(vo.getSiteCode());
        data.setVipGradeCode(vo.getVipGradeCode());
        //单日免费提款总次数
        data.setSingleDayWithdrawCount(vo.getDailyWithdrawalFreeNum());
        //单日免费提款额度
        data.setSingleMaxWithdrawAmount(vo.getDailyWithdrawalFreeAmountLimit());
        //单日提款次数上限
        data.setDailyWithdrawalNumsLimit(vo.getDailyWithdrawalNumLimit());
        //单日提款额度最大值
        data.setDailyWithdrawAmountLimit(vo.getDailyWithdrawAmountLimit());
        userWithdrawConfigApi.updateBySiteCodeAndVipGradeCode(data);
        clearData(vo.getSiteCode(),vo.getCurrencyCode());
        return ResponseVO.success();
    }

    public void clearData(String siteCode,String currencyCode){
        String key= CacheConstants.SITE_CN_VIP_CONFIG+siteCode+":"+currencyCode;
        RedisUtil.deleteKey(key);
    }

    public SiteVipFeeRateVO getVipGradeSiteCodeAndCurrency(String siteCode, Integer vipGradeCode, String currencyCode, String withdrawWayId) {
        LambdaQueryWrapper<SiteVipOptionPO> vipRankLqw = Wrappers.lambdaQuery();
        vipRankLqw.eq(SiteVipOptionPO::getSiteCode, siteCode);
        vipRankLqw.eq(SiteVipOptionPO::getVipGradeCode, vipGradeCode);
        vipRankLqw.eq(SiteVipOptionPO::getCurrencyCode, currencyCode);
        SiteVipOptionPO siteVipOptionPO = this.baseMapper.selectOne(vipRankLqw);
        SiteVipFeeRateVO siteVipFeeRateVO = new SiteVipFeeRateVO();
        if(null != siteVipOptionPO){
            LambdaQueryWrapper<SiteVipOptionCurrencyConfigPO> query = Wrappers.lambdaQuery();
            query.eq(SiteVipOptionCurrencyConfigPO::getSiteVipOptionId, siteVipOptionPO.getId())
                    .eq(SiteVipOptionCurrencyConfigPO::getWithdrawWayId, withdrawWayId);
            SiteVipOptionCurrencyConfigPO siteVipRankCurrencyConfigPO = siteVipOptionCurrencyConfigService.getOne(query);
            siteVipFeeRateVO = ConvertUtil.entityToModel(siteVipRankCurrencyConfigPO, SiteVipFeeRateVO.class);
            siteVipFeeRateVO.setEncryCoinFee(siteVipOptionPO.getEncryCoinFee());
        }

        return siteVipFeeRateVO;
    }

    public SiteVipOptionVO getVipGradeInfoByCode(String siteCode, Integer vipGradeCode, String currencyCode) {

        LambdaQueryWrapper<SiteVipOptionPO> vipRankLqw = Wrappers.lambdaQuery();
        vipRankLqw.eq(SiteVipOptionPO::getSiteCode, siteCode);
        vipRankLqw.eq(SiteVipOptionPO::getVipGradeCode, vipGradeCode);
        vipRankLqw.eq(SiteVipOptionPO::getCurrencyCode, currencyCode);
        SiteVipOptionPO siteVipOptionPO = this.baseMapper.selectOne(vipRankLqw);
        return ConvertUtil.entityToModel(siteVipOptionPO,SiteVipOptionVO.class);
    }
}
