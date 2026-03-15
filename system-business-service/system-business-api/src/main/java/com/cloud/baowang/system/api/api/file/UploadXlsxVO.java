package com.cloud.baowang.system.api.api.file;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "文件导出 Request")
public class UploadXlsxVO {

    @Schema(title = "MINIO桶名")
    private String bucket;

    @Schema(title = "字节数组")
    private byte[] byteArray;

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "操作人id")
    private String adminId;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "文件名称")
    private String fileName;
    @Schema(description = "时区")
    private String timeZone;

    public String getTimeZone() {
        String currentTimeZone=CurrReqUtils.getTimezone();
        if (StringUtils.hasText(currentTimeZone)) {
            this.timeZone=currentTimeZone;
        }else {
            this.timeZone= DateUtils.UTC5_TIME_ZONE;
        }
        return timeZone;
    }
}
