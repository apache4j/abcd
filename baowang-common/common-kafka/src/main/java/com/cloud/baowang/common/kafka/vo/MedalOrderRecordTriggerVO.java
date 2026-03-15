package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Schema(description = "勋章注单盈亏触发类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MedalOrderRecordTriggerVO extends MessageBaseVO {
    List<MedalOrderRecordMqVO> medalOrderRecordMqVOS;
}
