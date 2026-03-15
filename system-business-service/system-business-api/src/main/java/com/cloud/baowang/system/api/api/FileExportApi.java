package com.cloud.baowang.system.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.param.AddFileExportVO;
import com.cloud.baowang.system.api.vo.param.FileExportPageVO;
import com.cloud.baowang.system.api.vo.param.FileExportRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "remoteFileExportApi", value = ApiConstants.NAME)
@Tag(name = "RPC 文件导出 服务")
public interface FileExportApi {

    String PREFIX = ApiConstants.PREFIX + "/fileExport/api/";

    @Operation(summary = "新增文件导出数据")
    @PostMapping(value = PREFIX + "addFileExport")
    void addFileExport(@RequestBody AddFileExportVO vo);


    @Operation(summary = "文件导出分页查询")
    @PostMapping(value = PREFIX + "fileExportPage")
    ResponseVO<Page<FileExportRespVO>> fileExportPage(@RequestBody FileExportPageVO fileExportPageVO);
}
