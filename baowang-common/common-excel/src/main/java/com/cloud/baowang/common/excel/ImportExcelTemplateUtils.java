package com.cloud.baowang.common.excel;

import cn.hutool.core.io.IoUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * 模板导出excel方法
 */
@Slf4j
public class ImportExcelTemplateUtils {
    /**
     * 导出模板
     *
     * @param response     response
     * @param enumFileName 文件名称，后缀统一为.xlsx,
     */
    public static void importExcelTemplateByCode(HttpServletResponse response, String enumFileName) {
        // 设置响应头
        InputStream stream = null;
        try {
            ExcelTemplateFileNameEnum fileNameEnum = ExcelTemplateFileNameEnum.getFileNameByCode(enumFileName);
            if (fileNameEnum == null) {
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }
            stream = ImportExcelTemplateUtils.class.getClassLoader().getResourceAsStream("static/" + fileNameEnum.getFileName() + ".xlsx");
            // 设置响应头
            String fileName = URLEncoder.encode(fileNameEnum.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            IoUtil.copy(stream,response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载模版失败，原因:{},当前fileName:{}", e.getMessage(), enumFileName);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }
}
