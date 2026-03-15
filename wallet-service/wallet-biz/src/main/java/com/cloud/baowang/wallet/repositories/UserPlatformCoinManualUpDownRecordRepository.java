package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordPageVO;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpReviewPageVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinManualUpDownRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 会员平台币上下分 Mapper 接口
 *
 * @author qiqi
 */
@Mapper
public interface UserPlatformCoinManualUpDownRecordRepository extends BaseMapper<UserPlatformCoinManualUpDownRecordPO> {


    Page<UserPlatformCoinManualUpDownRecordPO> selectUpReviewPage(Page<UserPlatformCoinManualUpDownRecordPO> page, @Param("vo") UserPlatformCoinManualUpReviewPageVO vo);

    Long getPageCount(@Param("vo") UserPlatformCoinManualUpRecordPageVO vo);

    BigDecimal getPlatManualUpDownAmount(@Param("userId") String userId);
}
