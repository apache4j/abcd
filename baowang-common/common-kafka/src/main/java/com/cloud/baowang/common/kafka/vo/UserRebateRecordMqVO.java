package com.cloud.baowang.common.kafka.vo;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "返水消息实体")
public class UserRebateRecordMqVO extends MessageBaseVO {
    private String siteCode;
    private List<UserRebateRecordDetailsVO> userRebateRecordList;
}
