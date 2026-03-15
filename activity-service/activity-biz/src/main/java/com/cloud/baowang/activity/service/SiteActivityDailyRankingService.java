package com.cloud.baowang.activity.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.po.SiteActivityDailyRankingPO;
import com.cloud.baowang.activity.repositories.SiteActivityDailyRankingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class SiteActivityDailyRankingService extends
        ServiceImpl<SiteActivityDailyRankingRepository, SiteActivityDailyRankingPO> {
}
