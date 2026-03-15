package com.cloud.baowang.site.controller.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.cloud.baowang.system.api.api.site.rebate.UserVenueRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditQueryVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateAuditRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateDetailsRspVO;
import com.cloud.baowang.system.api.vo.site.rebate.user.UserRebateRecordExportVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@Tag(name = "站点-游戏返水配置")
@RequestMapping("/user-rebate-record/api/")
@AllArgsConstructor
@Slf4j
public class UserRebateRecordController {

    private final UserVenueRebateApi userVenueRebateApi;


    private final MinioUploadApi minioUploadApi;

    private final VipRankApi vipRankApi;

    @PostMapping("userRebateRecordPage")
    @Operation(summary = "返水记录列表")
    ResponseVO<Page<UserRebateAuditRspVO>> userRebateRecordPage(@RequestBody UserRebateAuditQueryVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userVenueRebateApi.userRebateRecordPage(vo);
    }

    @PostMapping("userRebateRecordDetails")
    @Operation(summary = "返水明细 只要userId,orderNo")
    ResponseVO<List<UserRebateDetailsRspVO>> userRebateRecordDetails(@RequestBody UserRebateAuditQueryVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userVenueRebateApi.userRebateDetails(vo);
    }

    @Operation(summary = "vip段位下拉框")
    @PostMapping(value = "getDownBox")
    public ResponseVO<List<CodeValueVO>> getDownBox(){
        List<SiteVIPRankVO> vipRankListBySiteCode = vipRankApi.getVipRankListBySiteCode(CurrReqUtils.getSiteCode()).getData();

        List<CodeValueVO> vipRankNameEnums = Lists.newArrayList();

        for (SiteVIPRankVO respVo : vipRankListBySiteCode) {
            CodeValueVO codeValueVO = new CodeValueVO();
            codeValueVO.setType(respVo.getVipRankCode().toString());
            codeValueVO.setCode(respVo.getVipRankCode().toString());
            codeValueVO.setValue(respVo.getVipRankNameI18nCode());
            vipRankNameEnums.add(codeValueVO);
        }
        return ResponseVO.success(vipRankNameEnums);
    }

    @PostMapping("export")
    @Operation(summary = "导出")
    public ResponseVO<?> export( @RequestBody UserRebateAuditQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "rebate-record-export::site::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> resp = userVenueRebateApi.rebateRecordCount(vo);
        if (!resp.isOk() || resp.getData() <= 0) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        vo.setPageSize(10000);
        Long count = resp.getData();
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserRebateRecordExportVO.class,
                vo,
                1,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> ConvertUtil.entityListToModelList(listExportPage(param),UserRebateRecordExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_REBATE_RECORD)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

    List<UserRebateRecordExportVO> listExportPage(UserRebateAuditQueryVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        Page<UserRebateAuditRspVO> data = userVenueRebateApi.userRebateRecordPage(vo).getData();
        List<UserRebateAuditRspVO> exportData = data.getRecords();
        return ConvertUtil.entityListToModelList(exportData, UserRebateRecordExportVO.class);
    }


}
