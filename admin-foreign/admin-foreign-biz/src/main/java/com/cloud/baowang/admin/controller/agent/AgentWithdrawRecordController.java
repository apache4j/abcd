/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentWithdrawRecordApi;
import com.cloud.baowang.agent.api.enums.depositWithdrawal.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentWithdrawalRecordResVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

*/
/**
 * @author: kimi
 *//*

@Tag(name = "资金-代理资金记录-代理提款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-withdrawal-record/api")
public class AgentWithdrawRecordController {

    

    private AgentWithdrawRecordApi agentWithdrawRecordApi;

    private SystemParamApi systemParamApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @GetMapping("getWithdrawalRecordSelect")
    public ResponseVO<Map<String, Object>> getWithdrawalRecordSelect() {
        Map<String, Object> resultMap = new HashMap<>();
        // 订单来源-下拉框
        ResponseVO<List<CodeValueVO>> stateResVO = systemParamApi.getSystemParamByType(CommonConstant.GAME_SUPPORT_DEVICE);

        List<CodeValueVO> orderSource = Lists.newArrayList();
        if (stateResVO.getCode() == ResultCode.SUCCESS.getCode()) {
            orderSource = stateResVO.getData();
        }
        List<DepositWithdrawalOrderStatusEnum> agentDepositWithdrawalOrderStatusEnums = new ArrayList<>(DepositWithdrawalOrderStatusEnum.getList());
        agentDepositWithdrawalOrderStatusEnums.removeIf(agentDepositWithdrawalOrderStatusEnum -> DepositWithdrawalOrderStatusEnum.FAIL.getCode().equals(agentDepositWithdrawalOrderStatusEnum.getCode()));
        // 订单状态
        List<Map<String, Object>> orderStatus = agentDepositWithdrawalOrderStatusEnums.stream().map(one -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", one.getCode());
            map.put("value", one.getName());
            return map;
        }).collect(Collectors.toList());
        // 提款类型

        // 提款方式

        // 是否为大额提款
        List<Map<String, Object>> isLargeWithdraw = YesOrNoEnum.getList().stream().map(one -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", one.getCode());
            map.put("value", one.getName());
            return map;
        }).collect(Collectors.toList());
        // 是否为首提
        List<Map<String, Object>> isFirstWithdraw = YesOrNoEnum.getList().stream().map(one -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", one.getCode());
            map.put("value", one.getName());
            return map;
        }).collect(Collectors.toList());
        resultMap.put("orderSource", orderSource);
        resultMap.put("orderStatus", orderStatus);
        resultMap.put("isLargeWithdraw", isLargeWithdraw);
        resultMap.put("isFirstWithdraw", isFirstWithdraw);

        return ResponseVO.success(resultMap);

    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getAgentWithdrawalRecordPageList")
    public ResponseVO<Page<AgentWithdrawalRecordResVO>> getAgentWithdrawalRecordPageList(@RequestBody AgentWithdrawalRecordReqVO requestVO) {
        return agentWithdrawRecordApi.getAgentWithdrawalRecordPageList(requestVO);
    }


    @PostMapping("export")
    @Operation(summary = "导出")
    public ResponseVO<?> export(@RequestBody AgentWithdrawalRecordReqVO vo) {
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::agentWithdrawRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentWithdrawRecordApi.agentWithdrawRecordRecordPageCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentWithdrawalRecordResVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> agentWithdrawRecordApi.getAgentWithdrawalRecordPageList(param).getData().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_WITHDRAWAL_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
*/
