package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: mufan
 * @createTime: 2025/10/25 18:11
 * @description:
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "注单第一次结算发送的消息实体集合")
public class AccountFlowMqVO extends MessageBaseVO {
    List<AccountRequestMqVO> accountRequestList;
}
