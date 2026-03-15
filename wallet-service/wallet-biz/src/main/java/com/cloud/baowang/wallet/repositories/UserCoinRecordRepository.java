package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateReqWalletVO;
import com.cloud.baowang.wallet.api.vo.report.WinLoseRecalculateWalletVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordCallFriendsRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserCoinRecordVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendPageVO;
import com.cloud.baowang.wallet.api.vo.userDividend.UserDividendRequestVO;
import com.cloud.baowang.wallet.po.UserCoinRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author qiqi
 */
@Mapper
public interface UserCoinRecordRepository extends BaseMapper<UserCoinRecordPO> {

    /**
     * 会员账变记录总计
     *
     * @param vo
     * @return
     */
    UserCoinRecordVO sumUserCoinRecord(@Param("vo") UserCoinRecordRequestVO vo);

    Page<UserDividendPageVO> userDividendPage(Page<UserDividendPageVO> page, @Param("vo") UserDividendRequestVO requestVO);

    Long callFriendRechargeCount(@Param("vo") UserCoinRecordCallFriendsRequestVO requestVO);

    List<String> getOrderNoByOrders(@Param("orders") List<String> orders);

    Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(Page<Object> objectPage, WinLoseRecalculateReqWalletVO vo);

  /*  Page<UserCoinRecordDetailVO> listUserCoinDetail(@Param("startTime") long startTime, @Param("endTime") long endTime,
                                                    @Param("vo") UserCoinRecordDetailPageVO vo, @Param("page") Page page);*/
}
