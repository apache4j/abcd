package com.cloud.baowang.system.service.member;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoPageVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import com.cloud.baowang.system.po.member.BusinessAdminPO;
import com.cloud.baowang.system.po.member.BusinessLoginInfoPO;
import com.cloud.baowang.system.repositories.member.BusinessAdminRepository;
import com.cloud.baowang.system.repositories.member.BusinessLoginInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author qiqi
 */
@Service
public class BusinessLoginInfoService {


    private final BusinessLoginInfoRepository businessLoginInfoRepository;

    private final BusinessAdminRepository businessAdminRepository;


    public BusinessLoginInfoService(BusinessLoginInfoRepository businessLoginInfoRepository, BusinessAdminRepository businessAdminRepository) {
        this.businessLoginInfoRepository = businessLoginInfoRepository;
        this.businessAdminRepository = businessAdminRepository;
    }

    public String addLoginInfo(BusinessLoginInfoAddVO businessLoginInfoAddVO) {
        BusinessLoginInfoPO businessLoginInfoPO = new BusinessLoginInfoPO();
        BeanUtils.copyProperties(businessLoginInfoAddVO, businessLoginInfoPO);
        Long currentTimeMillis = System.currentTimeMillis();
        businessLoginInfoPO.setAccessTime(currentTimeMillis);
        businessLoginInfoRepository.insert(businessLoginInfoPO);

        LambdaUpdateWrapper<BusinessAdminPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BusinessAdminPO::getUserName, businessLoginInfoAddVO.getUserName());
        updateWrapper.eq(BusinessAdminPO::getSiteCode, businessLoginInfoAddVO.getSiteCode());
        updateWrapper.set(BusinessAdminPO::getLastLoginTime, System.currentTimeMillis());
        updateWrapper.set(BusinessAdminPO::getLastLoginIp, businessLoginInfoAddVO.getIpaddr());
        businessAdminRepository.update(null, updateWrapper);
        return businessLoginInfoPO.getId();
    }

    public Page<BusinessLoginInfoVO> queryBusinessLoginInfoPage(UserLoginRequestVO requestVO) {
        Page<BusinessLoginInfoPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
        Page<BusinessLoginInfoVO> iPage = businessLoginInfoRepository.queryBusinessLoginInfoPage(page, requestVO);
        iPage.getRecords().forEach(x -> {
            if (TransferStatusEnum.SUCCESS.getCode().equals(x.getStatus())) {
                x.setStatusName(TransferStatusEnum.SUCCESS.getName());
            } else {
                x.setStatusName(TransferStatusEnum.FAIL.getName());
            }
        });

        return iPage;
    }

    public Page<BusinessLoginInfoPageVO> listBusinessLoginInfo(BusinessLoginInfoQueryVO businessLoginInfoQueryVO) {
        Page<BusinessLoginInfoPO> page = new Page<>(businessLoginInfoQueryVO.getPageNumber(), businessLoginInfoQueryVO.getPageSize());
        LambdaQueryWrapper<BusinessLoginInfoPO> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotBlank(businessLoginInfoQueryVO.getUserName()), BusinessLoginInfoPO::getUserName, businessLoginInfoQueryVO.getUserName());
        lqw.eq(StringUtils.isNotBlank(businessLoginInfoQueryVO.getIpaddr()), BusinessLoginInfoPO::getIpaddr, businessLoginInfoQueryVO.getIpaddr());
        lqw.orderByDesc(BusinessLoginInfoPO::getAccessTime);
        Page<BusinessLoginInfoPO> businessLoginInfoPOPage = businessLoginInfoRepository.selectPage(page, lqw);
        Page<BusinessLoginInfoPageVO> businessLoginInfoPageVOPage = new Page<>();
        BeanUtils.copyProperties(businessLoginInfoPOPage, businessLoginInfoPageVOPage);
        return businessLoginInfoPageVOPage;

    }
}
