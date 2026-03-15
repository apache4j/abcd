package com.cloud.baowang.system.service.verify;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.system.api.enums.*;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.api.vo.verify.ChannelSiteLinkVO;
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.api.vo.verify.SiteLinkCountVO;
import com.cloud.baowang.system.api.vo.verify.SiteLinkVO;
import com.cloud.baowang.system.po.verify.MailChannelConfigPO;
import com.cloud.baowang.system.po.verify.MailSiteLinkPO;
import com.cloud.baowang.system.repositories.verify.MailChannelConfigRepository;
import com.cloud.baowang.system.repositories.verify.MailSiteLinkRepository;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
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
public class MailSiteLinkService extends ServiceImpl<MailSiteLinkRepository, MailSiteLinkPO> {

    private final MailSiteLinkRepository mailSiteLinkRepository;
    private final MailChannelConfigRepository mailChannelConfigRepository;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;

    @Transactional
    public boolean addBatch(String siteCode, ChannelSiteLinkVO vo,String siteName) {
        //获取邮箱之前通道信息
        List<String> before= mailSiteLinkRepository.querySiteLinkVOBySiteCode(siteCode).stream().map(SiteLinkVO::getChannelCode).collect(Collectors.toList());
        List<String> codeList = vo.getChannelCodeList();
        if (codeList == null || codeList.size() == 0) {
            //删除此站点所有配置的通道
            LambdaQueryWrapper<MailSiteLinkPO> del = Wrappers.lambdaQuery();
            del.eq(MailSiteLinkPO::getSiteCode, siteCode);
            this.remove(del);
        }else{
            //之前存在的不变，没有的的新增，多的删除
            LambdaQueryWrapper<MailSiteLinkPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(MailSiteLinkPO::getSiteCode, siteCode);
            List<MailSiteLinkPO> poList = mailSiteLinkRepository.selectList(queryWrapper);
            List<String> poCodeList = poList.stream().map(MailSiteLinkPO::getChannelCode).toList();
            List<String> delList = new ArrayList<>(poCodeList);
            List<String> addList = new ArrayList<>(codeList);

            delList.removeAll(codeList);
            addList.removeAll(poCodeList);

            if (CollectionUtil.isNotEmpty(delList)) {
                LambdaQueryWrapper<MailSiteLinkPO> del = Wrappers.lambdaQuery();
                del.eq(MailSiteLinkPO::getSiteCode, siteCode);
                del.in(MailSiteLinkPO::getChannelCode, delList);
                this.remove(del);
            }

            if (CollectionUtil.isNotEmpty(addList)) {
                List<MailSiteLinkPO> list = new ArrayList<>();
                //新增的话状态与总控的一致
                LambdaQueryWrapper<MailChannelConfigPO> configWrapper = Wrappers.lambdaQuery();
                configWrapper.in(MailChannelConfigPO::getChannelCode, addList);
                List<MailChannelConfigPO> configPOS = mailChannelConfigRepository.selectList(configWrapper);
                Map<String, MailChannelConfigPO> mapPO = configPOS.stream().collect(Collectors.toMap(MailChannelConfigPO::getChannelCode, p -> p, (k1, k2) -> k2));
                addList.forEach(channelCode -> {
                    MailSiteLinkPO po = new MailSiteLinkPO();
                    po.setCreator(vo.getCreator());
                    po.setCreatedTime(System.currentTimeMillis());
                    po.setSiteCode(siteCode);
                    po.setStatus(String.valueOf(mapPO.get(channelCode).getStatus()));
                    po.setChannelCode(channelCode);
                    list.add(po);
                });
                this.saveBatch(list);
            }
        }
        //获取邮箱之后通道信息
        List<String> after= mailSiteLinkRepository.querySiteLinkVOBySiteCode(siteCode).stream().map(SiteLinkVO::getChannelCode).collect(Collectors.toList());
        Map<String,List<String>> beforeBody=new HashMap<>();
        Map<String,List<String>> afterBody=new HashMap<>();
        if (CollectionUtils.isNotEmpty(before)){
            beforeBody.put(SitClounmDefaultEnum.baseClounm.getCode(),before);
        }
        if (CollectionUtils.isNotEmpty(after)){
            afterBody.put(SitClounmDefaultEnum.baseClounm.getCode(),after);
        }
        //短信通道修改前后日志记录
        Map<String,String> mailMap=new HashMap<>();
        mailMap.put(SitClounmDefaultEnum.baseClounm.getCode(), SiteChangeTypeEnum.mailAuthor.getname());
        SiteInfoChangeBodyVO mailVo=new SiteInfoChangeBodyVO();
        mailVo.setChangeBeforeObj(beforeBody);
        mailVo.setChangeAfterObj(afterBody);
        mailVo.setColumnNameMap(mailMap);
        mailVo.setChangeType(SiteChangeTypeEnum.mailAuthor.getname());
        List<JsonDifferenceVO> smsChange=  siteInfoChangeRecordService.getJsonDifferenceList(mailVo);
        SiteInfoChangeRecordListReqVO smsChangerBody =new SiteInfoChangeRecordListReqVO();
        smsChangerBody.setLoginIp(CurrReqUtils.getReqIp());
        smsChangerBody.setCreator(CurrReqUtils.getAccount());
        smsChangerBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
        smsChangerBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
        smsChangerBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
        smsChangerBody.setOptionCode(siteCode);
        smsChangerBody.setOptionName(siteName);
        List<JsonDifferenceVO>  sitadataList=new ArrayList<>();
        sitadataList.addAll(smsChange);
        smsChangerBody.setData(sitadataList);
        siteInfoChangeRecordService.addJsonDifferenceList(smsChangerBody);
        return true;
    }

    public Map<String, Integer> queryChannelCount() {
        List<SiteLinkCountVO> list = mailSiteLinkRepository.queryEmailLinkCountChannel();
        if (ObjectUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(SiteLinkCountVO::getChannelCode, SiteLinkCountVO::getCount, (k1, k2) -> k2));
    }

    public int editStatus(ChannelStatusVO channelStatusVO) {
        MailSiteLinkPO linkPO = mailSiteLinkRepository.selectById(channelStatusVO.getId());
        if (channelStatusVO.getStatus().equals(CommonConstant.business_one_str)) {
            //总控禁用，站点不能启用
            LambdaQueryWrapper<MailChannelConfigPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(MailChannelConfigPO::getChannelCode, linkPO.getChannelCode());
            MailChannelConfigPO channelConfigPO = mailChannelConfigRepository.selectOne(queryWrapper);
            if (channelConfigPO.getStatus() == 0) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_CLOSED);
            }
        }
        MailSiteLinkPO mailSiteLinkPO = new MailSiteLinkPO();
        mailSiteLinkPO.setStatus(channelStatusVO.getStatus());
        mailSiteLinkPO.setId(channelStatusVO.getId());
        mailSiteLinkPO.setUpdatedTime(System.currentTimeMillis());
        mailSiteLinkPO.setUpdater(channelStatusVO.getUpdater());
        return mailSiteLinkRepository.updateById(mailSiteLinkPO);
    }
}
