package com.cloud.baowang.account.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.account.po.UserCoinRecordPO;
import org.apache.ibatis.annotations.Mapper;


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
  /*  UserCoinRecordVO sumUserCoinRecord(@Param("vo") UserCoinRecordRequestVO vo);

    Page<UserDividendPageVO> userDividendPage(Page<UserDividendPageVO> page, @Param("vo") UserDividendRequestVO requestVO);

    Long callFriendRechargeCount(@Param("vo") UserCoinRecordCallFriendsRequestVO requestVO);

    List<String> getOrderNoByOrders(@Param("orders") List<String> orders);

    Page<WinLoseRecalculateWalletVO> winLoseRecalculateMainPage(Page<Object> objectPage, WinLoseRecalculateReqWalletVO vo);*/

  /*  Page<UserCoinRecordDetailVO> listUserCoinDetail(@Param("startTime") long startTime, @Param("endTime") long endTime,
                                                    @Param("vo") UserCoinRecordDetailPageVO vo, @Param("page") Page page);*/
}
