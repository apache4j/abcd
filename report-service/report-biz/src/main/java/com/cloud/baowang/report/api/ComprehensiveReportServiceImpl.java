package com.cloud.baowang.report.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportComprehensiveReportVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.report.api.api.ComprehensiveReportServiceApi;
import com.cloud.baowang.report.po.ReportMembershipStatsPO;
import com.cloud.baowang.report.repositories.MembershipStatsRepository;
import com.cloud.baowang.report.service.MembershipStatsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @Description 综合报表
 * @auther amos
 * @create 2024-11-04
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class ComprehensiveReportServiceImpl implements ComprehensiveReportServiceApi {
    
    private MembershipStatsRepository membershipStatsRepository;

    private MembershipStatsService service;

    @Async
    public ResponseVO execute(ReportComprehensiveReportVO vo) {
        Long startTime = vo.getStartTime();
        Long endTime = vo.getEndTime();
        String siteCode = vo.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            LambdaQueryWrapper<ReportMembershipStatsPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(ReportMembershipStatsPO::getDate, startTime);
            queryWrapper.le(ReportMembershipStatsPO::getDate, endTime);
            queryWrapper.eq(StringUtils.isNotBlank(vo.getSiteCode()), ReportMembershipStatsPO::getSiteCode, vo.getSiteCode());
            membershipStatsRepository.delete(queryWrapper);
        }
        List<CompletableFuture<Map>> futureList = new ArrayList<>();
        Executor executor = ThreadUtil.newExecutor();

        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getRegSumInfo(vo); //ok
        }, executor));
        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getAgentRegSumInfo(vo);//ok
        }, executor));
        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getUserLoginSumInfo(vo);//ok
        }, executor));
        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getUserDepositWithdrawInfo(vo);//report_user_deposit_withdraw -》 report_user_recharge_withdraw
        }, executor));

//        futureList.add(CompletableFuture.supplyAsync(() -> {
//            return userReportApi.getMemberWithdrawalReport(vo);
//        }, executor));

        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getUserFirstDepositSumInfo(vo);//user_info  ok
        }, executor));

        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getUserBetting(vo);//report_user_win_lose  todo
        }, executor));
        //代存会员
        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getAgentDepositSum(vo);//agent_deposit_subordinates ok
        }, executor));

        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getUserAdjustments(vo);//user_coin_record  ok
        }, executor));


        futureList.add(CompletableFuture.supplyAsync(() -> {
            return service.getPlatformAdjustInfo(vo);//user_coin_record  ok
        }, executor));

/*
        futureList.add(CompletableFuture.supplyAsync(() -> {
            return userReportApi.getAgentDepositReport(vo);
        }, executor));

        futureList.add(CompletableFuture.supplyAsync(() -> {
            return userReportApi.getAgentWithdrawalReport(vo);
        }, executor));

       */

        String timezone = "UTC-5";
        String dateRangeStr = TimeZoneUtils.formatTimestampToTimeZone(startTime, timezone) + "$" + timezone;
        Map<String, ReportMembershipStatsPO> map = new HashedMap();
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenAccept(v -> {
                    for (CompletableFuture<Map> future : futureList) {
                        try {
                            buildMap(future.get(), map, startTime,dateRangeStr);
                        } catch (Exception e) {
                           log.error("comprehensive report first build failed : " + e.getMessage());
                        }

                    }
                }).join();



        if (CollectionUtil.isEmpty(map)) {
            return ResponseVO.success();
        }
        List<ReportMembershipStatsPO> addList = new ArrayList<>(map.values());
        service.saveBatch(addList);
        return ResponseVO.success();

    }

    private void buildMap(Map vo, Map<String, ReportMembershipStatsPO> targetMap, Long date,String dateRangeStr) {
        if (CollectionUtil.isNotEmpty(vo)) {
            vo.keySet().stream().forEach(v -> {
                Object source = vo.get(v);
                ReportMembershipStatsPO target = targetMap.get(v);
                if (target == null) {
                    target = new ReportMembershipStatsPO();
                    String key = (String) v;
                    targetMap.put(key, target);
                }
                BeanUtil.copyProperties(source, target, CopyOptions.create().setIgnoreNullValue(true));
                target.setDate(date);
                target.setDateRangeStr(dateRangeStr);
            });
        }
    }



        public static Map<String, ReportMembershipStatsPO> mergeStatsByCurrency(List<ReportMembershipStatsPO> statsList) {

            Map<String, ReportMembershipStatsPO> resultMap = new HashMap<>();
            Field[] fields = ReportMembershipStatsPO.class.getDeclaredFields();

            // 遍历 statsList
            try {
                for (ReportMembershipStatsPO stats : statsList) {
                    String currency = stats.getCurrency();
                    // 获取当前 currency 对应的合并结果，-> 一个币种一条数据
                    ReportMembershipStatsPO mergedStats = resultMap.computeIfAbsent(currency, k -> new ReportMembershipStatsPO());
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object mergedValue = field.get(mergedStats);
                        Object statsValue = field.get(stats);

                        if (statsValue != null) {
                            mergedValue = statsValue;
                        }

                        // 合并后返回 mergedStats 对象
                        field.set(mergedStats, mergedValue);
                    }
                }
            } catch (Exception e) {
                log.error("10001-综合报表-定时任务-ComprehensiveReportServiceImpl.mergeStatsByCurrency=========》{}", e.getMessage());
            }

            return resultMap;
    }
}
