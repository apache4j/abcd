package com.cloud.baowang.report.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.report.po.ReportUserWinLoseMessagePO;
import com.cloud.baowang.report.repositories.ReportUserWinLoseMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 会员每日盈亏-MQ消息体 服务类
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Slf4j
@Service
public class ReportUserWinLoseMqMessageService extends ServiceImpl<ReportUserWinLoseMessageRepository, ReportUserWinLoseMessagePO> {

}
