package com.cloud.baowang.system.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.FileExportApi;
import com.cloud.baowang.system.api.vo.param.AddFileExportVO;
import com.cloud.baowang.system.api.vo.param.FileExportPageVO;
import com.cloud.baowang.system.api.vo.param.FileExportRespVO;
import com.cloud.baowang.system.service.FileExportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class FileExportApiImpl implements FileExportApi {

    private FileExportService fileExportService;

    @Override
    public void addFileExport(AddFileExportVO vo) {
        fileExportService.addFileExport(vo);
    }

    @Override
    public ResponseVO<Page<FileExportRespVO>> fileExportPage(FileExportPageVO fileExportPageVO) {
        return  fileExportService.fileExportPage(fileExportPageVO);
    }
}
