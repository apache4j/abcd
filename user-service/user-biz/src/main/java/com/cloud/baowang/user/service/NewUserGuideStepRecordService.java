package com.cloud.baowang.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.user.po.SiteNewUserGuideStepRecordPO;
import com.cloud.baowang.user.repositories.SiteNewUserGuideStepRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @className: NewUserGuideStepRecordService
 * @author: wade
 * @description: 实现类
 * @date: 16/4/25 18:26
 */
@Service
@AllArgsConstructor
@Slf4j
public class NewUserGuideStepRecordService extends ServiceImpl<SiteNewUserGuideStepRecordRepository, SiteNewUserGuideStepRecordPO> {
}
