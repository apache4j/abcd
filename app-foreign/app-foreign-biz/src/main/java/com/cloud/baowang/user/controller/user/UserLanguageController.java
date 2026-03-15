package com.cloud.baowang.user.controller.user;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserLanguageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @auther amos
 * @create 2024-10-31
 */
@Tag(name = "用户语言设置")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/user-language/api")
public class UserLanguageController {

    private UserInfoApi userInfoApi;

    @Operation(summary = "设置用户语言")
    @PostMapping("updateUserLanguage")
    public ResponseVO<UserLanguageVO> updateUserLanguage(@RequestBody UserLanguageVO vo) {

        return ResponseVO.success(userInfoApi.updateUserLanguage(vo));
    }
}
