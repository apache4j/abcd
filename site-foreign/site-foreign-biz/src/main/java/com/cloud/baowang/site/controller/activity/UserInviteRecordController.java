package com.cloud.baowang.site.controller.activity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.user.api.api.SiteUserInviteConfigApi;
import com.cloud.baowang.user.api.api.SiteUserInviteRecordApi;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteConfigResponseVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordResVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author: fangfei
 * @createTime: 2024/11/24 14:49
 * @description:
 */
@Tag(name = "活动记录-邀请好友记录")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/invite/api")
public class UserInviteRecordController {

    private final SiteUserInviteConfigApi siteUserInviteConfigApi;
    private final SiteUserInviteRecordApi siteUserInviteRecordApi;
    private final MinioUploadApi minioUploadApi;

    @PostMapping("/getUserInviteConfig")
    @Operation(summary = "获取邀请好友配置")
    public ResponseVO<SiteUserInviteConfigResponseVO> getUserInviteConfig() {
        String siteCode = CurrReqUtils.getSiteCode();
        return ResponseVO.success(siteUserInviteConfigApi.getInviteConfig(siteCode));
    }

    @PostMapping("/userInviteConfig")
    @Operation(summary = "邀请好友配置")
    public ResponseVO userInviteConfig(@RequestBody SiteUserInviteConfigReqVO reqVO) {
        if (reqVO.getDepositAmountTotal().compareTo(BigDecimal.ZERO) < 0 ||
        reqVO.getFirstDepositAmount().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseVO.fail(ResultCode.AMOUNT_GREATER_ZERO);
        }
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return siteUserInviteConfigApi.userInviteConfig(reqVO);
    }

    @PostMapping("/getInviteRecordPage")
    @Operation(summary = "分页查询邀请好友记录")
    public ResponseVO<Page<SiteUserInviteRecordResVO>> getInviteRecordPage(@RequestBody SiteUserInviteRecordReqVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(siteUserInviteRecordApi.getInviteRecordPage(reqVO));
    }

    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody SiteUserInviteRecordReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "tableExport::centerControl::inviteRecord::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        vo.setPageSize(10000);
        Long count  = siteUserInviteRecordApi.getInviteRecordCount(vo);
        if (count ==  null || count <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteUserInviteRecordResVO.class,
                vo,
                1,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> siteUserInviteRecordApi.getInviteRecordPage(vo).getRecords());

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.SITE_INVITE_RECORD)
                        .adminId(CurrReqUtils.getAccount())
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }

}
