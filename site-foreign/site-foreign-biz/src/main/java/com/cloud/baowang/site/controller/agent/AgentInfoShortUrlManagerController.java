package com.cloud.baowang.site.controller.agent;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentInfoShortUrlManagerApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentTransferRecordPageParam;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerAddVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerRespVO;
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
import com.cloud.baowang.site.vo.export.AgentShortUrlManagerExcelVO;
import com.cloud.baowang.site.vo.export.AgentTransferRecordExportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "代理绑定短链接管理api")
@RestController
@AllArgsConstructor
@RequestMapping("/agent-short-url-manager/api")
public class AgentInfoShortUrlManagerController {
    private final AgentInfoShortUrlManagerApi shortUrlManagerApi;
    private final MinioUploadApi minioUploadApi;

    @PostMapping("pageQuery")
    @Operation(summary = "分页查询列表")
    public ResponseVO<Page<AgentShortUrlManagerRespVO>> pageQuery(@RequestBody AgentShortUrlManagerPageQueryVO queryVO) {
        queryVO.setSiteCode(CurrReqUtils.getSiteCode());
        return shortUrlManagerApi.pageQuery(queryVO);
    }

    @PostMapping("addShortUrl")
    @Operation(summary = "新增/修改代理短链接")
    public ResponseVO<Boolean> addShortUrl(@RequestBody AgentShortUrlManagerAddVO agentShortUrlManagerAddVO) {
        agentShortUrlManagerAddVO.setSiteCode(CurrReqUtils.getSiteCode());
        agentShortUrlManagerAddVO.setBindShortUrlTime(System.currentTimeMillis());
        agentShortUrlManagerAddVO.setBindShortUrlOperator(CurrReqUtils.getAccount());
        return shortUrlManagerApi.addShortUrl(agentShortUrlManagerAddVO);
    }

    @GetMapping("deleteShortUrl")
    @Operation(summary = "删除短链接")
    public ResponseVO<Boolean> deleteShortUrl(@RequestParam("id") String id) {
        return shortUrlManagerApi.deleteShortUrl(id);
    }


    @Operation(summary = "短链接-导出")
    @PostMapping(value = "/agentShortRecordExport")
    public ResponseVO<?> export(@RequestBody AgentShortUrlManagerPageQueryVO vo) {
        String currentUserAccount = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::agentShort::agentTransferRecordExport::" + CurrReqUtils.getSiteCode() + currentUserAccount;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setPageSize(10000);
        Long responseVO = shortUrlManagerApi.pageCount(vo);
        if (responseVO <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                AgentShortUrlManagerExcelVO.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO),
                param -> ConvertUtil.entityListToModelList(pageQuery(param).getData().getRecords(), AgentShortUrlManagerExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.AGENT_SHORT_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

    }


}
