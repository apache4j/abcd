package com.cloud.baowang.system.repositories.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.api.vo.verify.SiteLinkCountVO;
import com.cloud.baowang.system.api.vo.verify.SiteLinkVO;
import com.cloud.baowang.system.po.verify.MailSiteLinkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:43
 * @description:
 */
@Mapper
public interface MailSiteLinkRepository extends BaseMapper<MailSiteLinkPO> {
    List<SiteLinkCountVO> queryEmailLinkCountChannel();
    List<SiteLinkVO> querySiteLinkVOBySiteCode(@Param("siteCode") String siteCode);
}
