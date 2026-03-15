package com.cloud.baowang.system.api.redissonOperate;

import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.redissonOperate.RedissonOperateApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class RedissonOperateApiImpl implements RedissonOperateApi {


    @Override
    public ResponseVO<Void> clear() {
        RedisUtil.localCacheMapClear(CacheConstants.KEY_I18N_MESSAGE);
        RedisUtil.localCacheMapClear(CacheConstants.KEY_SYSTEM_PARAM);
        return ResponseVO.success();
    }
}
