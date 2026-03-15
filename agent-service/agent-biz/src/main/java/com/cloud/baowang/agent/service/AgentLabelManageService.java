package com.cloud.baowang.agent.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageAddVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageDeleteVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageEditVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelManageResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelRequestVO;
import com.cloud.baowang.agent.api.vo.label.AgentLabelUserResponseVO;
import com.cloud.baowang.agent.api.vo.label.AgentSaveLabelUserResVO;
import com.cloud.baowang.agent.api.vo.label.GetLabelsByAgentAccountVO;
import com.cloud.baowang.agent.api.vo.label.SaveUserAssociationLabelResVO;
import com.cloud.baowang.agent.po.AgentLabelManagePO;
import com.cloud.baowang.agent.repositories.AgentLabelManageRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class AgentLabelManageService extends ServiceImpl<AgentLabelManageRepository, AgentLabelManagePO> {
    private final AgentLabelManageRepository agentLabelManageRepository;

    public ResponseVO<Void> add(AgentLabelManageAddVO agentLabelAddVO) {
        long count = this.count(Wrappers.lambdaQuery(AgentLabelManagePO.class)
                .eq(AgentLabelManagePO::getLabel, agentLabelAddVO.getLabel())
                .eq(AgentLabelManagePO::getSiteCode, agentLabelAddVO.getSiteCode())
                .eq(AgentLabelManagePO::getAgentAccount, agentLabelAddVO.getAgentAccount()));
        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.AGENT_LABEL_EXISTED);
        }
        AgentLabelManagePO agentLabelPo = new AgentLabelManagePO();
        agentLabelPo.setSiteCode(agentLabelAddVO.getSiteCode());
        agentLabelPo.setLabel(agentLabelAddVO.getLabel());
        agentLabelPo.setAgentAccount(agentLabelAddVO.getAgentAccount());
        agentLabelPo.setCreator(agentLabelAddVO.getOperator());
        agentLabelPo.setCreatedTime(System.currentTimeMillis());
        agentLabelPo.setUpdater(agentLabelAddVO.getOperator());
        agentLabelPo.setUpdatedTime(System.currentTimeMillis());
        agentLabelManageRepository.insert(agentLabelPo);
        return ResponseVO.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> edit(AgentLabelManageEditVO editVO) {
        String beforeLabel = editVO.getBeforeLabel();
        String afterLabel = editVO.getAfterLabel();
        LambdaUpdateWrapper<AgentLabelManagePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(AgentLabelManagePO::getLabel, afterLabel);
        updateWrapper.eq(AgentLabelManagePO::getAgentAccount, editVO.getAgentAccount()).eq(AgentLabelManagePO::getSiteCode, editVO.getSiteCode());
        updateWrapper.eq(AgentLabelManagePO::getLabel, beforeLabel);
        agentLabelManageRepository.update(null, updateWrapper);
        return ResponseVO.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> delete(AgentLabelManageDeleteVO deleteVO) {
        LambdaUpdateWrapper<AgentLabelManagePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AgentLabelManagePO::getAgentAccount, deleteVO.getAgentAccount()).eq(AgentLabelManagePO::getSiteCode, deleteVO.getSiteCode());
        updateWrapper.eq(AgentLabelManagePO::getLabel, deleteVO.getLabel());
        agentLabelManageRepository.delete(updateWrapper);
        return ResponseVO.success();
    }


    public ResponseVO<Map<String, AgentLabelManageResponseVO>> listAllLabel(AgentLabelRequestVO requestVO) {
        Map<String, AgentLabelManageResponseVO> responseVOMap = new HashMap<>(16);
        LambdaQueryWrapper<AgentLabelManagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentLabelManagePO::getAgentAccount, requestVO.getAgentAccount()).eq(AgentLabelManagePO::getSiteCode, requestVO.getSiteCode());
        List<AgentLabelManagePO> agentLabelManagePOS = agentLabelManageRepository.selectList(lambdaQueryWrapper);
        for (int i = 0; i < agentLabelManagePOS.size(); i++) {
            AgentLabelManagePO po = agentLabelManagePOS.get(i);
            String key = po.getLabel();
            if (responseVOMap.containsKey(key)) {
                AgentLabelManageResponseVO responseVO = responseVOMap.get(key);
                if (StringUtils.isNotBlank(po.getAgentAccount())) {
                    if (StringUtils.isNotBlank(po.getUserAccount())) {
                        responseVO.setCount(responseVO.getCount() + 1);
                        if (StringUtils.isNotBlank(responseVO.getUserAccounts())) {
                            responseVO.setUserAccounts(responseVO.getUserAccounts() + CommonConstant.COMMA + po.getUserAccount());
                        } else {
                            responseVO.setUserAccounts(po.getUserAccount());
                        }
                    } else {
                        // 如果该标签下没有配置会员
                        responseVO.setUserAccounts(po.getUserAccount());
                        responseVO.setCount(responseVO.getCount());
                    }
                    responseVOMap.put(key, responseVO);
                }
            } else {
                AgentLabelManageResponseVO responseVO = new AgentLabelManageResponseVO();
                responseVO.setLabel(po.getLabel());
                if (StringUtils.isNotBlank(po.getUserAccount())) {
                    responseVO.setUserAccounts(po.getUserAccount());
                    responseVO.setCount(CommonConstant.business_one);
                } else {
                    responseVO.setCount(CommonConstant.business_zero);
                }
                responseVO.setUpdatedTime(po.getUpdatedTime());
                responseVOMap.put(key, responseVO);
            }
        }
        return ResponseVO.success(responseVOMap);

    }


    /**
     * 代理保存会员标签
     * 添加标签
     */

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> saveUserLabel(AgentSaveLabelUserResVO saveResVO) {
        // 清理所有的标签
        if (StringUtils.isBlank(saveResVO.getLabels())) {
            LambdaUpdateWrapper<AgentLabelManagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AgentLabelManagePO::getAgentAccount, saveResVO.getAgentAccount());
            updateWrapper.eq(AgentLabelManagePO::getUserAccount, saveResVO.getUserAccount())
                    .eq(AgentLabelManagePO::getSiteCode, saveResVO.getSiteCode());
            agentLabelManageRepository.delete(updateWrapper);
            return ResponseVO.success();
        }
        String[] labels = saveResVO.getLabels().split(CommonConstant.COMMA);
        if (labels.length == 0) {
            throw new BaowangDefaultException(ResultCode.RECORD_IS_NOT_EXIST);
        }
        AgentLabelRequestVO agentLabelRequestVO = new AgentLabelRequestVO();
        agentLabelRequestVO.setSiteCode(saveResVO.getSiteCode());
        agentLabelRequestVO.setAgentAccount(saveResVO.getAgentAccount());
        agentLabelRequestVO.setUserAccount(saveResVO.getUserAccount());
        ResponseVO<?> response = queryUserLabelRecord(agentLabelRequestVO);
        if (!response.isOk()) {
            throw new BaowangDefaultException(ResultCode.RECORD_IS_NOT_FAIL);
        }
        // 保存新的
        List<AgentLabelManagePO> insertList = Lists.newArrayList();
        // 删除旧的
        List<String> deletetList = Lists.newArrayList();
        Map<String, AgentLabelUserResponseVO> recordMap = (Map<String, AgentLabelUserResponseVO>) response.getData();
        for (String label : labels) {
            AgentLabelUserResponseVO responseVO = recordMap.get(label);
            if (responseVO == null) {
                continue;
            }
            if (responseVO.getIsLabel() == 0) {
                //需要保存的
                AgentLabelManagePO insert = new AgentLabelManagePO();
                insert.setSiteCode(saveResVO.getSiteCode());
                insert.setUserAccount(saveResVO.getUserAccount());
                insert.setAgentAccount(saveResVO.getAgentAccount());
                insert.setLabel(label);
                insert.setCreator(saveResVO.getOperator());
                insert.setCreatedTime(System.currentTimeMillis());
                insert.setUpdater(saveResVO.getOperator());
                insert.setUpdatedTime(System.currentTimeMillis());
                insertList.add(insert);
            }
        }

        for (Map.Entry<String, AgentLabelUserResponseVO> entry : recordMap.entrySet()) {
            AgentLabelUserResponseVO valueDto = entry.getValue();
            if (valueDto.getIsLabel() == 1) {
                // 已经存在的标签
                boolean isExists = false;
                for (int i = 0; i < labels.length; i++) {
                    // 新保存的不存在
                    if (StringUtils.equals(labels[i], entry.getKey())) {
                        isExists = true;
                    }
                }
                if (!isExists) {
                    //删除
                    deletetList.add(entry.getKey());
                }
            }
        }
        this.saveBatch(insertList);
        // 删除动作
        if (!deletetList.isEmpty()) {
            LambdaUpdateWrapper<AgentLabelManagePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AgentLabelManagePO::getSiteCode, saveResVO.getSiteCode());
            updateWrapper.eq(AgentLabelManagePO::getAgentAccount, saveResVO.getAgentAccount());
            updateWrapper.eq(AgentLabelManagePO::getUserAccount, saveResVO.getUserAccount());
            updateWrapper.in(AgentLabelManagePO::getLabel, deletetList);
            agentLabelManageRepository.delete(updateWrapper);
        }
        return ResponseVO.success();
    }


    public List<GetLabelsByAgentAccountVO> getLabelsByAgentAccount(String siteCode, String agentAccount, String userAccount) {
        List<AgentLabelManagePO> list =
                this.lambdaQuery()
                        .eq(AgentLabelManagePO::getSiteCode, siteCode)
                        .eq(AgentLabelManagePO::getAgentAccount, agentAccount)
                        .eq(StrUtil.isNotEmpty(userAccount), AgentLabelManagePO::getUserAccount, userAccount)
                        .list();
        return ConvertUtil.entityListToModelList(list, GetLabelsByAgentAccountVO.class);
    }

    /**
     * 查询该代理下该会员所有的标签记录，记录的标签，会员人数，以及会员账号
     */
    public ResponseVO<Map<String, AgentLabelUserResponseVO>> queryUserLabelRecord(AgentLabelRequestVO requestVO) {
        Map<String, AgentLabelUserResponseVO> responseVOMap = new HashMap<>(16);
        LambdaQueryWrapper<AgentLabelManagePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AgentLabelManagePO::getSiteCode, requestVO.getSiteCode());
        lambdaQueryWrapper.eq(AgentLabelManagePO::getAgentAccount, requestVO.getAgentAccount());
        List<AgentLabelManagePO> agentLabelManagePOS = agentLabelManageRepository.selectList(lambdaQueryWrapper);
        for (int i = 0; i < agentLabelManagePOS.size(); i++) {
            AgentLabelManagePO po = agentLabelManagePOS.get(i);
            String key = po.getLabel();
            if (responseVOMap.containsKey(key)) {
                AgentLabelUserResponseVO responseVO = responseVOMap.get(key);
                if (StringUtils.isNotBlank(po.getAgentAccount())) {
                    responseVO.setCount(responseVO.getCount() + 1);
                    if (StringUtils.isNotBlank(responseVO.getUserAccounts())) {
                        responseVO.setUserAccounts(responseVO.getUserAccounts() + CommonConstant.COMMA + po.getUserAccount());
                        // 是否是该用户的标签，如是则设置1
                        if (StringUtils.equals(po.getUserAccount(), requestVO.getUserAccount())) {
                            responseVO.setIsLabel(CommonConstant.business_one);
                        }
                    } else {
                        // 如果该标签下没有配置会员
                        responseVO.setUserAccounts(po.getUserAccount());
                        if (StringUtils.equals(po.getUserAccount(), requestVO.getUserAccount())) {
                            responseVO.setIsLabel(CommonConstant.business_one);
                        }

                    }
                    responseVOMap.put(key, responseVO);
                }
            } else {
                AgentLabelUserResponseVO responseVO = new AgentLabelUserResponseVO();
                responseVO.setLabel(po.getLabel());
                if (StringUtils.isNotBlank(po.getUserAccount())) {
                    responseVO.setUserAccounts(po.getUserAccount());
                    responseVO.setCount(CommonConstant.business_one);
                    if (StringUtils.equals(po.getUserAccount(), requestVO.getUserAccount())) {
                        responseVO.setIsLabel(CommonConstant.business_one);
                    }
                } else {
                    responseVO.setCount(CommonConstant.business_zero);
                }
                responseVOMap.put(key, responseVO);
            }
        }
        return ResponseVO.success(responseVOMap);

    }


    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> saveUserAssociationLabel(SaveUserAssociationLabelResVO saveResVO) {
        String[] labelsArr = saveResVO.getLabels().split(CommonConstant.COMMA);
        if (labelsArr.length == 0) {
            throw new BaowangDefaultException(ResultCode.RECORD_IS_NOT_EXIST);
        }
        Set<String> labels = new HashSet<>(Arrays.asList(labelsArr));

        String[] userAccounts = saveResVO.getUserAccounts().split(CommonConstant.COMMA);
        if (userAccounts.length == 0) {
            return ResponseVO.success();
        }
        Set<String> userAccountSet = new HashSet<>(Arrays.asList(userAccounts));
        AgentLabelRequestVO agentLabelRequestVO = new AgentLabelRequestVO();
        agentLabelRequestVO.setAgentAccount(saveResVO.getAgentAccount());
        agentLabelRequestVO.setSiteCode(saveResVO.getSiteCode());
        ResponseVO<?> response = listAllLabel(agentLabelRequestVO);
        if (!response.isOk()) {
            throw new BaowangDefaultException(ResultCode.RECORD_IS_NOT_FAIL);
        }
        log.info("AllLabel: " + response.getData().toString());
        // 保存新的
        List<AgentLabelManagePO> insertList = Lists.newArrayList();
        Map<String, AgentLabelManageResponseVO> recordMap = (Map<String, AgentLabelManageResponseVO>) response.getData();

        String siteCode = saveResVO.getSiteCode();
        for (String label : labels) {
            //
            AgentLabelManageResponseVO responseVO = recordMap.get(label);

            if (ObjectUtil.isNotEmpty(responseVO) && StringUtils.isNotBlank(responseVO.getUserAccounts())) {
                String userAccountRecords = responseVO.getUserAccounts();
                Set<String> userAccountRecordSet = new HashSet<>(Arrays.asList(userAccountRecords.split(CommonConstant.COMMA)));
                // 查询哪些需要插入的
                Set<String> differenceSet = new HashSet<>(userAccountSet);
                differenceSet.removeAll(userAccountRecordSet);
                log.info("1. label:{}, db userAccount:{},differenceAccount:{}", label, userAccountRecords, differenceSet);
                buildInsertRecords(siteCode, insertList, differenceSet, saveResVO.getAgentAccount(), saveResVO.getAgentAccountId(), label);
            } else {
                log.info("2. label:{}, userAccount:{}", label, userAccountSet);
                buildInsertRecords(siteCode, insertList, userAccountSet, saveResVO.getAgentAccount(), saveResVO.getAgentAccountId(), label);
            }
        }

        this.saveBatch(insertList);
        return ResponseVO.success();
    }

    private void buildInsertRecords(String siteCode, List<AgentLabelManagePO> insertList, Set<String> differenceSet,
                                    String agentAccount, String agentAccountId, String label) {
        for (String userAccount : differenceSet) {
            AgentLabelManagePO insert = new AgentLabelManagePO();
            insert.setSiteCode(siteCode);
            insert.setUserAccount(userAccount);
            insert.setAgentAccount(agentAccount);
            insert.setLabel(label);
            insert.setCreator(agentAccountId);
            insert.setCreatedTime(System.currentTimeMillis());
            insert.setUpdater(agentAccountId);
            insert.setUpdatedTime(System.currentTimeMillis());
            insertList.add(insert);
        }
    }
}