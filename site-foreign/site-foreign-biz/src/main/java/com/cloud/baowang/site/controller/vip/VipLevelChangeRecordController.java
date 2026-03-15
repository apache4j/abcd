package com.cloud.baowang.site.controller.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.user.api.api.vip.VipLevelChangeRecordApi;
import com.cloud.baowang.user.api.enums.VipChangeTypeEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordExportVo;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@Tag(name = "vip等级变更记录相关")
@RestController
@RequestMapping("/vip/changeRecord")
@AllArgsConstructor
public class VipLevelChangeRecordController {

    private final VipLevelChangeRecordApi recordApi;

    private final MinioUploadApi minioUploadApi;


    @PostMapping("/queryChangeRecordPage")
    @Operation(summary = "分页列表,获取下拉框在system_param中根据vip_level_change_type 类型获取")
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryChangeRecordPage(@RequestBody SiteVipChangeRecordPageQueryVO pageQueryVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        pageQueryVO.setSiteCode(siteCode);
        //默认查询vip等级变更记录
        pageQueryVO.setOperationType(VipChangeTypeEnum.VIP_GRADE_CHANGE.getType());
        return recordApi.queryChangeRecordPage(pageQueryVO);
    }

    @PostMapping("/export")
    @Operation(summary = "导出")
    public ResponseVO<?> export(@RequestBody SiteVipChangeRecordPageQueryVO vo) {
        String siteCode = CurrReqUtils.getSiteCode();
        vo.setSiteCode(siteCode);
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::vipChangeRecord::" + siteCode + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        ResponseVO<Long> responseVO = recordApi.getTotalCount(vo);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteVipChangeRecordExportVo.class,
                vo,
                4,
                ExcelUtil.getPages(vo.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(queryChangeRecordPage(param).getData().getRecords(), SiteVipChangeRecordExportVo.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.VIP_CHANGE_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

    }


}
