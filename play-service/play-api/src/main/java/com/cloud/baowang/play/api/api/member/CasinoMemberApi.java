package com.cloud.baowang.play.api.api.member;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.ApiConstants;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.game.PublicSettingsReq;
import com.cloud.baowang.play.api.vo.game.PublicSettingsVO;
import com.cloud.baowang.play.api.vo.game.SportFollowDetailVO;
import com.cloud.baowang.play.api.vo.game.SportFollowReq;
import com.cloud.baowang.play.api.vo.game.SportFollowVO;
import com.cloud.baowang.play.api.vo.game.SportUnFollowReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "casinoMemberApi", value = ApiConstants.NAME)
@Tag(name = "游戏三方会员信息")
public interface CasinoMemberApi {

    String PREFIX = ApiConstants.PREFIX + "/casinoMember/api/";

    @Operation(summary = "获取三方会员信息")
    @PostMapping(PREFIX + "getCasinoMember")
    ResponseVO<CasinoMemberRespVO> getCasinoMember(@RequestBody CasinoMemberReqVO reqVO);

    @Operation(summary = "会员登出三方游戏")
    @PostMapping(PREFIX + "userLogOut")
    void userLogOut(@RequestParam("userId") String userId);
}
