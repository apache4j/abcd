package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentCommissionPlanPO;
import com.cloud.baowang.agent.po.AgentCommissionPlanTurnoverConfigPO;
import com.cloud.baowang.agent.po.AgentCommissionPlanTurnoverPO;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanRepository;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanTurnoverConfigRepository;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanTurnoverRepository;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代理佣金方案（有效流水） + 配置管理服务 业务实现类
 *
 * @author remo
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionPlanTurnoverService extends ServiceImpl<AgentCommissionPlanRepository, AgentCommissionPlanPO> {

    private final AgentCommissionPlanTurnoverRepository turnoverRepository;

    private final AgentCommissionPlanTurnoverConfigRepository turnoverConfigRepository;

    private final AgentInfoService agentInfoService;

    /**
     * 分页查询佣金方案（有效流水）列表
     *
     * @param reqVO 查询导航
     * @return 分页数据
     */
    public ResponseVO<Page<CommissionPlanTurnoverPageListVO>> planTurnoverPageList(CommissionPlanTurnoverPageQueryVO reqVO) {
        Page<AgentCommissionPlanTurnoverPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());

        IPage<AgentCommissionPlanTurnoverPO> pageResult = turnoverRepository.selectPage(page, Wrappers.<AgentCommissionPlanTurnoverPO>lambdaQuery()
                .like(StringUtils.hasText(reqVO.getPlanName()), AgentCommissionPlanTurnoverPO::getPlanName, reqVO.getPlanName())
                .eq(StringUtils.hasText(reqVO.getSiteCode()), AgentCommissionPlanTurnoverPO::getSiteCode, reqVO.getSiteCode())
                .eq(StringUtils.hasText(reqVO.getCreator()), AgentCommissionPlanTurnoverPO::getCreator, reqVO.getCreator())
                .eq(StringUtils.hasText(reqVO.getUpdater()), AgentCommissionPlanTurnoverPO::getUpdater, reqVO.getUpdater())
                .ge(reqVO.getStartTime() != null, AgentCommissionPlanTurnoverPO::getCreatedTime, reqVO.getStartTime())
                .le(reqVO.getEndTime() != null, AgentCommissionPlanTurnoverPO::getCreatedTime, reqVO.getEndTime())
                .orderByDesc(AgentCommissionPlanTurnoverPO::getCreatedTime));

        IPage<CommissionPlanTurnoverPageListVO> convertPageList = pageResult.convert(po -> {
            CommissionPlanTurnoverPageListVO listVO = new CommissionPlanTurnoverPageListVO();
            BeanUtil.copyProperties(po, listVO);
            Long count = agentInfoService.getAgentCountByPlanCode(po.getPlanCode());
            listVO.setAgentCount(count);
            return listVO;
        });
        return ResponseVO.success((Page<CommissionPlanTurnoverPageListVO>) convertPageList);
    }

    /**
     * 获取佣金方案（有效流水）详情
     *
     * @param planCode 方案编码
     * @return 详情信息
     */
    public ResponseVO<CommissionPlanTurnoverDetailVO> planTurnoverDetail(String siteCode, String planCode) {
        AgentCommissionPlanTurnoverPO plan = turnoverRepository.selectOne(Wrappers
                .<AgentCommissionPlanTurnoverPO>lambdaQuery()
                .eq(AgentCommissionPlanTurnoverPO::getSiteCode, siteCode)
                .eq(AgentCommissionPlanTurnoverPO::getPlanCode, planCode));

        if (plan == null) {
            return ResponseVO.fail(ResultCode.AGENT_PLAN_TURNOVER_NOT_EXIST);
        }

        List<AgentCommissionPlanTurnoverConfigPO> configs = turnoverConfigRepository.selectList(Wrappers
                .<AgentCommissionPlanTurnoverConfigPO>lambdaQuery()
                .eq(AgentCommissionPlanTurnoverConfigPO::getPlanCode, planCode)
                .orderByAsc(AgentCommissionPlanTurnoverConfigPO::getVenueType));

        CommissionPlanTurnoverDetailVO detailVO = new CommissionPlanTurnoverDetailVO();
        BeanUtil.copyProperties(plan, detailVO);

        detailVO.setVenueGroups(buildVenueGroups(configs));

        return ResponseVO.success(detailVO);
    }

    /**
     * 构建详情分组
     *
     * @param configs 分组元数据
     * @return 分组后的配置列表
     */
    private List<CommissionPlanTurnoverVenueGroupVO> buildVenueGroups(List<AgentCommissionPlanTurnoverConfigPO> configs) {
        if (configs == null || configs.isEmpty()) {
            return Collections.emptyList();
        }
        // 1. venueType 分组
        Map<Integer, List<AgentCommissionPlanTurnoverConfigPO>> venueMap = configs.stream()
                .collect(Collectors.groupingBy(AgentCommissionPlanTurnoverConfigPO::getVenueType));

        List<CommissionPlanTurnoverVenueGroupVO> venueGroups = new ArrayList<>();
        for (Map.Entry<Integer, List<AgentCommissionPlanTurnoverConfigPO>> venueEntry : venueMap.entrySet()) {
            CommissionPlanTurnoverVenueGroupVO venueGroup = new CommissionPlanTurnoverVenueGroupVO();
            venueGroup.setVenueType(venueEntry.getKey());
            // 2. currency 分组
            Map<String, List<AgentCommissionPlanTurnoverConfigPO>> currencyMap = venueEntry.getValue().stream()
                    .collect(Collectors.groupingBy(AgentCommissionPlanTurnoverConfigPO::getCurrency));
            List<CommissionPlanTurnoverCurrencyGroupVO> currencyGroups = new ArrayList<>();

            for (Map.Entry<String, List<AgentCommissionPlanTurnoverConfigPO>> currencyEntry : currencyMap.entrySet()) {
                CommissionPlanTurnoverCurrencyGroupVO currencyGroup = new CommissionPlanTurnoverCurrencyGroupVO();
                currencyGroup.setCurrency(currencyEntry.getKey());

                List<CommissionPlanTurnoverConfigItemVO> items = currencyEntry.getValue().stream()
                        .sorted(Comparator.comparing(AgentCommissionPlanTurnoverConfigPO::getBetAmount))
                        .map(po -> {
                            CommissionPlanTurnoverConfigItemVO item = new CommissionPlanTurnoverConfigItemVO();
                            item.setTierNum(po.getTierNum());
                            item.setBetAmount(po.getBetAmount());
                            item.setRate(po.getRate());
                            return item;
                        }).toList();
                currencyGroup.setConfigs(items);
                currencyGroups.add(currencyGroup);
            }
            venueGroup.setCurrencyGroups(currencyGroups);
            venueGroups.add(venueGroup);
        }

        return venueGroups;
    }

    /**
     * 新增佣金方案（有效流水）
     *
     * @param addVO 新增参数
     * @return 成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> addPlanTurnover(CommissionPlanTurnoverAddVO addVO) {
        // 方案名称重复校验
        if (turnoverRepository.selectCount(Wrappers
                .<AgentCommissionPlanTurnoverPO>lambdaQuery()
                .eq(AgentCommissionPlanTurnoverPO::getPlanName, addVO.getPlanName())
                .eq(AgentCommissionPlanTurnoverPO::getSiteCode, addVO.getSiteCode())) > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_REPEAT);
        }

        // 校验方案配置项
        validateConfigs(addVO.getConfigs());

        long now = System.currentTimeMillis();
        AgentCommissionPlanTurnoverPO plan = new AgentCommissionPlanTurnoverPO();
        plan.setSiteCode(addVO.getSiteCode());
        plan.setPlanCode(OrderUtil.createCharacter(6));
        plan.setPlanName(addVO.getPlanName());
        plan.setRemark(addVO.getRemark());
        plan.setCreator(addVO.getCreator());
        plan.setCreatedTime(now);
        plan.setUpdater(addVO.getCreator());
        plan.setUpdatedTime(now);
        // 保存主数据
        turnoverRepository.insert(plan);
        // 保存配置
        saveConfigs(plan.getPlanCode(), addVO.getConfigs(), addVO.getCreator(), now);

        return ResponseVO.success();
    }

    /**
     * 编辑佣金方案（有效流水）
     *
     * @param updateVO 编辑参数
     * @return 成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> editPlanTurnover(CommissionPlanTurnoverUpdateVO updateVO) {
        AgentCommissionPlanTurnoverPO orgData = turnoverRepository.selectById(updateVO.getId());
        if (orgData == null || !orgData.getSiteCode().equals(updateVO.getSiteCode())) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_TURNOVER_NOT_EXIST);
        }
        validateConfigs(updateVO.getConfigs());

        AgentCommissionPlanTurnoverPO update = new AgentCommissionPlanTurnoverPO();
        update.setId(updateVO.getId());
        update.setRemark(updateVO.getRemark());
        update.setUpdater(updateVO.getUpdater());
        update.setUpdatedTime(System.currentTimeMillis());

        turnoverRepository.updateById(update);
        // 重建配置
        turnoverConfigRepository.delete(Wrappers
                .<AgentCommissionPlanTurnoverConfigPO>lambdaQuery()
                .eq(AgentCommissionPlanTurnoverConfigPO::getPlanCode, orgData.getPlanCode()));

        saveConfigs(orgData.getPlanCode(), updateVO.getConfigs(), updateVO.getUpdater(), System.currentTimeMillis());

        return ResponseVO.success();
    }

    /**
     * 删除佣金方案（有效流水）
     *
     * @param id 主键ID
     * @return 成功/失败
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> removePlanTurnover(String id) {
        AgentCommissionPlanTurnoverPO exist = turnoverRepository.selectById(id);
        if (exist == null) {
            return ResponseVO.fail(ResultCode.AGENT_PLAN_TURNOVER_NOT_EXIST);
        }
        Long count = agentInfoService.getAgentCountByPlanCode(exist.getPlanCode());
        if (count > 0) {
            return ResponseVO.fail(ResultCode.AGENT_PLAN_DELETE_FAIL);
        }

        turnoverConfigRepository.delete(Wrappers
                .<AgentCommissionPlanTurnoverConfigPO>lambdaQuery()
                .eq(AgentCommissionPlanTurnoverConfigPO::getPlanCode, exist.getPlanCode()));
        turnoverRepository.deleteById(id);

        return ResponseVO.success();
    }

    /**
     * 校验有效流水方案配置项
     *
     * @param configs 配置项
     */
    private void validateConfigs(List<CommissionPlanTurnoverConfigVO> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_TURNOVER_NO_CONFIG_ITEMS);
        }
        if (configs.size() > 50) {
            throw new BaowangDefaultException(ResultCode.AGENT_PLAN_TURNOVER_CONFIG_MAX_LIMIT);
        }

        Set<String> unique = new HashSet<>();
        for (CommissionPlanTurnoverConfigVO c : configs) {
            String key = c.getVenueType() + "_" + c.getCurrency() + "_" + c.getBetAmount();
            if (!unique.add(key)) {
                throw new BaowangDefaultException(ResultCode.AGENT_PLAN_TURNOVER_CONFIG_REPEAT);
            }
        }
    }

    /**
     * 保存有效流水方案配置项
     *
     * @param configs 配置项
     */
    private void saveConfigs(String planCode, List<CommissionPlanTurnoverConfigVO> configs, String user, long now) {
        for (CommissionPlanTurnoverConfigVO vo : configs) {
            AgentCommissionPlanTurnoverConfigPO po = new AgentCommissionPlanTurnoverConfigPO();
            po.setPlanCode(planCode);
            po.setVenueType(vo.getVenueType());
            po.setCurrency(vo.getCurrency());
            po.setTierNum(vo.getTierNum());
            po.setBetAmount(vo.getBetAmount());
            po.setRate(vo.getRate());
            po.setCreator(user);
            po.setCreatedTime(now);
            turnoverConfigRepository.insert(po);
        }
    }

    public CommissionPlanTurnoverConfigVO getCommissionPlanForCalc(String planCode, Integer venueType, String currencyCode, BigDecimal totalValid) {
        LambdaQueryWrapper<AgentCommissionPlanTurnoverConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionPlanTurnoverConfigPO::getPlanCode, planCode);
        queryWrapper.eq(AgentCommissionPlanTurnoverConfigPO::getVenueType, venueType);
        queryWrapper.eq(AgentCommissionPlanTurnoverConfigPO::getCurrency, currencyCode);
        queryWrapper.le(AgentCommissionPlanTurnoverConfigPO::getBetAmount, totalValid);
        queryWrapper.orderByDesc(AgentCommissionPlanTurnoverConfigPO::getTierNum);
        queryWrapper.last("limit 1");
        AgentCommissionPlanTurnoverConfigPO po = turnoverConfigRepository.selectOne(queryWrapper);
        CommissionPlanTurnoverConfigVO vo = new CommissionPlanTurnoverConfigVO();
        BeanUtil.copyProperties(po, vo);
        return vo;
    }


}
