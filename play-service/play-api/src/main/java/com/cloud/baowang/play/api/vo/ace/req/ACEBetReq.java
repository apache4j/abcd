package com.cloud.baowang.play.api.vo.ace.req;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ACEBetReq extends ACEBaseReq{

    @Schema(title = "下注编号(唯一码)")
    String referenceID;

    @Schema(title = "回合号")
    String playSessionID;

    @Schema(title = "回合明细")
    String roundDetails;

    @Schema(title = "游戏编号")
    String gameID;

    @Schema(title = "押注金额")
    BigDecimal betAmount;

    @Schema(title = "交易建立时间(年-月-日 时:分:秒)")
    String created;


    public boolean isValid() {
        return super.isValid()
                && StrUtil.isNotEmpty(referenceID)
                && StrUtil.isNotEmpty(playSessionID)
                && betAmount!=null
                && ObjUtil.isNotEmpty(gameID)
                ;
    }

}
