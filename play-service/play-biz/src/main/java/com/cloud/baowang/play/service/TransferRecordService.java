package com.cloud.baowang.play.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.play.game.base.GameBaseService;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserCoinAddVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.play.api.vo.transferRecordVO.TransferRecordResultVO;
import com.cloud.baowang.play.po.TransferRecordPO;
import com.cloud.baowang.play.repositories.TransferRecordRepository;
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TransferRecordService extends ServiceImpl<TransferRecordRepository, TransferRecordPO> {

    private final GameBaseService gameBaseService;


    public TransferRecordResultVO getTransferRecord(TransferRecordResultVO transferRecordVO) {
        TransferRecordPO po = baseMapper.selectOne(getWrapper(transferRecordVO));
        if (ObjectUtil.isNotEmpty(po)) {
            TransferRecordResultVO vo = TransferRecordResultVO.builder().build();
            BeanUtils.copyProperties(po, vo);
            return vo;
        }
        return null;
    }

    public List<TransferRecordResultVO> getTransferRecordList(TransferRecordResultVO req) {
        List<TransferRecordResultVO> resultList = Lists.newArrayList();
        List<TransferRecordPO> list = baseMapper.selectList(getWrapper(req));
        if (CollectionUtils.isNotEmpty(list)) {
            for (TransferRecordPO tmp : list) {
                TransferRecordResultVO vo = TransferRecordResultVO.builder().build();
                BeanUtils.copyProperties(tmp, vo);
                resultList.add(vo);
            }
        }
        return resultList;
    }


    private LambdaQueryWrapper<TransferRecordPO> getWrapper(TransferRecordResultVO transferRecordVO) {
        return Wrappers.<TransferRecordPO>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getVenueCode()), TransferRecordPO::getVenueCode, transferRecordVO.getVenueCode())
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getOrderId()), TransferRecordPO::getOrderId, transferRecordVO.getOrderId())
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getTransId()), TransferRecordPO::getTransId, transferRecordVO.getTransId())
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getBetId()), TransferRecordPO::getBetId, transferRecordVO.getBetId())
                .in(CollectionUtils.isNotEmpty(transferRecordVO.getBetIds()), TransferRecordPO::getBetId, transferRecordVO.getBetIds())
                .in(CollectionUtils.isNotEmpty(transferRecordVO.getOrderIds()), TransferRecordPO::getOrderId, transferRecordVO.getOrderIds())
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getUserAccount()), TransferRecordPO::getUserAccount, transferRecordVO.getUserAccount())
                .eq(ObjectUtil.isNotEmpty(transferRecordVO.getOrderStatus()), TransferRecordPO::getOrderStatus, transferRecordVO.getOrderStatus())
                .in(CollectionUtils.isNotEmpty(transferRecordVO.getOrderStatusIds()), TransferRecordPO::getOrderStatus, transferRecordVO.getOrderStatusIds())
                .ge(ObjectUtil.isNotEmpty(transferRecordVO.getCreateStartTime()), TransferRecordPO::getCreatedTime, transferRecordVO.getCreateStartTime())
                .le(ObjectUtil.isNotEmpty(transferRecordVO.getCreateEndTime()), TransferRecordPO::getCreatedTime, transferRecordVO.getCreateEndTime())
                .orderByDesc(TransferRecordPO::getCreatedTime);
    }


    public IPage<TransferRecordResultVO> getTransferRecordPage(IPage<TransferRecordPO> iPage, TransferRecordResultVO transferRecordVO) {
        IPage<TransferRecordPO> page = baseMapper.selectPage(iPage, getWrapper(transferRecordVO));
        return page.convert(x -> {
            TransferRecordResultVO vo = TransferRecordResultVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            return vo;
        });
    }

    public Boolean saveTransferRecord(TransferRecordResultVO transferRecordPO) {
        TransferRecordPO po = TransferRecordPO.builder().build();
        BeanUtils.copyProperties(transferRecordPO, po);
        po.setCreatedTime(System.currentTimeMillis());
        po.setUpdatedTime(System.currentTimeMillis());
        return baseMapper.insert(po) > 0;
    }

    public Boolean updateRecordStatus(TransferRecordResultVO transferRecordVO) {
        if (ObjectUtil.isEmpty(transferRecordVO.getOrderId())) {
            return false;
        }
        log.info("修改场馆转账记录:{},venueCode:{}", transferRecordVO.getOrderId(), transferRecordVO.getVenueCode());
        TransferRecordPO po = TransferRecordPO.builder()
                .orderStatus(transferRecordVO.getOrderStatus())
                .betId(transferRecordVO.getBetId())
                .remark(transferRecordVO.getRemark())
                .settleCount(transferRecordVO.getSettleCount())
                .build();
        po.setUpdatedTime(System.currentTimeMillis());
        int count = baseMapper.update(po, Wrappers.lambdaQuery(TransferRecordPO.class)
                .eq(TransferRecordPO::getOrderId, transferRecordVO.getOrderId())
                .eq(TransferRecordPO::getVenueCode, transferRecordVO.getVenueCode()));
        log.info("修改场馆转账记录:{},count:{}", transferRecordVO.getOrderId(), count);
        return count > 0;
    }


    @DistributedLock(name = RedisConstants.SABA_COIN_LOCK, unique = "#transferRecordVO.orderId", waitTime = 2, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public CoinRecordResultVO addTransferRecordCoin(TransferRecordResultVO transferRecordVO, UserCoinAddVO userCoinAddVO) {
        TransferRecordResultVO transferRecordPO = getTransferRecord(TransferRecordResultVO.builder().orderId(transferRecordVO.getOrderId()).build());
        boolean transferResult;
        if (ObjectUtils.isEmpty(transferRecordPO)) {
            transferResult = saveTransferRecord(transferRecordVO);
            log.info("场馆转账记录插入:{},结果:{}", transferRecordVO, transferResult);
        } else {
            transferResult = updateRecordStatus(transferRecordVO);
            log.info("场馆转账记录修改:{},结果:{}", transferRecordVO, transferResult);
        }

        if (!transferResult) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        CoinRecordResultVO recordResultVO = gameBaseService.toUserCoinHandle(userCoinAddVO);
        if (ObjectUtil.isEmpty(recordResultVO) || !recordResultVO.getResult()) {
            log.info("调用扣费失败,param:{},result:{},transferRecordVO:{}", userCoinAddVO, recordResultVO, transferRecordVO);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        return recordResultVO;
    }


}
