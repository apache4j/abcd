package com.cloud.baowang.site.controller.agent;

import com.cloud.baowang.agent.api.api.AgentTransferApi;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinRecordVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordTotalVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.AgentTransferRecordExportVO;
import com.cloud.baowang.system.api.enums.TransferStatusEnum;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@AllArgsConstructor
@RequestMapping("agent-finance-record/api")
@Tag(name = "资金-代理资金记录")
public class AgentFinanceRecordController {

    private final AgentTransferApi agentTransferApi;
    private final MinioUploadApi minioUploadApi;


    @GetMapping("getDownBox")
    @Operation(summary = "获取下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        List<AgentCoinRecordTypeEnum.AgentWalletTypeEnum> list = AgentCoinRecordTypeEnum.AgentWalletTypeEnum.getList();
        List<CodeValueVO> wallet = list.stream().map(e -> {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(e.getCode());
            codeValueVO.setValue(e.getName());
            return codeValueVO;
        }).toList();
        List<CodeValueVO> status = Arrays.stream(TransferStatusEnum.values()).map(e -> {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setCode(e.getCode().toString());
            codeValueVO.setValue(e.getName());
            return codeValueVO;
        }).toList();
        Map<String, List<CodeValueVO>> map = Maps.newHashMap();
        map.put("wallet",wallet);
        map.put("status",status);
        return ResponseVO.success(map);
    }
    @PostMapping("agentTransferRecord")
    @Operation(summary = "代理转账记录")
    public ResponseVO<AgentTransferRecordTotalVO> agentTransferRecord(@Valid @RequestBody AgentTransferRecordPageParam agentFinanceRequestVO){
        agentFinanceRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentTransferApi.siteQueryAgentTransferRecord(agentFinanceRequestVO);
    }

    @Operation(summary = "代理转账记录-导出")
    @PostMapping(value = "/agentTransferRecordExport")
    public ResponseVO<?> export(@RequestBody AgentTransferRecordPageParam vo) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::agentTransferRecord::" + CommonConstant.ADMIN_CENTER_SITE_CODE + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        Long responseVO = agentTransferApi.siteQueryAgentTransferRecordCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentTransferRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(agentTransferRecord(param).getData().getPageList().getRecords(), AgentTransferRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_TRANSFER_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

    }

}
