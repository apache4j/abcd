package com.cloud.baowang.user.controller.lobby;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.game.PublicSettingsApi;
import com.cloud.baowang.play.api.vo.game.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "公共参数设置")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/publicSetting/api")
public class GamePublicSettingController {

    private final PublicSettingsApi publicSettingsApi;


    @Operation(summary = "添加体育关注")
    @PostMapping("/saveFollow")
    ResponseVO<Boolean> saveFollow(@Valid @RequestBody SportFollowReq req) {
        if (ObjectUtil.isEmpty(req.getThirdId())
                || ObjectUtil.isEmpty(req.getType())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return publicSettingsApi.saveFollow(req, CurrReqUtils.getOneId());
    }

    @Operation(summary = "取消体育关注")
    @PostMapping("/unFollow")
    ResponseVO<Boolean> unFollow(@Valid @RequestBody SportUnFollowReq req) {
        return publicSettingsApi.unFollow(req, CurrReqUtils.getOneId());
    }


    @Operation(summary = "取消体育关注-皮肤2")
    @PostMapping("/unFollowList")
    ResponseVO<Boolean> unFollowList(@Valid @RequestBody SportUnFollowListReq req) {
        return publicSettingsApi.unFollowList(req, CurrReqUtils.getOneId());
    }


    @Operation(summary = "查询体育关注详情")
    @PostMapping("/getSportsFollowDetail")
    ResponseVO<List<SportFollowDetailVO>> getSportsFollowMap() {
        return ResponseVO.success(publicSettingsApi.getSportsFollowMap(CurrReqUtils.getOneId()));
    }

    @Operation(summary = "新增-公共配置")
    @PostMapping("/saveSetting")
    ResponseVO<Boolean> saveSetting(@RequestBody PublicSettingsReq settingsReq) {
        if (ObjectUtil.isEmpty(settingsReq.getType()) || ObjectUtil.isEmpty(settingsReq.getValue())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return publicSettingsApi.saveSetting(settingsReq, CurrReqUtils.getOneId());
    }

    @Operation(summary = "查询-公共配置")
    @PostMapping("/getPublicSetting")
    ResponseVO<List<PublicSettingsVO>> getPublicSetting(@RequestBody PublicSettingsReq settingsReq) {
        if (ObjectUtil.isEmpty(settingsReq.getType())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return ResponseVO.success(publicSettingsApi.getPublicSetting(settingsReq, CurrReqUtils.getOneId()));
    }
}
