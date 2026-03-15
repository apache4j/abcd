package com.cloud.baowang.site.controller.agentManualUpDown;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentManualUpApi;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordPageVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentGetRecordResponseResultVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentUpReviewDetailsVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewOperationEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.agentManual.AgentGetRecordResponseExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: kimi
 */
@Tag(name = "代理人工加额审核记录")
@RestController
@RequestMapping("/agent-up-review-record/api")
@RequiredArgsConstructor
public class AgentManualUpReviewRecordController {

    private final SystemParamApi systemParamApi;
    private final AgentManualUpApi agentManualUpApi;
    private final MinioUploadApi minioUploadApi;


    @Operation(summary = "下拉框")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {

        List<String> param = new ArrayList<>();
        param.add(CommonConstant.AUDIT_TIME_TYPE);
        param.add(CommonConstant.USER_REVIEW_REVIEW_STATUS);
        //钱包类型
        param.add(CommonConstant.AGENT_WALLET_TYPE);
        param.add(CommonConstant.AGENT_MANUAL_ADJUST_TYPE);
        return systemParamApi.getSystemParamsByList(param);
    }

    @Operation(summary = "分页列表")
    @PostMapping(value = "/getRecordPage")
    public ResponseVO<Page<AgentGetRecordResponseResultVO>> getRecordPage(@RequestBody AgentGetRecordPageVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
        return ResponseVO.success(agentManualUpApi.getRecordPage(vo));
    }
    @Operation(summary = "审核详情")
    @PostMapping(value = "/getUpReviewDetailsManualUp")
    public ResponseVO<AgentUpReviewDetailsVO> getUpReviewDetailsManualUp(@Valid @RequestBody IdVO vo) {
        return agentManualUpApi.getUpReviewDetailsManualUp(vo);
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO export(@RequestBody AgentGetRecordPageVO vo) {
        String adminId = CurrReqUtils.getOneId();
        vo.setReviewOperation(ReviewOperationEnum.CHECK.getCode());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::agentManualUpReviewRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);

            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = agentManualUpApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentGetRecordResponseExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(getRecordPage(param).getData().getRecords(), AgentGetRecordResponseExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_MANUAL_UP_REVIEW_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
