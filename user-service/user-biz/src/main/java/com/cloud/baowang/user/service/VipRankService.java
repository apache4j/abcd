package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.po.VipRankPO;
import com.cloud.baowang.user.repositories.VipRankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VipRankService extends ServiceImpl<VipRankRepository, VipRankPO> {

}
