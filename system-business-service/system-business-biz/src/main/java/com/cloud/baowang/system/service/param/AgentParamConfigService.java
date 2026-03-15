package com.cloud.baowang.system.service.param;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.job.api.rest.JobInfoApi;
import com.cloud.baowang.job.api.vo.JobUpdateAndStartVo;
import com.cloud.baowang.system.api.enums.AgentParamConfigEnum;
import com.cloud.baowang.system.api.enums.AgentParamValueEnum;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigBO;
import com.cloud.baowang.system.api.vo.param.AgentParamConfigVO;
import com.cloud.baowang.system.po.param.AgentParamConfigPO;
import com.cloud.baowang.system.repositories.AgentParamConfigRepository;
import com.cloud.baowang.system.util.CronUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代理参数配置
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class AgentParamConfigService extends ServiceImpl<AgentParamConfigRepository, AgentParamConfigPO> {


    private final AgentParamConfigRepository agentParamConfigRepository;

    private final JobInfoApi jobInfoApi;


    /**
     * 获取代理参数配置的常量
     */
    public HashMap<String, Object> getEnumList() {
        LinkedList<Map<String, Object>> agentParamConfigList = AgentParamConfigEnum.toList();
        LinkedList<Map<String, Object>> agentParamValueList = AgentParamValueEnum.toList();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("agentParamConfigList", agentParamConfigList);
        resultMap.put("agentParamValueList", agentParamValueList);
        return resultMap;
    }


    private void verifyAgentParamConfig(AgentParamConfigVO agentParamConfigVO) {
        Integer paramType = agentParamConfigVO.getParamType();
        String paramValue = agentParamConfigVO.getParamValue();

        //代理参数配置类型
        if (AgentParamConfigEnum.isNotExist(paramType)) {
            throw new BaowangDefaultException("代理参数配置类型错误");
        }

        if (paramValue == null) {
            throw new BaowangDefaultException("代理参数配置值是空");
        }
        /*if (paramType == AgentParamConfigEnum.Percent.getType()) {
            if (paramValue.compareTo(new BigDecimal("0")) < 0 || paramValue.compareTo(new BigDecimal("100")) > 0) {
                throw new BaowangDefaultException("代理参数配置值范围是0-100");
            }

        }
        if (paramType == AgentParamConfigEnum.FixedValue.getType()) {
            if (paramValue.compareTo(new BigDecimal("0")) < 0) {
                throw new BaowangDefaultException("代理参数配置值范围是大于等于0");
            }
        }*/
    }


    /**
     * 修改代理参数配置
     */
    public void updateAgentParamConfig(AgentParamConfigVO agentParamConfigVO) {
        String id = agentParamConfigVO.getId();
        AgentParamConfigPO agentParamConfigPO = agentParamConfigRepository.selectById(id);
        if (agentParamConfigPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        verifyAgentParamConfig(agentParamConfigVO);

        //修改代理参数配置
        AgentParamConfigPO paramConfigPO = new AgentParamConfigPO();
        BeanUtils.copyProperties(agentParamConfigVO, paramConfigPO);
        paramConfigPO.setUpdateName(agentParamConfigVO.getAgentAccount());
        paramConfigPO.setUpdater(agentParamConfigVO.getUpdater());
        paramConfigPO.setUpdatedTime(System.currentTimeMillis());
        int ii = agentParamConfigRepository.updateById(paramConfigPO);
        // 更新redis
        AgentParamConfigPO newOne = agentParamConfigRepository.selectById(id);
        String redisKey = String.format(RedisConstants.SYSTEM_PARAM_CONFIG_KEY, agentParamConfigPO.getParamCode());
        RedisUtil.deleteKey(redisKey);
        AgentParamConfigBO bo = ConvertUtil.entityToModel(newOne, AgentParamConfigBO.class);
        RedisUtil.setValue(redisKey, JSON.toJSONString(bo));
        // 是否每周奖金发放时间或者每月奖金发放时间
        if (StringUtils.equals(agentParamConfigPO.getParamCode(), AgentParamValueEnum.WEEKLY_BONUS_ISSUE_TIME.getType())) {
            if (StringUtils.isNotBlank(agentParamConfigPO.getJobHandler())) {
                String paramValue = paramConfigPO.getParamValue();
                String[] weekDays = paramValue.split(CommonConstant.COMMA);
                Integer num = Integer.valueOf(weekDays[0].trim());
                String time = weekDays[1].trim();
                String cron = CronUtils.generateCronExpressionForDayOfWeek(num, time);
                JobUpdateAndStartVo requestParam = new JobUpdateAndStartVo();
                requestParam.setExecutorHandler(agentParamConfigPO.getJobHandler());
                requestParam.setScheduleConf(cron);
                jobInfoApi.updateAndStart(requestParam);
            }
        }
        if (StringUtils.equals(agentParamConfigPO.getParamCode(), AgentParamValueEnum.MONTHLY_BONUS_ISSUE_TIME.getType())) {
            if (StringUtils.isNotBlank(agentParamConfigPO.getJobHandler())) {
                String paramValue = paramConfigPO.getParamValue();
                String[] weekDays = paramValue.split(CommonConstant.COMMA);
                Integer num = Integer.valueOf(weekDays[0].trim());
                String time = weekDays[1].trim();
                String cron = CronUtils.generateCronExpressionForDayOfMonth(num, time);
                JobUpdateAndStartVo requestParam = new JobUpdateAndStartVo();
                requestParam.setExecutorHandler(agentParamConfigPO.getJobHandler());
                requestParam.setScheduleConf(cron);
                jobInfoApi.updateAndStart(requestParam);
            }
        }

        //weekly_bonus_payment_time
        if (ii <= 0) {
            throw new BaowangDefaultException(ResultCode.UPDATE_ERROR);
        }
    }


    /**
     * 获取代理参数配置
     */
    public AgentParamConfigBO getAgentParamConfigById(AgentParamConfigVO agentParamConfigVO) {
        String id = agentParamConfigVO.getId();
        AgentParamConfigPO agentParamConfigPO = agentParamConfigRepository.selectById(id);
        if (agentParamConfigPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        AgentParamConfigBO agentParamConfigBO = new AgentParamConfigBO();
        BeanUtils.copyProperties(agentParamConfigPO, agentParamConfigBO);
        return agentParamConfigBO;
    }

    /**
     * 获取代理参数配置
     */
    public List<AgentParamConfigBO> getAgentParamConfigAll() {
        return ConvertUtil.entityListToModelList(super.list(), AgentParamConfigBO.class);
    }


    /**
     * 获取代理参数配置的列表
     */
    public Page<AgentParamConfigBO> getAgentParamConfigList(AgentParamConfigVO agentParamConfigVO) {
        LambdaQueryWrapper<AgentParamConfigPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(AgentParamConfigPO::getId);

        //
        Page<AgentParamConfigPO> page = new Page<>(agentParamConfigVO.getPageNumber(), agentParamConfigVO.getPageSize());
        Page<AgentParamConfigPO> pageList = agentParamConfigRepository.selectPage(page, lambdaQueryWrapper);

        Page<AgentParamConfigBO> pageResultList = new Page<>();
        BeanUtils.copyProperties(pageList, pageResultList);
        List<AgentParamConfigBO> agentParamConfigBOList = pageList.getRecords().stream().map(po -> {
            AgentParamConfigBO versionBO = new AgentParamConfigBO();
            BeanUtils.copyProperties(po, versionBO);
            if (StringUtils.equals(versionBO.getParamCode(), AgentParamValueEnum.WEEKLY_BONUS_ISSUE_TIME.getType())) {
                String paramValue = versionBO.getParamValue();
                String[] weekDays = paramValue.split(CommonConstant.COMMA);
                Integer num = Integer.valueOf(weekDays[0].trim());
                String name = convertToChinese(num);
                versionBO.setParamValueName(name);
            }
            return versionBO;
        }).collect(Collectors.toList());
        pageResultList.setRecords(agentParamConfigBOList);

        return pageResultList;
    }

    public static String convertToChinese(int number) {
        return switch (number) {
            case 1 -> "一";
            case 2 -> "二";
            case 3 -> "三";
            case 4 -> "四";
            case 5 -> "五";
            case 6 -> "六";
            case 7 -> "日";
            default -> String.valueOf(number);
        };
    }

    public List<AgentParamConfigBO> queryAgentParamConfig(final List<String> paramCode) {
        try {
            LambdaQueryWrapper<AgentParamConfigPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentParamConfigPO::getSiteCode, CurrReqUtils.getSiteCode());
            queryWrapper.in(AgentParamConfigPO::getParamCode, paramCode);
            List<AgentParamConfigPO> list = agentParamConfigRepository.selectList(queryWrapper);
            return ConvertUtil.convertListToList(list, new AgentParamConfigBO());
        } catch (Exception e) {
            log.error("查询字典参数发生异常", e);
            throw new BaowangDefaultException("查询字典参数错误");
        }
    }

    public AgentParamConfigBO queryAgentParamConfigByCode(String paramCode) {
        //查询redis
        try {
            String redisKey = String.format(RedisConstants.SYSTEM_PARAM_CONFIG_KEY, paramCode);
            AgentParamConfigBO bo = null;
            String valueStr = RedisUtil.getValue(redisKey);
            if (StringUtils.isBlank(valueStr)) {
                LambdaQueryWrapper<AgentParamConfigPO> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AgentParamConfigPO::getParamCode, paramCode);
                AgentParamConfigPO po = agentParamConfigRepository.selectOne(queryWrapper);
                if (po != null) {
                    bo = ConvertUtil.entityToModel(po, AgentParamConfigBO.class);
                    //  放入redis
                    RedisUtil.setValue(redisKey, JSON.toJSONString(bo));
                }
            } else {
                bo = JSON.parseObject(valueStr, AgentParamConfigBO.class);
            }
            return bo;
        } catch (Exception e) {
            log.error("查询字典参数发生异常", e);
            throw new BaowangDefaultException("查询字典参数错误");
        }
    }
}
