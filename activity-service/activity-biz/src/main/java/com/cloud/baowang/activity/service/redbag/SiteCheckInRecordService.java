package com.cloud.baowang.activity.service.redbag;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.po.SiteCheckInRecordPO;
import com.cloud.baowang.activity.repositories.SiteCheckInRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class SiteCheckInRecordService extends ServiceImpl<SiteCheckInRecordRepository, SiteCheckInRecordPO> {

    private final SiteCheckInRecordRepository siteCheckInRecordRepository;
}
