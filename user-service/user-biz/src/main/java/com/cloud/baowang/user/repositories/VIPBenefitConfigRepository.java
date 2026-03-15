package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.user.po.SiteVIPBenefitPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author 小智
 * @Date 4/5/23 2:16 PM
 * @Version 1.0
 */
@Mapper
public interface VIPBenefitConfigRepository extends BaseMapper<SiteVIPBenefitPO> {
}
