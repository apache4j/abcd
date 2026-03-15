/*
package com.cloud.baowang.admin.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentDepositRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordReq;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositRecordRes;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

*/
/**
 * @author: kimi
 *//*

@Tag(name = "资金-代理资金记录-代理存款记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agentDeposit")
public class AgentDepositRecordController {

    private AgentDepositRecordApi agentDepositRecordApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping("/querySelect")
    public ResponseVO<Map<String, Object>> getDepositRecordBox() {
        return agentDepositRecordApi.getDepositRecordBox();
    }

    @Operation(summary = "分页记录")
    @PostMapping(value = "/listPage")
    public ResponseVO<Page<AgentDepositRecordRes>> listPage(@Valid @RequestBody AgentDepositRecordReq requestVO) {
        return agentDepositRecordApi.depositListPage(requestVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentDepositRecordReq vo) {
        String uniqueKey = "tableExport::agent::deposit::" + CommonConstant.ADMIN_CENTER_SITE_CODE+ CurrReqUtils.getAccount();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentDepositRecordApi.depositExportCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentDepositRecordRes.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> agentDepositRecordApi.depositListPage(param).getData().getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_DEPOSIT_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
*/
