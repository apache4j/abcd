package com.cloud.baowang.system.service.partner;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPaymentVendorVO;
import com.cloud.baowang.system.po.partner.SitePaymentVendorPO;
import com.cloud.baowang.system.po.partner.SystemPaymentVendorPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.partner.SystemPaymentVendorRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemPaymentVendorService extends ServiceImpl<SystemPaymentVendorRepository, SystemPaymentVendorPO> {
    private final MinioFileService fileService;
    private final SystemPaymentVendorRepository repository;
    private final SitePaymentVendorService sitePaymentVendorService;
    private final SiteRepository siteRepository;

    @Transactional
    public Boolean add(SystemPaymentVendorVO vendorVO) {
        SystemPaymentVendorPO paymentVendorPO = BeanUtil.copyProperties(vendorVO, SystemPaymentVendorPO.class);

       /* LambdaQueryWrapper<SystemPaymentVendorPO> query = Wrappers.lambdaQuery();
        query.eq(SystemPaymentVendorPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        long count = this.count(query);
        if (count < 12) {
            paymentVendorPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        } else {
            paymentVendorPO.setStatus(EnableStatusEnum.DISABLE.getCode());
        }*/
        paymentVendorPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        repository.insert(paymentVendorPO);
        //清除缓存
        clearSiteCache();
        //同步至站点
        syncPaymentVendorToSite(paymentVendorPO);

        return true;
    }

    @Transactional
    public Boolean upd(SystemPaymentVendorVO vendorVO) {
        SystemPaymentVendorPO paymentVendorPO = BeanUtil.copyProperties(vendorVO, SystemPaymentVendorPO.class);
        repository.updateById(paymentVendorPO);
        //清除缓存
        clearSiteCache();
        //同步至站点
        syncPaymentVendorToSite(paymentVendorPO);
        return true;
    }

    @Transactional
    public Boolean del(String id) {
        SystemPaymentVendorPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        //清除缓存
        clearSiteCache();
        //顺便删除一下站点配置表中对应的id
        LambdaQueryWrapper<SitePaymentVendorPO> del = Wrappers.lambdaQuery();
        del.eq(SitePaymentVendorPO::getSystemPaymentVendorId, id);
        sitePaymentVendorService.remove(del);
        repository.deleteById(id);
        return true;
    }

    /**
     * 清除站点缓存
     */
    private void clearSiteCache() {
        LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
        List<SitePO> sitePOS = siteRepository.selectList(siteQuery);
        for (SitePO sitePO : sitePOS) {
            RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PAYMENT_VENDOR, sitePO.getSiteCode()));
        }
    }

    public Page<SystemPaymentVendorVO> pageQuery(SystemPaymentVendorPageQueryVO pageQueryVO) {
        LambdaQueryWrapper<SystemPaymentVendorPO> query = Wrappers.lambdaQuery();
        Page<SystemPaymentVendorPO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
        String vendorName = pageQueryVO.getVendorName();
        if (StringUtils.isNotBlank(vendorName)) {
            query.like(SystemPaymentVendorPO::getVendorName, vendorName);
        }
        Integer status = pageQueryVO.getStatus();
        if (status != null) {
            query.eq(SystemPaymentVendorPO::getStatus, status);
        }
        query.orderByAsc(SystemPaymentVendorPO::getUpdatedTime);
        page = repository.selectPage(page, query);
        String minioDomain = fileService.getMinioDomain();

        return ConvertUtil.toConverPage(page.convert(item -> {
            SystemPaymentVendorVO vo = BeanUtil.copyProperties(item, SystemPaymentVendorVO.class);
            vo.setVendorIconImage(minioDomain + "/" + vo.getVendorIcon());
            return vo;
        }));
    }

    public Boolean enableAndDisAble(SystemPaymentVendorVO vendorVO) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(vendorVO.getStatus()) &&
                !EnableStatusEnum.DISABLE.getCode().equals(vendorVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (EnableStatusEnum.DISABLE.getCode().equals(vendorVO.getStatus())) {
            /*long count = this.count();
            //当前总数小于12，不可禁用
            if (count <= 12) {
                throw new BaowangDefaultException(ResultCode.PAYMENT_VENDOR_ENABLE_ERROR);
            }*/
            String id = vendorVO.getId();
            SystemPaymentVendorPO po = this.getById(id);
            po.setUpdatedTime(System.currentTimeMillis());
            po.setStatus(vendorVO.getStatus());
            po.setUpdater(vendorVO.getUpdater());
            repository.updateById(po);
            //禁用,清除一下站点缓存
            clearSiteCache();
            //同步站点
            syncPaymentVendorToSite(po);
        } else {
            /*LambdaQueryWrapper<SystemPaymentVendorPO> query = Wrappers.lambdaQuery();
            query.eq(SystemPaymentVendorPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            long enableCount = this.count(query);
            if (enableCount >= 12) {
                throw new BaowangDefaultException(ResultCode.PARTNER_ENABLE_MAX_ERROR);
            }*/
            SystemPaymentVendorPO po = BeanUtil.copyProperties(vendorVO, SystemPaymentVendorPO.class);
            repository.updateById(po);
        }

        return true;
    }


    /**
     * 总台每启用一个支付商，同步数据至站点配置
     *
     * @param systemPO 总台支付商
     */
    private void syncPaymentVendorToSite(SystemPaymentVendorPO systemPO) {
        //清除一下站点对应的支付商缓存
        clearSiteCache();
        LambdaQueryWrapper<SitePaymentVendorPO> siteParQuery = Wrappers.lambdaQuery();
        siteParQuery.eq(SitePaymentVendorPO::getSystemPaymentVendorId, systemPO.getId());
        long count = sitePaymentVendorService.count(siteParQuery);
        if (count > 0) {
            //存在,批量修改
            LambdaUpdateWrapper<SitePaymentVendorPO> upd = Wrappers.lambdaUpdate();
            upd.eq(SitePaymentVendorPO::getSystemPaymentVendorId, systemPO.getId());
            upd.set(SitePaymentVendorPO::getVendorName, systemPO.getVendorName());
            upd.set(SitePaymentVendorPO::getVendorIcon, systemPO.getVendorIcon());
            upd.set(SitePaymentVendorPO::getUpdatedTime, systemPO.getUpdatedTime());
            upd.set(SitePaymentVendorPO::getUpdater, "superadmin");
            if (EnableStatusEnum.DISABLE.getCode().equals(systemPO.getStatus())) {
                upd.set(SitePaymentVendorPO::getStatus, EnableStatusEnum.DISABLE.getCode());
            }
            sitePaymentVendorService.update(upd);
        } else {
            long sortCount = this.count();
            if (sortCount > 0) {
                sortCount = sortCount - 1;
            }
            List<SitePO> sitePOS = siteRepository.selectList(Wrappers.lambdaQuery());
            long finalSortCount = sortCount;
            List<SitePaymentVendorPO> sitePaymentVendorVOS = sitePOS.stream().map(site -> {
                SitePaymentVendorPO paymentVendorPO = new SitePaymentVendorPO();
                paymentVendorPO.setSystemPaymentVendorId(Long.parseLong(systemPO.getId()));
                paymentVendorPO.setSiteCode(site.getSiteCode());
                paymentVendorPO.setVendorName(systemPO.getVendorName());
                paymentVendorPO.setVendorIcon(systemPO.getVendorIcon());
                paymentVendorPO.setStatus(systemPO.getStatus());
                paymentVendorPO.setSort((int) finalSortCount);
                return paymentVendorPO;
            }).toList();
            sitePaymentVendorService.saveBatch(sitePaymentVendorVOS);
        }
    }

    public List<SystemPaymentVendorVO> listQuery() {
        List<SystemPaymentVendorPO> list = this.list(Wrappers.lambdaQuery(SystemPaymentVendorPO.class)
                .eq(SystemPaymentVendorPO::getStatus, EnableStatusEnum.ENABLE.getCode()));
        return BeanUtil.copyToList(list, SystemPaymentVendorVO.class);
    }

    public SystemPaymentVendorVO detail(String id) {
        SystemPaymentVendorPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SystemPaymentVendorVO vo = BeanUtil.copyProperties(po, SystemPaymentVendorVO.class);
        String vendorIcon = vo.getVendorIcon();
        if (StringUtils.isNotBlank(vendorIcon)) {
            String minioDomain = fileService.getMinioDomain();
            vo.setVendorIconImage(minioDomain + "/" + vendorIcon);
        }
        return vo;
    }
}
