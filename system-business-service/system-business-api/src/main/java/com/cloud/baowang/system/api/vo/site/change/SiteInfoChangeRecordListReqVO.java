package com.cloud.baowang.system.api.vo.site.change;

import com.cloud.baowang.system.api.vo.JsonDifferenceVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : mufan
 * @Date : 2025/4/5 11:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点操作记录消息实体")
public class SiteInfoChangeRecordListReqVO {
    @Schema(title = "操作对象名称，对应站点名称")
    private String optionCode;
    @Schema(title = "操作对象code，对应站点编码")
    private String optionName;
    @Schema(title = "操作模块名称，对应站点列表")
    private String optionModelName;
    @Schema(title = "登入ip")
    private String loginIp;
    @Schema(title = "修改前修改后主题判断内容")
    private List<JsonDifferenceVO> data;
    @Schema(description = "0:新增、1:修改,2:删除等 SiteOptionTypeEnum")
    private Integer optionType;
    @Schema(description = "0:失败,1:成功 SiteOptionStatusEnum")
    private Integer optionStatus;
    @Schema(title = "操作人")
    private String creator;
}
