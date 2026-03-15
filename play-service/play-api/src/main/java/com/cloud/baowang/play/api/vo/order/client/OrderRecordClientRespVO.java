package com.cloud.baowang.play.api.vo.order.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@I18nClass
@Schema(description = "客户端注单记录返回参数")
public class OrderRecordClientRespVO {
    @Schema(description = "赛事注单信息 电竞 斗鸡")
    private Page<EventOrderClientResVO> eventOrderPage;
    @Schema(description = "赛事注单信息 沙巴专属")
    private List<EventOrderClientResVO> sabOrderList;
    @Schema(description = "电子 棋牌类注单")
    private Page<BasicOrderClientResVO> basicOrderPage;
    @Schema(description = "桌台类注单 真人")
    private Page<TableOrderClientResVO> tableOrderPage;
    @Schema(description = "彩票类注单")
    private Page<LtOrderClientResVO> ltOrderPage;
    @Schema(description = "体育类注单信息")
    private Page<SportOrderClientResVO> sportOrderPage;
    private Boolean isSBA=true;
    @Schema(description = "总计vo 电竞与体育没有")
    private ClientOrderTotalVO totalVO = new ClientOrderTotalVO();
}
