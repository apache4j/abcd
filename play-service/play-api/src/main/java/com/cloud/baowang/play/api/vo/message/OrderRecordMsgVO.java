package com.cloud.baowang.play.api.vo.message;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import com.cloud.baowang.play.api.vo.order.OrderRecordVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单消息实体")
public class OrderRecordMsgVO extends MessageBaseVO {

    private OrderRecordVO recordVO;
}
