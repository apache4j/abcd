package com.cloud.baowang.system.api.vo.site.change;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author : mufan
 * @Date : 2025/4/7 11:57
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点列表新增传入参数")
public class SiteInfoChangeRecordVO {

    @Schema(title = "id主键")
    private String id;

    @Schema(title = "操作对象名称，对应站点名称")
    private String optionCode;

    @Schema(title = "操作对象code，对应站点编码")
    private String optionName;

    @Schema(title = "操作模块名称，对应站点列表")
    private String optionModelName;

    @Schema(title = "操作类型 0:新增、1:修改,2:删除")
    private String optionTypeStr;

    @Schema(title = "状态(0:失败,1:成功)")
    private String optionStatusStr;

    @Schema(title = "登入ip")
    private String loginIp;

    @Schema(title = "变更后的状态描述json保存")
    private Object changeAfter;

    @Schema(title = "操作人")
    private String creator;

    @Schema(title = "操作时间")
    private Long createdTime;
}
