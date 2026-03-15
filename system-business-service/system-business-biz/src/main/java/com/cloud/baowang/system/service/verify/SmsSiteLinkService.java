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
import com.cloud.baowang.system.po.verify.SmsChannelConfigPO;
import com.cloud.baowang.system.po.verify.SmsSiteLinkPO;
import com.cloud.baowang.system.repositories.verify.SmsChannelConfigRepository;
import com.cloud.baowang.system.repositories.verify.SmsSiteLinkRepository;
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
public class SmsSiteLinkService extends ServiceImpl<SmsSiteLinkRepository, SmsSiteLinkPO> {

    private final SmsSiteLinkRepository smsSiteLinkRepository;
    private final SmsChannelConfigRepository smsChannelConfigRepository;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;


    @Transactional
    public boolean addBatch(String siteCode, ChannelSiteLinkVO vo,String siteName) {

        //获取短信之前通道信息
        List<String> before= smsSiteLinkRepository.querySiteLinkVOBySiteCode(siteCode).stream().map(SiteLinkVO::getChannelCode).collect(Collectors.toList());
        List<String> codeList = vo.getChannelCodeList();
        if (codeList == null || codeList.size() == 0) {
            //删除此站点所有配置的通道
            LambdaQueryWrapper<SmsSiteLinkPO> del = Wrappers.lambdaQuery();
            del.eq(SmsSiteLinkPO::getSiteCode, siteCode);
            this.remove(del);
        }else{
            //之前存在的不变，没有的的新增，多的删除
            LambdaQueryWrapper<SmsSiteLinkPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SmsSiteLinkPO::getSiteCode, siteCode);
            List<SmsSiteLinkPO> poList = smsSiteLinkRepository.selectList(queryWrapper);
            List<String> poCodeList = poList.stream().map(SmsSiteLinkPO::getChannelCode).toList();
            List<String> delList = new ArrayList<>(poCodeList);
            List<String> addList = new ArrayList<>(codeList);

            delList.removeAll(codeList);
            addList.removeAll(poCodeList);

            if (CollectionUtil.isNotEmpty(delList)) {
                LambdaQueryWrapper<SmsSiteLinkPO> del = Wrappers.lambdaQuery();
                del.eq(SmsSiteLinkPO::getSiteCode, siteCode);
                del.in(SmsSiteLinkPO::getChannelCode, delList);
                this.remove(del);
            }

            if (CollectionUtil.isNotEmpty(addList)) {
                List<SmsSiteLinkPO> list = new ArrayList<>();
                //新增的话状态与总控的一致
                LambdaQueryWrapper<SmsChannelConfigPO> configWrapper = Wrappers.lambdaQuery();
                configWrapper.in(SmsChannelConfigPO::getChannelCode, addList);
                List<SmsChannelConfigPO> configPOS = smsChannelConfigRepository.selectList(configWrapper);
                Map<String, SmsChannelConfigPO> mapPO = configPOS.stream().collect(Collectors.toMap(SmsChannelConfigPO::getChannelCode, p -> p, (k1, k2) -> k2));
                addList.forEach(channelCode -> {
                    SmsSiteLinkPO po = new SmsSiteLinkPO();
                    po.setCreator(vo.getCreator());
                    po.setCreatedTime(System.currentTimeMillis());
                    po.setSiteCode(siteCode);
                    po.setChannelCode(channelCode);
                    po.setStatus(String.valueOf(mapPO.get(channelCode).getStatus()));
                    list.add(po);
                });
                this.saveBatch(list);
            }
        }
        //获取短信之后通道信息
        List<String> after= smsSiteLinkRepository.querySiteLinkVOBySiteCode(siteCode).stream().map(SiteLinkVO::getChannelCode).collect(Collectors.toList());
        Map<String,List<String>> beforeBody=new HashMap<>();
        Map<String,List<String>> afterBody=new HashMap<>();
        if (CollectionUtils.isNotEmpty(before)){
            beforeBody.put(SitClounmDefaultEnum.baseClounm.getCode(),before);
        }
        if (CollectionUtils.isNotEmpty(after)){
            afterBody.put(SitClounmDefaultEnum.baseClounm.getCode(),after);
        }
        //短信通道修改前后日志记录
        Map<String,String> smsMap=new HashMap<>();
        smsMap.put(SitClounmDefaultEnum.baseClounm.getCode(),SiteChangeTypeEnum.smsAuthor.getname());
        SiteInfoChangeBodyVO smsVo=new SiteInfoChangeBodyVO();
        smsVo.setChangeBeforeObj(beforeBody);
        smsVo.setChangeAfterObj(afterBody);
        smsVo.setColumnNameMap(smsMap);
        smsVo.setChangeType(SiteChangeTypeEnum.smsAuthor.getname());

        List<JsonDifferenceVO> smsChange=  siteInfoChangeRecordService.getJsonDifferenceList(smsVo);
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
        List<SiteLinkCountVO> list = smsSiteLinkRepository.querySmsLinkCountChannel();
        if (ObjectUtils.isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(SiteLinkCountVO::getChannelCode, SiteLinkCountVO::getCount, (k1, k2) -> k2));
    }

    public int editStatus(ChannelStatusVO channelStatusVO) {
        SmsSiteLinkPO linkPO = smsSiteLinkRepository.selectById(channelStatusVO.getId());
        if (channelStatusVO.getStatus().equals(CommonConstant.business_one_str)) {
            //总控禁用，站点不能启用
            LambdaQueryWrapper<SmsChannelConfigPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SmsChannelConfigPO::getChannelCode, linkPO.getChannelCode());
            SmsChannelConfigPO channelConfigPO = smsChannelConfigRepository.selectOne(queryWrapper);
            if (channelConfigPO.getStatus() == 0) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_CLOSED);
            }
        }
        SmsSiteLinkPO smsSiteLinkPO = new SmsSiteLinkPO();
        smsSiteLinkPO.setStatus(channelStatusVO.getStatus());
        smsSiteLinkPO.setId(channelStatusVO.getId());
        smsSiteLinkPO.setUpdatedTime(System.currentTimeMillis());
        smsSiteLinkPO.setUpdater(channelStatusVO.getUpdater());
        return smsSiteLinkRepository.updateById(smsSiteLinkPO);
    }
}
