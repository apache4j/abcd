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
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.partner.AddPartnerSortVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SitePartnerVO;
import com.cloud.baowang.system.po.partner.SitePartnerPO;
import com.cloud.baowang.system.po.partner.SystemPartnerPO;
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
public class SitePartnerService extends ServiceImpl<SitePartnerRepository, SitePartnerPO> {
    private final SystemPartnerRepository sysRepository;
    private final MinioFileService minioFileService;

    public Page<SitePartnerVO> pageQuery(SitePartnerPageQueryVO pageQueryVO) {
        LambdaQueryWrapper<SitePartnerPO> query = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(pageQueryVO.getSiteCode())) {
            query.eq(SitePartnerPO::getSiteCode, pageQueryVO.getSiteCode());
        }
        String partnerName = pageQueryVO.getPartnerName();
        if (StringUtils.isNotBlank(partnerName)) {
            query.like(SitePartnerPO::getPartnerName, partnerName);
        }
        Integer status = pageQueryVO.getStatus();
        if (status != null) {
            query.eq(SitePartnerPO::getStatus, status);
        }
        query.orderByAsc(SitePartnerPO::getSort);
        Page<SitePartnerPO> page = new Page<>(pageQueryVO.getPageNumber(), pageQueryVO.getPageSize());
        page = this.page(page, query);
        String minioDomain = minioFileService.getMinioDomain();
        return ConvertUtil.toConverPage(page.convert(item -> {
            SitePartnerVO sitePartnerVO = BeanUtil.copyProperties(item, SitePartnerVO.class);
            sitePartnerVO.setPartnerIconImage(minioDomain + "/" + sitePartnerVO.getPartnerIcon());
            return sitePartnerVO;
        }));
    }

    @Transactional
    public Boolean upd(SitePartnerVO sitePartnerVO) {
        SitePartnerPO sitePartnerPO = BeanUtil.copyProperties(sitePartnerVO, SitePartnerPO.class);
        this.updateById(sitePartnerPO);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER));
        return true;
    }

    @Transactional
    public Boolean enableAndDisable(SitePartnerVO sitePartnerVO) {
        String id = sitePartnerVO.getId();
        SitePartnerPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        //判断当前是禁用还是启用
        if (EnableStatusEnum.ENABLE.getCode().equals(sitePartnerVO.getStatus())) {
            //总控禁用的支付商，站点可不启用
            SystemPartnerPO systemPartnerPO = sysRepository.selectById(po.getSystemPartnerId());
            if (systemPartnerPO == null) {
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
            if (EnableStatusEnum.DISABLE.getCode().equals(systemPartnerPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.PARTNER_CENTER_DISABLE_ERROR);
            }
        }
        SitePartnerPO sitePartnerPO = BeanUtil.copyProperties(sitePartnerVO, SitePartnerPO.class);
        this.updateById(sitePartnerPO);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER));
        return true;
    }

    public Boolean del(String id) {
        this.removeById(id);
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER));
        return true;
    }

    /**
     * 初始化站点赞助商信息
     *
     * @param siteCode siteCode
     */
    public void setPartner(String siteCode) {
        LambdaQueryWrapper<SitePartnerPO> query = Wrappers.lambdaQuery();
        query.eq(SitePartnerPO::getSiteCode, siteCode);
        if (this.count(query) <= 0) {
            //新增站点才做处理
            LambdaQueryWrapper<SystemPartnerPO> sysQuery = Wrappers.lambdaQuery();
            List<SystemPartnerPO> sysPos = sysRepository.selectList(sysQuery);
            if (CollectionUtil.isNotEmpty(sysPos)) {
                List<SitePartnerPO> poList = sysPos.stream()
                        .map(sysPo -> {
                            SitePartnerPO sitePo = new SitePartnerPO();
                            sitePo.setSiteCode(siteCode);
                            sitePo.setPartnerName(sysPo.getPartnerName());
                            sitePo.setPartnerIcon(sysPo.getPartnerIcon());
                            sitePo.setStatus(sysPo.getStatus()); // 启用状态
                            sitePo.setCreator(sysPo.getCreator()); // 创建人
                            sitePo.setCreatedTime(sysPo.getCreatedTime()); // 创建时间
                            sitePo.setUpdater(sysPo.getUpdater()); // 修改人
                            sitePo.setUpdatedTime(sysPo.getUpdatedTime()); // 修改时间
                            sitePo.setSystemPartnerId(Long.parseLong(sysPo.getId())); // 使用 sysPo 的 id 赋值
                            return sitePo;
                        })
                        .toList();
                this.saveBatch(poList);
            }
        }
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER));
    }

    public List<SitePartnerVO> getListBySiteCode(String siteCode) {
        LambdaQueryWrapper<SitePartnerPO> query = Wrappers.lambdaQuery();
        query.eq(SitePartnerPO::getSiteCode, siteCode).eq(SitePartnerPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        query.orderByAsc(SitePartnerPO::getSort);
        List<SitePartnerPO> list = this.list(query);
        List<SitePartnerVO> sitePartnerVOS = BeanUtil.copyToList(list, SitePartnerVO.class);
        if (CollectionUtil.isNotEmpty(sitePartnerVOS)) {
            String minioDomain = minioFileService.getMinioDomain();
            for (SitePartnerVO sitePartnerVO : sitePartnerVOS) {
                sitePartnerVO.setPartnerIconImage(minioDomain + "/" + sitePartnerVO.getPartnerIcon());
            }
        }
        return sitePartnerVOS;
    }

    public Boolean addSortRules(String operator, List<AddPartnerSortVO> sortVOList) {
        List<SitePartnerPO> sitePartnerPOS = BeanUtil.copyToList(sortVOList, SitePartnerPO.class);
        if (CollectionUtil.isNotEmpty(sitePartnerPOS)) {
            long l = System.currentTimeMillis();
            sitePartnerPOS.forEach(item -> {
                item.setUpdater(operator);
                item.setUpdatedTime(l);
            });
            this.updateBatchById(sitePartnerPOS);
        }
        RedisUtil.deleteKey(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_QUERY_LOBBY_TOP_PARTNER));
        return true;
    }

    public List<AddPartnerSortVO> getSortRules(String siteCode) {
        LambdaQueryWrapper<SitePartnerPO> query = Wrappers.lambdaQuery();
        query.eq(SitePartnerPO::getSiteCode, siteCode).orderByAsc(SitePartnerPO::getSort);
        List<SitePartnerPO> list = this.list(query);
        return BeanUtil.copyToList(list, AddPartnerSortVO.class);
    }

    public SitePartnerVO detail(String id) {
        SitePartnerPO po = this.getById(id);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SitePartnerVO vo = BeanUtil.copyProperties(po, SitePartnerVO.class);
        String minioDomain = minioFileService.getMinioDomain();
        String partnerIcon = vo.getPartnerIcon();
        if (StringUtils.isNotBlank(partnerIcon)) {
            vo.setPartnerIconImage(minioDomain + "/" + partnerIcon);
        }
        return vo;
    }
}
