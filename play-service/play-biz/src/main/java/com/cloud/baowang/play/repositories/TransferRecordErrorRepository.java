package com.cloud.baowang.play.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cloud.baowang.play.po.TransferRecordErrorPO;
import com.cloud.baowang.play.po.TransferRecordPO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface TransferRecordErrorRepository extends BaseMapper<TransferRecordErrorPO> {

}
