package com.cloud.baowang.play.api.vo.mq;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "免费游戏触发类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FreeGameRecordTriggerVO extends MessageBaseVO {
    List<FreeGameRecordVO> freeGameVOList;
}
