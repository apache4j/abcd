package com.cloud.baowang.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nIgnore;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.vo.param.FileExportPageVO;
import com.cloud.baowang.system.api.vo.param.FileExportRespVO;
import feign.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: sheldon
 * @Date: 3/22/24 1:14 下午
 */
@Tag(name = "上传文件接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/file/api")
@Slf4j
public class MinioController {

    private final MinioUploadApi minioUploadApi;

    private final FileExportApi fileExportApi;

    @Operation(summary = "文件上传,文件名自动生成,成功返回文件地址")
    @Parameters({
            //@Parameter(name = "bigFileSecret", description = "文件50M以下"),
            @Parameter(name = "bucket", description = "bucket参数,提前联系管理员获取", required = true)})
    @PostMapping("uploadChangeFilename/{bucket}")
    @I18nIgnore
    public ResponseVO<Map<String, Object>> uploadChangeFilename(@PathVariable("bucket") String bucket, @RequestPart("file") MultipartFile file) {
        return minioUploadApi.uploadChangeFilename(bucket, file, new Request.Options(10, TimeUnit.SECONDS, 360, TimeUnit.SECONDS, true));
    }


    @Operation(summary = "文件上传,原始文件名称存储,成功返回文件地址")
    @Parameters({
            //@Parameter(name = "bigFileSecret", description = "文件50M以下"),
            @Parameter(name = "bucket", description = "bucket参数,提前联系管理员获取", required = true)})
    @PostMapping("upload/{bucket}")
    public ResponseVO<Map<String, Object>> upload(@PathVariable("bucket") String bucket, @RequestPart("file") MultipartFile file) {
        long startTime = System.currentTimeMillis();
        log.info("开始上传文件,当前时间:{}", startTime);
        ResponseVO<Map<String, Object>> resp = minioUploadApi.upload(bucket, file, new Request.Options(10, TimeUnit.SECONDS, 360, TimeUnit.SECONDS, true));
        long endTime = System.currentTimeMillis();
        if (resp.isOk()) {
            Map<String, Object> data = resp.getData();
            if (CollectionUtil.isNotEmpty(data)) {
                log.info("上传文件成功,当前文件路径:{},文件大小:{} B,上传用时:{}毫秒",data.get("url"),data.get("size"),endTime-startTime);
            }

        } else {
            log.info("上传文件失败,当前时间:{}", endTime);
        }
        log.info("本次上传共计用时:{} 毫秒", endTime - startTime);
        return resp;
    }

    @Operation(summary = "文件上传,原始文件名称存储,成功返回文件地址")
    @Parameters({
            //@Parameter(name = "bigFileSecret", description = "文件50M以下"),
            @Parameter(name = "bucket", description = "bucket参数,提前联系管理员获取", required = true)})
    @PostMapping("maxFileUpload/{bucket}")
    public ResponseVO<Map<String, Object>> maxFileUpload(@PathVariable("bucket") String bucket,
                                                         @RequestPart("file") MultipartFile file) {
        return minioUploadApi.maxFileUpload(bucket, file, new Request.Options(10, TimeUnit.SECONDS, 360, TimeUnit.SECONDS, true));
    }

    @Operation(summary = "文件上传,原始文件名称存储,成功返回文件地址,文件大小")
    @Parameters({
            //@Parameter(name = "bigFileSecret", description = "文件50M以下"),
            @Parameter(name = "bucket", description = "bucket参数,提前联系管理员获取", required = true),
            @Parameter(name = "maxSize", description = "maxSize,为前端上传文件时提供的文件大小,单位是字节,如10m=10*1024KB*1024B")})
    @PostMapping("upload/{bucket}/{maxSize}")
    public ResponseVO<Map<String, Object>> uploadFile(@PathVariable("bucket") String bucket,
                                                      @PathVariable("maxSize") String maxSize,
                                                      @RequestPart("file") MultipartFile file) {
        return minioUploadApi.uploadFile(bucket, maxSize, file, new Request.Options(10, TimeUnit.SECONDS, 360, TimeUnit.SECONDS, true));
    }

    @Operation(summary = "文件上传(自定义后缀),成功返回文件地址")
    @Parameters({
            @Parameter(name = "suffix", description = "后缀名称 比如png 不要加. ", required = true),
            @Parameter(name = "bucket", description = "bucket名称,提前联系管理员获取", required = true)})
    @PostMapping("/upload/{bucket}/{suffix}")
    public ResponseVO<Map<String, Object>> uploadSuffix(@PathVariable("bucket") String bucket,
                                                        @PathVariable("suffix") String suffix,
                                                        @RequestPart("file") MultipartFile file) {
        return minioUploadApi.uploadSuffix(bucket, suffix, file, new Request.Options(10, TimeUnit.SECONDS, 360, TimeUnit.SECONDS, true));
    }

    @Operation(summary = "文件上传(服务器本地文件),原始文件名称存储")
    @Parameters({
            @Parameter(name = "filename", description = "本地文件绝对路径", required = true),
            @Parameter(name = "bucket", description = "bucket名称,提前联系管理员获取", required = true)})
    @PostMapping("/upload/local/{bucket}")
    public ResponseVO<Map<String, Object>> uploadMinioObject(@RequestParam("filename") String filename, @PathVariable("bucket") String bucket) {
        return minioUploadApi.uploadMinioObject(filename, bucket);
    }


    @Operation(summary = "文件导出列表分页查询")
    @PostMapping("/filePage")
    public ResponseVO<Page<FileExportRespVO>> filePage(@RequestBody FileExportPageVO fileExportPageVO) {
        fileExportPageVO.setAdminId(CurrReqUtils.getAccount());
        fileExportPageVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        log.info("文件列表查询参数:{}", fileExportPageVO);
        return fileExportApi.fileExportPage(fileExportPageVO);
    }

}
