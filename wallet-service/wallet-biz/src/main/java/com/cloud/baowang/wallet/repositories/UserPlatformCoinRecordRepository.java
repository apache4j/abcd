package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserPlatformCoinRecordVO;
import com.cloud.baowang.wallet.po.UserPlatformCoinRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * @author qiqi
 */
@Mapper
public interface UserPlatformCoinRecordRepository extends BaseMapper<UserPlatformCoinRecordPO> {


    /**
     * 会员账变记录总计
     * @param vo
     * @return
     */
    UserPlatformCoinRecordVO sumUserPlatformCoinRecord(@Param("vo") UserPlatformCoinRecordRequestVO vo);

    Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(Page<Object> objectPage, WinLoseRecalculateReqWalletVO vo);
}
