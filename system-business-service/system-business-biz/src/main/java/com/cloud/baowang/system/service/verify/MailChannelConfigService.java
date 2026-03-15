package com.cloud.baowang.system.service.verify;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.MailChannelConfigPO;
import com.cloud.baowang.system.po.verify.MailSiteLinkPO;
import com.cloud.baowang.system.repositories.verify.MailChannelConfigRepository;
import com.cloud.baowang.system.repositories.verify.MailSiteLinkRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:56
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class MailChannelConfigService extends ServiceImpl<MailChannelConfigRepository, MailChannelConfigPO>  {

    private final MailChannelConfigRepository mailChannelConfigRepository;
    private final MailSiteLinkService mailSiteLinkService;
    private final MailSiteLinkRepository mailSiteLinkRepository;
    
    public Page<MailChannelConfigPageVO> getMailConfigPage(MailChannelQueryVO reqVO) {
        LambdaQueryWrapper<MailChannelConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelName()), MailChannelConfigPO::getChannelName, reqVO.getChannelName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelCode()), MailChannelConfigPO::getChannelCode, reqVO.getChannelCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getStatus()), MailChannelConfigPO::getStatus, reqVO.getStatus());
        queryWrapper.orderByDesc(MailChannelConfigPO::getCreatedTime);

        Page<MailChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<MailChannelConfigPO> poPage = mailChannelConfigRepository.selectPage(page, queryWrapper);
        Page<MailChannelConfigPageVO> voPage = new Page<>();
        List<MailChannelConfigPageVO> voList = new ArrayList<>();
        if (poPage != null && !poPage.getRecords().isEmpty()) {
            Map<String,Integer> siteMap = mailSiteLinkService.queryChannelCount();
            poPage.getRecords().forEach(info -> {
                MailChannelConfigPageVO respVO = new MailChannelConfigPageVO();
                BeanUtils.copyProperties(info, respVO);
                Integer count = siteMap.get(info.getChannelCode());
                respVO.setAuthCount(count == null ? 0 : count);
                voList.add(respVO);
            });
            BeanUtils.copyProperties(poPage, voPage);
            voPage.setRecords(voList);
        }

        return voPage;
    }

    public ResponseVO<SiteEmailChannelVO> queryEmailChannel(final MailChannelQueryVO reqVO) {
        SiteEmailChannelVO vo = new SiteEmailChannelVO();
        Page<MailChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteEmailChannelPageVO> resultPage = mailChannelConfigRepository.queryEmailChannel(page, reqVO);
        List<String> chooseId = Lists.newArrayList();
        List<String> allId = Lists.newArrayList();
        mailSiteLinkService.lambdaQuery().eq(MailSiteLinkPO::getSiteCode, reqVO.getSiteCode()).list()
            .forEach(obj->chooseId.add(obj.getChannelCode()));
        this.lambdaQuery().list().forEach(obj->allId.add(obj.getChannelCode()));
        vo.setChooseId(chooseId);
        vo.setAllId(allId);
        vo.setPageVO(resultPage);
        return ResponseVO.success(vo);
    }

    public int editStatus(ChangeStatusVO changeStatusVO) {
        MailChannelConfigPO MailChannelConfigPO = new MailChannelConfigPO();
        MailChannelConfigPO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        MailChannelConfigPO.setId(changeStatusVO.getId());
        MailChannelConfigPO.setUpdatedTime(System.currentTimeMillis());
        MailChannelConfigPO.setUpdater(changeStatusVO.getUpdater());
        int count = mailChannelConfigRepository.updateById(MailChannelConfigPO);

        //如果是禁用， 则同时禁用站点对应的通道
        if (EnableStatusEnum.DISABLE.getCode().equals(Integer.parseInt(changeStatusVO.getAbleStatus()))) {
            MailChannelConfigPO configPO = mailChannelConfigRepository.selectById(changeStatusVO.getId());
            LambdaQueryWrapper<MailSiteLinkPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(MailSiteLinkPO::getChannelCode, configPO.getChannelCode());
            List<MailSiteLinkPO> poList = mailSiteLinkRepository.selectList(queryWrapper);
            if (poList != null && poList.size() > 0) {
                LambdaUpdateWrapper<MailSiteLinkPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(MailSiteLinkPO::getChannelCode, configPO.getChannelCode())
                        .set( MailSiteLinkPO::getStatus, String.valueOf(EnableStatusEnum.DISABLE.getCode()))
                        .set( MailSiteLinkPO::getUpdatedTime, System.currentTimeMillis())
                        .set( MailSiteLinkPO::getUpdater, "system");

                mailSiteLinkRepository.update(null, updateWrapper);
            }
        }

        return count;
    }

    public MailChannelConfigVO querySiteChannel(String siteCode) {
        return mailChannelConfigRepository.querySiteChannel(siteCode);
    }

    public Page<SiteBackEmailChannelPageVO> getSiteMailConfigPage(MailChannelQueryVO reqVO) {
        Page<MailChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteBackEmailChannelPageVO> pageVO = mailChannelConfigRepository.getSiteMailConfigPage(page, reqVO);
        return pageVO;
    }



    public ResponseVO<List<SMSAuthorVO>> mailAuthorList(String id){
        return ResponseVO.success(mailChannelConfigRepository.mailAuthorList(id));
    }
}
