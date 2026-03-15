package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.po.UserChangeTypeHistoryRecordPO;
import com.cloud.baowang.user.repositories.UserDetailsHistoryRemarkRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserDetailsHistoryRemarkService extends ServiceImpl<UserDetailsHistoryRemarkRepository, UserChangeTypeHistoryRecordPO> {


}
