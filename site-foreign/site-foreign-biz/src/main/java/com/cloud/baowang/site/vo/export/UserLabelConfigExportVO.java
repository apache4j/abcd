package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "标签记录VO")
public class UserLabelConfigExportVO {

    @ExcelProperty(value = "标签ID", index = 0)
    @Schema(description = "标签ID")
    private String labelId;

    @Schema(description = "标签名称")
    @ExcelProperty(value = "标签名称", index = 1)
    private String labelName;

    @ExcelProperty(value = "标签描述",index = 2)
    @Schema(description = "标签描述")
    private String labelDescribe;

    @ExcelProperty(value = "会员数",index = 3)
    @Schema(description = "标签人数")
    private String labelCount;


    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @ExcelProperty(value = "状态", index = 4)
    @Schema(description = "状态名称")
    private String statusText;


    private long createdTime;

    @ExcelProperty(value = "创建时间", index = 5)
    @Schema(description = "创建时间")
    private String createdTimeStr;

    @ExcelProperty(value = "最后操作人",index = 6)
    @Schema(description = "最后操作人")
    private String updaterName;


    private long updatedTime;

    @ExcelProperty(value = "最后操作时间", index = 7)
    @Schema(description = "最后操作时间")
    private String updatedTimeStr;




    public String getUpdatedTimeStr() {
        return 0 == updatedTime ? null : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

    public String getCreatedTimeStr() {
        return 0 == createdTime ? null : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

}
