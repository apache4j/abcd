package com.cloud.baowang.system.repositories.verify;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.api.vo.verify.SiteLinkCountVO;
import com.cloud.baowang.system.api.vo.verify.SiteLinkVO;
import com.cloud.baowang.system.po.verify.SmsSiteLinkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/07/31 15:43
 * @description:
 */
@Mapper
public interface SmsSiteLinkRepository extends BaseMapper<SmsSiteLinkPO> {
    List<SiteLinkCountVO> querySmsLinkCountChannel();
    List<SiteLinkVO> querySiteLinkVOBySiteCode(@Param("siteCode") String siteCode);
}
