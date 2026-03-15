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
import com.cloud.baowang.user.api.api.vip.VipAwardApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordReqVo;
import com.cloud.baowang.user.api.vo.vip.SiteVipAwardRecordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @auther amos
 * @create 2024-10-28
 */
@Tag(name = "资金-会员资金记录-vip奖励记录")
@RestController
@Slf4j
@RequestMapping("/vip/award")
@AllArgsConstructor
public class VipAwardRecordController {
    @Autowired
    private VipAwardApi vipAwardApi;
    @Autowired
    private final MinioUploadApi minioUploadApi;

    @PostMapping("query")
    @Operation(summary ="站点后台vip奖励记录查询")
    public ResponseVO<Page<SiteVipAwardRecordVo>> query(@RequestBody SiteVipAwardRecordReqVo reqVo) {
        reqVo.setSiteCode(CurrReqUtils.getSiteCode());
        return vipAwardApi.queryVIPAwardList(reqVo);
    }

    @PostMapping("export")
    @Operation(summary = "站点后台vip奖励记录导出")
    public ResponseVO<?> export(@RequestBody SiteVipAwardRecordReqVo reqVo){
        String adminId = CurrReqUtils.getOneId();
        reqVo.setSiteCode(CurrReqUtils.getSiteCode());
        String uniqueKey = "tableExport::centerControl::VipAwardRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            log.warn(remain + "秒后才能导出下载");
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        reqVo.setPageSize(10000);
        ResponseVO<Page<SiteVipAwardRecordVo>> responseVO = vipAwardApi.queryVIPAwardList(reqVo);
        if(!responseVO.isOk()){
            throw new BaowangDefaultException(responseVO.getMessage());
        }

        long totalNum=responseVO.getData().getTotal();
        if (totalNum <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteVipAwardRecordVo.class,
                reqVo,
                4,
                ExcelUtil.getPages(reqVo.getPageSize(), totalNum),
                param -> ConvertUtil.entityListToModelList(vipAwardApi.queryVIPAwardList(reqVo).getData().getRecords(), SiteVipAwardRecordVo.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.USER_VIP_AWARD_LIST)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());

    }
}
