package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.po.UserAccountUpdateReviewPO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewReqVO;
import com.cloud.baowang.user.api.vo.UserAccountUpdateReviewVO.UserAccountUpdateReviewResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserAccountUpdateReviewRepository extends BaseMapper<UserAccountUpdateReviewPO> {
    Page<UserAccountUpdateReviewResVO> getReviewPage(Page<UserAccountUpdateReviewResVO> page, @Param("vo") UserAccountUpdateReviewReqVO vo,@Param("adminName")String adminName);
}
