package com.cloud.baowang.site.controller.agent;

import com.cloud.baowang.agent.api.api.AgentLoginRecordApi;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordExportVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordParam;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import com.cloud.baowang.user.api.vo.user.excel.UserLoginInfoExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


/**
 * @Author : kimi
 * @Date : 11/10/23 5:21 PM
 * @Version : 1.0
 */
@Tag(name = "代理-代理登录信息")
@AllArgsConstructor
@RestController
@RequestMapping("/agentLoginRecord")
public class AgentLoginRecordController {

    private final AgentLoginRecordApi agentLoginRecordApi;
    private final MinioUploadApi minioUploadApi;
    private final SiteApi siteApi;

    @Operation(summary = "代理登录日志")
    @PostMapping(value = "/queryAgentLoginRecord")
    public ResponseVO<AgentLoginRecordVO> queryAgentLoginRecord(@RequestBody AgentLoginRecordParam param) {
        param.setSiteCode(CurrReqUtils.getSiteCode());
        SiteVO siteDetail = siteApi.getSiteDetail(CurrReqUtils.getSiteCode());
        param.setTimezone(siteDetail.getTimezone());
        return agentLoginRecordApi.queryAgentLoginRecord(param);
    }

    @Operation(summary = "代理登录日志导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody AgentLoginRecordParam vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::queryAgentLogin::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = agentLoginRecordApi.getTotalCount(vo);

        long totalCount  = responseVO.isOk()?responseVO.getData(): 0L;

        vo.setPageSize((int) totalCount);
        if (totalCount <= 0L) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentLoginRecordExportVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), totalCount),
                param -> ConvertUtil.entityListToModelList(queryAgentLoginRecord(param).getData().getAgentLoginRecordPageVO().getRecords(), AgentLoginRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_LOGIN_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }
}
