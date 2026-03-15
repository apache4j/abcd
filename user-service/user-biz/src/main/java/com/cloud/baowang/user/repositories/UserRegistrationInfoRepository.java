package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.po.UserRegistrationInfoPO;
import com.cloud.baowang.user.api.vo.user.UserRegistrationInfoResVO;
import com.cloud.baowang.user.api.vo.user.request.UserRegistrationInfoReqVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRegistrationInfoRepository extends BaseMapper<UserRegistrationInfoPO> {

    Page<UserRegistrationInfoResVO> getByPage(Page<UserRegistrationInfoResVO> page, @Param("vo") UserRegistrationInfoReqVO userRegistrationInfoReqVO);

    Long getTotalCount(@Param("vo") UserRegistrationInfoReqVO vo);
}
