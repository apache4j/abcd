package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.user.po.UserInfoPO;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cloud.baowang.common.core.utils.MD5Util;


@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class GenerateInviteService {

    private final UserInfoRepository userInfoRepository;


    public String generateInviteCode(String siteName) {
        String friendInviteCode = MD5Util.inviteCode(siteName);
        LambdaQueryWrapper<UserInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfoPO::getFriendInviteCode, friendInviteCode);
        UserInfoPO one = userInfoRepository.selectOne(queryWrapper);
        if (one == null) {
            return friendInviteCode;
        } else {
            return generateInviteCode(siteName);

        }
    }

}
