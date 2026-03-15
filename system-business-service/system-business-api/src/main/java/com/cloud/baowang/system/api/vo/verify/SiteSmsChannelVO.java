package com.cloud.baowang.system.api.vo.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/31 16:27
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "新增站点短信返回对象")
@I18nClass
public class SiteSmsChannelVO {

    @Schema(description = "选中通道集合")
    private List<String> chooseId;
    @Schema(description = "全部通道集合")
    private List<String>  allId;

    @Schema(description = "站点短信返回分页对象")
    private Page<SiteSmsChannelPageVO> pageVO;
}
