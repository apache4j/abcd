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
@Schema(title = "标签配置变更记录VO")
public class UserLabelConfigRecordExportVO {

    private Long updatedTime;

    @Schema(description = "变更时间")
    @ExcelProperty(value="变更时间", index = 0)
    private String updatedTimeStr;

    @Schema(description = "标签名称")
    @ExcelProperty(value="标签名称", index = 1)
    private String labelName;

    @Schema(description = "变更类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_LABEL_CHANGE_TYPE)
    private String changeType;

    @Schema(description = "变更类型名称")
    @ExcelProperty(value="变更类型", index = 2)
    private String changeTypeText;

    @Schema(description = "变更前信息")
    @ExcelProperty(value="变更前信息", index = 3)
    private String beforeChange;

    @Schema(description = "变更后信息")
    @ExcelProperty(value="变更后信息", index =4)
    private String afterChange;

    @Schema(description = "操作人")
    @ExcelProperty(value="操作人", index = 5)
    private String updaterName;


    public String getUpdatedTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }

}
