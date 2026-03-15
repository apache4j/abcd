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
@TableName("site_customer")
public class SiteCustomerPO extends BasePO {

    private String siteCode;

    private String channelCode;

    private Integer enableStatus;

}
