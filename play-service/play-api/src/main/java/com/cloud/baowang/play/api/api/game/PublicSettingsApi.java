package com.cloud.baowang.play.api.api.game;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.game.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "publicSettingApi", value = ApiConstants.NAME)
@Tag(name = "公共参数设置")
public interface PublicSettingsApi {

    String PREFIX = ApiConstants.PREFIX + "/publicSetting/api/";

    @Operation(summary = "添加公共配置")
    @PostMapping(PREFIX + "saveSetting")
    ResponseVO<Boolean> saveSetting(@RequestBody PublicSettingsReq vo, @RequestParam("userId") String userId);

    @Operation(summary = "添加体育关注")
    @PostMapping(PREFIX + "saveFollow")
    ResponseVO<Boolean> saveFollow(@RequestBody SportFollowReq vo, @RequestParam("userId") String userId);

    @Operation(summary = "取消体育关注")
    @PostMapping(PREFIX + "unFollow")
    ResponseVO<Boolean> unFollow(@RequestBody SportUnFollowReq vo, @RequestParam("userId") String userId);


    @Operation(summary = "取消体育关注-皮肤2")
    @PostMapping(PREFIX + "unFollowList")
    ResponseVO<Boolean> unFollowList(@RequestBody SportUnFollowListReq vo, @RequestParam("userId") String userId);


    @Operation(summary = "查询体育关注列表")
    @PostMapping(PREFIX + "getSportsFollowList")
    List<SportFollowVO> getSportsFollowList(@RequestBody SportFollowReq req, @RequestParam("userId") String userId);

    @Operation(summary = "查询体育关注列表")
    @PostMapping(PREFIX + "getSportsFollowMap")
    List<SportFollowDetailVO> getSportsFollowMap(@RequestParam("userId") String userId);

    @Operation(summary = "查询公共配置")
    @PostMapping(PREFIX + "getPublicSetting")
    List<PublicSettingsVO> getPublicSetting(@RequestBody PublicSettingsReq settingsReq, @RequestParam("userId") String userId);

}
