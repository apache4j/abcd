package com.cloud.baowang.system.service.customer.vendor.service;

import com.alibaba.fastjson.JSONObject;
import com.cloud.baowang.common.core.utils.AESUtils;
import com.cloud.baowang.system.po.operations.CustomerChannelPO;
import com.cloud.baowang.system.service.customer.vendor.CustomerService;
import org.springframework.stereotype.Service;

/**
 * @author: fangfei
 * @createTime: 2024/11/04 12:39
 * @description: 爱挚能在线客服
 */
@Service(value = "AIONLINE")
public class AiOnlineService implements CustomerService {
    @Override
    public String getApiUrl(CustomerChannelPO po, String userAccount) {
        if (userAccount == null) {
            return po.getChannelAddr();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("vipid", po.getMerId());
        jsonObject.put("name", userAccount);
        String extradata = AESUtils.encrypt(po.getSecretKey(), jsonObject.toJSONString());
        return po.getChannelAddr() + "?extradata=" + extradata;
    }
}
