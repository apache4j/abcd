package com.cloud.baowang.wallet.repositories.vipV2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.wallet.api.vo.rebate.VIPAwardPageVO;
import com.cloud.baowang.wallet.api.vo.rebate.VIPAwardQueryVO;
import com.cloud.baowang.wallet.po.VIPAwardRecordPO;
import com.cloud.baowang.wallet.po.vipV2.VIPAwardRecordV2PO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VIPAwardRecordV2Repository extends BaseMapper<VIPAwardRecordV2PO> {
    Page<VIPAwardPageVO> selectVIPAwardPage(Page<VIPAwardRecordPO> page, @Param("vo") VIPAwardQueryVO vo);

    List<String> selectUpgradeAward(
            @Param("vo") VIPChangeRequestVO vo,
            @Param("dayStart") long dayStart,
            @Param("dayEnd")long dayEnd);

    List<String> selectCanReceiveAccounts();

    Long selectVIPAwardPageCount( @Param("vo") VIPAwardQueryVO vo);
}
