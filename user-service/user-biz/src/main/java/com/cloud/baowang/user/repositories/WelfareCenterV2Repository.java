package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardPageQueryVO;
import com.cloud.baowang.user.api.vo.welfarecenter.WelfareCenterRewardRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WelfareCenterV2Repository {
    List<WelfareCenterRewardRespVO> getList(@Param("vo") WelfareCenterRewardPageQueryVO queryVO);

    Page<WelfareCenterRewardRespVO> pageQuery(Page<WelfareCenterRewardRespVO> page, @Param("vo") WelfareCenterRewardPageQueryVO queryVO);

    WelfareCenterRewardRespVO detail(@Param("vo") WelfareCenterRewardPageQueryVO queryVO);

    Integer getWaitReceiveByUserId(@Param("siteCode") String siteCode,
                                   @Param("userAccount")String userAccount);
}
