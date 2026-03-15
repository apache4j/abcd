package com.cloud.baowang.system.service.site;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoQueryVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteLoginInfoPageVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteLoginInfoVO;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import com.cloud.baowang.system.po.site.SiteLoginInfoPO;
import com.cloud.baowang.system.repositories.site.SiteAdminRepository;
import com.cloud.baowang.system.repositories.site.SiteLoginInfoRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author qiqi
 */
@Service
@AllArgsConstructor
public class SiteAdminLoginInfoService {


    private final SiteLoginInfoRepository siteLoginInfoRepository;


    private final SiteAdminRepository siteAdminRepository;



    public String addLoginInfo(BusinessLoginInfoAddVO businessLoginInfoAddVO) {
        SiteLoginInfoPO siteLoginInfoPO = new SiteLoginInfoPO();
        BeanUtils.copyProperties(businessLoginInfoAddVO, siteLoginInfoPO);
        Long currentTimeMillis = System.currentTimeMillis();
        siteLoginInfoPO.setAccessTime(currentTimeMillis);
        siteLoginInfoRepository.insert(siteLoginInfoPO);

        LambdaUpdateWrapper<BusinessAdminPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BusinessAdminPO::getUserName, businessLoginInfoAddVO.getUserName());
        updateWrapper.eq(BusinessAdminPO::getSiteCode, businessLoginInfoAddVO.getSiteCode());
        updateWrapper.set(BusinessAdminPO::getLastLoginTime, System.currentTimeMillis());
        updateWrapper.set(BusinessAdminPO::getLastLoginIp, businessLoginInfoAddVO.getIpaddr());
        siteAdminRepository.update(null, updateWrapper);
        return siteLoginInfoPO.getId();
    }

    public Page<SiteLoginInfoVO> querySiteAdminLoginInfoPage(UserLoginRequestVO requestVO) {
        Page<SiteLoginInfoPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        Page<SiteLoginInfoVO> iPage = siteLoginInfoRepository.querySiteLoginInfoPage(page, requestVO);
        iPage.getRecords().forEach(x -> {
            if (TransferStatusEnum.SUCCESS.getCode().equals(x.getStatus())) {
                x.setStatusName(TransferStatusEnum.SUCCESS.getName());
            } else {
                x.setStatusName(TransferStatusEnum.FAIL.getName());
            }
        });

        return iPage;
    }

    public Page<SiteLoginInfoPageVO> listBusinessLoginInfo(BusinessLoginInfoQueryVO businessLoginInfoQueryVO) {
        Page<SiteLoginInfoPO> page = new Page<>(businessLoginInfoQueryVO.getPageNumber(), businessLoginInfoQueryVO.getPageSize());
        LambdaQueryWrapper<SiteLoginInfoPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(businessLoginInfoQueryVO.getUserName()), SiteLoginInfoPO::getUserName, businessLoginInfoQueryVO.getUserName());
        lqw.eq(StringUtils.isNotBlank(businessLoginInfoQueryVO.getIpaddr()), SiteLoginInfoPO::getIpaddr, businessLoginInfoQueryVO.getIpaddr());
        lqw.orderByDesc(SiteLoginInfoPO::getAccessTime);
        Page<SiteLoginInfoPO> businessLoginInfoPOPage = siteLoginInfoRepository.selectPage(page, lqw);
        Page<SiteLoginInfoPageVO> siteLoginInfoPageVOPage = new Page<>();
        BeanUtils.copyProperties(businessLoginInfoPOPage, siteLoginInfoPageVOPage);
        return siteLoginInfoPageVOPage;

    }
}
