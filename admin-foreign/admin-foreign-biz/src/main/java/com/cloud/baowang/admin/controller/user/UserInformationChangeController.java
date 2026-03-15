package com.cloud.baowang.admin.controller.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.service.UserInformationChangeService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeReqVO;
import com.cloud.baowang.user.api.vo.UserInformationChange.UserInformationChangeResVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "会员-会员信息变更记录")
@RestController
@RequestMapping(value = "/user-info-change/api")
@AllArgsConstructor
public class UserInformationChangeController {



    private final UserInformationChangeService userInformationChangeService;

    @Schema(description = "分页查询")
    @PostMapping("/getUserInformationChange")
    public ResponseVO<Page<UserInformationChangeResVO>> getUserInformationChange(@RequestBody UserInformationChangeReqVO userInformationChangeReqVO) {
        userInformationChangeReqVO.setDataDesensitization(CurrReqUtils.getDataDesensity());
        return userInformationChangeService.getUserInformationChange(userInformationChangeReqVO);
    }
}
