package com.cloud.baowang.wallet.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.wallet.api.enums.PlatformCoinManualAdjustWayEnum;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.*;
import com.cloud.baowang.wallet.po.UserPlatformCoinManualUpDownRecordPO;
import com.cloud.baowang.wallet.repositories.UserPlatformCoinManualUpDownRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPlatformCoinManualUpDownRecordService extends ServiceImpl<UserPlatformCoinManualUpDownRecordRepository, UserPlatformCoinManualUpDownRecordPO> {

    private final UserPlatformCoinManualUpDownRecordRepository userPlatformCoinManualUpDownRecordRepository;

    private final VipGradeApi vipGradeApi;


    public UserPlatformCoinManualUpRecordResult getUpRecordPage(UserPlatformCoinManualUpRecordPageVO vo) {
        UserPlatformCoinManualUpRecordResult result = new UserPlatformCoinManualUpRecordResult();
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        Page<UserPlatformCoinManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());

        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> query = Wrappers.lambdaQuery();
        query.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, PlatformCoinManualAdjustWayEnum.PLATFORM_COIN_MANUAL_UP.getCode());
        query.orderByDesc(UserPlatformCoinManualUpDownRecordPO::getApplyTime);
        Long applyStartTime = vo.getApplyStartTime();
        Long applyEndTime = vo.getApplyEndTime();
        String orderNo = vo.getOrderNo();
        String userAccount = vo.getUserAccount();
        Integer auditStatus = vo.getAuditStatus();
        Integer adjustType = vo.getAdjustType();
        String adjustAmountMax = vo.getAdjustAmountMax();
        String adjustAmountMin = vo.getAdjustAmountMin();

        query.eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        if (applyStartTime != null) {
            query.ge(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyStartTime);
        }

        if (applyEndTime != null) {
            query.le(UserPlatformCoinManualUpDownRecordPO::getApplyTime, applyEndTime);
        }

        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getOrderNo, orderNo);
        }

        if (StringUtils.isNotBlank(userAccount)) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getUserAccount, userAccount);
        }

        if (auditStatus != null) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, auditStatus);
        }

        if (adjustType != null) {
            query.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustType, adjustType);
        }

        if (StringUtils.isNotBlank(adjustAmountMin)) {
            query.ge(UserPlatformCoinManualUpDownRecordPO::getAdjustAmount, new BigDecimal(adjustAmountMin));
        }

        if (StringUtils.isNotBlank(adjustAmountMax)) {
            query.le(UserPlatformCoinManualUpDownRecordPO::getAdjustAmount, new BigDecimal(adjustAmountMax));
        }
        page = userPlatformCoinManualUpDownRecordRepository.selectPage(page, query);
        List<UserPlatformCoinManualUpDownRecordPO> records = page.getRecords();


        Map<Integer, String> gradeCodeNameMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(records)) {
            List<Integer> gradeCodes = records.stream()
                    .map(UserPlatformCoinManualUpDownRecordPO::getVipGradeCode)
                    .filter(Objects::nonNull)
                    .toList();
            List<SiteVIPGradeVO> gradeVOS = vipGradeApi.getSiteVipGradeListByCodes(vo.getSiteCode(), gradeCodes);
            gradeCodeNameMap = gradeVOS.stream()
                    .collect(Collectors.toMap(
                            SiteVIPGradeVO::getVipGradeCode,
                            SiteVIPGradeVO::getVipGradeName
                    ));

        }
        Map<Integer, String> finalGradeCodeNameMap = gradeCodeNameMap;

        IPage<UserPlatformCoinManualUpRecordResponseVO> convert = page.convert(item -> {
            UserPlatformCoinManualUpRecordResponseVO responseVO = BeanUtil.copyProperties(item, UserPlatformCoinManualUpRecordResponseVO.class);
            Integer vipGradeCode = responseVO.getVipGradeCode();
            if (finalGradeCodeNameMap.containsKey(vipGradeCode)) {
                responseVO.setVipGradeCodeName(finalGradeCodeNameMap.get(vipGradeCode));
            }
            return responseVO;
        });

        BigDecimal adjustAmountAll = BigDecimal.ZERO;
        for (UserPlatformCoinManualUpRecordResponseVO record : convert.getRecords()) {
            // 调整金额相加
            adjustAmountAll = adjustAmountAll.add(record.getAdjustAmount());
        }
        // 小计
        UserPlatformCoinManualUpRecordResponseVO currentPage = new UserPlatformCoinManualUpRecordResponseVO();
        currentPage.setOrderNo("小计");
        currentPage.setAdjustAmount(adjustAmountAll);
        result.setCurrentPage(currentPage);

        UserPlatformCoinManualUpRecordResponseVO total = new UserPlatformCoinManualUpRecordResponseVO();
        total.setOrderNo("总计");
        List<UserPlatformCoinManualUpDownRecordPO> totalList = userPlatformCoinManualUpDownRecordRepository.selectList(query);
        BigDecimal totalAdjustAmount = BigDecimal.ZERO;
        for (UserPlatformCoinManualUpDownRecordPO userManualUpDownRecordPO : totalList) {
            totalAdjustAmount = totalAdjustAmount.add(userManualUpDownRecordPO.getAdjustAmount());
        }
        total.setAdjustAmount(totalAdjustAmount);
        // 总计
        result.setTotalPage(total);
        result.setPageList(ConvertUtil.toConverPage(convert));
        return result;
    }

    public ResponseVO<Long> getUpRecordPageCount(UserPlatformCoinManualUpRecordPageVO vo) {
        try {
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMin())) {
                Double.parseDouble(vo.getAdjustAmountMin());
            }
            if (StrUtil.isNotEmpty(vo.getAdjustAmountMax())) {
                Double.parseDouble(vo.getAdjustAmountMax());
            }
        } catch (Exception e) {
            throw new BaowangDefaultException(ResultCode.ADJUST_AMOUNT_INCORRECT);
        }
        Long pageCount = userPlatformCoinManualUpDownRecordRepository.getPageCount(vo);
        return ResponseVO.success(pageCount);
    }

    public UserPlatformCoinManualDownRecordResponseVO listUserManualDownRecordPage(UserPlatformCoinManualDownRecordRequestVO vo) {

        Page<UserPlatformCoinManualUpDownRecordPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        //绑定条件
        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> lqw = buildLqw(vo);

        Page<UserPlatformCoinManualUpDownRecordPO> userManualUpDownRecordPOPage = userPlatformCoinManualUpDownRecordRepository.selectPage(page, lqw);

        Page<UserPlatformCoinManualDownRecordVO> userManualDownRecordVOPage = new Page<>();
        BeanUtils.copyProperties(userManualUpDownRecordPOPage, userManualDownRecordVOPage);
        List<UserPlatformCoinManualDownRecordVO> userManualDownRecordVOList =
                ConvertUtil.entityListToModelList(userManualDownRecordVOPage.getRecords(), UserPlatformCoinManualDownRecordVO.class);
        //转换数据
        convertProperty(vo.getSiteCode(), userManualDownRecordVOList);
        userManualDownRecordVOPage.setRecords(userManualDownRecordVOList);

        UserPlatformCoinManualDownRecordResponseVO userManualDownRecordResponseVO = new UserPlatformCoinManualDownRecordResponseVO();
        BeanUtils.copyProperties(userManualDownRecordVOPage, userManualDownRecordResponseVO);

        //汇总小计
        userManualDownRecordResponseVO.setCurrentPage(getSubtotal(userManualDownRecordVOList));
        UserPlatformCoinManualDownRecordVO total = new UserPlatformCoinManualDownRecordVO();
        total.setOrderNo("总计");
        List<UserPlatformCoinManualUpDownRecordPO> recordPOS = userPlatformCoinManualUpDownRecordRepository.selectList(lqw);
        BigDecimal totalAdjustAmount = recordPOS.stream()
                .map(UserPlatformCoinManualUpDownRecordPO::getAdjustAmount) // 提取 adjust_amount
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 求和
        total.setAdjustAmount(totalAdjustAmount);
        //汇总总计
        userManualDownRecordResponseVO.setTotalPage(total);
        return userManualDownRecordResponseVO;
    }

    private void convertProperty(String siteCode, List<UserPlatformCoinManualDownRecordVO> userManualDownRecordVOList) {
        List<Integer> vipGradeCodeList = userManualDownRecordVOList.stream()
                .map(UserPlatformCoinManualDownRecordVO::getVipGradeCode)
                .filter(Objects::nonNull)
                .toList();
        Map<Integer, String> gradeNameMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(vipGradeCodeList)) {
            List<SiteVIPGradeVO> siteVipGradeListByCodes = vipGradeApi.getSiteVipGradeListByCodes(siteCode, vipGradeCodeList);
            if (CollectionUtil.isNotEmpty(siteVipGradeListByCodes)) {
                gradeNameMap = siteVipGradeListByCodes.stream()
                        .collect(Collectors.toMap(
                                SiteVIPGradeVO::getVipGradeCode,
                                SiteVIPGradeVO::getVipGradeName
                        ));

            }
        }

        Map<Integer, String> finalGradeNameMap = gradeNameMap;
        List<UserPlatformCoinManualDownRecordVO> list = userManualDownRecordVOList.stream().peek(record -> {
            try {
                ExecutorService adminReportExecutorService = Executors.newFixedThreadPool(2);
                Integer vipGradeCode = record.getVipGradeCode();
                if (vipGradeCode != null && finalGradeNameMap.containsKey(vipGradeCode)) {
                    record.setVipGradeCodeName(finalGradeNameMap.get(vipGradeCode));
                }
                adminReportExecutorService.shutdown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    public LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> buildLqw(UserPlatformCoinManualDownRecordRequestVO vo) {
        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        lqw.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, PlatformCoinManualAdjustWayEnum.PLATFORM_COIN_MANUAL_DOWN.getCode());
        lqw.ge(null != vo.getApplyStartTime(), UserPlatformCoinManualUpDownRecordPO::getCreatedTime, vo.getApplyStartTime());
        lqw.lt(null != vo.getApplyEndTime(), UserPlatformCoinManualUpDownRecordPO::getCreatedTime, vo.getApplyEndTime());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), UserPlatformCoinManualUpDownRecordPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), UserPlatformCoinManualUpDownRecordPO::getUserAccount, vo.getUserAccount());
        lqw.eq(StringUtils.isNotBlank(vo.getUserName()), UserPlatformCoinManualUpDownRecordPO::getUserName, vo.getUserName());
        if (vo.getAuditStatus() != null) {
            lqw.eq(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, vo.getAuditStatus());
        }
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(UserPlatformCoinManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }

        lqw.eq(null != vo.getAdjustType(), UserPlatformCoinManualUpDownRecordPO::getAdjustType, vo.getAdjustType());
        lqw.ge(null != vo.getAdjustAmountMin(), UserPlatformCoinManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMin());
        lqw.le(null != vo.getAdjustAmountMax(), UserPlatformCoinManualUpDownRecordPO::getAdjustAmount, vo.getAdjustAmountMax());
        if (vo.getBalanceChangeStatus() != null) {
            lqw.eq(UserPlatformCoinManualUpDownRecordPO::getBalanceChangeStatus, vo.getBalanceChangeStatus());
        }
        lqw.orderByDesc(UserPlatformCoinManualUpDownRecordPO::getCreatedTime);
        return lqw;
    }

    public UserPlatformCoinManualDownRecordVO getSubtotal(List<UserPlatformCoinManualDownRecordVO> userCoinRecordVOList) {
        //汇总小计
        BigDecimal sumAdjustAmount = userCoinRecordVOList.stream()
                .map(UserPlatformCoinManualDownRecordVO::getAdjustAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        UserPlatformCoinManualDownRecordVO userManualDownRecordVO = new UserPlatformCoinManualDownRecordVO();
        userManualDownRecordVO.setOrderNo("小计");
        userManualDownRecordVO.setAdjustAmount(sumAdjustAmount);

        return userManualDownRecordVO;
    }


    public Long listUserManualDownRecordPageExportCount(UserPlatformCoinManualDownRecordRequestVO userCoinRecordRequestVO) {

        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> lqw = buildLqw(userCoinRecordRequestVO);
        return userPlatformCoinManualUpDownRecordRepository.selectCount(lqw);

    }

    public Long getUpRecordTodoCount(UserPlatformCoinManualUpRecordVO vo) {

        LambdaQueryWrapper<UserPlatformCoinManualUpDownRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPlatformCoinManualUpDownRecordPO::getSiteCode, vo.getSiteCode());
        wrapper.eq(UserPlatformCoinManualUpDownRecordPO::getAdjustWay, CommonConstant.business_one);
        //状态 待处理和处理中
        wrapper.in(UserPlatformCoinManualUpDownRecordPO::getAuditStatus, Arrays.asList(1, 2));
        return userPlatformCoinManualUpDownRecordRepository.selectCount(wrapper);
    }
}
