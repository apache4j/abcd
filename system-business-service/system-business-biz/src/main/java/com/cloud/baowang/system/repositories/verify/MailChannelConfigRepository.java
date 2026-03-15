package com.cloud.baowang.system.repositories.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.MailChannelConfigPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:43
 * @description:
 */
@Mapper
public interface MailChannelConfigRepository extends BaseMapper<MailChannelConfigPO> {
    Page<SiteEmailChannelPageVO> queryEmailChannel(@Param("page") Page<MailChannelConfigPO> page,
                                                   @Param("vo") MailChannelQueryVO reqVO);

    MailChannelConfigVO querySiteChannel(@Param("siteCode") String siteCode);

    Page<SiteBackEmailChannelPageVO> getSiteMailConfigPage(@Param("page") Page<MailChannelConfigPO> page,
                                                           @Param("vo") MailChannelQueryVO reqVO);

    List<SMSAuthorVO>  mailAuthorList(@Param("id") String id);
}
