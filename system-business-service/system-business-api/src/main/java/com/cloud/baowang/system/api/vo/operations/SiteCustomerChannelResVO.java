package com.cloud.baowang.system.api.vo.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author : 小智
 * @Date : 2024/7/31 11:19
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="新增站点客服授权返回对象")
@I18nClass
public class SiteCustomerChannelResVO {

    @Schema(description = "选中的通道")
    private List<String> chooseId;

    @Schema(description = "全部通道")
    private List<String> allId;

    @Schema(description = "客服授权通道分页对象")
    private Page<SiteCustomerChannelPageVO> pageVO;

}
