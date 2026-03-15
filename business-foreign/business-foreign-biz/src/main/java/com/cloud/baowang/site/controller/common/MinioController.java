package com.cloud.baowang.site.controller.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nIgnore;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.system.api.vo.param.FileExportPageVO;
import com.cloud.baowang.system.api.vo.param.FileExportRespVO;
import feign.Request;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    @Operation(summary = "文件导出列表分页查询")
    @PostMapping("/filePage")
    public ResponseVO<Page<FileExportRespVO>> filePage(@RequestBody FileExportPageVO fileExportPageVO) {
        fileExportPageVO.setAdminId(CurrReqUtils.getAccount());
        fileExportPageVO.setSiteCode(CommonConstant.ADMIN_CENTER_SITE_CODE);
        log.info("文件列表查询参数:{}", fileExportPageVO);
        return fileExportApi.fileExportPage(fileExportPageVO);
    }

}
