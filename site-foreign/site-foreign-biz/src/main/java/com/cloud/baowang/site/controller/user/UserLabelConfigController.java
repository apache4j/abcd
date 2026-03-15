package com.cloud.baowang.site.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.site.vo.export.UserLabelConfigExportVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserPageByLabelIdRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.GetUserPageByLabelIdVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelAddRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelConfigPageResponseVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelDelRequestVO;
import com.cloud.baowang.user.api.vo.userlabel.UserLabelEditRequestVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 会员标签管理
 */
@Tag(name = "会员-会员管理-标签管理")
@RestController
@AllArgsConstructor
@RequestMapping("/user-label-config/api")
public class UserLabelConfigController {
    private final SiteUserLabelConfigApi siteUserLabelConfigApi;
    private final MinioUploadApi minioUploadApi;

    @Operation(summary = "新增会员标签")
    @PostMapping(value = "/addLabel")
    public ResponseVO addLabel(@Valid @RequestBody UserLabelAddRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return siteUserLabelConfigApi.addLabel(vo);
    }

    @Operation(summary = "会员标签配置分页查询")
    @PostMapping(value = "/getLabelConfigPage")
    public ResponseVO<Page<UserLabelConfigPageResponseVO>> getLabelConfigPage(@RequestBody UserLabelConfigPageRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(siteUserLabelConfigApi.getLabelConfigPage(vo));
    }

    @Operation(summary = "编辑会员标签")
    @PostMapping(value = "/editLabel")
    public ResponseVO editLabel(@Valid @RequestBody UserLabelEditRequestVO vo) {
        vo.setOperator(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteUserLabelConfigApi.editLabel(vo);
    }

    @Operation(summary = ("删除会员标签"))
    @PostMapping(value = "/delLabel")
    public ResponseVO delLabel(@Valid @RequestBody UserLabelDelRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setOperator(CurrReqUtils.getAccount());
        return siteUserLabelConfigApi.delLabel(vo);
    }

    @Operation(summary = "标签对应的会员 分页查询")
    @PostMapping(value = "/getUserPageByLabelId")
    public ResponseVO<Page<GetUserPageByLabelIdVO>> getUserPageByLabelId(@Valid @RequestBody GetUserPageByLabelIdRequestVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return ResponseVO.success(siteUserLabelConfigApi.getUserPageByLabelId(vo));
    }


    @Operation(summary = "导出")
    @PostMapping(value = "/export")
    public ResponseVO<?> export(@RequestBody UserLabelConfigPageRequestVO reqVO) {
        reqVO.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getAccount();
        String uniqueKey = "tableExport::centerControl::userLabelConfig::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> responseVO = siteUserLabelConfigApi.getTotalCount(reqVO);
        if (!responseVO.isOk()) {
            throw new BaowangDefaultException(responseVO.getMessage());
        }
        reqVO.setPageSize(responseVO.getData().intValue());
        if (responseVO.getData() <= 0) {
            // 无数据
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                UserLabelConfigExportVO.class,
                reqVO,
                4,
                ExcelUtil.getPages(reqVO.getPageSize(), responseVO.getData()),
                param -> ConvertUtil.entityListToModelList(getLabelConfigPage(param).getData().getRecords(), UserLabelConfigExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.LABEL_CONFIG_RECORD)
                        .adminId(adminId)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .build());
    }
}
