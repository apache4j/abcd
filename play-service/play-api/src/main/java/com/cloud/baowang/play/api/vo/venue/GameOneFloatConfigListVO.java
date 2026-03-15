package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
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
@Schema(description = "一级分类悬浮")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class GameOneFloatConfigListVO implements Serializable {

    private String id;

    @Schema(description = "一级分类")
    private String gameOneId;

    private String floatNameI18nCodeName;

    private List<GameOneFloatConfigListVO> list;

}
