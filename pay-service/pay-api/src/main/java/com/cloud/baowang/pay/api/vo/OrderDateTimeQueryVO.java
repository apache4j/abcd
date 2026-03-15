package com.cloud.baowang.pay.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import lombok.Data;

@Data
public class OrderDateTimeQueryVO extends PageVO {

    private Long startTime;

    private Long endTime;


}
