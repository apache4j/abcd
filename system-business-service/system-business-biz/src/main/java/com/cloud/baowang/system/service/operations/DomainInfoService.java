package com.cloud.baowang.system.service.operations;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.api.AgentDomainApi;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainVO;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.enums.*;
import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeBodyVO;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordListReqVO;
import com.cloud.baowang.system.po.operations.DomainInfoPO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.repositories.member.BusinessAdminRepository;
import com.cloud.baowang.system.repositories.operations.DomainInfoRepository;
import com.cloud.baowang.system.service.SiteService;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DomainInfoService extends ServiceImpl<DomainInfoRepository, DomainInfoPO> {

    private final DomainInfoRepository domainInfoRepository;
    private final BusinessAdminRepository businessAdminRepository;
    private final SiteRepository siteRepository;
    private final AgentDomainApi agentDomainApi;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;
    private final SiteService siteService;

    @Transactional
    public ResponseVO<Boolean> add(DomainAddVO domainAddVO) {
        DomainInfoPO domainInfoPO = new DomainInfoPO();
        LambdaQueryWrapper<DomainInfoPO> query = new LambdaQueryWrapper<>();
        query.eq(DomainInfoPO::getDomainType, domainAddVO.getDomainType());
        query.eq(DomainInfoPO::getDomainAddr, domainAddVO.getDomainAddr());
        //域名地址唯一校验
        if (domainInfoRepository.selectCount(query) > 0) {
            throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
        }
        log.info("新增域名,当前域名地址:{},域名类型:{}",domainAddVO.getDomainAddr(),domainAddVO.getDomainType());
        BeanUtils.copyProperties(domainAddVO, domainInfoPO);
        Long currentTimeMillis = System.currentTimeMillis();
        domainInfoPO.setBind(DomainBindStatusEnum.UN_BIND.getCode());
        domainInfoPO.setCreatedTime(currentTimeMillis);
        domainInfoPO.setUpdatedTime(currentTimeMillis);
        String operator = domainAddVO.getOperator();
        domainInfoPO.setCreator(operator);
        domainInfoPO.setUpdater(operator);
        this.save(domainInfoPO);
        return ResponseVO.success();
    }

    /**
     * 域名管理首页条件查询
     *
     * @param domainRequestVO req
     * @return page
     */
    public ResponseVO<Page<DomainVO>> queryDomainPage(DomainRequestVO domainRequestVO) {
        Page<DomainInfoPO> page = new Page<>(domainRequestVO.getPageNumber(), domainRequestVO.getPageSize());
        LambdaQueryWrapper<DomainInfoPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Objects.nonNull(domainRequestVO.getBind()), DomainInfoPO::getBind, domainRequestVO.getBind());
        lqw.eq(StringUtils.isNoneBlank(domainRequestVO.getSiteCode()), DomainInfoPO::getSiteCode, domainRequestVO.getSiteCode());
        lqw.eq(Objects.nonNull(domainRequestVO.getDomainType()), DomainInfoPO::getDomainType, domainRequestVO.getDomainType());
        lqw.like(StringUtils.isNotBlank(domainRequestVO.getDomainAddr()), DomainInfoPO::getDomainAddr, domainRequestVO.getDomainAddr());
        lqw.orderByDesc(DomainInfoPO::getBind);
        lqw.orderByDesc(DomainInfoPO::getUpdatedTime);
        Page<DomainInfoPO> domainPOPage = this.page(page, lqw);
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        IPage<DomainVO> convert = domainPOPage.convert(item -> {
            DomainVO domainVO = BeanUtil.copyProperties(item, DomainVO.class);
            String sideCode = domainVO.getSiteCode();
            if (StringUtils.isNotBlank(sideCode)) {
                LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
                query.eq(SitePO::getSiteCode, sideCode);
                SitePO sitePO = siteRepository.selectOne(query);
                if (sitePO != null) {
                    domainVO.setIsSiteUsed(yesCode);
                    domainVO.setSiteName(sitePO.getSiteName());
                } else {
                    domainVO.setIsSiteUsed(noCode);
                }
            } else {
                domainVO.setIsSiteUsed(noCode);
            }
            return domainVO;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    /**
     * 修改域名
     *
     * @param editVO vo
     * @return true
     */
    @Transactional
    public ResponseVO<Boolean> editDomainStatus(DomainEditVO editVO) {
        DomainInfoPO domainInfoPO = getById(editVO.getId());
        if (domainInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        if (domainInfoPO.getBind().equals(DomainBindStatusEnum.BIND.getCode())) {
            throw new BaowangDefaultException(ResultCode.AL_BIND_DOMAIN_CANT_OPER);
        }
        domainInfoPO.setDomainAddr(editVO.getDomainAddr());
        domainInfoPO.setDomainType(editVO.getDomainType());
        domainInfoPO.setRemark(editVO.getRemark());
        domainInfoPO.setUpdater(editVO.getOperator());
        domainInfoPO.setUpdatedTime(System.currentTimeMillis());
        this.updateById(domainInfoPO);
        return ResponseVO.success();
    }

    public DomainVO getDomainByAddress(String domain) {
        LambdaQueryWrapper<DomainInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DomainInfoPO::getDomainAddr, domain);

        queryWrapper.eq(DomainInfoPO::getBind, CommonConstant.business_one);
        queryWrapper.orderByDesc(DomainInfoPO::getUpdatedTime);
        queryWrapper.last("LIMIT 1");

        DomainInfoPO po = domainInfoRepository.selectOne(queryWrapper);
        if (po == null) return null;
        DomainVO vo = new DomainVO();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> delete(IdVO idVO) {
        DomainInfoPO infoPO = getById(idVO.getId());
        if (infoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        log.info("删除域名,当前域名地址:{}", infoPO.getDomainAddr());
        if (infoPO.getBind().equals(DomainBindStatusEnum.BIND.getCode())) {
            throw new BaowangDefaultException(ResultCode.AL_BIND_DOMAIN_CANT_OPER);
        }
        if (ObjUtil.isNotEmpty(infoPO)) {
            domainInfoRepository.deleteById(idVO.getId());
        }
        return ResponseVO.success();
    }

    @Transactional
    public ResponseVO<Boolean> unbind(DomainEditVO editVO) {
        DomainInfoPO domainInfoPO = super.getById(editVO.getId());
        if (domainInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String siteCode=domainInfoPO.getSiteCode();
        log.info("解除绑定域名,当前域名为:{},对应siteCode:{}", domainInfoPO.getDomainAddr(), domainInfoPO.getSiteCode());
        List<String> before=this.getBaseMapper().selectList(new LambdaUpdateWrapper<DomainInfoPO>().eq(DomainInfoPO::getSiteCode,siteCode))
                .stream().map(DomainInfoPO::getDomainAddr).collect(Collectors.toList());
        if (domainInfoPO.getBind().equals(DomainBindStatusEnum.UN_BIND.getCode())) {
            throw new BaowangDefaultException(ResultCode.NOT_BIND_DOMAIN_CANT_OPER);
        }
        domainInfoPO.setSiteCode(StringUtils.EMPTY);
        domainInfoPO.setUpdatedTime(System.currentTimeMillis());
        domainInfoPO.setBind(DomainBindStatusEnum.UN_BIND.getCode());//解绑状态

        super.updateById(domainInfoPO);
        agentDomainApi.deleteAgentDomain(domainInfoPO.getDomainAddr());
        //清空一下缓存
        RedisUtil.deleteLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, domainInfoPO.getDomainAddr());
        //获取绑定前绑定后数据
        List<String> after=this.getBaseMapper().selectList(new LambdaUpdateWrapper<DomainInfoPO>().eq(DomainInfoPO::getSiteCode,siteCode))
                .stream().map(DomainInfoPO::getDomainAddr).collect(Collectors.toList());
        Map<String,String> domainColumnMap=new HashMap<>();
        Map<String,List<String>> domiansBeforeMap=new HashMap<>();
        Map<String,List<String>> domiansAfterMap=new HashMap<>();
        domainColumnMap.put(SitClounmDefaultEnum.baseClounm.getCode(), SitClounmDefaultEnum.DomainSetting.getname());
        SiteInfoChangeBodyVO domainVo=new SiteInfoChangeBodyVO();
        domiansBeforeMap.put(SitClounmDefaultEnum.baseClounm.getCode(),before);
        domiansAfterMap.put(SitClounmDefaultEnum.baseClounm.getCode(),after);
        domainVo.setChangeBeforeObj(domiansBeforeMap);
        domainVo.setChangeAfterObj(domiansAfterMap);
        domainVo.setColumnNameMap(domainColumnMap);
        domainVo.setChangeType(SiteChangeTypeEnum.option.getname());
        List<JsonDifferenceVO> domainChange=  siteInfoChangeRecordService.getJsonDifferenceList(domainVo);
        SiteInfoChangeRecordListReqVO domainChangerBody =new SiteInfoChangeRecordListReqVO();
        domainChangerBody.setLoginIp(CurrReqUtils.getReqIp());
        domainChangerBody.setCreator(CurrReqUtils.getAccount());
        domainChangerBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
        domainChangerBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
        domainChangerBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
        domainChangerBody.setOptionCode(siteCode);
        domainChangerBody.setOptionName(siteService.getSiteInfo(siteCode).getData().getSiteName());
        List<JsonDifferenceVO>  sitadataList=new ArrayList<>();
        sitadataList.addAll(domainChange);
        domainChangerBody.setData(sitadataList);
        siteInfoChangeRecordService.addJsonDifferenceList(domainChangerBody);
        return ResponseVO.success();
    }

    /**
     * 绑定域名
     *
     * @param domainBindVO vo
     * @return true
     */
    public ResponseVO<Boolean> bind(DomainBindVO domainBindVO) {
        List<String> before=this.getBaseMapper().selectList(new LambdaUpdateWrapper<DomainInfoPO>().eq(DomainInfoPO::getSiteCode,domainBindVO.getSiteCode()))
                .stream().map(DomainInfoPO::getDomainAddr).collect(Collectors.toList());
        List<String> idList = domainBindVO.getIdList();
        List<DomainInfoPO> domainInfoPOS = this.listByIds(idList);
        if (CollectionUtil.isEmpty(domainInfoPOS)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        log.info("绑定域名,当前请求参数:{}", JSON.toJSONString(domainInfoPOS));
        domainInfoPOS.forEach(item -> {
            if (item.getBind().equals(DomainBindStatusEnum.BIND.getCode())) {
                throw new BaowangDefaultException(ResultCode.AL_BIND_DOMAIN_CANT_OPER);
            }
        });

        if (ObjectUtils.isNotEmpty(domainBindVO.getIdList())) {
            String siteCode = domainBindVO.getSiteCode();
            List<AgentDomainVO> agentDomainVOList = new ArrayList<>();
            for (DomainInfoPO domainInfoPO : domainInfoPOS) {
                if (domainInfoPO.getDomainType().equals(DomainInfoTypeEnum.WEB_PORTAL.getType())
                        || domainInfoPO.getDomainType().equals(DomainInfoTypeEnum.AGENT_BACKEND.getType())
                        || domainInfoPO.getDomainType().equals(DomainInfoTypeEnum.AGENT_MERCHANT.getType())) {
                    AgentDomainVO domainVO = new AgentDomainVO();
                    domainVO.setSiteCode(siteCode);
                    domainVO.setDomainType(domainInfoPO.getDomainType());
                    domainVO.setDomainName(domainInfoPO.getDomainAddr());
                    domainVO.setCreator("superAdmin");
                    domainVO.setUpdater("superAdmin");
                    agentDomainVOList.add(domainVO);
                }

            }
            if (CollectionUtil.isNotEmpty(agentDomainVOList)) {
                //生成推广管理-域名管理对应数据
                log.info("存在需要同步代理推广域名的域名列表:{},开始同步", JSON.toJSONString(agentDomainVOList));
                agentDomainApi.addAgentDomain(agentDomainVOList);
            }

            LambdaUpdateWrapper<DomainInfoPO> lambdaUpdate = new LambdaUpdateWrapper<>();
            lambdaUpdate.in(DomainInfoPO::getId, domainBindVO.getIdList())
                    .set(DomainInfoPO::getSiteCode, siteCode)
                    .set(DomainInfoPO::getBind, DomainBindStatusEnum.BIND.getCode())
                    .set(DomainInfoPO::getUpdater, domainBindVO.getOperator())
                    .set(DomainInfoPO::getUpdatedTime, System.currentTimeMillis());
            boolean result = this.update(null, lambdaUpdate);
            if (result) {
                LambdaQueryWrapper<SitePO> query = Wrappers.lambdaQuery();
                query.eq(SitePO::getSiteCode, domainBindVO.getSiteCode());
                SitePO sitePO = siteRepository.selectOne(query);

                //更新redis
                List<DomainInfoPO> list = domainInfoRepository.selectBatchIds(domainBindVO.getIdList());
                for (DomainInfoPO po : list) {
                    RedisUtil.setLocalCachedMap(CacheConstants.KEY_DOMAIN_INFO, po.getDomainAddr(),
                            siteCode + CommonConstant.COLON + sitePO.getTimezone());
                }
            }
        }
        //获取绑定前绑定后数据
        List<String> after=this.getBaseMapper().selectList(new LambdaUpdateWrapper<DomainInfoPO>().eq(DomainInfoPO::getSiteCode,domainBindVO.getSiteCode()))
                .stream().map(DomainInfoPO::getDomainAddr).collect(Collectors.toList());
        Map<String,String> domainColumnMap=new HashMap<>();
        Map<String,List<String>> domiansBeforeMap=new HashMap<>();
        Map<String,List<String>> domiansAfterMap=new HashMap<>();
        domainColumnMap.put(SitClounmDefaultEnum.baseClounm.getCode(), SitClounmDefaultEnum.DomainSetting.getname());
        SiteInfoChangeBodyVO domainVo=new SiteInfoChangeBodyVO();
        domiansBeforeMap.put(SitClounmDefaultEnum.baseClounm.getCode(),before);
        domiansAfterMap.put(SitClounmDefaultEnum.baseClounm.getCode(),after);
        domainVo.setChangeBeforeObj(domiansBeforeMap);
        domainVo.setChangeAfterObj(domiansAfterMap);
        domainVo.setColumnNameMap(domainColumnMap);
        domainVo.setChangeType(SiteChangeTypeEnum.option.getname());
        List<JsonDifferenceVO> domainChange=  siteInfoChangeRecordService.getJsonDifferenceList(domainVo);
        SiteInfoChangeRecordListReqVO domainChangerBody =new SiteInfoChangeRecordListReqVO();
        domainChangerBody.setLoginIp(CurrReqUtils.getReqIp());
        domainChangerBody.setCreator(CurrReqUtils.getAccount());
        domainChangerBody.setOptionType(SiteOptionTypeEnum.DataUpdate.getCode());
        domainChangerBody.setOptionStatus(SiteOptionStatusEnum.success.getCode());
        domainChangerBody.setOptionModelName(SiteOptionModelNameEnum.site.getname());
        domainChangerBody.setOptionCode(domainBindVO.getSiteCode());
        domainChangerBody.setOptionName(siteService.getSiteInfo(domainBindVO.getSiteCode()).getData().getSiteName());
        List<JsonDifferenceVO>  sitadataList=new ArrayList<>();
        sitadataList.addAll(domainChange);
        domainChangerBody.setData(sitadataList);
        siteInfoChangeRecordService.addJsonDifferenceList(domainChangerBody);
        return ResponseVO.success();
    }

    public List<DomainVO> getAll() {
        List<DomainInfoPO> list = domainInfoRepository.selectList(new LambdaQueryWrapper<>());
        try {
            return ConvertUtil.convertListToList(list, new DomainVO());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public DomainVO getDomainByType(DomainQueryVO domainQueryVO) {
        LambdaQueryWrapper<DomainInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DomainInfoPO::getSiteCode, domainQueryVO.getSiteCode());
        queryWrapper.eq(DomainInfoPO::getDomainType, domainQueryVO.getDomainType());
        List<DomainInfoPO> list = domainInfoRepository.selectList(queryWrapper);
        if (list != null && !list.isEmpty()) {
            return ConvertUtil.entityToModel(list.get(0), DomainVO.class);
        }

        return null;
    }


    public DomainVO getDomainByAddress(String domain, List<Integer> domainType, Integer bind) {
        LambdaQueryWrapper<DomainInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DomainInfoPO::getDomainAddr, domain);
        queryWrapper.in(DomainInfoPO::getDomainType, domainType);
        queryWrapper.eq(DomainInfoPO::getBind, bind);
        queryWrapper.orderByDesc(DomainInfoPO::getUpdatedTime);
        queryWrapper.last("limit 0,1");
        DomainInfoPO po = domainInfoRepository.selectOne(queryWrapper);
        if (po == null) {
            return null;
        }
        return BeanUtil.copyProperties(po, DomainVO.class);
    }

    /**
     * 站点域名设置-查询未使用域名与当前站点已绑定域名列表
     *
     * @param reqVO req
     * @return 分页
     */
    public ResponseVO<Page<DomainVO>> queryUnBindDomainPage(DomainRequestVO reqVO) {
        Page<DomainInfoPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        LambdaQueryWrapper<DomainInfoPO> query = Wrappers.lambdaQuery();
        Integer bind = reqVO.getBind();
        String siteCode = reqVO.getSiteCode();
        if (bind != null || StringUtils.isNotBlank(siteCode)) {
            // 直接构建 OR 条件
            query.and(wrapper -> {
                wrapper.eq(DomainInfoPO::getBind, bind);       // 第一个条件：bind = ?
                wrapper.or().eq(DomainInfoPO::getSiteCode, siteCode);  // 第二个条件：OR side_code = ?
            });
        }
        String domainAddr = reqVO.getDomainAddr();
        if (StringUtils.isNotBlank(domainAddr)) {
            query.like(DomainInfoPO::getDomainAddr, domainAddr);
        }
        Integer domainType = reqVO.getDomainType();
        if (domainType != null) {
            query.eq(DomainInfoPO::getDomainType, domainType);
        }
        query.orderByAsc(DomainInfoPO::getBind);
        query.orderByDesc(DomainInfoPO::getUpdatedTime);

        page = this.page(page, query);
        int yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        int noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        IPage<DomainVO> convert = page.convert(item -> {
            DomainVO domainVO = BeanUtil.copyProperties(item, DomainVO.class);
            String voSiteCode = domainVO.getSiteCode();
            if (StringUtils.isNotBlank(voSiteCode)) {
                LambdaQueryWrapper<SitePO> siteQuery = Wrappers.lambdaQuery();
                siteQuery.eq(SitePO::getSiteCode, voSiteCode);
                SitePO sitePO = siteRepository.selectOne(siteQuery);
                if (sitePO != null) {
                    domainVO.setSiteName(sitePO.getSiteName());
                    domainVO.setIsSiteUsed(yesCode);
                } else {
                    domainVO.setIsSiteUsed(noCode);
                }
            } else {
                domainVO.setIsSiteUsed(noCode);
            }
            return domainVO;
        });
        return ResponseVO.success(ConvertUtil.toConverPage(convert));
    }

    public DomainVO getDomainbyAddressAndSitecode(DomainRequestVO domainRequestVO) {
        LambdaQueryWrapper<DomainInfoPO> query = Wrappers.lambdaQuery();
        query.eq(DomainInfoPO::getDomainAddr, domainRequestVO.getDomainAddr());
        if (StringUtils.isNotEmpty(domainRequestVO.getSiteCode())) {
            query.eq(DomainInfoPO::getSiteCode, domainRequestVO.getSiteCode());
        }
        query.orderByDesc(DomainInfoPO::getUpdatedTime).last("limit 1");
        DomainInfoPO domainInfoPO = this.baseMapper.selectOne(query);
        return Objects.nonNull(domainInfoPO) ? BeanUtil.copyProperties(domainInfoPO, DomainVO.class) : null;
    }
}
