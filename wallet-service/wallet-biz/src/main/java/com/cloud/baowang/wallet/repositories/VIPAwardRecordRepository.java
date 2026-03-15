package com.cloud.baowang.wallet.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.vip.VIPChangeRequestVO;
import com.cloud.baowang.wallet.api.vo.rebate.VIPAwardPageVO;
import com.cloud.baowang.wallet.api.vo.rebate.VIPAwardQueryVO;
import com.cloud.baowang.wallet.po.VIPAwardRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VIPAwardRecordRepository extends BaseMapper<VIPAwardRecordPO> {
    Page<VIPAwardPageVO> selectVIPAwardPage(Page<VIPAwardRecordPO> page, @Param("vo") VIPAwardQueryVO vo);

    List<String> selectUpgradeAward(
            @Param("vo") VIPChangeRequestVO vo,
            @Param("dayStart") long dayStart,
            @Param("dayEnd")long dayEnd);

    List<String> selectCanReceiveAccounts();

    Long selectVIPAwardPageCount( @Param("vo") VIPAwardQueryVO vo);
}
