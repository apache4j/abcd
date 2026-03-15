package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.user.request.UserCheckExistReqVO;
import com.cloud.baowang.user.po.UserReviewPO;
import com.cloud.baowang.user.api.vo.UserReviewPageVO;
import com.cloud.baowang.user.api.vo.UserReviewResponseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员审核 Mapper 接口
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Mapper
public interface UserReviewRepository extends BaseMapper<UserReviewPO> {

    Page<UserReviewResponseVO> getReviewPage(Page<UserReviewResponseVO> page,
                                             @Param("vo") UserReviewPageVO vo,
                                             @Param("adminName") String adminName);

    Long getTotalCount(@Param("vo") UserReviewPageVO vo,
                       @Param("adminName") String adminName);
    UserReviewPO getByUserAccount(@Param("userAccount") String userAccount,@Param("siteCode") String siteCode);

    UserReviewPO findUserReviewExist(@Param("vo") UserCheckExistReqVO vo);
}