package com.cloud.baowang.system.repositories.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.system.api.vo.member.BusinessLoginInfoVO;
import com.cloud.baowang.system.api.vo.member.UserLoginRequestVO;
import com.cloud.baowang.system.po.member.BusinessLoginInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 角色 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface BusinessLoginInfoRepository extends BaseMapper<BusinessLoginInfoPO> {

    Page<BusinessLoginInfoVO> queryBusinessLoginInfoPage(Page<BusinessLoginInfoPO> page, @Param("vo") UserLoginRequestVO dto);

}
