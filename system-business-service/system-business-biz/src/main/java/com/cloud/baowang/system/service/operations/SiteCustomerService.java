package com.cloud.baowang.system.service.operations;

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
import com.cloud.baowang.system.api.vo.verify.ChannelStatusVO;
import com.cloud.baowang.system.po.operations.CustomerChannelPO;
import com.cloud.baowang.system.po.operations.SiteCustomerPO;
import com.cloud.baowang.system.repositories.operations.CustomerChannelRepository;
import com.cloud.baowang.system.repositories.operations.SiteCustomerRepository;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteCustomerService extends ServiceImpl<SiteCustomerRepository, SiteCustomerPO> {
    private final SiteCustomerRepository customerRepository;
    private final CustomerChannelRepository customerChannelRepository;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;

    /**
     * 批量保存 站点与客服通道关系
     *
     * @param siteCode
     * @param channelCodeList
     * @return
     */
    public boolean addSiteCustomer(String siteCode, List<String> channelCodeList,String siteName) {
        try {
            //获取操作之前的客服授权信息 by mufan
            List<String> before = customerRepository.selectList(new LambdaQueryWrapper<SiteCustomerPO>().eq(SiteCustomerPO::getSiteCode, siteCode))
                    .stream().map(SiteCustomerPO::getChannelCode).collect(Collectors.toList());;
            String updater = CurrReqUtils.getAccount();
            if (channelCodeList == null || channelCodeList.size() == 0) {
                //删除此站点所有配置的通道
                LambdaQueryWrapper<SiteCustomerPO> del = Wrappers.lambdaQuery();
                del.eq(SiteCustomerPO::getSiteCode, siteCode);
                this.remove(del);
            }else{
                //之前存在的不变，没有的的新增，多的删除
                LambdaQueryWrapper<SiteCustomerPO> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(SiteCustomerPO::getSiteCode, siteCode);
                List<SiteCustomerPO> poList = customerRepository.selectList(queryWrapper);
                List<String> poCodeList = poList.stream().map(SiteCustomerPO::getChannelCode).toList();
                List<String> delList = new ArrayList<>(poCodeList);
                List<String> addList = new ArrayList<>(channelCodeList);
                delList.removeAll(channelCodeList);
                addList.removeAll(poCodeList);
                if (CollectionUtil.isNotEmpty(delList)) {
                    LambdaQueryWrapper<SiteCustomerPO> del = Wrappers.lambdaQuery();
                    del.eq(SiteCustomerPO::getSiteCode, siteCode);
                    del.in(SiteCustomerPO::getChannelCode, delList);
                    this.remove(del);
                }
                if (CollectionUtil.isNotEmpty(addList)) {
                    List<SiteCustomerPO> list = new ArrayList<>();
                    //新增的话状态与总控的一致
                    LambdaQueryWrapper<CustomerChannelPO> configWrapper = Wrappers.lambdaQuery();
                    configWrapper.in(CustomerChannelPO::getChannelCode, addList);
                    List<CustomerChannelPO> configPOS = customerChannelRepository.selectList(configWrapper);
                    Map<String, CustomerChannelPO> mapPO = configPOS.stream().collect(Collectors.toMap(CustomerChannelPO::getChannelCode, p -> p, (k1, k2) -> k2));
                    addList.forEach(channelCode -> {
                        SiteCustomerPO po = new SiteCustomerPO();
                        po.setCreator(updater);
                        po.setCreatedTime(System.currentTimeMillis());
                        po.setSiteCode(siteCode);
                        po.setChannelCode(channelCode);
                        po.setEnableStatus(mapPO.get(channelCode).getStatus());
                        list.add(po);
                    });
                    this.saveBatch(list);
                }
            }
            //获取删除之后的值
            List<String> after = customerRepository.selectList(new LambdaQueryWrapper<SiteCustomerPO>().eq(SiteCustomerPO::getSiteCode, siteCode))
                    .stream().map(SiteCustomerPO::getChannelCode).collect(Collectors.toList());
            Map<String,List<String>> beforeBody=new HashMap<>();
            Map<String,List<String>> afterBody=new HashMap<>();
            if (CollectionUtils.isNotEmpty(before)){
                beforeBody.put(SitClounmDefaultEnum.baseClounm.getCode(),before);
            }
            if (CollectionUtils.isNotEmpty(after)){
                afterBody.put(SitClounmDefaultEnum.baseClounm.getCode(),after);
            }
            //客服通道修改前后日志记录
            Map<String,String> customerMap=new HashMap<>();
            customerMap.put(SitClounmDefaultEnum.baseClounm.getCode(), SiteChangeTypeEnum.customerAuthor.getname());
            SiteInfoChangeBodyVO customerVo=new SiteInfoChangeBodyVO();
            customerVo.setChangeBeforeObj(beforeBody);
            customerVo.setChangeAfterObj(afterBody);
            customerVo.setColumnNameMap(customerMap);
            customerVo.setChangeType(SiteChangeTypeEnum.customerAuthor.getname());
            List<JsonDifferenceVO> customerChange=  siteInfoChangeRecordService.getJsonDifferenceList(customerVo);
            SiteInfoChangeRecordListReqVO customerChangerBody =new SiteInfoChangeRecordListReqVO();
            customerChangerBody.setLoginIp(CurrReqUtils.getReqIp());
            customerChangerBody.setCreator(CurrReqUtils.getAccount());
            customerChangerBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
            customerChangerBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
            customerChangerBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
            customerChangerBody.setOptionCode(siteCode);
            customerChangerBody.setOptionName(siteName);
            List<JsonDifferenceVO>  sitadataList=new ArrayList<>();
            sitadataList.addAll(customerChange);
            customerChangerBody.setData(sitadataList);
            siteInfoChangeRecordService.addJsonDifferenceList(customerChangerBody);
        } catch (Exception e) {
            log.error(" save customer have error ", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    public int getCountByStatus(String siteCode) {
       return customerRepository.getCountByStatus(siteCode);
    }

    public int editStatus(ChannelStatusVO changeStatusVO) {
        SiteCustomerPO linkPO = customerRepository.selectById(changeStatusVO.getId());
        if (changeStatusVO.getStatus().equals(CommonConstant.business_one_str)) {
            //总控禁用，站点不能启用
            LambdaQueryWrapper<CustomerChannelPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(CustomerChannelPO::getChannelCode, linkPO.getChannelCode());
            CustomerChannelPO channelConfigPO = customerChannelRepository.selectOne(queryWrapper);
            if (channelConfigPO.getStatus() == 0) {
                throw new BaowangDefaultException(ResultCode.CHANNEL_CLOSED);
            }
        }
        SiteCustomerPO SiteCustomerPO = new SiteCustomerPO();
        SiteCustomerPO.setEnableStatus(Integer.valueOf(changeStatusVO.getStatus()));
        SiteCustomerPO.setId(changeStatusVO.getId());
        SiteCustomerPO.setUpdatedTime(System.currentTimeMillis());
        SiteCustomerPO.setUpdater(changeStatusVO.getUpdater());
        return customerRepository.updateById(SiteCustomerPO);
    }
}
