package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "代理短链接视图VO")
@Data
@ExcelIgnoreUnannotated
public class AgentShortUrlManagerExcelVO implements Serializable {

    @Schema(description = "代理账号")
    @ExcelProperty("代理账号")
    private String agentAccount;

    @Schema(description = "短链接")
    @ExcelProperty("短链接")
    private String shortUrl;

    @Schema(description = "创建人")
    @ExcelProperty("创建人")
    private String bindShortUrlOperator;

    @Schema(description = "创建时间")
    private Long bindShortUrlTime;
    @ExcelProperty("创建时间")
    private String bindShortUrlTimeStr;

    public String getBindShortUrlTimeStr() {
        return bindShortUrlTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(bindShortUrlTime, CurrReqUtils.getTimezone());
    }
}
