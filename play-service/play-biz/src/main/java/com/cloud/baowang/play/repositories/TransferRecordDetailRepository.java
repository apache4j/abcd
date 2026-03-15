package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.api.vo.transferRecordVO.DbSportTransferRecordDetailVO;
import com.cloud.baowang.play.po.DbSportTransferRecordDetailPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface TransferRecordDetailRepository extends BaseMapper<DbSportTransferRecordDetailPO> {

    /**
     * 查询出未处理的订单 熊猫体育
     */
    List<DbSportTransferRecordDetailVO> queryUnConfirmErrorOrder();

}
