package com.cloud.baowang.system.service.verify;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.member.ChangeStatusVO;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.*;
import com.cloud.baowang.system.po.verify.SmsChannelConfigPO;
import com.cloud.baowang.system.repositories.verify.SmsChannelConfigRepository;
import com.cloud.baowang.system.repositories.verify.SmsSiteLinkRepository;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:56
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class SmsChannelConfigService extends ServiceImpl<SmsChannelConfigRepository, SmsChannelConfigPO>  {

    private final SmsChannelConfigRepository smsChannelConfigRepository;
    private final SmsSiteLinkService smsSiteLinkService;
    private final SmsSiteLinkRepository smsSiteLinkRepository;

    public Page<SmsChannelConfigPageVO> getSmsConfigPage(SmsChannelQueryVO reqVO) {
        LambdaQueryWrapper<SmsChannelConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getAddressCode()), SmsChannelConfigPO::getAddressCode, reqVO.getAddressCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelName()), SmsChannelConfigPO::getChannelName, reqVO.getChannelName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getChannelCode()), SmsChannelConfigPO::getChannelCode, reqVO.getChannelCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(reqVO.getStatus()), SmsChannelConfigPO::getStatus, reqVO.getStatus());
        queryWrapper.orderByDesc(SmsChannelConfigPO::getCreatedTime);

        Page<SmsChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SmsChannelConfigPO> poPage = smsChannelConfigRepository.selectPage(page, queryWrapper);
        Page<SmsChannelConfigPageVO> voPage = new Page<>();
        List<SmsChannelConfigPageVO> voList = new ArrayList<>();
        if (poPage != null && !poPage.getRecords().isEmpty()) {
            Map<String,Integer> siteMap = smsSiteLinkService.queryChannelCount();
            poPage.getRecords().forEach(info -> {
                SmsChannelConfigPageVO respVO = new SmsChannelConfigPageVO();
                BeanUtils.copyProperties(info, respVO);
                //统计授权数量
                Integer count = siteMap.get(info.getChannelCode());
                respVO.setAuthCount(count == null ? 0 : count);
                voList.add(respVO);
            });
            BeanUtils.copyProperties(poPage, voPage);
            voPage.setRecords(voList);
        }

        return voPage;
    }

    public ResponseVO<SiteSmsChannelVO> querySmsChannel(SmsChannelQueryVO reqVO) {
        SiteSmsChannelVO vo = new SiteSmsChannelVO();
        Page<SmsChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        Page<SiteSmsChannelPageVO> resultPage = smsChannelConfigRepository.querySmsChannel(page, reqVO);
        List<String> chooseId = Lists.newArrayList();
        List<String> allId = Lists.newArrayList();
        smsSiteLinkService.lambdaQuery().eq(SmsSiteLinkPO::getSiteCode, reqVO.getSiteCode()).list()
            .forEach(obj->chooseId.add(obj.getChannelCode()));
        this.list().forEach(obj->allId.add(obj.getChannelCode()));
        vo.setChooseId(chooseId);
        vo.setAllId(allId);
        vo.setPageVO(resultPage);
        return ResponseVO.success(vo);
    }
    public int editStatus(ChangeStatusVO changeStatusVO) {
        SmsChannelConfigPO SmsChannelConfigPO = new SmsChannelConfigPO();
        SmsChannelConfigPO.setStatus(Integer.parseInt(changeStatusVO.getAbleStatus()));
        SmsChannelConfigPO.setId(changeStatusVO.getId());
        SmsChannelConfigPO.setUpdatedTime(System.currentTimeMillis());
        SmsChannelConfigPO.setUpdater(changeStatusVO.getUpdater());
        int count = smsChannelConfigRepository.updateById(SmsChannelConfigPO);

        //如果是禁用， 则同时禁用站点对应的通道
        if (EnableStatusEnum.DISABLE.getCode().equals(Integer.parseInt(changeStatusVO.getAbleStatus()))) {
            SmsChannelConfigPO configPO = smsChannelConfigRepository.selectById(changeStatusVO.getId());
            LambdaQueryWrapper<SmsSiteLinkPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SmsSiteLinkPO::getChannelCode, configPO.getChannelCode());
            List<SmsSiteLinkPO> linkPOS = smsSiteLinkRepository.selectList(queryWrapper);
            if (linkPOS != null && linkPOS.size() > 0) {
                LambdaUpdateWrapper<SmsSiteLinkPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SmsSiteLinkPO::getChannelCode, configPO.getChannelCode())
                        .set( SmsSiteLinkPO::getStatus, String.valueOf(EnableStatusEnum.DISABLE.getCode()))
                        .set( SmsSiteLinkPO::getUpdatedTime, System.currentTimeMillis())
                        .set( SmsSiteLinkPO::getUpdater, "system");

                smsSiteLinkRepository.update(null, updateWrapper);
            }
        }

        return count;
    }

    public SmsChannelConfigVO querySiteChannel(VerifyCodeSendVO verifyCodeSendVO) {
        return smsChannelConfigRepository.querySiteChannel(verifyCodeSendVO);
    }

    public List<CodeValueNoI18VO> getAddressDownBox() {
        List<SmsChannelConfigPO> list = this.list();
        Map<String, String> map = list.stream().collect(Collectors.toMap(SmsChannelConfigPO::getAddressCode, SmsChannelConfigPO::getAddress, (k1, k2) -> k2));
        List<CodeValueNoI18VO> resultList = new ArrayList<>();
        map.forEach((k, v) -> {
            CodeValueNoI18VO vo = new CodeValueNoI18VO();
            vo.setValue(v);
            vo.setCode(k);
            resultList.add(vo);
        });

        return resultList;
    }

    public Page<SiteBackSmsChannelConfigPageVO> getSiteSmsConfigPage(SmsChannelQueryVO reqVO) {
        Page<SmsChannelConfigPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        return smsChannelConfigRepository.getSiteSmsConfigPage(page, reqVO);
    }

    public ResponseVO<List<SMSAuthorVO>> smsAuthorList(String id) {
        return ResponseVO.success(smsChannelConfigRepository.smsAuthorList(id));
    }


}
