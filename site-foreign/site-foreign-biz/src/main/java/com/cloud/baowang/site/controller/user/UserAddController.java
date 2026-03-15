package com.cloud.baowang.site.controller.user;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.user.api.api.UserAddApi;
import com.cloud.baowang.user.api.vo.userreview.UserAddVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: kimi
 */
@Tag(name = "新增会员配置")
@RestController
@RequestMapping("/user-add/api")
@AllArgsConstructor
public class UserAddController {

    private final UserAddApi userAddApi;


    @Operation(summary = "站点后台-新增会员")
    @PostMapping(value = "/addUser")
    public ResponseVO<?> addUser(@Valid @RequestBody UserAddVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return userAddApi.addUser(vo, CurrReqUtils.getOneId(), CurrReqUtils.getAccount());
    }
}
