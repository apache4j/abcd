package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.wallet.api.vo.recharge.RegisterVirtualWalletInfosVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletMerchantVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteVirtualWalletVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteVirtualWalletPageQueryVO;
import com.cloud.baowang.wallet.po.SiteVirtualWalletPO;
import com.cloud.baowang.wallet.repositories.SiteVirtualWalletRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class SiteVirtualWalletService extends ServiceImpl<SiteVirtualWalletRepository, SiteVirtualWalletPO> {

    private final SiteVirtualWalletRepository repository;
    private final VirtualWalletInfoRegisterService registerService;

    public Page<SiteVirtualWalletVO> selectPage(SiteVirtualWalletPageQueryVO pageVO) {
        LambdaQueryWrapper<SiteVirtualWalletPO> query = Wrappers.lambdaQuery();
        query.eq(StringUtils.isNotBlank(pageVO.getSiteCode()), SiteVirtualWalletPO::getSiteCode, pageVO.getSiteCode());
        query.eq(StringUtils.isNotBlank(pageVO.getSiteName()), SiteVirtualWalletPO::getSiteName, pageVO.getSiteName());
        query.eq(StringUtils.isNotBlank(pageVO.getParentCompany()), SiteVirtualWalletPO::getCompany, pageVO.getParentCompany());
        Page<SiteVirtualWalletPO> page = new Page<>(pageVO.getPageNumber(), pageVO.getPageSize());
        page = repository.selectPage(page, query);
        return ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, SiteVirtualWalletVO.class)));
    }


    public Boolean insertSiteBaseInfo(SiteVirtualWalletVO siteWalletInfo) {
        SiteVirtualWalletPO siteBasePO = BeanUtil.copyProperties(siteWalletInfo, SiteVirtualWalletPO.class);
        LambdaUpdateWrapper<SiteVirtualWalletPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteVirtualWalletPO>();
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getSiteName,siteBasePO.getSiteName())
                .set(SiteVirtualWalletPO::getCompany,siteBasePO.getCompany())
                .set(SiteVirtualWalletPO::getUpdateTime, System.currentTimeMillis())
                .set(SiteVirtualWalletPO::getOperator,siteWalletInfo.getOperator())
                .eq(SiteVirtualWalletPO::getSiteCode, siteWalletInfo.getSiteCode());
        this.saveOrUpdate(siteBasePO,lambdaUpdateWrapper);
        return true;
    }

    public Boolean saveOrUpdateSiteMerchantInfo(SiteVirtualWalletMerchantVO vo) {
        LambdaQueryWrapper<SiteVirtualWalletPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVirtualWalletPO::getMerchantNo, vo.getMerchantNo());
        query.ne(SiteVirtualWalletPO::getSiteCode, vo.getSiteCode());
        Long count = repository.selectCount(query);
        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.MERCHANT_NO_EXIST);
        }
        LambdaUpdateWrapper<SiteVirtualWalletPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteVirtualWalletPO>();
        lambdaUpdateWrapper.eq(SiteVirtualWalletPO::getSiteCode,vo.getSiteCode());
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getMerchantNo,vo.getMerchantNo());
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getPublicKey,vo.getPublicKey());
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getOperator, CurrReqUtils.getAccount());
        lambdaUpdateWrapper.set(SiteVirtualWalletPO::getUpdateTime, System.currentTimeMillis());
        RegisterVirtualWalletInfosVO httpReqVo = RegisterVirtualWalletInfosVO.builder().platNo(vo.getSiteCode()).platName(vo.getSiteName())
                .signMerNo(vo.getMerchantNo()).signPubKey(vo.getPublicKey()).build();

        boolean bHttpRsp = registerService.saveOrUpdateSiteMerchantInfo(httpReqVo);
        log.info("SiteVirtualWalletService.saveOrUpdateSiteMerchantInfo---操作人:{} 站点:{} isSuccess:{} 商户信息修改后 -> 商户好{} 公钥:{}",CurrReqUtils.getAccount(), vo.getSiteCode(), bHttpRsp, vo.getMerchantNo(),vo.getPublicKey());
        if (!bHttpRsp) {
            throw new BaowangDefaultException(ResultCode.UPDATE_ERROR);
        }
         return this.update(lambdaUpdateWrapper);


    }
}


