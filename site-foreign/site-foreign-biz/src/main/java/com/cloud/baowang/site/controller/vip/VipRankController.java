package com.cloud.baowang.site.controller.vip;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.BigDecimalConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.PageVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.site.vo.export.UserInfoExportVO;
import com.cloud.baowang.site.vo.export.vip.SiteVipChangeRecordExcelVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.SiteRebateInitVO;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.enums.VipChangeTypeEnum;
import com.cloud.baowang.user.api.enums.VipCurrencyFeeTypeEnum;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import com.cloud.baowang.user.api.vo.vip.VIPRankUpdateVO;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author : 小智
 * @Date : 2024/8/2 09:51
 * @Version : 1.0
 */
@Slf4j
@Tag(name = "站点后台vip段位相关配置")
@RestController
@RequestMapping("/vip/api")
@AllArgsConstructor
public class VipRankController {

    private final VipRankApi vipRankApi;
    private final MinioUploadApi minioUploadApi;
    private final SiteWithdrawWayApi withdrawWayApi;
    private final SystemParamApi paramApi;
    private final SiteRebateApi siteRebateApi;

    @Operation(summary = "VIP段位查询")
    @PostMapping(value = "/queryVIPRank")
    public ResponseVO<Page<SiteVIPRankVO>> queryVIPRankPage(@RequestBody PageVO pageVO) {
        return vipRankApi.queryVIPRankPage(pageVO);
    }

    @GetMapping("getDownBox")
    @Operation(summary = "提款方式下拉框")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox() {
        Map<String, List<CodeValueVO>> result = new HashMap<>();
        ResponseVO<List<CodeValueVO>> resp = paramApi.getSystemParamByType(CommonConstant.FEE_TYPE);
        if (resp.isOk()) {
            List<CodeValueVO> data = resp.getData();
            data = data.stream().filter(item ->
                            String.valueOf(VipCurrencyFeeTypeEnum.FIXED_AMOUNT.getFeeType()).equals(item.getCode()) ||
                                    String.valueOf(VipCurrencyFeeTypeEnum.PERCENTAGE.getFeeType()).equals(item.getCode()))
                    .toList();
            resp.setData(data);
        }
        result.put(CommonConstant.FEE_TYPE, resp.getData());
        return ResponseVO.success(result);
    }

    @GetMapping("withdrawBoxByCurrency")
    @Operation(summary = "根据币种获取提款方式下拉框")
    public ResponseVO<List<CodeValueVO>> withdrawBoxByCurrency(@RequestParam("currencyCode") String currencyCode) {
        return withdrawWayApi.queryListBySiteAndCurrencyCode(CurrReqUtils.getSiteCode(), currencyCode);
    }

    @GetMapping("/queryVIPRankDetailById")
    @Operation(summary = "查看详情-编辑回显用")
    public ResponseVO<SiteVIPRankVO> queryVIPRankDetailById(@RequestParam("id") String id) {
        log.info("vip段位编辑查询,id:{}", id);
        ResponseVO<SiteVIPRankVO> resp = vipRankApi.queryVIPRankDetailById(id);
        log.info("vip段位编辑查询结果,id:{},结果:{}", id, JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "VIP段位编辑")
    @PostMapping(value = "/updateVIPRank")
    public ResponseVO<?> updateVIPRank(@Valid @RequestBody VIPRankUpdateVO vipRankUpdateVO) {
        return vipRankApi.updateVIPRank(vipRankUpdateVO);
        // 初始化反水配置
//        SiteRebateInitVO reqVO = SiteRebateInitVO.builder()
//                .siteCode(CurrReqUtils.getSiteCode())
//                .vipGradeCode(vipRankUpdateVO.getVipRankCode())
//                .status(vipRankUpdateVO.getRebateConfig())
//                .capMode(CurrReqUtils.getHandicapMode()).build();
//        return siteRebateApi.updateVipGradeRebateConfig(reqVO);
    }

    @Operation(summary = "VIP段位变更记录")
    @PostMapping(value = "/queryVIPRankOperation")
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryVIPRankOperation(
            @RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setOperationType(VipChangeTypeEnum.VIP_RANK_CHANGE.getType());
        ResponseVO<Page<SiteVipChangeRecordVO>> pageResponseVO = vipRankApi.queryVIPRankOperation(reqVO);
        return pageResponseVO;
    }


    @Operation(summary = "导出VIP段位变更记录")
    @PostMapping(value = "/exportOperation")
    public ResponseVO<?> exportOperation(@RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        reqVO.setOperationType(VipChangeTypeEnum.VIP_RANK_CHANGE.getType());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::vipRankOperation::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        reqVO.setPageSize(10000);
        ResponseVO<Long> responseVO = vipRankApi.queryOperationCount(reqVO);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteVipChangeRecordExcelVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(queryVIPRankOperation(reqVO).getData().getRecords(), SiteVipChangeRecordExcelVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.VIP_RANK_OPERATION)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());

    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(
            @RequestBody SiteVipChangeRecordPageQueryVO reqVO) {
        reqVO.setOperationType(BigDecimalConstants.ZERO.intValue());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::vipRank::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reqVO.setPageSize(10000);
        ResponseVO<Long> responseVO = vipRankApi.getTotalCount(reqVO);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserInfoExportVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(queryVIPRankOperation(param).getData().getRecords(), UserInfoExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_LIST)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }
}
