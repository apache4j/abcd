package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.api.vo.vip.SiteVipOptionCurrencyConfigVO;
import com.cloud.baowang.user.po.SiteVipOptionCurrencyConfigPO;
import com.cloud.baowang.user.repositories.SiteVipOptionCurrencyConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: mufan
 * @Version: V1.0
 **/
@Service
@AllArgsConstructor
@Slf4j
public class SiteVipOptionCurrencyConfigService extends ServiceImpl<SiteVipOptionCurrencyConfigRepository, SiteVipOptionCurrencyConfigPO> {

   public Map<String, List<SiteVipOptionCurrencyConfigVO>> getSiteVipOptionCurrencyConfigPOs(List<String> ids){
       List<SiteVipOptionCurrencyConfigPO> data= this.getBaseMapper().selectList(Wrappers.<SiteVipOptionCurrencyConfigPO>lambdaQuery()
               .in(SiteVipOptionCurrencyConfigPO::getSiteVipOptionId, ids));
       Map<String, List<SiteVipOptionCurrencyConfigVO>> siteToGradeInfoMap =new HashMap<>();
       if (CollectionUtil.isNotEmpty(data)){
           siteToGradeInfoMap = data.stream().map(po -> {
                       SiteVipOptionCurrencyConfigVO info = new SiteVipOptionCurrencyConfigVO();
                       BeanUtils.copyProperties(po, info);
                       return info;
                   }).collect(Collectors.groupingBy(SiteVipOptionCurrencyConfigVO::getSiteVipOptionId));
       }
       return siteToGradeInfoMap;
   }

    public void batchSave(String siteVipOptionId, List<SiteVipOptionCurrencyConfigVO> currencyConfigVOs){
        if (CollectionUtil.isNotEmpty(currencyConfigVOs)){
            this.getBaseMapper().delete(Wrappers.lambdaQuery(SiteVipOptionCurrencyConfigPO.class)
                    .eq(SiteVipOptionCurrencyConfigPO::getSiteVipOptionId, siteVipOptionId));
            List<SiteVipOptionCurrencyConfigPO> insertData= currencyConfigVOs.stream().map(po -> {
                SiteVipOptionCurrencyConfigPO data=new SiteVipOptionCurrencyConfigPO();
                BeanUtils.copyProperties(po, data);
                data.setSiteVipOptionId(siteVipOptionId);
                return data;
            }).collect(Collectors.toList());
            this.saveBatch(insertData);
        }
    }
}
