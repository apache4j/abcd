package com.cloud.baowang.site.vo.export.userManual;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description ="会员人工加减额excel模版")
@ExcelIgnoreUnannotated
public class UserManualExcelImportVO implements Serializable {


    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    private String userAccount;

}
