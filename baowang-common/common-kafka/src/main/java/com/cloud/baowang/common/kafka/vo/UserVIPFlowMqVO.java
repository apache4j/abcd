package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/11 18:11
 * @description:
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "VIP晋级消息实体")
public class UserVIPFlowMqVO extends MessageBaseVO {
    List<UserVIPFlowRequestVO> vipFlowRequestList;
}
