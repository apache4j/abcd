package com.cloud.baowang.system.api.api.file;

import com.cloud.baowang.system.api.vo.PwaVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import feign.Request;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(contextId = "minio-upload-api", value = ApiConstants.NAME)
@Tag(name = "Minio文件上传api")
public interface MinioUploadApi {

    String PREFIX = ApiConstants.PREFIX + "/minio-upload-api/api/";

    @PostMapping(value = PREFIX + "uploadChangeFilename/{bucket}", produces = "application/json;charset=UTF-8", consumes = "multipart/form-data")
    ResponseVO<Map<String, Object>> uploadChangeFilename(@PathVariable("bucket") String bucket, @RequestPart("file") MultipartFile file, Request.Options options);


    @PostMapping(value = PREFIX + "upload/{bucket}", produces = "application/json;charset=UTF-8", consumes = "multipart/form-data")
    ResponseVO<Map<String, Object>> upload(@PathVariable("bucket") String bucket, @RequestPart("file") MultipartFile file, Request.Options options);

    @PostMapping(value = PREFIX + "uploadFile/{bucket}/{maxSize}", produces = "application/json;charset=UTF-8", consumes = "multipart/form-data")
    ResponseVO<Map<String, Object>> uploadFile(@PathVariable("bucket") String bucket,
                                               @PathVariable("maxSize") String maxSize,
                                               @RequestPart("file") MultipartFile file,
                                               Request.Options options);

    @PostMapping(PREFIX + "uploadXlsxAndFileExport")
    ResponseVO<String> uploadXlsxAndFileExport(@RequestBody UploadXlsxVO vo);

    @PostMapping(PREFIX + "upload/{bucket}/{suffix}")
    ResponseVO<Map<String, Object>> uploadSuffix(@PathVariable("bucket") String bucket,
                                                 @PathVariable("suffix") String suffix,
                                                 @RequestPart("file") MultipartFile file,
                                                 Request.Options options);

    @PostMapping(PREFIX + "upload/local/{bucket}")
    ResponseVO<Map<String, Object>> uploadMinioObject(@RequestParam("filename") String filename, @PathVariable("bucket") String bucket);

    /**
     * 大文件上传
     *
     * @param bucket
     * @param file
     * @param options
     * @return
     */
    @PostMapping(value = PREFIX + "maxFileUpload/{bucket}", produces = "application/json;charset=UTF-8", consumes = "multipart/form-data")
    ResponseVO<Map<String, Object>> maxFileUpload(@PathVariable("bucket") String bucket, @RequestPart("file") MultipartFile file, Request.Options options);


    @PostMapping(value = PREFIX + "uploadPwa")
    ResponseVO<Map<String, Object>> uploadPwa(@RequestBody PwaVO pwaVO,
                                              @RequestParam("fileName") String fileName);
}