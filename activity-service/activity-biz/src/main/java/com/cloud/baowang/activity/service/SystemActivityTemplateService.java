package com.cloud.baowang.activity.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SystemActivityTemplatePO;
import com.cloud.baowang.activity.repositories.SystemActivityTemplateRepository;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Desciption:
 * @Author: mufan
 * @Date: 2025/8/20 10:37
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class SystemActivityTemplateService  extends ServiceImpl<SystemActivityTemplateRepository, SystemActivityTemplatePO> {

    public ResponseVO<List<SiteActivityTemplateVO>> querySiteActivityTemplate(String siteCode,Integer handicapMode){
        return ResponseVO.success(this.getBaseMapper().querySiteActivityTemplate(siteCode));
    }

    public ResponseVO<Page<SystemActivityTemplateVO>> getPage(SystemActivityTemplateReqVO vo){
        Page<SystemActivityTemplateVO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        return ResponseVO.success(this.getBaseMapper().getPage(page, vo));
    }

    public ResponseVO<List<SystemActivityTemplateInfoVO>> getInfo(SystemActivityTemplateReqVO vo){
        return ResponseVO.success(this.getBaseMapper().getInfo(vo));
    }

}
