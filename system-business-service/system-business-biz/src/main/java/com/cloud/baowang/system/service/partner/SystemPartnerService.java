package com.cloud.baowang.system.service.partner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.cloud.baowang.system.api.vo.partner.SystemPartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerVO;
import com.cloud.baowang.system.po.partner.SitePartnerPO;
import com.cloud.baowang.system.po.partner.SystemPartnerPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.partner.SitePartnerRepository;
import com.cloud.baowang.system.repositories.partner.SystemPartnerRepository;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SystemPartnerService extends ServiceImpl<SystemPartnerRepository, SystemPartnerPO> {
    private final MinioFileService fileService;
    private final SiteRepository siteRepository;
    private final SystemPartnerRepository repository;
    private final SitePartnerService sitePartnerService;

    @Transactional
    public Boolean add(SystemPartnerVO partnerVO) {
        SystemPartnerPO systemPartnerPO = BeanUtil.copyProperties(partnerVO, SystemPartnerPO.class);
        /*LambdaQueryWrapper<SystemPartnerPO> query = Wrappers.lambdaQuery();
        query.eq(SystemPartnerPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        long count = this.count(query);
        if (count < 6) {
            systemPartnerPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        } else systemPartnerPO.setStatus(EnableStatusEnum.DISABLE.getCode());*/
        systemPartnerPO.setStatus(EnableStatusEnum.ENABLE.getCode());
        repository.insert(systemPartnerPO);
        //同步数据给站点
        syncPartnerToSite(systemPartnerPO.getId());

        return true;
    }

    @Transactional
    public Boolean upd(SystemPartnerVO partnerVO) {
        SystemPartnerPO systemPartnerPO = BeanUtil.copyProperties(partnerVO, SystemPartnerPO.class);
        repository.updateById(systemPartnerPO);
        //更新站点的配置信息
        LambdaUpdateWrapper<SitePartnerPO> update = Wrappers.lambdaUpdate();
        update.eq(SitePartnerPO::getSystemPartnerId, partnerVO.getId());
        update.set(SitePartnerPO::getPartnerName, partnerVO.getPartnerName());
        update.set(SitePartnerPO::getPartnerIcon, partnerVO.getPartnerIcon());
        update.set(SitePartnerPO::getStatus, partnerVO.getStatus());
        update.set(SitePartnerPO::getUpdatedTime, partnerVO.getUpdatedTime());
        update.set(SitePartnerPO::getUpdater, "superadmin");
        sitePartnerService.update(update);
        return true;
    }

    @Transactional
    public Boolean del(String id) {
        SystemPartnerPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(po.getStatus())) {
            throw new BaowangDefaultException(ResultCode.DELETED_AFTER_DISABLED);
        }
        repository.deleteById(id);
        List<SitePO> sitePOS = siteRepository.selectList(new LambdaQueryWrapper<>());
        for (SitePO sitePO : sitePOS) {
            RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER, sitePO.getSiteCode()));
        }
        //顺便删除一下站点配置表中对应的id
        LambdaQueryWrapper<SitePartnerPO> del = Wrappers.lambdaQuery();
        del.eq(SitePartnerPO::getSystemPartnerId, id);
        sitePartnerService.remove(del);

        return true;
    }


    public Page<SystemPartnerVO> pageQuery(SystemPartnerPageQueryVO pageQueryVO) {
        LambdaQueryWrapper<SystemPartnerPO> query = Wrappers.lambdaQuery();
        Page<SystemPartnerPO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
        String partnerName = pageQueryVO.getPartnerName();
        if (StringUtils.isNotBlank(partnerName)) {
            query.like(SystemPartnerPO::getPartnerName, partnerName);
        }
        Integer status = pageQueryVO.getStatus();
        if (status != null) {
            query.eq(SystemPartnerPO::getStatus, status);
        }
        query.orderByAsc(SystemPartnerPO::getUpdatedTime);
        page = repository.selectPage(page, query);
        String minioDomain = fileService.getMinioDomain();
        return ConvertUtil.toConverPage(page.convert(item -> {
            SystemPartnerVO vo = BeanUtil.copyProperties(item, SystemPartnerVO.class);
            vo.setPartnerIconImage(minioDomain + "/" + vo.getPartnerIcon());
            return vo;
        }));
    }

    public Boolean enableAndDisAble(SystemPartnerVO partnerVO) {
        if (!EnableStatusEnum.ENABLE.getCode().equals(partnerVO.getStatus()) &&
                !EnableStatusEnum.DISABLE.getCode().equals(partnerVO.getStatus())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (EnableStatusEnum.DISABLE.getCode().equals(partnerVO.getStatus())) {
            //当前总数小于6，不可禁用
            /*LambdaQueryWrapper<SystemPartnerPO> query = Wrappers.lambdaQuery();
            query.eq(SystemPartnerPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            if (this.count(query) <= 6) {
                throw new BaowangDefaultException(ResultCode.PARTNER_ENABLE_ERROR);
            }*/
            List<SitePO> sitePOS = siteRepository.selectList(new LambdaQueryWrapper<>());
            for (SitePO sitePO : sitePOS) {
                RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER, sitePO.getSiteCode()));
            }
            //顺便禁用站点赞助商
            LambdaUpdateWrapper<SitePartnerPO> upd = Wrappers.lambdaUpdate();
            upd.eq(SitePartnerPO::getSystemPartnerId, partnerVO.getId()).set(SitePartnerPO::getStatus, EnableStatusEnum.DISABLE.getCode())
                    .set(SitePartnerPO::getUpdatedTime, partnerVO.getUpdatedTime()).set(SitePartnerPO::getUpdater, "superadmin");
            sitePartnerService.update(upd);
        } else {
            //启用总数不能超过六个，超过六个提示超出可启用数量
            /*LambdaQueryWrapper<SystemPartnerPO> query = Wrappers.lambdaQuery();
            query.eq(SystemPartnerPO::getStatus, EnableStatusEnum.ENABLE.getCode());
            if (this.count(query) >= 6) {
                throw new BaowangDefaultException(ResultCode.PARTNER_ENABLE_MAX_ERROR);
            }*/
            String systemPartnerId = partnerVO.getId();
            //同步一下站点赞助商数据
            syncPartnerToSite(systemPartnerId);

        }
        SystemPartnerPO po = BeanUtil.copyProperties(partnerVO, SystemPartnerPO.class);
        repository.updateById(po);
        return true;
    }

    /**
     * 总台每启用一个赞助商，同步数据至站点配置
     *
     * @param systemPartnerId 总台赞助商id
     */
    private void syncPartnerToSite(String systemPartnerId) {
        SystemPartnerPO systemPartnerPO = this.getById(systemPartnerId);
        if (systemPartnerPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        //获取下当前所有站点
        List<SitePO> sitePOS = siteRepository.selectList(new QueryWrapper<>());
        if (CollectionUtil.isNotEmpty(sitePOS)) {
            for (SitePO sitePO : sitePOS) {
                RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER, sitePO.getSiteCode()));
            }
            List<String> siteCodes = sitePOS.stream().map(SitePO::getSiteCode).toList();
            LambdaQueryWrapper<SitePartnerPO> siteParQuery = Wrappers.lambdaQuery();
            siteParQuery.eq(SitePartnerPO::getSystemPartnerId, systemPartnerId).in(SitePartnerPO::getSiteCode, siteCodes);
            List<SitePartnerPO> sitePartnerPOS = sitePartnerService.list(siteParQuery);
            if (CollectionUtil.isEmpty(sitePartnerPOS)) {
                long count = this.count();
                if (count > 0) {
                    count = count - 1;
                }
                //站点中
                long finalCount = count;
                List<SitePartnerPO> list = siteCodes.stream()
                        .map(siteCode -> {
                            SitePartnerPO partner = new SitePartnerPO();
                            partner.setSystemPartnerId(Long.parseLong(systemPartnerId));
                            partner.setSiteCode(siteCode);
                            partner.setStatus(EnableStatusEnum.ENABLE.getCode());
                            partner.setUpdatedTime(System.currentTimeMillis());
                            partner.setPartnerName(systemPartnerPO.getPartnerName());
                            partner.setPartnerIcon(systemPartnerPO.getPartnerIcon());
                            partner.setStatus(systemPartnerPO.getStatus());
                            partner.setSort((int) finalCount);
                            return partner;
                        })
                        .toList();
                sitePartnerService.saveBatch(list);
            }
        }
    }

    public List<SystemPartnerVO> listQuery() {
        List<SystemPartnerPO> list = this.list(Wrappers.lambdaQuery(SystemPartnerPO.class).eq(SystemPartnerPO::getStatus, EnableStatusEnum.ENABLE.getCode()));
        return BeanUtil.copyToList(list, SystemPartnerVO.class);
    }

    public SystemPartnerVO detail(String id) {
        SystemPartnerPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SystemPartnerVO vo = BeanUtil.copyProperties(po, SystemPartnerVO.class);
        String partnerIcon = vo.getPartnerIcon();
        String minioDomain = fileService.getMinioDomain();
        if (StringUtils.isNotBlank(partnerIcon)) {
            vo.setPartnerIconImage(minioDomain + "/" + partnerIcon);
        }
        return vo;
    }
}
