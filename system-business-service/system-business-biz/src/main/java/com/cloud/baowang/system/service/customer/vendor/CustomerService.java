package com.cloud.baowang.system.service.customer.vendor;

import com.cloud.baowang.system.po.operations.CustomerChannelPO;

/**
 * @author: fangfei
 * @createTime: 2024/11/04 12:37
 * @description:
 */
public interface CustomerService {
    String getApiUrl(CustomerChannelPO po, String userAccount);
}
