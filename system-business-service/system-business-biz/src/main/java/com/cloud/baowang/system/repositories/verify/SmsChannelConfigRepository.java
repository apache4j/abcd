package com.cloud.baowang.system.repositories.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.ChannelSendingStatisticPO;
import com.cloud.baowang.system.po.verify.SmsChannelConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:43
 * @description:
 */
@Mapper
public interface SmsChannelConfigRepository extends BaseMapper<SmsChannelConfigPO> {
    Page<SiteSmsChannelPageVO> querySmsChannel(@Param("page") Page<SmsChannelConfigPO> page,
                                               @Param("vo") SmsChannelQueryVO reqVO);
    SmsChannelConfigVO querySiteChannel(@Param("vo") VerifyCodeSendVO verifyCodeSendVO);

    Page<SiteBackSmsChannelConfigPageVO> getSiteSmsConfigPage(@Param("page") Page<SmsChannelConfigPO> page,
                                                          @Param("vo") SmsChannelQueryVO reqVO);

    Page<ChannelSendingStatisticVO> queryChannelStatistic(@Param("page") Page<ChannelSendingStatisticPO> page, @Param("vo") ChannelSendStatisticQueryVO queryVO);

    Long queryChannelStatisticCount(@Param("vo") ChannelSendStatisticQueryVO queryVO);

    List<SMSAuthorVO> smsAuthorList(@Param("id") String id);
}
