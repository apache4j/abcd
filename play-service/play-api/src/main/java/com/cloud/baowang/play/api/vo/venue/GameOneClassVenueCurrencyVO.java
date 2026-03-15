package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiqi
 */
@Schema(description = "游戏平台VO对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class GameOneClassVenueCurrencyVO implements Serializable {

    @Schema(description = "选中的场馆")
    private List<GameOneClassCurrencyListVO> inList;

    @Schema(description = "所有的场馆")
    private List<GameOneClassCurrencyListVO> allList;




}
