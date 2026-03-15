package com.cloud.baowang.system.api.file;

import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.system.api.vo.PwaVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.system.api.vo.param.AddFileExportVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Validated
@Slf4j
public class MinioUploadApiImpl implements MinioUploadApi {

    @Autowired
    private MinioClient client;
    @Autowired
    private MinioFileService minioFileService;
    @Autowired
    private FileExportApi fileExportApi;

    @Value("${minio.secretKey}")
    private String configSecretKey;

    @Value("${minio.maxFileSize}")
    private Long maxFileSize;

    private MinioClient getInstance() {
        return client;
    }


    /**
     * 文件上传,文件名自动生成,成功返回文件地址
     *
     * @param bucket
     * @param file
     * @return
     */
    public ResponseVO<Map<String, Object>> uploadChangeFilename(@PathVariable("bucket") String bucket,
                                                                @RequestPart("file") MultipartFile file, Request.Options options) {
        if (file == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能为null");
        }
        if (StringUtils.isEmpty(bucket)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket 为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            //if (FileServerUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
            // if (size == 0 || size > 50 * 1024 * 1024) {

            if (size == 0 || size > maxFileSize) {
                throw new BaowangDefaultException(ResultCode.FILE_MAX_SIZE_ERROR);
                //return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
            }
            // }

            String oriFilename = file.getOriginalFilename();
            String[] split = oriFilename.split("\\.");
            String suffix = split[split.length - 1];
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            // String filename = oriFilename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    10 * 1024 * 1024).contentType(file.getContentType()).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", oriFilename);
            data.put("size", size);
            return ResponseVO.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }


    //endpoint: 域名或IP  ，bucket: 不同项目不同名称
    public ResponseVO<Map<String, Object>> upload(@PathVariable("bucket") String bucket,
                                                  @RequestPart("file") MultipartFile file,
                                                  Request.Options options) {//, String bigFileSecret
        if (file == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能为null");
        }
        if (StringUtils.isEmpty(bucket)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket 为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            //if (FileServerUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
            // if (size == 0 || size > 50 * 1024 * 1024) {

            if (size == 0 || size > maxFileSize) {
                throw new BaowangDefaultException(ResultCode.FILE_MAX_SIZE_ERROR);
                //return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
            }
            // }

            String oriFilename = file.getOriginalFilename();
            String[] split = oriFilename.split("\\.");
            String suffix = split[split.length - 1];
            // String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            String filename = oriFilename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    10 * 1024 * 1024).contentType(file.getContentType()).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", oriFilename);
            data.put("size", size);
            return ResponseVO.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

    }

    public ResponseVO<Map<String, Object>> uploadFile(@PathVariable("bucket") String bucket,
                                                      @PathVariable("maxSize") String maxSize,
                                                      @RequestPart("file") MultipartFile file,
                                                      Request.Options options) {//, String bigFileSecret
        if (file == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能为null");
        }
        if (StringUtils.isEmpty(bucket)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket 为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            if (BigDecimal.valueOf(size).compareTo(new BigDecimal(maxSize)) > 0) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件大小超过允许的限制");
            }

            String oriFilename = file.getOriginalFilename();
            String[] split = oriFilename.split("\\.");
            String suffix = split[split.length - 1];
            //  String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            String filename = oriFilename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    -1).contentType(file.getContentType()).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("fileSize", size);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", oriFilename);
            return ResponseVO.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

    }

    //endpoint: 域名或IP  ，bucket: 不同项目不同名称
    public ResponseVO<Map<String, Object>> uploadSuffix(@PathVariable("bucket") String bucket,
                                                        @PathVariable("suffix") String suffix,
                                                        @RequestPart("file") MultipartFile file,
                                                        Request.Options options) {//, String bigFileSecret
        if (file == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能为null");
        }
        if (StringUtils.isEmpty(bucket) || StringUtils.isEmpty(suffix)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket/suffix 为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            //if (FileServerUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
            if (size == 0 || size > 50 * 1024 * 1024) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
            }
            //}

            String oriFilename = file.getOriginalFilename();
            // String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            String filename = oriFilename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    -1).contentType(file.getContentType()).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", oriFilename);
            return ResponseVO.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

    }

    public ResponseVO<Map<String, Object>> uploadMinioObject(@RequestParam("filename") String filename,
                                                             @PathVariable("bucket") String bucket) {
        if (StringUtils.isEmpty(bucket) || StringUtils.isEmpty(filename)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket/filename 为空");
        }
        File file = new File(filename);
        if (!file.exists()) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "no this file");
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");
            if (size == 0 || size > 50 * 1024 * 1024) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
            }
            Long start = System.currentTimeMillis();
            log.info("开始上传{}", filename);
            //String objectName = UUID.randomUUID().toString().replaceAll("-", "") + filename.substring(filename.lastIndexOf("."));
            String objectName = filename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(objectName)
                    .stream(inputStream, size, -1).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + objectName;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", filename.substring(filename.lastIndexOf("/") + 1));
            log.info("上传耗时 {} 毫秒", System.currentTimeMillis() - start);
            return ResponseVO.success(data);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("uploadMinioObject e", e);
        }
        return null;
    }

    @Override
    public ResponseVO<Map<String, Object>> maxFileUpload(String bucket, MultipartFile file, Request.Options options) {
        if (file == null) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能为null");
        }
        if (StringUtils.isEmpty(bucket)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket 为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            //if (FileServerUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
            // if (size == 0 || size > 50 * 1024 * 1024) {

            if (size == 0 || size > maxFileSize) {
                throw new BaowangDefaultException(ResultCode.FILE_MAX_SIZE_ERROR);
                //return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
            }
            // }

            String oriFilename = file.getOriginalFilename();
            String[] split = oriFilename.split("\\.");
            String suffix = split[split.length - 1];
            // String filename = UUID.randomUUID().toString().replaceAll("-", "") + "." + suffix;
            String filename = oriFilename;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    10 * 1024 * 1024).contentType(file.getContentType()).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = minioFileService.getMinioDomain() + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            data.put("fileName", oriFilename);
            data.put("size", size);
            return ResponseVO.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }

    }

    @Override
    public ResponseVO<Map<String, Object>> uploadPwa(PwaVO pwaVO, String fileName) {
        convertToMultipartFile(pwaVO, fileName);
        return ResponseVO.success();
    }

    /**
     * 上传pwa文件到minio
     *
     * @param pwaVO
     * @param fileName
     * @throws IOException
     */
    public void convertToMultipartFile(PwaVO pwaVO, String fileName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] jsonBytes = objectMapper.writeValueAsBytes(pwaVO);
            InputStream inputStream = new ByteArrayInputStream(jsonBytes);
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket("pwa")
                    .object(fileName)
                    .stream(inputStream, jsonBytes.length, -1)
                    .contentType("application/json")
                    .build();
            getInstance().putObject(args);
        } catch (Exception e) {
            log.error("上传pwa文件失败,当前文件名:{},原因:{}", fileName, e.getMessage());
        }
    }

    private boolean checkBigFileSecret(String bigFileSecret) {
        return bigFileSecret.equals(configSecretKey);
    }

    //创建bucket
    //@PostMapping("createBucket")
    //@ApiOperation(("创建bucket"))
    //@Parameters({@Parameter(name = "name", description = "bucket名称", required = true),
    //        @Parameter(name = "secretKey", description = "秘钥", required = true),})
    public ResponseVO<?> createBucket(String name, String secretKey, HttpServletRequest request) {
        if (FileServerUtil.checkNull(name) || name.length() < 3 || name.length() > 63) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "名称长度3-63个字符");
        }
        if (!configSecretKey.equals(secretKey)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "秘钥无效");
        }
        try {
            if (checkBucketExists(name)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket 已存在");
            }
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(name).build();
            getInstance().makeBucket(makeBucketArgs);

            log.info("创建bucketName: {}", name);
            return ResponseVO.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }


    private boolean checkBucketExists(String bucket) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
        return getInstance().bucketExists(bucketExistsArgs);
    }

    public ResponseVO<String> uploadXlsxAndFileExport(UploadXlsxVO vo) {
        String fileName = vo.getPageName() + DateUtils.formatDateByZoneId(System.currentTimeMillis(), DateUtils.yyyyMMddHHmmss, vo.getTimeZone()) + ".xlsx";
        vo.setFileName(fileName);
        ResponseVO<String> minioResponse = uploadXlsx(vo);
        // 保存fileKey到文件导出表file_export
        if (minioResponse.isOk()) {
            String fileKey = minioResponse.getData();
            AddFileExportVO param = new AddFileExportVO()
                    .setPageName(vo.getPageName())
                    .setFileKey(fileKey)
                    .setFileName(fileName)
                    .setSiteCode(vo.getSiteCode())
                    .setAdminId(vo.getAdminId());
            fileExportApi.addFileExport(param);
            return ResponseVO.success(fileName);
        }
        return minioResponse;
    }

    // 导出接口专用 文件格式:xlsx
    public ResponseVO<String> uploadXlsx(UploadXlsxVO vo) {
        String bucket = vo.getBucket();
        byte[] byteArray = vo.getByteArray();

        if (ObjectUtils.isEmpty(byteArray)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件字节数组不能为null");
        }
        if (StringUtils.isEmpty(bucket)) {
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "bucket为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "不支持，请联系技术");
            }
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            long size = inputStream.available();

//            if (size == 0 || size > 50 * 1024 * 1024) {
//                return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR, "文件不能大于50M");
//            }
            String filename = vo.getFileName();
            // String filename = UUID.randomUUID().toString().replaceAll("-", "") + ".xlsx";
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    -1).build();
            //上传到MINIO
            getInstance().putObject(args);
            String fileKey = bucket + "/" + filename;
            return ResponseVO.success(fileKey);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseVO.fail(ResultCode.SERVER_INTERNAL_ERROR);
        }
    }
}
