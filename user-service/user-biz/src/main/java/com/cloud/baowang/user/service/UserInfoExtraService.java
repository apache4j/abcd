package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.user.api.vo.user.UserInfoExtraUpdateAmountVO;
import com.cloud.baowang.user.po.UserInfoExtraPO;
import com.cloud.baowang.user.repositories.UserInfoExtraRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.Objects;



/**
 * 会员额外信息服务
 **/
@Service
@AllArgsConstructor
@Slf4j
public class UserInfoExtraService extends ServiceImpl<UserInfoExtraRepository, UserInfoExtraPO> {



    @Transactional(rollbackFor = Exception.class)
    @DistributedLock(name = RedisConstants.USER_EXTRA_UPDATE_KEY, unique = "#vo.userId", waitTime = 3, leaseTime = 180)
    public UserInfoExtraPO updateValidAmount(UserInfoExtraUpdateAmountVO vo) {
        UserInfoExtraPO userInfoExtraPO = getBaseMapper().selectOne(Wrappers.<UserInfoExtraPO>lambdaQuery().eq(UserInfoExtraPO::getUserId, vo.getUserId()).eq(UserInfoExtraPO::getSiteCode, vo.getSiteCode()));
        if (Objects.isNull(userInfoExtraPO)){
            userInfoExtraPO = new UserInfoExtraPO();
            BeanUtils.copyProperties(vo,userInfoExtraPO);
            userInfoExtraPO.setTotalValidAmount(vo.getValidAmount());
            userInfoExtraPO.setTotalWinLoseAmount(vo.getWinLoseAmount());
            save(userInfoExtraPO);
        }else {
            BigDecimal validAmount=userInfoExtraPO.getTotalValidAmount()==null?BigDecimal.ZERO:userInfoExtraPO.getTotalValidAmount();
            BigDecimal totalWinLoseAmount=userInfoExtraPO.getTotalWinLoseAmount()==null?BigDecimal.ZERO:userInfoExtraPO.getTotalWinLoseAmount();
            userInfoExtraPO.setTotalValidAmount(validAmount.add(vo.getValidAmount()));
            userInfoExtraPO.setTotalWinLoseAmount(totalWinLoseAmount.add(vo.getWinLoseAmount()));
            updateById(userInfoExtraPO);
        }
        return userInfoExtraPO;
    }

}
