package com.cloud.baowang.system.po.operations;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("customer_channel")
public class CustomerChannelPO extends BasePO {

    private String platformCode;

    private String channelCode;

    private String channelName;

    private Integer customerType;

    private String channelAddr;

    private String secretKey;

    private String merId;

    private Integer status;

    private String remark;

    private String creatorName;

    private String updaterName;
}
