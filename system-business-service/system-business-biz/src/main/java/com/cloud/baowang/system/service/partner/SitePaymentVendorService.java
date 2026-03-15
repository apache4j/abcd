package com.cloud.baowang.system.service.partner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.partner.AddSitePaymentVendorSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePaymentVendorVO;
import com.cloud.baowang.system.po.partner.SitePaymentVendorPO;
import com.cloud.baowang.system.po.partner.SystemPaymentVendorPO;
import com.cloud.baowang.system.repositories.partner.SitePaymentVendorRepository;
import com.cloud.baowang.system.repositories.partner.SystemPaymentVendorRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SitePaymentVendorService extends ServiceImpl<SitePaymentVendorRepository, SitePaymentVendorPO> {
    private final SystemPaymentVendorRepository systemPayRepository;
    private final MinioFileService minioFileService;

    public Page<SitePaymentVendorVO> pageQuery(SitePaymentVendorPageQueryVO pageQueryVO) {
        String siteCode = pageQueryVO.getSiteCode();
        String vendorName = pageQueryVO.getVendorName();
        Integer status = pageQueryVO.getStatus();
        Page<SitePaymentVendorPO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
        LambdaQueryWrapper<SitePaymentVendorPO> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(SitePaymentVendorPO::getSiteCode, siteCode);
        }
        if (StringUtils.isNotBlank(vendorName)) {
            query.like(SitePaymentVendorPO::getVendorName, vendorName);
        }
        if (status != null) {
            query.eq(SitePaymentVendorPO::getStatus, status);
        }
        query.orderByAsc(SitePaymentVendorPO::getSort);
        page = this.page(page, query);
        String minioDomain = minioFileService.getMinioDomain();
        return ConvertUtil.toConverPage(page.convert(item -> {
            SitePaymentVendorVO sitePaymentVendorVO = BeanUtil.copyProperties(item, SitePaymentVendorVO.class);
            sitePaymentVendorVO.setVendorIconImage(minioDomain + "/" + sitePaymentVendorVO.getVendorIcon());
            return sitePaymentVendorVO;
        }));
    }

    public Boolean upd(SitePaymentVendorVO sitePartnerVO) {
        SitePaymentVendorPO po = BeanUtil.copyProperties(sitePartnerVO, SitePaymentVendorPO.class);
        this.updateById(po);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR));
        return true;
    }

    public Boolean enableAndDisable(SitePaymentVendorVO sitePartnerVO) {
        SitePaymentVendorPO po = this.getById(sitePartnerVO.getId());
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        //判断当前是禁用还是启用
        if (EnableStatusEnum.ENABLE.getCode().equals(sitePartnerVO.getStatus())) {
            //总控禁用的支付商，站点可不启用
            SystemPaymentVendorPO systemPaymentVendorPO = systemPayRepository.selectById(po.getSystemPaymentVendorId());
            if (systemPaymentVendorPO == null) {
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
            if (EnableStatusEnum.DISABLE.getCode().equals(systemPaymentVendorPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.PAYMENT_CENTER_DISABLE_ERROR);
            }
        }
        SitePaymentVendorPO updPo = BeanUtil.copyProperties(sitePartnerVO, SitePaymentVendorPO.class);
        updPo.setSystemPaymentVendorId(po.getSystemPaymentVendorId());
        this.updateById(updPo);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR));
        return true;
    }

    public Boolean del(String id) {
        this.removeById(id);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR));
        return true;
    }

    /**
     * 初始化站点支付商信息
     *
     * @param siteCode siteCode
     */
    public void setVendor(String siteCode) {
        LambdaQueryWrapper<SitePaymentVendorPO> query = Wrappers.lambdaQuery();
        query.eq(SitePaymentVendorPO::getSiteCode, siteCode);
        if (this.count(query) <= 0) {
            //新增站点才做处理
            LambdaQueryWrapper<SystemPaymentVendorPO> sysQuery = Wrappers.lambdaQuery();
            List<SystemPaymentVendorPO> sysPos = systemPayRepository.selectList(sysQuery);
            if (CollectionUtil.isNotEmpty(sysPos)) {
                List<SitePaymentVendorPO> poList = sysPos.stream()
                        .map(sysPo -> {
                            SitePaymentVendorPO sitePo = new SitePaymentVendorPO();
                            sitePo.setSiteCode(siteCode);
                            sitePo.setVendorName(sysPo.getVendorName()); // 支付商名称
                            sitePo.setVendorIcon(sysPo.getVendorIcon()); // 支付商图标
                            sitePo.setStatus(sysPo.getStatus()); // 启用状态
                            sitePo.setCreator(sysPo.getCreator()); // 创建人
                            sitePo.setCreatedTime(sysPo.getCreatedTime()); // 创建时间
                            sitePo.setUpdater(sysPo.getUpdater()); // 修改人
                            sitePo.setUpdatedTime(sysPo.getUpdatedTime()); // 修改时间
                            sitePo.setSystemPaymentVendorId(Long.parseLong(sysPo.getId())); // 使用 sysPo 的 id 赋值
                            return sitePo;
                        })
                        .toList();
                this.saveBatch(poList);
            }
        }
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR));
    }

    public List<SitePaymentVendorVO> getListBySiteCode(String siteCode) {
        List<SitePaymentVendorPO> list = this.list(Wrappers.lambdaQuery(SitePaymentVendorPO.class).
                eq(SitePaymentVendorPO::getSiteCode, siteCode).eq(SitePaymentVendorPO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .orderByAsc(SitePaymentVendorPO::getSort));
        List<SitePaymentVendorVO> sitePaymentVendorVOS = BeanUtil.copyToList(list, SitePaymentVendorVO.class);
        if (CollectionUtil.isNotEmpty(sitePaymentVendorVOS)) {
            for (SitePaymentVendorVO sitePaymentVendorVO : sitePaymentVendorVOS) {
                sitePaymentVendorVO.setVendorIconImage(sitePaymentVendorVO.getVendorIcon());
            }
        }
        return sitePaymentVendorVOS;
    }

    public Boolean addSortRules(String operator, List<AddSitePaymentVendorSortVO> sortVOList) {
        List<SitePaymentVendorPO> sitePaymentVendorPOS = BeanUtil.copyToList(sortVOList, SitePaymentVendorPO.class);
        if (CollectionUtil.isNotEmpty(sitePaymentVendorPOS)) {
            long l = System.currentTimeMillis();
            sitePaymentVendorPOS.forEach(item -> {
                item.setUpdater(operator);
                item.setUpdatedTime(l);
            });
            this.updateBatchById(sitePaymentVendorPOS);
        }
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR));
        return true;
    }

    public List<AddSitePaymentVendorSortVO> getSortRules(String siteCode) {
        LambdaQueryWrapper<SitePaymentVendorPO> query = Wrappers.lambdaQuery();
        query.eq(SitePaymentVendorPO::getSiteCode, siteCode).orderByAsc(SitePaymentVendorPO::getSort);
        List<SitePaymentVendorPO> list = this.list(query);
        return BeanUtil.copyToList(list, AddSitePaymentVendorSortVO.class);
    }

    public SitePaymentVendorVO detail(String id) {
        SitePaymentVendorPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SitePaymentVendorVO vo = BeanUtil.copyProperties(po, SitePaymentVendorVO.class);
        String minioDomain = minioFileService.getMinioDomain();
        String vendorIcon = vo.getVendorIcon();
        if (StringUtils.isNotBlank(vendorIcon)) {
            vo.setVendorIconImage(minioDomain+"/"+vendorIcon);
        }
        return vo;
    }
}
