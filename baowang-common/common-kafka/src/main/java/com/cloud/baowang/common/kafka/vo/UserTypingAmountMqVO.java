package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "打码量消息实体")
public class UserTypingAmountMqVO extends MessageBaseVO {

    private List<UserTypingAmountRequestVO> userTypingAmountRequestVOList;
}
