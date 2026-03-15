package com.cloud.baowang.user.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.RiskTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.RiskApi;
import com.cloud.baowang.system.api.vo.risk.RiskAccountVO;
import com.cloud.baowang.system.api.vo.risk.RiskListAccountQueryVO;
import com.cloud.baowang.user.api.vo.GetUserLabelByIdsResponseVO;
import com.cloud.baowang.user.api.vo.UserGuideVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoCountVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginLogVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import com.cloud.baowang.user.po.SiteNewUserGuideStepRecordPO;
import com.cloud.baowang.user.po.SiteUserLabelConfigPO;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.po.UserLoginInfoPO;
import com.cloud.baowang.user.repositories.SiteNewUserGuideStepRecordRepository;
import com.cloud.baowang.user.repositories.SiteUserLabelConfigRepository;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.UserLoginInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserLoginLogService extends ServiceImpl<UserLoginInfoRepository, UserLoginInfoPO> {


    private final UserLoginInfoRepository userLoginInfoRepository;
    private final SiteUserLabelConfigRepository labelConfigRepository;
    private final UserInfoRepository userInfoRepository;

    private final SiteNewUserGuideStepRecordRepository newUserGuideStepRecordRepository;


    private final RiskApi riskApi;

    public Long getTotalCount(UserLoginRequestVO requestVO) {
        return userLoginInfoRepository.getTotalCount(requestVO);
    }

    public ResponseVO<UserLoginLogVO> queryUserLogin(UserLoginRequestVO requestVO) {
        try {
            UserLoginLogVO userLoginLogVO = new UserLoginLogVO();
            Page<UserLoginInfoPO> page = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            LambdaQueryWrapper<UserLoginInfoPO> queryWrapper = assembleWrapper(requestVO);
            Page<UserLoginInfoVO> resultPage = userLoginInfoRepository.selectUserLoginPage(page, requestVO);
            List<UserLoginInfoVO> list = resultPage.getRecords();
            // 获取IP风控层级list ip
            RiskListAccountQueryVO vo = new RiskListAccountQueryVO();
            vo.setRiskControlTypeCode(RiskTypeEnum.RISK_IP.getCode());
            vo.setRiskControlAccounts(list.stream().map(UserLoginInfoVO::getIp).toList());
            vo.setSiteCode(requestVO.getSiteCode());
            List<RiskAccountVO> ipRisk = riskApi.getRiskListAccount(vo);
            // 获取设备风控层级list device
            vo = new RiskListAccountQueryVO();
            vo.setRiskControlTypeCode(RiskTypeEnum.RISK_DEVICE.getCode());
            vo.setRiskControlAccounts(list.stream().map(UserLoginInfoVO::getDeviceNo).toList());
            vo.setSiteCode(requestVO.getSiteCode());
            List<RiskAccountVO> deviceRisk = riskApi.getRiskListAccount(vo);
            List<UserLoginInfoVO> records = resultPage.getRecords();
            //会员id和标签id映射的map
            Map<String, String> userLabelIdsMap = new HashMap<>();
            //标签id和标签信息的映射map
            Map<String, SiteUserLabelConfigPO> labelIdPOMap = new HashMap<>();

            if (CollectionUtil.isNotEmpty(records)) {
                List<String> userIdList = records.stream().map(UserLoginInfoVO::getUserId).toList();
                LambdaQueryWrapper<UserInfoPO> userQuery = Wrappers.lambdaQuery();
                userQuery.in(UserInfoPO::getUserId, userIdList);
                List<UserInfoPO> userInfoPOS = userInfoRepository.selectList(userQuery);

                //取出当前会员所有不为空的标签id,逗号拼接的数据,全部取出来
                if (CollectionUtil.isNotEmpty(userInfoPOS)) {
                    //添加映射
                    userLabelIdsMap = userInfoPOS.stream()
                            .filter(obj -> StringUtils.isNotBlank(obj.getUserLabelId()))
                            .collect(Collectors.toMap(UserInfoPO::getUserId, UserInfoPO::getUserLabelId));


                    List<String> userLabelIds = userInfoPOS.stream()
                            .filter(obj -> StringUtils.isNotBlank(obj.getUserLabelId()))
                            .flatMap(obj -> Arrays.stream(obj.getUserLabelId().split(CommonConstant.COMMA))).toList();
                    if (CollectionUtil.isNotEmpty(userLabelIds)) {
                        LambdaQueryWrapper<SiteUserLabelConfigPO> labelQuery = Wrappers.lambdaQuery();
                        labelQuery.in(SiteUserLabelConfigPO::getId, userLabelIds);
                        List<SiteUserLabelConfigPO> userLabelList = labelConfigRepository.selectList(labelQuery);
                        labelIdPOMap = userLabelList.stream()
                                .collect(Collectors.toMap(SiteUserLabelConfigPO::getId, label -> label));
                    }
                }
            }
            Map<String, String> finalUserLabelIdsMap = userLabelIdsMap;
            Map<String, SiteUserLabelConfigPO> finalLabelIdPOMap = labelIdPOMap;
            resultPage.getRecords().forEach(obj -> {
                // IP地址风控层级
                if (null != obj.getIp() && !CollectionUtils.isEmpty(ipRisk)) {
                    Optional<RiskAccountVO> ipOptional = ipRisk.stream().filter(item -> item.getRiskControlAccount()
                            .equals(obj.getIp())).findFirst();
                    ipOptional.ifPresent(riskAccountVO -> obj.setIpControl(riskAccountVO.getRiskControlLevel()));
                }
                // 设备号风控层级
                if (null != obj.getDeviceNo() && !CollectionUtils.isEmpty(deviceRisk)) {
                    Optional<RiskAccountVO> deviceOptional = deviceRisk.stream().filter(item -> item.getRiskControlAccount()
                            .equals(obj.getDeviceNo())).findFirst();
                    deviceOptional.ifPresent(riskAccountVO -> obj.setDeviceControl(riskAccountVO.getRiskControlLevel()));
                }
                if (finalUserLabelIdsMap.containsKey(obj.getUserId())) {
                    String labelIds = finalUserLabelIdsMap.get(obj.getUserId());
                    List<GetUserLabelByIdsResponseVO> labelResp = new ArrayList<>();
                    for (String labelId : labelIds.split(CommonConstant.COMMA)) {
                        if (finalLabelIdPOMap.containsKey(labelId)) {
                            SiteUserLabelConfigPO labelPO = finalLabelIdPOMap.get(labelId);
                            GetUserLabelByIdsResponseVO resp = new GetUserLabelByIdsResponseVO();
                            resp.setColor(labelPO.getColor());
                            resp.setLabelName(labelPO.getLabelName());
                            labelResp.add(resp);
                        }
                    }
                    obj.setLabelList(labelResp);
                }

                obj.setRemark(obj.getLoginType() == 0 ? "-" : obj.getRemark());
                // 脱敏

            });
            userLoginLogVO.setUserLoginPage(resultPage);
            // 查询全部满足条件的登录信息  queryWrapper
            UserLoginInfoCountVO result = userLoginInfoRepository.selectUserLoginAll(requestVO);
            userLoginLogVO.setTotalLoginNum(resultPage.getTotal());
            userLoginLogVO.setLoginSuccess(resultPage.getTotal() - (null == result.getLoginNum()?0L:result.getLoginNum()));
            userLoginLogVO.setLoginError(result.getLoginNum());
            return ResponseVO.success(userLoginLogVO);
        } catch (Exception e) {
            log.error("会员登录日志查询异常", e);
            return ResponseVO.fail(ResultCode.USER_LOGIN_LOG_QUERY_ERROR);
        }
    }

    private LambdaQueryWrapper<UserLoginInfoPO> assembleWrapper(final UserLoginRequestVO requestVO) {
        LambdaQueryWrapper<UserLoginInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(requestVO.getUserAccount())) {
            queryWrapper.eq(UserLoginInfoPO::getUserAccount, requestVO.getUserAccount());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getAccountType())) {
            queryWrapper.in(UserLoginInfoPO::getAccountType, requestVO.getAccountType());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getLoginType())) {
            queryWrapper.eq(UserLoginInfoPO::getLoginType, requestVO.getLoginType());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getIp())) {
            queryWrapper.eq(UserLoginInfoPO::getIp, requestVO.getIp());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getIpAddress())) {
            queryWrapper.eq(UserLoginInfoPO::getIpAddress, requestVO.getIpAddress());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getLoginTerminal())) {
            queryWrapper.in(UserLoginInfoPO::getLoginTerminal, requestVO.getLoginTerminal());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getDeviceNo())) {
            queryWrapper.eq(UserLoginInfoPO::getDeviceNo, requestVO.getDeviceNo());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getLoginStartTime())) {
            queryWrapper.ge(UserLoginInfoPO::getLoginTime, requestVO.getLoginStartTime());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getLoginEndTime())) {
            queryWrapper.le(UserLoginInfoPO::getLoginTime, requestVO.getLoginEndTime());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getSiteCode())) {
            queryWrapper.eq(UserLoginInfoPO::getSiteCode, requestVO.getSiteCode());
        }
        if (ObjectUtil.isNotEmpty(requestVO.getSuperAgentAccount())) {
            queryWrapper.like(UserLoginInfoPO::getSuperAgentAccount, requestVO.getSuperAgentAccount());
        }
        return queryWrapper;
    }

    @Async
    public void insertUserLogin(final UserLoginInfoVO userLoginInfoVO) {
        UserLoginInfoPO po = new UserLoginInfoPO();
        BeanUtils.copyProperties(userLoginInfoVO, po);
        po.setAccountType(Integer.valueOf(userLoginInfoVO.getAccountType()));
        po.setLoginTerminal(String.valueOf(userLoginInfoVO.getLoginTerminal()));
        userLoginInfoRepository.insert(po);
    }

    /**
     * 判断用户是否有成功登录记录
     *
     * @param userId userId
     * @return 返回true表示有登录记录
     */
    public boolean isLogin(String userId) {
        LambdaQueryWrapper<UserLoginInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserLoginInfoPO::getUserId, userId);
        // 0 是登录成，1 是登录失败
        queryWrapper.eq(UserLoginInfoPO::getLoginType, CommonConstant.business_zero);
        queryWrapper.last(" limit 1 ");
        return userLoginInfoRepository.exists(queryWrapper);
    }


    public ResponseVO<?> setNewUserGuide(UserGuideVO vo) {
        LambdaQueryWrapper<SiteNewUserGuideStepRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteNewUserGuideStepRecordPO::getUserId, vo.getUserId());
        queryWrapper.last(" limit 1 ");
        boolean flag = newUserGuideStepRecordRepository.exists(queryWrapper);
        if (!flag) {
            SiteNewUserGuideStepRecordPO po = new SiteNewUserGuideStepRecordPO();
            BeanUtils.copyProperties(vo, po);
            po.setCreatedTime(System.currentTimeMillis());
            po.setUpdatedTime(System.currentTimeMillis());
            try {
                newUserGuideStepRecordRepository.insert(po);
            } catch (DuplicateKeyException e) {
                // 解决前端同时调两次接口导致异常
                LambdaUpdateWrapper<SiteNewUserGuideStepRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SiteNewUserGuideStepRecordPO::getUserId, vo.getUserId());
                updateWrapper.set(SiteNewUserGuideStepRecordPO::getStep, vo.getStep());
                updateWrapper.set(SiteNewUserGuideStepRecordPO::getUpdatedTime, System.currentTimeMillis());
                newUserGuideStepRecordRepository.update(null, updateWrapper);
                return ResponseVO.success(true);
            }
        } else {
            LambdaUpdateWrapper<SiteNewUserGuideStepRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SiteNewUserGuideStepRecordPO::getUserId, vo.getUserId());
            updateWrapper.set(SiteNewUserGuideStepRecordPO::getStep, vo.getStep());
            updateWrapper.set(SiteNewUserGuideStepRecordPO::getUpdatedTime, System.currentTimeMillis());
            newUserGuideStepRecordRepository.update(null, updateWrapper);
        }

        return ResponseVO.success(true);
    }

    public Integer getNewUserGuide(String userId) {
        LambdaQueryWrapper<SiteNewUserGuideStepRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteNewUserGuideStepRecordPO::getUserId, userId);
        queryWrapper.last(" limit 1 ");
        SiteNewUserGuideStepRecordPO siteNewUserGuideStepRecordPO = newUserGuideStepRecordRepository.selectOne(queryWrapper);
        if (siteNewUserGuideStepRecordPO == null) {
            return 0;
        } else {
            return siteNewUserGuideStepRecordPO.getStep();
        }

    }
}
