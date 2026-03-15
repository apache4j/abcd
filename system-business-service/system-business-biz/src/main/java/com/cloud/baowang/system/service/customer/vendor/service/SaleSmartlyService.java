package com.cloud.baowang.system.service.customer.vendor.service;

import com.cloud.baowang.system.po.operations.CustomerChannelPO;
import com.cloud.baowang.system.service.customer.vendor.CustomerService;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/11/20 9:41
 * @description:
 */
@Service(value = "SaleSmartly")
public class SaleSmartlyService implements CustomerService {
    @Override
    public String getApiUrl(CustomerChannelPO po, String userAccount) {
        return po.getChannelAddr();
    }
}
