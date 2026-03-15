package com.cloud.baowang.system.api.api.redissonOperate;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(contextId = "redissonOperateApi", value = ApiConstants.NAME)
@Tag(name = "RPC 系统-清空i18n缓存")
public interface RedissonOperateApi {

    String PREFIX = ApiConstants.PREFIX + "/redissonOperate/api";

    @PostMapping(PREFIX + "/i18nClear")
    @Schema(description = "登录日志添加")
    ResponseVO<Void> clear();
}
