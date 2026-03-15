package com.cloud.baowang.play.api.casino;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.member.CasinoMemberApi;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberReqVO;
import com.cloud.baowang.play.api.vo.casinoMember.CasinoMemberRespVO;
import com.cloud.baowang.play.api.vo.order.CasinoMemberVO;
import com.cloud.baowang.play.game.base.GameLogOutService;
import com.cloud.baowang.play.service.CasinoMemberService;
import com.cloud.baowang.play.vo.casinomember.CasinoMemberReq;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class CasinoMemberApiImpl implements CasinoMemberApi {

    private final CasinoMemberService casinoMemberService;

    private final GameLogOutService gameLogOutService;

    @Override
    public ResponseVO<CasinoMemberRespVO> getCasinoMember(CasinoMemberReqVO reqVO) {
        CasinoMemberReq casinoMemberReq = BeanUtil.toBean(reqVO, CasinoMemberReq.class);
        CasinoMemberVO casinoMember = casinoMemberService.getCasinoMember(casinoMemberReq);
        if (casinoMember == null){
            return ResponseVO.fail(ResultCode.USER_NOT_EXIST);
        }
        CasinoMemberRespVO respVO = BeanUtil.toBean(casinoMember, CasinoMemberRespVO.class);
        return ResponseVO.success(respVO);
    }

    @Override
    public void userLogOut(String userId) {
        gameLogOutService.userLogOut(userId);
    }
}
