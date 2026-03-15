package com.cloud.baowang.system.repositories.site.agreement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.system.po.site.config.UserAgreementPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAgreementRepository extends BaseMapper<UserAgreementPO> {
}
