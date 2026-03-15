package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendPageVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendRequestVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "userDividendApi", value = ApiConstants.NAME)
@Tag(name = "RPC 用户红利 服务")
public interface UserDividendApi {

    String PREFIX = ApiConstants.PREFIX + "/userDividendApi/api/";
    @PostMapping(value = "/userDividendPage")
    ResponseVO<Page<UserDividendPageVO>> userDividendPage(@RequestBody UserDividendRequestVO requestVO);
}
