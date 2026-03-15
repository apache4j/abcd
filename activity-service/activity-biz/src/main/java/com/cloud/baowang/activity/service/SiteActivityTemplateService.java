package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateCheckVO;
import com.cloud.baowang.activity.api.vo.SiteActivityTemplateSaveVO;
import com.cloud.baowang.activity.po.SiteActivityTemplatePO;
import com.cloud.baowang.activity.po.SystemActivityTemplatePO;
import com.cloud.baowang.activity.repositories.SiteActivityTemplateRepository;
import com.cloud.baowang.activity.repositories.SystemActivityTemplateRepository;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: mufan
 * @Date: 2025/8/20 10:37
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityTemplateService extends ServiceImpl<SiteActivityTemplateRepository, SiteActivityTemplatePO> {

    private final  SystemActivityTemplateRepository systemActivityTemplateRepository;

    public ResponseVO<Boolean> batchBindAndUnBindActivityTemplate(SiteActivityTemplateSaveVO siteActivityTemplateSaveVO){
        if (CollUtil.isNotEmpty(siteActivityTemplateSaveVO.getCheckActivityTemplate())) {
            List<SystemActivityTemplatePO> all=systemActivityTemplateRepository.selectList(null);
            Map<String, SystemActivityTemplatePO> idToDataMap = all.stream()
                    .collect(Collectors.toMap(SystemActivityTemplatePO::getActivityTemplate, data -> data));

            List<SiteActivityTemplatePO> sitePOs = this.getBaseMapper().selectList(Wrappers.<SiteActivityTemplatePO>lambdaQuery()
                    .eq(SiteActivityTemplatePO::getSiteCode, siteActivityTemplateSaveVO.getSiteCode())
            );
            Long time=System.currentTimeMillis();
            //批量解绑活动
            List<SiteActivityTemplatePO> update = new ArrayList<>();
            List<String> siteActivityTemplates = new ArrayList<>();
            sitePOs.forEach(e ->{
                siteActivityTemplates.add(e.getActivityTemplate());
                SiteActivityTemplatePO data=new SiteActivityTemplatePO();
                if (!siteActivityTemplateSaveVO.getCheckActivityTemplate().contains(e.getActivityTemplate())){
                    BeanUtil.copyProperties(e, data);
                    data.setBindStatus(0);
                    data.setUpdater(siteActivityTemplateSaveVO.getOperator());
                    data.setUpdatedTime(time);
                    update.add(data);
                }else{
                    if (e.getBindStatus()==0){
                        BeanUtil.copyProperties(e, data);
                        data.setBindStatus(1);
                        data.setUpdater(siteActivityTemplateSaveVO.getOperator());
                        data.setUpdatedTime(time);
                        update.add(data);
                    }
                }
            });
            //批量新增绑定
            List<SiteActivityTemplatePO> insert = new ArrayList<>();
            List<String> addData=  siteActivityTemplateSaveVO.getCheckActivityTemplate();
            addData.removeAll(siteActivityTemplates);
            addData.forEach(e->{
                SiteActivityTemplatePO data=new SiteActivityTemplatePO();
                data.setActivityName(idToDataMap.get(e).getActivityName());
                data.setSiteCode(siteActivityTemplateSaveVO.getSiteCode());
                data.setActivityTemplate(e);
                data.setBindStatus(1);
                data.setCreator(siteActivityTemplateSaveVO.getOperator());
                data.setCreatedTime(time);
                data.setUpdater(siteActivityTemplateSaveVO.getOperator());
                data.setUpdatedTime(time);
                insert.add(data);
            });
            if (CollUtil.isNotEmpty(update)) {
                this.saveOrUpdateBatch(update);
            }
            if (CollUtil.isNotEmpty(insert)) {
                this.saveOrUpdateBatch(insert);
            }
        }else{
            // update
            LambdaUpdateWrapper<SiteActivityTemplatePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(SiteActivityTemplatePO::getBindStatus, 0);
            updateWrapper.eq(SiteActivityTemplatePO::getSiteCode, siteActivityTemplateSaveVO.getSiteCode()) ;
            this.getBaseMapper().update(null, updateWrapper);
        }
        return ResponseVO.success(true);
    }

   public ResponseVO<Boolean> checkBindFlag(SiteActivityTemplateCheckVO siteActivityTemplateCheckVO){
       SiteActivityTemplatePO siteActivityTemplatePO= this.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteActivityTemplatePO.class)
                .eq(SiteActivityTemplatePO::getSiteCode,siteActivityTemplateCheckVO.getSiteCode())
               .eq(SiteActivityTemplatePO::getActivityTemplate,siteActivityTemplateCheckVO.getActivityTemplate())
                       .eq(SiteActivityTemplatePO::getBindStatus,1));
       Boolean flag=false;
       if (ObjectUtil.isNotEmpty(siteActivityTemplatePO)&& siteActivityTemplatePO.getBindStatus() ==1){
           flag=true;
       }
        return ResponseVO.success(flag);
    }

}
