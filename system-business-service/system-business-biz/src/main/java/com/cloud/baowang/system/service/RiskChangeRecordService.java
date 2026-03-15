package com.cloud.baowang.system.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.system.api.vo.risk.RiskChangeRecordVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskRecordReqVO;
import com.cloud.baowang.system.po.risk.RiskChangeRecordPO;
import com.cloud.baowang.system.repositories.RiskRecordRepository;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import com.cloud.baowang.system.service.site.SiteAdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RiskChangeRecordService extends ServiceImpl<RiskRecordRepository, RiskChangeRecordPO> {

    private final RiskRecordRepository riskRecordRepository;
    private final SystemParamService systemParamService;
    private final BusinessAdminService businessAdminService;
    private final RiskControlTypeService riskControlTypeService;

    private final SiteAdminService siteAdminService;

    /**
     * 分页查询
     *
     * @param riskRecordReqVO
     * @return
     */
    public Page<RiskChangeRecordVO> getRiskRecordListPage(RiskRecordReqVO riskRecordReqVO) {
        /*if (ObjectUtil.isNotEmpty(riskRecordReqVO.getCreateName())) {
            //  判断站点的模式，查询不同的表
            //boolean flag = siteService.checkSiteIncludesRiskControl(riskRecordReqVO.getSiteCode());
            List<String> idList = siteAdminService.getUserIdsByUseName(riskRecordReqVO.getCreateName());
            if (CollectionUtils.isEmpty(idList)) {
                return new Page<>();
            }
            riskRecordReqVO.setCreator(idList.get(0));
        }*/

        if (ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskBefore())) {
            RiskLevelResVO resVO = riskControlTypeService.getRiskLevelByLevelCode(riskRecordReqVO.getRiskBefore(), riskRecordReqVO.getSiteCode());
            riskRecordReqVO.setRiskBefore(resVO.getRiskControlLevel());
        }

        if (ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskAfter())) {
            RiskLevelResVO resVO = riskControlTypeService.getRiskLevelByLevelCode(riskRecordReqVO.getRiskAfter(), riskRecordReqVO.getSiteCode());
            riskRecordReqVO.setRiskAfter(resVO.getRiskControlLevel());
        }

        LambdaQueryWrapper<RiskChangeRecordPO> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskControlAccount()),
                RiskChangeRecordPO::getRiskControlAccount, riskRecordReqVO.getRiskControlAccount());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskControlTypeCode()),
                RiskChangeRecordPO::getRiskControlType, riskRecordReqVO.getRiskControlTypeCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskBefore()),
                RiskChangeRecordPO::getRiskBefore, riskRecordReqVO.getRiskBefore());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getRiskAfter()),
                RiskChangeRecordPO::getRiskAfter, riskRecordReqVO.getRiskAfter());
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getCreateName()),
                RiskChangeRecordPO::getCreator, riskRecordReqVO.getCreateName());
        // 添加站点
        queryWrapper.eq(ObjectUtil.isNotEmpty(riskRecordReqVO.getSiteCode()),
                RiskChangeRecordPO::getSiteCode, riskRecordReqVO.getSiteCode());
        if (StringUtils.isNotBlank(riskRecordReqVO.getOrderField())) {
            // 创建时间排序
            if (riskRecordReqVO.getOrderField().equals("createdTime") && riskRecordReqVO.getOrderType().equals("asc")) {
                queryWrapper.orderByAsc(RiskChangeRecordPO::getCreatedTime);
            }
            if (riskRecordReqVO.getOrderField().equals("createdTime") && riskRecordReqVO.getOrderType().equals("desc")) {
                queryWrapper.orderByDesc(RiskChangeRecordPO::getCreatedTime);
            }
        } else {
            queryWrapper.orderByDesc(RiskChangeRecordPO::getCreatedTime);
        }

        RiskLevelDownReqVO riskLevelDownReqVO = new RiskLevelDownReqVO();
        riskLevelDownReqVO.setRiskControlType(riskRecordReqVO.getRiskControlTypeCode());
        riskLevelDownReqVO.setSiteCode(riskRecordReqVO.getSiteCode());
        ResponseVO<List<RiskLevelResVO>> levelListVO = riskControlTypeService.getRiskLevelList(riskLevelDownReqVO);
        List<RiskLevelResVO> levelList = levelListVO.getData();
        Map<String, String> levelMap = levelList.stream().collect(Collectors.toMap(RiskLevelResVO::getRiskControlLevel, RiskLevelResVO::getRiskControlLevelDescribe, (k1, k2) -> k2));

        Page<RiskChangeRecordPO> page = new Page<>(riskRecordReqVO.getPageNumber(), riskRecordReqVO.getPageSize());
        Page<RiskChangeRecordPO> riskChangeRecordPOPage = riskRecordRepository.selectPage(page, queryWrapper);
        Page<RiskChangeRecordVO> riskChangeRecordVOPage = new Page<>();
        List<RiskChangeRecordVO> voList = new ArrayList<>();
        if (riskChangeRecordPOPage != null && !riskChangeRecordPOPage.getRecords().isEmpty()) {
            Map<String, String> map = systemParamService.getSystemParamMap(CommonConstant.RISK_CONTROL_TYPE);
            List<String> collect = riskChangeRecordPOPage.getRecords().stream().map(BasePO::getCreator).toList();
            Map<String, String> userMap = businessAdminService.getUserByIds(collect).stream().collect(Collectors.toMap(BusinessAdminVO::getId, BusinessAdminVO::getUserName));
            riskChangeRecordPOPage.getRecords().forEach(info -> {
                RiskChangeRecordVO vo = new RiskChangeRecordVO();
                BeanUtils.copyProperties(info, vo);
                // 脱敏信息处理
                handleSensitiveInfo(vo, riskRecordReqVO.getDataDesensitization());
                vo.setRiskControlType(map.get(vo.getRiskControlType()));
                vo.setCreateName(userMap.get(info.getCreator()));
                vo.setRiskBeforeDesc(levelMap.get(vo.getRiskBefore()));
                vo.setRiskAfterDesc(levelMap.get(vo.getRiskAfter()));
                voList.add(vo);
            });
            BeanUtils.copyProperties(riskChangeRecordPOPage, riskChangeRecordVOPage);
            riskChangeRecordVOPage.setRecords(voList);
            return riskChangeRecordVOPage;
        }
        return new Page<>();
    }

    /**
     * 敏感信息处理
     *
     * @param vo 返回vo
     */
    private void handleSensitiveInfo(RiskChangeRecordVO vo, Boolean dataDesensitization) {
        // 判断是否是银行卡或者是虚拟货币链
        if ((StringUtils.equals(vo.getRiskControlType(), RiskTypeEnum.RISK_BANK.getCode())
                || StringUtils.equals(vo.getRiskControlType(), RiskTypeEnum.RISK_VIRTUAL.getCode()))
                && BooleanUtils.isTrue(dataDesensitization)) {
            vo.setRiskControlAccount(SymbolUtil.showBankOrVirtualNo(vo.getRiskControlAccount()));
        }
    }

    public RiskChangeRecordVO getLastRiskRecord(String account, String riskType) {
        LambdaQueryWrapper<RiskChangeRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskChangeRecordPO::getRiskControlType, riskType);
        queryWrapper.eq(RiskChangeRecordPO::getRiskControlAccount, account);
        queryWrapper.orderByDesc(RiskChangeRecordPO::getId).last("limit 1");

        RiskChangeRecordPO po = riskRecordRepository.selectOne(queryWrapper);
        if (po != null) {
            RiskChangeRecordVO vo = new RiskChangeRecordVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }

        return null;
    }


    public RiskChangeRecordVO getLastRiskRecord(String account, String riskType, String siteCode) {
        LambdaQueryWrapper<RiskChangeRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskChangeRecordPO::getSiteCode, siteCode);
        queryWrapper.eq(RiskChangeRecordPO::getRiskControlType, riskType);
        queryWrapper.eq(RiskChangeRecordPO::getRiskControlAccount, account);
        queryWrapper.orderByDesc(RiskChangeRecordPO::getCreatedTime).last("limit 1");

        RiskChangeRecordPO po = riskRecordRepository.selectOne(queryWrapper);
        if (po != null) {
            RiskChangeRecordVO vo = new RiskChangeRecordVO();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }

        return null;
    }
}
