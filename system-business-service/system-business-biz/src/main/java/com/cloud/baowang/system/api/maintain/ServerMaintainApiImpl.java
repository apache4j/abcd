package com.cloud.baowang.system.api.maintain;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.GoogleAuthUtil;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.maintain.ServerMaintainApi;
import com.cloud.baowang.system.api.vo.maintain.ServerMaintainChangeVO;
import com.cloud.baowang.system.api.vo.member.BusinessAdminDetailVO;
import com.cloud.baowang.system.service.member.BusinessAdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@AllArgsConstructor
@Slf4j
public class ServerMaintainApiImpl implements ServerMaintainApi {

    private final BusinessAdminService businessAdminService;

    @Override
    public ResponseVO<Void> change(ServerMaintainChangeVO vo) {
        BusinessAdminDetailVO adminDetailVO = businessAdminService.getAdminById(IdVO.builder().id(vo.getOperator()).build());
        boolean b = GoogleAuthUtil.checkCode(adminDetailVO.getGoogleAuthKey(), vo.getGoogleVerificationCode());
        if (!b) {
            throw new BaowangDefaultException(ResultCode.GOOGLE_AUTH_NO_PASS);
        }
        if (vo.getMaintainStatus().equals(CommonConstant.business_one)) {
            long time = vo.getMaintainEndTime() - vo.getMaintainBeginTime();
            // 用户端
           /* if (vo.getMaintainTerminal().contains(CommonConstant.business_zero)) {
                RedisUtil.setValue(RedisConstants.KEY_SERVER_MAINTAIN_USER_KEY, vo.getMaintainBeginTime() + CommonConstant.COLON + vo.getMaintainEndTime(), time / 1000);
            }
            // 代理端
            if (vo.getMaintainTerminal().contains(CommonConstant.business_one)) {
                RedisUtil.setValue(RedisConstants.KEY_SERVER_MAINTAIN_AGENT_KEY, vo.getMaintainBeginTime() + CommonConstant.COLON + vo.getMaintainEndTime(), time / 1000);
            }*/
        }
       // RedisUtil.setValue(RedisConstants.KEY_SERVER_MAINTAIN_INFO_KEY, vo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<ServerMaintainChangeVO> info() {
       /* ServerMaintainChangeVO vo = RedisUtil.getValue(RedisConstants.KEY_SERVER_MAINTAIN_SITE_KEY);
        if (Objects.nonNull(vo)) {
            return ResponseVO.success(vo);
        }
        return ResponseVO.success(new ServerMaintainChangeVO());*/
        return ResponseVO.success();
    }
}
