package com.cloud.baowang.system.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskChangeRecordVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDetailsVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelDownReqVO;
import com.cloud.baowang.system.api.vo.risk.RiskLevelResVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.system.api.vo.risk.RiskRecordReqVO;
import com.cloud.baowang.system.po.risk.RiskAccountPO;
import com.cloud.baowang.system.po.risk.RiskCtrlLevelPO;
import com.cloud.baowang.system.repositories.RiskAccountRepository;
import com.cloud.baowang.system.service.RiskChangeRecordService;
import com.cloud.baowang.system.service.RiskControlTypeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class RiskApiImpl implements RiskApi {

    private final RiskControlTypeService riskControlTypeService;
    private final RiskChangeRecordService riskChangeRecordService;
    private final RiskAccountRepository riskAccountRepository;

    @Override
    public List<RiskAccountVO> getRiskListAccount(RiskListAccountQueryVO queryVO) {
        try {
            LambdaQueryWrapper<RiskAccountPO> queryWrapper = new LambdaQueryWrapper<>();
            if (ObjectUtil.isNotEmpty(queryVO.getRiskControlAccounts())) {
                queryWrapper.in(RiskAccountPO::getRiskControlAccount, queryVO.getRiskControlAccounts());
            }
            if (StringUtils.isNotBlank(queryVO.getSiteCode())) {
                queryWrapper.eq(RiskAccountPO::getSiteCode, queryVO.getSiteCode());
            }
            queryWrapper.eq(RiskAccountPO::getRiskControlTypeCode, queryVO.getRiskControlTypeCode());
            List<RiskAccountPO> list = riskAccountRepository.selectList(queryWrapper);
            return ConvertUtil.convertListToList(list, new RiskAccountVO());
        } catch (Exception e) {
            log.error("查询该类型:{},该条件:{} 风控层级异常", queryVO.getRiskControlTypeCode(),
                    queryVO.getRiskControlAccounts(), e);
            return null;
        }
    }

    @Override
    public List<RiskAccountVO> getAll() {
        LambdaQueryWrapper<RiskAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        List<RiskAccountPO> riskAccountPOS = riskAccountRepository.selectList(queryWrapper);
        try {
            return ConvertUtil.convertListToList(riskAccountPOS, new RiskAccountVO());
        } catch (Exception e) {
            log.error("查询该类型 风控层级异常", e);
            return null;
        }
    }

    @Override
    public RiskAccountVO getRiskAccountByAccount(RiskAccountQueryVO riskAccountQueryVO) {
        LambdaQueryWrapper<RiskAccountPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RiskAccountPO::getRiskControlAccount, riskAccountQueryVO.getRiskControlAccount());
        queryWrapper.eq(RiskAccountPO::getRiskControlTypeCode, riskAccountQueryVO.getRiskControlTypeCode());
        queryWrapper.eq(RiskAccountPO::getSiteCode, riskAccountQueryVO.getSiteCode());
        RiskAccountPO riskAccountPO = riskAccountRepository.selectOne(queryWrapper);

        if (riskAccountPO == null) return null;
        RiskAccountVO vo = new RiskAccountVO();
        BeanUtils.copyProperties(riskAccountPO, vo);
        return vo;
    }

    @Override
    public int saveRiskListAccount(RiskAccountVO riskAccountVO) {
        RiskAccountPO po = new RiskAccountPO();
        if (riskAccountVO == null) return 0;
        BeanUtils.copyProperties(riskAccountVO, po);
        return riskAccountRepository.insert(po);
    }

    @Override
    public Integer updateRiskListAccount(RiskAccountVO riskAccountVO) {
        RiskAccountPO po = new RiskAccountPO();
        BeanUtils.copyProperties(riskAccountVO, po);
        return riskAccountRepository.updateById(po);
    }

    /**
     * todo 未实现，记得实现
     *
     * @param vo 根据id查询风控详情
     * @return
     */
    @Override
    public RiskLevelDetailsVO getById(IdVO vo) {
        RiskCtrlLevelPO byId = riskControlTypeService.getById(vo.getId());
        if (null == byId || byId.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
            return null;
        }
        return ConvertUtil.entityToModel(byId, RiskLevelDetailsVO.class);
    }

    /**
     * @param ids 根据ids查询风控详情
     * @return
     */
    @Override
    public Map<String, RiskLevelDetailsVO> getByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return new HashMap<>();
        }
        return riskControlTypeService.getByIds(ids);
    }

    @Override
    public ResponseVO<List<RiskLevelResVO>> getRiskLevelList(RiskLevelDownReqVO riskLevelDownReqVO) {
        return riskControlTypeService.getRiskLevelList(riskLevelDownReqVO);
    }

    @Override
    public ResponseVO<Page<RiskChangeRecordVO>> getRiskRecordListPage(RiskRecordReqVO riskRecordReqVO) {
        return ResponseVO.success(riskChangeRecordService.getRiskRecordListPage(riskRecordReqVO));
    }
}
