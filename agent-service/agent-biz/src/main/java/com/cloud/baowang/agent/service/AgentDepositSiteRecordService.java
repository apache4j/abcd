package com.cloud.baowang.agent.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogPageVO;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentDistributeLogReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentDepositSubordinatesPageReqVo;
import com.cloud.baowang.agent.api.vo.depositWithdraw.*;
import com.cloud.baowang.agent.api.vo.user.AgentComprehensiveReportVO;
import com.cloud.baowang.agent.po.AgentDepositSubordinatesPO;
import com.cloud.baowang.agent.repositories.AgentDepositSubordinatesRepository;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.agent.api.vo.user.AgentStoredMemberVO;
import com.cloud.baowang.user.api.vo.user.request.ComprehensiveReportVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineReqVO;
import com.cloud.baowang.wallet.api.vo.agent.WalletAgentSubLineResVO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 代理代存记录 服务实现类
 * </p>
 *
 * @author qiqi
 * @since 2023-10-24
 */
@Service
public class AgentDepositSiteRecordService extends ServiceImpl<AgentDepositSubordinatesRepository, AgentDepositSubordinatesPO> {

    @Autowired
    private AgentDepositSubordinatesRepository agentDepositSubordinatesRepository;

    /**
     * 倒出查询
     *
     * @param vo
     * @return
     */
    public Page<AgentDepositSubordinatesListPageResVO> doExportQuery(AgentDepositSiteRecordPageVO vo) {
        Page<AgentDepositSubordinatesPO> pageVO = new Page<>(vo.getPageNumber(), vo.getPageSize());
        LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositSubordinatesPO::getSiteCode, CurrReqUtils.getSiteCode());
        lqw.eq(StringUtils.isNotBlank(vo.getOrderNo()), AgentDepositSubordinatesPO::getOrderNo, vo.getOrderNo());
        lqw.eq(StringUtils.isNotBlank(vo.getAgentAccount()), AgentDepositSubordinatesPO::getAgentAccount, vo.getAgentAccount());
        lqw.ge(AgentDepositSubordinatesPO::getDepositTime,vo.getStartTime());
        lqw.le(AgentDepositSubordinatesPO::getDepositTime,vo.getEndTime());
        lqw.ge(!ObjectUtils.isEmpty(vo.getAmountMin()), AgentDepositSubordinatesPO::getAmount, vo.getAmountMin());
        lqw.le(!ObjectUtils.isEmpty(vo.getAmountMax()), AgentDepositSubordinatesPO::getAmount, vo.getAmountMax());
        if (StringUtils.isNotBlank(vo.getDepositSubordinatesType())) {
            lqw.eq(AgentDepositSubordinatesPO::getDepositSubordinatesType, vo.getDepositSubordinatesType());
        }
        if (StringUtils.isNotBlank(vo.getOrderField()) && StringUtils.isNotBlank(vo.getOrderType())) {
            if ("depositTime".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), AgentDepositSubordinatesPO::getDepositTime);
            }
            if ("amount".equals(vo.getOrderField())) {
                lqw.orderBy(true, vo.getOrderType().equals("asc"), AgentDepositSubordinatesPO::getAmount);
            }
        } else {
            lqw.orderByDesc(AgentDepositSubordinatesPO::getDepositTime).orderByDesc(AgentDepositSubordinatesPO::getAmount);
        }
        pageVO = agentDepositSubordinatesRepository.selectPage(pageVO, lqw);

        return ConvertUtil.toConverPage(pageVO.convert(item -> {
            AgentDepositSubordinatesListPageResVO record = BeanUtil.copyProperties(item, AgentDepositSubordinatesListPageResVO.class);
            record.setDepositSubordinatesType(AgentCoinRecordTypeEnum.AgentDepositSubordinatesTypeEnum.nameOfCode(item.getDepositSubordinatesType()).getName());
            if (ObjectUtil.isNotEmpty(record.getDepositTime())) {
                record.setDepositTimeStr(
                        DateUtil.format(new Date(record.getDepositTime()), DatePattern.NORM_DATETIME_PATTERN));
            }
            return record;
        }));
    }

    public ResponseVO<AgentDepositSubordinatesPageResVO> depositOfSubordinatesRecord(AgentDepositSiteRecordPageVO vo) {
        AgentDepositSubordinatesPageResVO resVO = new AgentDepositSubordinatesPageResVO();
        Page<AgentDepositSubordinatesListPageResVO> page = doExportQuery(vo);
        resVO.setPageList(page);
        if (!vo.getExportFlag()) {
            //小计
            BigDecimal orderSubTotal = BigDecimal.ZERO;
            for (AgentDepositSubordinatesListPageResVO pageResVO : page.getRecords()) {
                if (!ObjectUtils.isEmpty(pageResVO.getAmount())) {
                    orderSubTotal = orderSubTotal.add(pageResVO.getAmount());
                }
            }
            AgentDepositSubordinatesListPageResVO orderSubTotalVo = new AgentDepositSubordinatesListPageResVO();
            orderSubTotalVo.setAmount(orderSubTotal.setScale(2, RoundingMode.HALF_UP));
            resVO.setCurrentPage(orderSubTotalVo);

            //总计
            AgentDepositSubordinatesListPageResVO totalVo = new AgentDepositSubordinatesListPageResVO();
            AgentDepositSubordinatesPO agentDepositSubordinatesPO = agentDepositSubordinatesRepository.queryDepositOfSubordinatesAmountTotal(vo);
            if (ObjectUtil.isNotEmpty(agentDepositSubordinatesPO)) {
                BigDecimal amount = agentDepositSubordinatesPO.getAmount();
                totalVo.setAmount(ObjectUtil.isNotEmpty(amount) ? amount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            }
            resVO.setTotalPage(totalVo);
        }
        return ResponseVO.success(resVO);
    }

    public ResponseVO<Long> depositOfSubordinatesRecordExportCount(AgentDepositSiteRecordPageVO vo) {
        return ResponseVO.success(agentDepositSubordinatesRepository.depositOfSubordinatesRecordExportCount(vo));
    }



    public List<AgentDepositOfSubordinatesResVO> depositOfSubordinatesList(AgentDepositOfSubordinatesReqVO vo) {
        LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(vo.getUserAccount()), AgentDepositSubordinatesPO::getUserAccount, vo.getUserAccount());
        lqw.ge(null != vo.getStartTime(), AgentDepositSubordinatesPO::getCreatedTime, vo.getStartTime());
        lqw.le(null != vo.getEndTime(), AgentDepositSubordinatesPO::getCreatedTime, vo.getEndTime());
        List<AgentDepositSubordinatesPO> agentDepositSubordinatesPOS = this.baseMapper.selectList(lqw);

        List<AgentDepositOfSubordinatesResVO> list = ConvertUtil.entityListToModelList(agentDepositSubordinatesPOS, AgentDepositOfSubordinatesResVO.class);
        return list;
    }

    public List<AgentDepositOfSubordinatesResVO> depositSubordinatesByTime(Long startTime, Long endTime) {
        List<AgentDepositSubordinatesPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .ge(AgentDepositSubordinatesPO::getDepositTime, startTime)
                .lt(AgentDepositSubordinatesPO::getDepositTime, endTime)
                .list();
        List<AgentDepositOfSubordinatesResVO> result = Lists.newArrayList();
        if (CollUtil.isNotEmpty(list)) {
            result = ConvertUtil.entityListToModelList(list, AgentDepositOfSubordinatesResVO.class);
        }
        return result;
    }

    public Page<AgentDistributeLogPageVO> distributeLog(AgentDistributeLogReqVO vo) {
        return agentDepositSubordinatesRepository.distributeLog(new Page<>(vo.getPageNumber(), vo.getPageSize()), vo);
    }

    public List<WalletAgentSubLineResVO> depositSubordinatesByAgentList(WalletAgentSubLineReqVO reqVO) {
        return agentDepositSubordinatesRepository.depositSubordinatesByAgentList(reqVO);
    }


    public List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserAccount(String siteCode, String userAccount) {
        LambdaQueryWrapper<AgentDepositSubordinatesPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositSubordinatesPO::getSiteCode, siteCode).eq(AgentDepositSubordinatesPO::getUserAccount, userAccount);
        List<AgentDepositSubordinatesPO> list = this.list(query);
        return BeanUtil.copyToList(list, AgentDepositOfSubordinatesResVO.class);
    }
    public List<AgentDepositOfSubordinatesResVO> getAgentDepositAmountByUserId(String userId) {
        LambdaQueryWrapper<AgentDepositSubordinatesPO> query = Wrappers.lambdaQuery();
        query.eq(AgentDepositSubordinatesPO::getUserId, userId);
        List<AgentDepositSubordinatesPO> list = this.list(query);
        return BeanUtil.copyToList(list, AgentDepositOfSubordinatesResVO.class);
    }

    public AgentDepositOfSubordinatesResVO getAgentDepositAmountByOderNo(String orderNo) {
        LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentDepositSubordinatesPO::getOrderNo,orderNo);
        AgentDepositSubordinatesPO agentDepositSubordinatesPO = agentDepositSubordinatesRepository.selectOne(lqw);

        return ConvertUtil.entityToModel(agentDepositSubordinatesPO,AgentDepositOfSubordinatesResVO.class);
    }

    public List<AgentStoredMemberVO> getAgentDepositSum(AgentComprehensiveReportVO vo){
        return  agentDepositSubordinatesRepository.getAgentDepositSum(vo);
    }


    /**
     * 代理代存分页查询
     * @param vo
     * @return
     */
    public Page<AgentDepositOfSubordinatesResVO> listPage(AgentDepositSubordinatesPageReqVo vo) {
        Page<AgentDepositSubordinatesPO> page=new Page<>(vo.getPageNumber(),vo.getPageSize());
        LambdaQueryWrapper<AgentDepositSubordinatesPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StringUtils.isNotBlank(vo.getSiteCode()), AgentDepositSubordinatesPO::getSiteCode, vo.getSiteCode());
        lqw.eq(vo.getAccountType()!=null, AgentDepositSubordinatesPO::getAccountType, vo.getAccountType());
        lqw.ge(null != vo.getStartTime(), AgentDepositSubordinatesPO::getCreatedTime, vo.getStartTime());
        lqw.le(null != vo.getEndTime(), AgentDepositSubordinatesPO::getCreatedTime, vo.getEndTime());
        page = this.baseMapper.selectPage(page,lqw);
        IPage<AgentDepositOfSubordinatesResVO> result = page.convert(item -> {
            AgentDepositOfSubordinatesResVO resp = BeanUtil.copyProperties(item, AgentDepositOfSubordinatesResVO.class);
            return resp;
        });
        return ConvertUtil.toConverPage(result);
    }
}
