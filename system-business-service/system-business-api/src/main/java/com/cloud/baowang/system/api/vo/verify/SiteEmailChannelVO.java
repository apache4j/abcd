package com.cloud.baowang.system.api.vo.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="新增站点邮箱授权返回对象")
@I18nClass
public class SiteEmailChannelVO extends BaseVO {

    @Schema(description = "选中的通道ID集合")
    private List<String> chooseId;
    @Schema(description = "全部的通道ID集合")
    private List<String> allId;

    @Schema(description = "站点邮箱通道分页对象")
    private Page<SiteEmailChannelPageVO> pageVO;
}
