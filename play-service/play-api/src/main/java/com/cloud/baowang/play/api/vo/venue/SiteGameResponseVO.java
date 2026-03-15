package com.cloud.baowang.play.api.vo.venue;

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
 * @Date : 2024/7/29 14:13
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(title = "站点游戏授权返回对象")
@I18nClass
public class SiteGameResponseVO {

    @Schema(description = "选中游戏ID集合")
    private List<String> chooseID;

    @Schema(description = "全部的游戏ID集合")
    private List<String> allID;

    @Schema(description = "站点游戏授权分页")
    private Page<SiteGameResponsePageVO> pageVO;
}
