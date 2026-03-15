package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.AddressTypeEnum;
import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.wallet.api.vo.recharge.RegisterVirtualWalletInfosVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteSingleVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteWalletAddrQueryVO;
import com.cloud.baowang.wallet.po.SiteVirtualWalletPO;
import com.cloud.baowang.wallet.po.SiteVirtualWalletSinglePO;
import com.cloud.baowang.wallet.repositories.SiteVirtualWalletInfoRepository;
import com.cloud.baowang.common.core.utils.AddressUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SiteVirtualWalletInfoService extends ServiceImpl<SiteVirtualWalletInfoRepository, SiteVirtualWalletSinglePO> {

    private final SiteVirtualWalletInfoRepository repository;
    private final SiteVirtualWalletService siteBaseService;

    private final VirtualWalletInfoRegisterService registerService;

    public List<SiteSingleVirtualWalletVO> singleSiteWalletInfo(SiteWalletAddrQueryVO queryVO) {
        LambdaQueryWrapper<SiteVirtualWalletSinglePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVirtualWalletSinglePO::getSiteCode, queryVO.getSiteCode());
        if (queryVO.getTypeFlag().equalsIgnoreCase(AddressTypeEnum.FEE.getTypeCode())) {
            query.eq(SiteVirtualWalletSinglePO::getAddressType, AddressTypeEnum.FEE.getTypeCode());
        } else if (queryVO.getTypeFlag().equalsIgnoreCase(AddressTypeEnum.COLLECT.getTypeCode())) {
            query.eq(SiteVirtualWalletSinglePO::getAddressType, AddressTypeEnum.COLLECT.getTypeCode());
        } else {
            query.eq(SiteVirtualWalletSinglePO::getAddressType, AddressTypeEnum.MAIN.getTypeCode());
        }
        List<SiteVirtualWalletSinglePO> walletInfoList = repository.selectList(query);
        try {
            return ConvertUtil.convertListToList(walletInfoList, new SiteSingleVirtualWalletVO());
        } catch (Exception e) {
            log.error("ConvertUtil.convertListToList error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
    public SiteVirtualWalletSinglePO  checkExistSiteWalletInfo(SiteSingleVirtualWalletVO vo ) {
        // 根据 site_code 查询是否存在
        LambdaQueryWrapper<SiteVirtualWalletSinglePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteVirtualWalletSinglePO::getSiteCode, vo.getSiteCode());
        queryWrapper.eq(SiteVirtualWalletSinglePO::getAddressType, vo.getAddressType());
        queryWrapper.eq(SiteVirtualWalletSinglePO::getNetworkType, vo.getNetworkType());
        return this.getOne(queryWrapper);
    }

    @Transactional
    public Boolean saveOrUpdateWalletInfo(List<SiteSingleVirtualWalletVO> siteWalletInfos) {
        if (siteWalletInfos.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        checkDuplicateAddressBySiteCode(siteWalletInfos);
//        Boolean b = checkWalletAddress(siteWalletInfos);
//        if (b){
//            throw new BaowangDefaultException(ResultCode.VIRTUAL_ADDRESS_ALREADY_EXIST);
//        }


        siteWalletInfos.forEach(e -> {
            SiteVirtualWalletSinglePO po = checkExistSiteWalletInfo(e);
            if (po != null) {
                //修改
                LambdaUpdateWrapper<SiteVirtualWalletSinglePO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteVirtualWalletSinglePO>();
                lambdaUpdateWrapper.set(SiteVirtualWalletSinglePO::getAddressType, e.getAddressType().toUpperCase())
                        .set(SiteVirtualWalletSinglePO::getNetworkType, e.getNetworkType().toUpperCase())
                        .set(SiteVirtualWalletSinglePO::getAddressNo, e.getAddressNo())
                        .eq(SiteVirtualWalletSinglePO::getId, po.getId());
                this.update(lambdaUpdateWrapper);
                log.info("SiteVirtualWalletInfoService.saveOrUpdateWalletInfo---操作人:{} 修改虚拟钱包地址信息---更新后:{}+++更新前:{}",CurrReqUtils.getAccount(), e,po);
            }else {
                SiteVirtualWalletSinglePO siteWalletPrePO = BeanUtil.copyProperties(e, SiteVirtualWalletSinglePO.class);
                this.save(siteWalletPrePO);
                log.info("SiteVirtualWalletInfoService.saveOrUpdateWalletInfo---操作人:{} 新增虚拟钱包地址信息:{}",CurrReqUtils.getAccount() ,siteWalletPrePO);
            }
        });

        String siteCode = siteWalletInfos.get(0).getSiteCode();
        LambdaUpdateWrapper<SiteVirtualWalletPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteVirtualWalletPO>();
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getOperator, CurrReqUtils.getAccount())
                .set(SiteVirtualWalletPO::getUpdateTime, System.currentTimeMillis())
                .eq(SiteVirtualWalletPO::getSiteCode, siteCode);
        siteBaseService.update(lambdaUpdateWrapper);

        //http传递
//        LambdaQueryWrapper<SiteVirtualWalletSinglePO> query = Wrappers.lambdaQuery();
//        query.eq(SiteVirtualWalletSinglePO::getSiteCode, siteCode);
//        List<SiteVirtualWalletSinglePO> siteVirtualWalletSinglePOS = repository.selectList(query);
        List<RegisterVirtualWalletInfosVO> registerAddrVOS = new ArrayList<>();
        siteWalletInfos.forEach(e -> {
            registerAddrVOS.add(RegisterVirtualWalletInfosVO.builder()
                    .platNo(e.getSiteCode())
                    .chainType(NetWorkTypeEnum.nameOfCode(e.getNetworkType()).getType())
                    .addressNo(e.getAddressNo()).addressType(e.getAddressType()).secretPhrase(e.getSecretPhrase()).build());
        });
        registerService.registerVirtualWalletInfos(registerAddrVOS);
        return true;
    }


   public void checkDuplicateAddressBySiteCode(List<SiteSingleVirtualWalletVO> list) {
       LambdaQueryWrapper<SiteVirtualWalletSinglePO> query = Wrappers.lambdaQuery();
       //地址合法性
        list.forEach(e -> {
            if(e.getAddressType().equals(AddressTypeEnum.MAIN.getTypeCode())){
                if (StringUtils.isBlank(e.getSecretPhrase())){
                    throw new BaowangDefaultException(ResultCode.MNEMONIC_PHRASE_IS_NULL);
                }
            }
            if (!AddressUtils.isValidAddress(e.getAddressNo(),NetWorkTypeEnum.nameOfCode(e.getNetworkType()).getType())) {
                throw new BaowangDefaultException(ResultCode.VIRTUAL_ADDRESS_ILLEGAL);
            }
            SiteVirtualWalletSinglePO po = checkExistSiteWalletInfo(e);
            if (po != null) {
                if (StringUtils.isBlank(po.getId())){
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }else {
                if (StringUtils.isNotBlank(e.getId())){
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }

            if (e.getAddressType().equals(AddressTypeEnum.FEE.getTypeCode()) || e.getAddressType().equals(AddressTypeEnum.COLLECT.getTypeCode())) {
                //判断是否第一次输入
                if (po == null) {
                    if (!e.getAddressNo().equals(e.getAddressNoVerify())){
                        throw new BaowangDefaultException(ResultCode.CONFIRM_VIRTUAL_CURRENCY_ADDRESS_ERROR);
                    }
                }else {
                    e.setAddressNo(e.getAddressNoVerify());
                }

            }
        });
        //地址唯一
       SiteSingleVirtualWalletVO source = list.get(0);
       query.ne(SiteVirtualWalletSinglePO::getSiteCode, source.getSiteCode());
        List<SiteVirtualWalletSinglePO> mainTempList = repository.selectList(query);
        if (!mainTempList.isEmpty()) {
            for (SiteSingleVirtualWalletVO walletVO : list) {
                boolean match = mainTempList.stream().anyMatch(e -> e.getAddressNo().equals(walletVO.getAddressNo()));
                if (match) {
                    log.info("SiteVirtualWalletInfoService.saveOrUpdateWalletInfo---操作人:{} 站点:{} 提交钱包地址重复:{}",CurrReqUtils.getAccount(), source.getSiteCode(), walletVO.getAddressNo());
                    throw new BaowangDefaultException(ResultCode.VIRTUAL_ADDRESS_ALREADY_EXIST);
                }
            }
        }

       LambdaQueryWrapper<SiteVirtualWalletSinglePO> queryCurSite = Wrappers.lambdaQuery();
        queryCurSite.eq(SiteVirtualWalletSinglePO::getSiteCode, source.getSiteCode());
       List<SiteVirtualWalletSinglePO> walletList = repository.selectList(queryCurSite);
       if (!walletList.isEmpty()) {
           for (SiteVirtualWalletSinglePO walletVO : walletList) {
               if (!walletVO.getAddressType().equals(source.getAddressType())) {
                   if (walletVO.getAddressNo().equals(source.getAddressNo())) {
                       log.info("SiteVirtualWalletInfoService.saveOrUpdateWalletInfo---操作人:{} 站点:{} 提交钱包地址重复:{}",CurrReqUtils.getAccount(), source.getSiteCode(), walletVO.getAddressNo());
                       throw new BaowangDefaultException(ResultCode.VIRTUAL_ADDRESS_ALREADY_EXIST);
                   }
               }
           }
       }


   }


    public Boolean checkWalletAddress(List<SiteSingleVirtualWalletVO> list) {
        String siteCode = list.get(0).getSiteCode();

        LambdaQueryWrapper<SiteVirtualWalletSinglePO> query = Wrappers.lambdaQuery();
        query.eq(SiteVirtualWalletSinglePO::getSiteCode, siteCode);
        List<SiteVirtualWalletSinglePO> pos = repository.selectList(query);
        List<SiteVirtualWalletSinglePO> erc20List = pos.stream()
                .filter(e -> e.getNetworkType().equals(NetWorkTypeEnum.ERC20.getCode())).toList();
        List<SiteVirtualWalletSinglePO> trc20List = pos.stream()
                .filter(e -> e.getNetworkType().equals(NetWorkTypeEnum.TRC20.getCode())).toList();

        boolean judge = false;
        for (SiteSingleVirtualWalletVO wallet : list) {
            if (wallet.getNetworkType().equals(NetWorkTypeEnum.TRC20.getCode())) {
                if (!erc20List.isEmpty()) {
                    judge = erc20List.stream().map(SiteVirtualWalletSinglePO::getAddressNo).anyMatch(s -> s.equals(wallet.getAddressNo()));
                }
            } else {
                if (!trc20List.isEmpty()) {
                    judge = trc20List.stream().map(SiteVirtualWalletSinglePO::getAddressNo).anyMatch(s -> s.equals(wallet.getAddressNo()));
                }
            }
            if (judge) {
                return judge;
            }

        }
        return judge;

    }
}


