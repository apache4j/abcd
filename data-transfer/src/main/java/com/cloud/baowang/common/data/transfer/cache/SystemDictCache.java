package com.cloud.baowang.common.data.transfer.cache;


import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/***
 * 字典表缓存
 */
@Component
public class SystemDictCache {

    @Autowired
    private SystemParamApi systemParamApi;

    /**
     * 根据字典type查询,先查本地缓存 key:type
     *
     * @param type
     * @return
     */
    public List<CodeValueVO> getSystemParamByType(String type) {
        String key = CacheConstants.SYSTEM_BUSINESS_DICT_CACHE + ":" + type;
        List<CodeValueVO> value = (List<CodeValueVO>) RedisUtil.getLocalCachedMap(CacheConstants.KEY_SYSTEM_PARAM, key);
        if (value != null) {
            return value;
        }
        ResponseVO<List<CodeValueVO>> responseVO = systemParamApi.getSystemParamByType(type);
        if (responseVO.isOk()) {
            List<CodeValueVO> data = responseVO.getData();
            RedisUtil.setLocalCachedMap(CacheConstants.KEY_SYSTEM_PARAM, key, data);
            return data;
        }
        return null;
    }
}
