package com.cloud.baowang.site.controller.deposit;

import com.cloud.baowang.agent.api.api.AgentDepositSiteRecordApi;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSiteRecordPageVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesListPageResVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositSubordinatesPageResVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: feng
 */
@Tag(name = "资金-代理资金记录-代理代存记录")
@AllArgsConstructor
@RestController
@RequestMapping("/agentDepositSiteRecord")
public class AgentDepositSubordinatesController {

    private AgentDepositSiteRecordApi agentDepositSiteRecordApi;

    private MinioUploadApi minioUploadApi;

    @Operation(summary = "下拉框")
    @PostMapping("getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox() {
        List<CodeValueVO> result = DepositWithdrawalOrderCustomerStatusEnum.getList()
                .stream()
                .map(item ->
                        CodeValueVO.builder().code(String.valueOf(item.getCode())).value(item.getName()).build())
                .toList();
        return ResponseVO.success(result);
    }

    @Operation(summary = "分页记录")
    @PostMapping(value = "/listPage")
    public ResponseVO<AgentDepositSubordinatesPageResVO> listPage(@Valid @RequestBody AgentDepositSiteRecordPageVO requestVO) {
        requestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentDepositSiteRecordApi.listPage(requestVO);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentDepositSiteRecordPageVO vo) {
        String uniqueKey = "tableExport::agent::deposit::" + CommonConstant.ADMIN_CENTER_SITE_CODE+ CurrReqUtils.getAccount();
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentDepositSiteRecordApi.depositExportCount(vo);
        if (!responseVO.isOk() || responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentDepositSubordinatesListPageResVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> agentDepositSiteRecordApi.doExport(param).getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AEGENT_DEPOSIT_SUBORDINATES_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
