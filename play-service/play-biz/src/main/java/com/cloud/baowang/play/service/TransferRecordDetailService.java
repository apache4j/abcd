package com.cloud.baowang.play.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.api.vo.transferRecordVO.DbSportTransferRecordDetailVO;
import com.cloud.baowang.play.po.DbSportTransferRecordDetailPO;
import com.cloud.baowang.play.repositories.TransferRecordDetailRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TransferRecordDetailService extends ServiceImpl<TransferRecordDetailRepository, DbSportTransferRecordDetailPO> {


    public List<DbSportTransferRecordDetailVO> queryUnConfirmErrorOrder(){
        return baseMapper.queryUnConfirmErrorOrder();
    }

}
