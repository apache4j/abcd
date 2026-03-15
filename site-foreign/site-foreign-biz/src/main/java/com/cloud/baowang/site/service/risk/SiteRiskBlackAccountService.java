package com.cloud.baowang.site.service.risk;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.auth.util.UserAuthUtil;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.JwtUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.site.service.SiteTokenService;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.site.SiteRiskCtrlBlackApi;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountAddVO;
import com.cloud.baowang.system.api.vo.risk.RiskBlackAccountVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountReqVO;
import com.cloud.baowang.user.api.vo.RiskUserBlackAccountVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SiteRiskBlackAccountService {

    private final SystemParamApi systemParamApi;
    private final SiteRiskCtrlBlackApi siteRiskCtrlBlackApi;

    private final UserInfoApi userInfoApi;
    private final SiteTokenService siteTokenService;

    public ResponseVO<Boolean> addBlackAccount(RiskBlackAccountAddVO addVO) {

        RiskBlackAccountVO vo = new RiskBlackAccountVO();
        BeanUtils.copyProperties(addVO, vo);
        vo.setCreator(CurrReqUtils.getAccount());
        vo.setCreatorName(CurrReqUtils.getAccount());
        vo.setCreatedTime(System.currentTimeMillis());
        vo.setSiteCode(addVO.getSiteCode());
        ResponseVO<Boolean> responseVO =  siteRiskCtrlBlackApi.addBlackAccount(vo);
        if (responseVO.isOk()) {
            //强制下线
            ResponseVO<Boolean> kickOutRO = kickOut(vo);
            if (!kickOutRO.isOk()) {
                return kickOutRO;
            }
        }
        return responseVO;
    }

    @Nullable
    private ResponseVO<Boolean> kickOut(RiskBlackAccountVO vo) {
        final String siteCode = vo.getSiteCode();
        String typeCode = vo.getRiskControlTypeCode();
        String account = vo.getRiskControlAccount();
        RiskUserBlackAccountReqVO requestVO = new RiskUserBlackAccountReqVO();
        requestVO.setSiteCode(siteCode);
        // "风控类型code 1-注册ip，2登录ip，3-注册设备，4-登录设备，5-银行卡，6-电子钱包，7-虚拟币"
        requestVO.setRiskControlTypeCode(typeCode);
        // 黑名单账号(有可能IP如：1.1.1.3，也有可能是IP段如：1.1.1.1～1.1.1.100)
        requestVO.setRiskControlAccount(account);
        // 是否IP段
        requestVO.setIpSegmentFlag(vo.getIpSegmentFlag());
        // IP段开始
        requestVO.setIpStart(vo.getIpStart());
        // IP段结束
        requestVO.setIpEnd(vo.getIpEnd());

        ResponseVO<List<UserInfoVO>> rAccounts = userInfoApi.getAllUserIdByRiskBlack(requestVO);
        if (!rAccounts.isOk()){
            log.error("获取所有用户失败，{}",rAccounts.getMessage());
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        List<UserInfoVO> list = rAccounts.getData();

        if (!list.isEmpty()) {
            list.forEach(userInfoVO->{
                //判断之前是否有登录，有登录的话要清空之前的token信息
                String tokenByUserId = getTokenByUserId(siteCode, userInfoVO.getUserId());
                if (!ObjectUtils.isEmpty(tokenByUserId)) {
                    delLoginUser(tokenByUserId);
                    log.info("站点风控黑名单触发,站点:{},会员:{}已被踢出",siteCode,userInfoVO.getUserAccount());
                }
            });
        }
        return ResponseVO.success(true);
    }

    public String getTokenByUserId(String siteCode,String userId) {
        return RedisUtil.getValue(UserAuthUtil.getJwtKey(siteCode,userId));
    }

    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String token) {
        if (!ObjectUtils.isEmpty(token)) {
            String userKey = JwtUtil.getUserKey(token);
            String userId = JwtUtil.getUserId(token);
            String siteCode = JwtUtil.getSiteCode(token);
            RedisUtil.deleteKey(getTokenKey(siteCode,userKey));
            RedisUtil.deleteKey(UserAuthUtil.getJwtKey(siteCode,userId));
        }

    }

    private String getTokenKey(String siteCode,String tokenUUId) {
        return UserAuthUtil.getTokenKey(siteCode,tokenUUId);
    }


    public ResponseVO<Boolean> updateBlackAccount(RiskBlackAccountVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());

        ResponseVO<Boolean> updateRO = siteRiskCtrlBlackApi.updateBlackAccount(vo);
        if (!updateRO.isOk()){
            return updateRO;
        }
        ResponseVO<Boolean> kickOutVO = kickOut(vo);
        if(!kickOutVO.isOk()){
            return kickOutVO;
        }
        return updateRO;
    }

    public ResponseVO<Page<RiskUserBlackAccountVO>> getRiskUserBlackListPage(RiskUserBlackAccountReqVO reqVO) {
        return userInfoApi.getRiskUserBlackListPage(reqVO);
    }
}
