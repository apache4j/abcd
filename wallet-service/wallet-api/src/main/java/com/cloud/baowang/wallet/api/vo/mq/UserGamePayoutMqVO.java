package com.cloud.baowang.wallet.api.vo.mq;

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
@Schema(title = "游戏派彩消息实体")
public class UserGamePayoutMqVO extends MessageBaseVO {

    private List<UserGamePayoutVO> userRecordPayoutVOList;
}
