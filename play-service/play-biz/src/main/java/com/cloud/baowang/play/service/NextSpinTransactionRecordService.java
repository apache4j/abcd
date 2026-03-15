package com.cloud.baowang.play.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.play.api.vo.nextSpin.NextSpinTransactionRecordVO;
import com.cloud.baowang.play.po.NextSpinTransactionRecordPO;
import com.cloud.baowang.play.repositories.NextSpinTransactionRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: sheldon
 * @Date: 3/29/24 6:31 下午
 */

@Slf4j
@Service
@AllArgsConstructor
public class NextSpinTransactionRecordService extends ServiceImpl<NextSpinTransactionRecordRepository, NextSpinTransactionRecordPO> {

    public void insert(NextSpinTransactionRecordVO vo){
        LambdaQueryWrapper<NextSpinTransactionRecordPO> query = Wrappers.lambdaQuery();
        query.eq(NextSpinTransactionRecordPO::getTransferId, vo.getTransferId());
        NextSpinTransactionRecordPO checkData=  this.getBaseMapper().selectOne(query);
        if (ObjectUtils.isEmpty(checkData)){
            NextSpinTransactionRecordPO po=new NextSpinTransactionRecordPO();
            BeanUtils.copyProperties(vo,po);
            po.setCreatedTime(System.currentTimeMillis());
            this.getBaseMapper().insert(po);
        }
    }


    public NextSpinTransactionRecordVO getByTransferId(String transferId){
        LambdaQueryWrapper<NextSpinTransactionRecordPO> query = Wrappers.lambdaQuery();
        query.eq(NextSpinTransactionRecordPO::getTransferId, transferId);
        NextSpinTransactionRecordPO req=  this.getBaseMapper().selectOne(query);
        if (Objects.nonNull(req)) {
            NextSpinTransactionRecordVO vo = new NextSpinTransactionRecordVO();
            BeanUtils.copyProperties(req, vo);
            return vo;
        }
        return null;
    }
}
