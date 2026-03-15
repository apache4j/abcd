package com.cloud.baowang.activity.vo.mq;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ActivitySendListMqVO extends MessageBaseVO {

    private List<ActivitySendMqVO> list;

}
