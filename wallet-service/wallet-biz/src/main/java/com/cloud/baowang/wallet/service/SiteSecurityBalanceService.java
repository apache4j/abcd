package com.cloud.baowang.wallet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.ReviewStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.enums.SiteSecurityAccountStatusEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityAmountDirectEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityBalanceAccountEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityCoinTypeEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityReviewEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecuritySourceCoinTypeEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityStatusEnums;
import com.cloud.baowang.wallet.api.enums.SiteSecurityUserTypeEnums;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityAuditSuccessReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceInitReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalancePageReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceThresholdAmountReqVO;
import com.cloud.baowang.wallet.po.SiteSecurityAdjustReviewPO;
import com.cloud.baowang.wallet.po.SiteSecurityBalancePO;
import com.cloud.baowang.wallet.po.SiteSecurityChangeLogPO;
import com.cloud.baowang.wallet.repositories.SiteSecurityAdjustReviewRepository;
import com.cloud.baowang.wallet.repositories.SiteSecurityBalanceRepository;
import com.cloud.baowang.wallet.repositories.SiteSecurityChangeLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Desciption: 站点保证金
 * @Author: Ford
 * @Date: 2025/6/27 17:31
 * @Version: V1.0
 * 帐变操作 详细记录: http://docs.mogm.top/pages/viewpage.action?pageId=58344304
 **/
@Service
@Slf4j
public class SiteSecurityBalanceService extends ServiceImpl<SiteSecurityBalanceRepository, SiteSecurityBalancePO> {

    @Autowired
    private SiteSecurityBalanceRepository siteSecurityBalanceRepository;

    @Autowired
    private SiteSecurityChangeLogRepository siteSecurityChangeLogRepository;

    @Autowired
    private SiteSecurityAdjustReviewRepository siteSecurityAdjustReviewRepository;
    @Autowired
    private SiteApi siteApi;


    /**
     *
     * @param securityBalanceInitReqVO
     * @return
     */
    public ResponseVO<Void> init(SiteSecurityBalanceInitReqVO securityBalanceInitReqVO) {
        //todo
        //开启=>关闭：需判断可用保证金余额 >= 0 且 保证金透支额度 = 剩余保证金透支额度
        //
        //                        若合法则关闭成功。若不合法则关闭失败，吐司提示：当前站点保证金未平账，无法关闭
        //
        //     关闭=>开启：吐司提示：站点保证金管理启用成功。

        log.info("站点保证金进行初始化:{}",securityBalanceInitReqVO);
        LambdaQueryWrapper<SiteSecurityBalancePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteSecurityBalancePO::getSiteCode,securityBalanceInitReqVO.getSiteCode());
        SiteSecurityBalancePO securityBalancePODb=this.baseMapper.selectOne(lambdaQueryWrapper);
        if(securityBalancePODb==null){
            SiteSecurityBalancePO securityBalancePO=new SiteSecurityBalancePO();
            BeanUtils.copyProperties(securityBalanceInitReqVO,securityBalancePO);
            securityBalancePO.setCurrency(CurrencyEnum.USD.getCode());
            securityBalancePO.setAvailableBalance(BigDecimal.ZERO);
            securityBalancePO.setFrozenBalance(BigDecimal.ZERO);
            securityBalancePO.setOverdrawAmount(BigDecimal.ZERO);
            securityBalancePO.setRemainOverdraw(BigDecimal.ZERO);
            securityBalancePO.setThresholdAmount(BigDecimal.ZERO);
            securityBalancePO.setFrozenOverdraw(BigDecimal.ZERO);
            securityBalancePO.setSecurityStatus(securityBalanceInitReqVO.getSecurityStatus());
            securityBalancePO.setCreator(securityBalanceInitReqVO.getOperatorUser());
            securityBalancePO.setUpdater(securityBalanceInitReqVO.getOperatorUser());
            securityBalancePO.setCreatedTime(System.currentTimeMillis());
            securityBalancePO.setUpdatedTime(System.currentTimeMillis());
            this.baseMapper.insert(securityBalancePO);
            log.info("站点保证金新增完成:{}",securityBalanceInitReqVO);
        }else {
            //
            if(Objects.equals(securityBalanceInitReqVO.getSecurityStatus(), SiteSecurityStatusEnums.CLOSE.getCode())){
                BigDecimal availableAmount=securityBalancePODb.getAvailableBalance();
                BigDecimal overdrawAmount=securityBalancePODb.getOverdrawAmount();
                BigDecimal remainOverdrawAmount=securityBalancePODb.getRemainOverdraw();
                if(availableAmount.compareTo(BigDecimal.ZERO)>=0&& overdrawAmount.compareTo(remainOverdrawAmount)==0){
                        log.info("站点保证金正常,可用={},透支额度={},剩余透支额度={}",availableAmount,overdrawAmount,remainOverdrawAmount);
                }else {
                    //当前站点保证金未平账，无法关闭
                    log.info("当前站点保证金未平账，无法关闭,可用={},透支额度={},剩余透支额度={}",availableAmount,overdrawAmount,remainOverdrawAmount);
                    return ResponseVO.fail(ResultCode.SECURITY_CANNOT_CLOSE);
                }
                //2.站点关闭保证金时 需要判断是否存在正在审核中订单 存在则不能关闭
                LambdaQueryWrapper<SiteSecurityAdjustReviewPO> lambdaQueryWrapperAdjustReview=new LambdaQueryWrapper<SiteSecurityAdjustReviewPO>();
                lambdaQueryWrapperAdjustReview.eq(SiteSecurityAdjustReviewPO::getSiteCode,securityBalanceInitReqVO.getSiteCode());
                List<Integer> auditStatus = com.google.common.collect.Lists.newArrayList(ReviewStatusEnum.REVIEW_REJECTED.getCode(),ReviewStatusEnum.REVIEW_PASS.getCode());
                lambdaQueryWrapperAdjustReview.notIn(SiteSecurityAdjustReviewPO::getReviewStatus,auditStatus );
                long reviewCountNum=siteSecurityAdjustReviewRepository.selectCount(lambdaQueryWrapperAdjustReview);
                if(reviewCountNum>0){
                    //当前站点保证金未平账，无法关闭
                    log.info("当前站点保证金存在处理中订单,无法关闭:{}",securityBalanceInitReqVO);
                    return ResponseVO.fail(ResultCode.SECURITY_CANNOT_CLOSE);
                }
            }
            securityBalancePODb.setSecurityStatus(securityBalanceInitReqVO.getSecurityStatus());
            securityBalancePODb.setUpdatedTime(System.currentTimeMillis());
            securityBalancePODb.setUpdater(securityBalanceInitReqVO.getOperatorUser());
            this.baseMapper.updateById(securityBalancePODb);
            log.info("站点保证金修改完成:{}",securityBalanceInitReqVO);
        }
        return ResponseVO.success();
    }

    public ResponseVO<Page<SiteSecurityBalanceRespVO>> listPage(SiteSecurityBalancePageReqVO siteSecurityBalancePageReqVO) {
        Page<SiteSecurityBalancePO> page = new Page<SiteSecurityBalancePO>(siteSecurityBalancePageReqVO.getPageNumber(), siteSecurityBalancePageReqVO.getPageSize());
        LambdaQueryWrapper<SiteSecurityBalancePO> lqw = new LambdaQueryWrapper<SiteSecurityBalancePO>();
        if(StringUtils.hasText(siteSecurityBalancePageReqVO.getSiteCode())){
            lqw.eq(SiteSecurityBalancePO::getSiteCode, siteSecurityBalancePageReqVO.getSiteCode());
        }
        if(StringUtils.hasText(siteSecurityBalancePageReqVO.getSiteName())){
            lqw.likeRight(SiteSecurityBalancePO::getSiteName, siteSecurityBalancePageReqVO.getSiteName());
        }
        if(StringUtils.hasText(siteSecurityBalancePageReqVO.getCompany())){
            lqw.likeRight(SiteSecurityBalancePO::getCompany, siteSecurityBalancePageReqVO.getCompany());
        }
        if(StringUtils.hasText(siteSecurityBalancePageReqVO.getLastModifyUser())){
            lqw.eq(SiteSecurityBalancePO::getUpdater, siteSecurityBalancePageReqVO.getLastModifyUser());
        }
        if(siteSecurityBalancePageReqVO.getAccountStatus()!=null){
            lqw.eq(SiteSecurityBalancePO::getAccountStatus, siteSecurityBalancePageReqVO.getAccountStatus());
        }
        if(siteSecurityBalancePageReqVO.getMinAmount()!=null){
            lqw.ge(SiteSecurityBalancePO::getAvailableBalance, siteSecurityBalancePageReqVO.getMinAmount());
        }
        if(siteSecurityBalancePageReqVO.getMaxAmount()!=null){
            lqw.le(SiteSecurityBalancePO::getAvailableBalance, siteSecurityBalancePageReqVO.getMaxAmount());
        }
        if(siteSecurityBalancePageReqVO.getStartTime()!=null){
            lqw.ge(SiteSecurityBalancePO::getUpdatedTime, siteSecurityBalancePageReqVO.getStartTime());
        }
        if(siteSecurityBalancePageReqVO.getEndTime()!=null){
            lqw.le(SiteSecurityBalancePO::getUpdatedTime, siteSecurityBalancePageReqVO.getEndTime());
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "availableBalance".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getAvailableBalance);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "availableBalance".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getAvailableBalance);
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "frozenBalance".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getFrozenBalance);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "frozenBalance".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getFrozenBalance);
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "thresholdAmount".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getThresholdAmount);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "thresholdAmount".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getThresholdAmount);
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "overdrawAmount".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getOverdrawAmount);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "overdrawAmount".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getOverdrawAmount);
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "remainOverdraw".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getRemainOverdraw);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "remainOverdraw".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getRemainOverdraw);
        }
        if("asc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "frozenOverdraw".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByAsc(SiteSecurityBalancePO::getFrozenOverdraw);
        }
        if("desc".equals(siteSecurityBalancePageReqVO.getOrderType()) && "frozenOverdraw".equals(siteSecurityBalancePageReqVO.getOrderField())){
            lqw.orderByDesc(SiteSecurityBalancePO::getFrozenOverdraw);
        }
        IPage<SiteSecurityBalancePO> siteSecurityBalancePOPage =  this.baseMapper.selectPage(page,lqw);
        Page<SiteSecurityBalanceRespVO> securityBalanceRespPage=new Page<SiteSecurityBalanceRespVO>(siteSecurityBalancePageReqVO.getPageNumber(), siteSecurityBalancePageReqVO.getPageSize());
        securityBalanceRespPage.setTotal(siteSecurityBalancePOPage.getTotal());
        securityBalanceRespPage.setPages(siteSecurityBalancePOPage.getPages());
        List<SiteSecurityBalanceRespVO> resultLists= Lists.newArrayList();
        if(!CollectionUtils.isEmpty(siteSecurityBalancePOPage.getRecords())){
            for(SiteSecurityBalancePO siteSecurityBalancePO :siteSecurityBalancePOPage.getRecords()){
                SiteSecurityBalanceRespVO siteSecurityBalanceRespVO=new SiteSecurityBalanceRespVO();
                BeanUtils.copyProperties(siteSecurityBalancePO,siteSecurityBalanceRespVO);
                SiteVO siteVO=siteApi.getSiteInfo(siteSecurityBalanceRespVO.getSiteCode()).getData();
                siteSecurityBalanceRespVO.setSiteStatus(siteVO.getStatus());
                resultLists.add(siteSecurityBalanceRespVO);
            }
            securityBalanceRespPage.setRecords(resultLists);
        }
        return ResponseVO.success(securityBalanceRespPage);
    }

    /**
     * 设置 预警阀值
     * @param siteSecurityBalanceThresholdAmountReqVO 预警阀值
     * @return success
     */
    public ResponseVO<Void> adminSetThresholdAmount(SiteSecurityBalanceThresholdAmountReqVO siteSecurityBalanceThresholdAmountReqVO) {
        LambdaQueryWrapper<SiteSecurityBalancePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteSecurityBalancePO::getSiteCode,siteSecurityBalanceThresholdAmountReqVO.getSiteCode());
        lambdaQueryWrapper.last("for update");
        SiteSecurityBalancePO securityBalancePODb=this.baseMapper.selectOne(lambdaQueryWrapper);
        if(securityBalancePODb!=null){
            Integer accountStatus = calAccountStatus(securityBalancePODb.getAvailableBalance(),siteSecurityBalanceThresholdAmountReqVO.getThresholdAmount(),securityBalancePODb.getOverdrawAmount(),securityBalancePODb.getRemainOverdraw());
            LambdaUpdateWrapper<SiteSecurityBalancePO> lambdaUpdateWrapper=new LambdaUpdateWrapper<SiteSecurityBalancePO>();
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getThresholdAmount,siteSecurityBalanceThresholdAmountReqVO.getThresholdAmount());
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getAccountStatus,accountStatus);
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getUpdatedTime,System.currentTimeMillis());
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getUpdater,siteSecurityBalanceThresholdAmountReqVO.getUpdateUser());
            lambdaUpdateWrapper.eq(SiteSecurityBalancePO::getSiteCode,siteSecurityBalanceThresholdAmountReqVO.getSiteCode());
            this.baseMapper.update(null,lambdaUpdateWrapper);
            log.info("设置预警阀值成功:{}",siteSecurityBalanceThresholdAmountReqVO);
        }
        return ResponseVO.success();
    }

    /**
     * 审核成功后
     *  记录保证金帐变记录 调整保证金金额
     *  记录保证金透支额度帐变记录 调整保证金透支额度
     * @return
     */
    public ResponseVO<Void> afterAuditSuccess(SiteSecurityAuditSuccessReqVO siteSecurityAuditSuccessReqVO){
        SiteSecurityReviewEnums siteSecurityReviewEnums= SiteSecurityReviewEnums.parseCode(siteSecurityAuditSuccessReqVO.getAdjustType());
        if(siteSecurityReviewEnums==null){
            log.info("保证金调整审核成功后记录帐变,调整类型不合法:{}",siteSecurityAuditSuccessReqVO);
            return ResponseVO.fail(ResultCode.PARAM_NOT_VALID);
        }
        log.info("保证金调整审核成功,开始记录:{}",siteSecurityAuditSuccessReqVO);
        //记录保证金透支额度帐变记录 调整保证金透支额度
        SiteSecurityBalanceChangeRecordReqVO vo =new SiteSecurityBalanceChangeRecordReqVO();
        vo.setSourceCoinType(siteSecurityReviewEnums.getSourceCoinTypeEnums().getCode());
        vo.setCoinType(siteSecurityReviewEnums.getCoinTypeEnums().getCode());
        BeanUtils.copyProperties(siteSecurityAuditSuccessReqVO,vo);
        return recordBalanceChangeLog(vo);
    }


    /**
     * 审核失败后
     *  记录保证金帐变记录 增加保证金金额、减少冻结金额 [减少保证金金额]
     * @return
     */
    public ResponseVO<Void> afterAuditFail(SiteSecurityAuditSuccessReqVO siteSecurityAuditSuccessReqVO){
        SiteSecurityReviewEnums siteSecurityReviewEnums= SiteSecurityReviewEnums.parseCode(siteSecurityAuditSuccessReqVO.getAdjustType());
        if(siteSecurityReviewEnums==null){
            log.info("保证金调整审核失败后记录帐变,调整类型不合法:{}",siteSecurityAuditSuccessReqVO);
            return ResponseVO.fail(ResultCode.PARAM_NOT_VALID);
        }
        log.info("保证金调整审核失败,开始记录:{}",siteSecurityAuditSuccessReqVO);
        //记录保证金透支额度帐变记录 减少保证金回滚
        SiteSecurityBalanceChangeRecordReqVO vo =new SiteSecurityBalanceChangeRecordReqVO();
        BeanUtils.copyProperties(siteSecurityAuditSuccessReqVO,vo);
        vo.setSourceCoinType(siteSecurityReviewEnums.getSourceCoinTypeEnums().getCode());
        vo.setCoinType(siteSecurityReviewEnums.getCoinTypeEnums().getCode());
        vo.setUpdateUser(null);//审核失败 不需要记录操作人
        //减少保证金审核失败 需要扣减冻结额度 增加保证金剩余额度
        return recordBalanceChangeLog(vo);
    }


    /**
     * 会员代理充值成功后调用
     * 会员代理提现申请、失败后调用
     * 会员代理提现成功后调用
     *
     * 会员代理充值成功 可用增加
     * 会员代理提现申请 可用减少、冻结增加
     * 会员代理提现失败 可用增加、冻结减少
     * 会员代理提现成功 冻结减少
     *
     * @param vo
     * @return
     */
    @DistributedLock(name = RedisConstants.OVER_DRAW_LOG_SITE_CODE_LOCK, unique = "#vo.siteCode", waitTime = 3, leaseTime = 180)
    @Transactional
    public  ResponseVO<Void> recordBalanceChangeLog(SiteSecurityBalanceChangeRecordReqVO vo){
        SiteSecuritySourceCoinTypeEnums sourceCoinTypeEnums=SiteSecuritySourceCoinTypeEnums.parseCode(vo.getSourceCoinType());
        if(sourceCoinTypeEnums==null){
            log.warn("站点保证金,业务型错误:{}",vo);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        SiteSecurityCoinTypeEnums coinTypeEnums=SiteSecurityCoinTypeEnums.parseCode(vo.getCoinType());
        if(coinTypeEnums==null){
            log.warn("站点保证金,帐变类型错误:{}",vo);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<SiteSecurityChangeLogPO> lambdaQueryWrapperChangeLog=new LambdaQueryWrapper<SiteSecurityChangeLogPO>();
        lambdaQueryWrapperChangeLog.eq(SiteSecurityChangeLogPO::getSourceOrderNo,vo.getSourceOrderNo());
        lambdaQueryWrapperChangeLog.eq(SiteSecurityChangeLogPO::getCoinType,coinTypeEnums.getCode());
        long countNum=siteSecurityChangeLogRepository.selectCount(lambdaQueryWrapperChangeLog);
        if(countNum>0){
            log.warn("站点保证金,来源订单号已存在:{}",vo);
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }

        LambdaQueryWrapper<SiteSecurityBalancePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteSecurityBalancePO::getSiteCode,vo.getSiteCode());
        lambdaQueryWrapper.last(" for update ");
        SiteSecurityBalancePO securityBalancePODb=this.baseMapper.selectOne(lambdaQueryWrapper);
        if(securityBalancePODb==null){
            log.warn("站点保证金不存在:{}",vo);
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if(SiteSecurityStatusEnums.CLOSE.getCode().equals(securityBalancePODb.getSecurityStatus())){
            log.warn("站点保证金未开启,无需处理:{}",vo);
            return ResponseVO.success();
        }

        log.info("开始记录保证金帐变:{}",vo);
        String operUserNo=null;
        if(
                SiteSecuritySourceCoinTypeEnums.ADD_SECURITY_BALANCE.getCode().equals(vo.getSourceCoinType())||
                SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_BALANCE.getCode().equals(vo.getSourceCoinType())||
                SiteSecuritySourceCoinTypeEnums.ADD_SECURITY_OVERDRAW.getCode().equals(vo.getSourceCoinType())||
                SiteSecuritySourceCoinTypeEnums.SUB_SECURITY_OVERDRAW.getCode().equals(vo.getSourceCoinType())||
                SiteSecuritySourceCoinTypeEnums.USER_MANUAL_WITHDRAW.getCode().equals(vo.getSourceCoinType())||
                SiteSecuritySourceCoinTypeEnums.AGENT_MANUAL_WITHDRAW.getCode().equals(vo.getSourceCoinType())
         ){
            operUserNo=vo.getUpdateUser();
        }
        //可用余额
        BigDecimal beforeAmount=securityBalancePODb.getAvailableBalance();
        BigDecimal afterAmount=securityBalancePODb.getAvailableBalance();
        //冻结金额
        BigDecimal beforeFrozenAmount=securityBalancePODb.getFrozenBalance();
        BigDecimal afterFrozenAmount=securityBalancePODb.getFrozenBalance();
        //透支额度
        BigDecimal beforeOverdrawAmount=securityBalancePODb.getOverdrawAmount();
        BigDecimal afterOverdrawAmount=securityBalancePODb.getOverdrawAmount();
        //剩余透支额度
        BigDecimal beforeRemainOverdrawAmount=securityBalancePODb.getRemainOverdraw();
        BigDecimal afterRemainOverdrawAmount=securityBalancePODb.getRemainOverdraw();

        //冻结透支额度
        BigDecimal beforeFrozenOverdrawAmount=securityBalancePODb.getFrozenOverdraw();
        BigDecimal afterFrozenOverdrawAmount=securityBalancePODb.getFrozenOverdraw();

        //可用余额实际调整金额
        BigDecimal actualAdjustAmount=BigDecimal.ZERO;
        //剩余透支实际调整金额
        BigDecimal actualOverdrawAdjustAmount=BigDecimal.ZERO;


        //最大可提现额度
        BigDecimal maxAvailableAmount=beforeAmount.add(securityBalancePODb.getRemainOverdraw());
        if(SiteSecurityCoinTypeEnums.USER_WITHDRAW.getCode().equals(coinTypeEnums.getCode())||
         SiteSecurityCoinTypeEnums.AGENT_WITHDRAW.getCode().equals(coinTypeEnums.getCode())
        ){
            if(vo.getAdjustAmount().compareTo(maxAvailableAmount)>0){
                log.info("提现金额超过保证金透支额度,不能操作:{}>{}+{}",vo.getAdjustAmount(),beforeAmount,securityBalancePODb.getRemainOverdraw());
                throw new BaowangDefaultException(ResultCode.CONTACT_CUSTOMER_SERVICE);
            }
        }
        //保证金减少 不能超过可用余额
        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE.getCode().equals(coinTypeEnums.getCode())){
            if(vo.getAdjustAmount().compareTo(beforeAmount)>0){
                log.info("减少金额超过保证金可用额度,不能操作:{}>{}",vo.getAdjustAmount(),beforeAmount);
                throw new BaowangDefaultException(ResultCode.OVER_BALANCE);
            }
        }
        //透支额度减少
        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW.getCode().equals(coinTypeEnums.getCode())){
            if(vo.getAdjustAmount().compareTo(beforeRemainOverdrawAmount)>0){
                log.info("减少金额超过剩余透支额度,不能操作:{}>{}",vo.getAdjustAmount(),beforeRemainOverdrawAmount);
                throw new BaowangDefaultException(ResultCode.OVER_BALANCE);
            }
        }
        //当 剩余透支+冻结透支>透支额度 时,存在异常
        BigDecimal sumOverdrawAmount=beforeRemainOverdrawAmount.add(beforeFrozenOverdrawAmount);
        if(sumOverdrawAmount.compareTo(beforeOverdrawAmount)>0){
            log.info("站点:{},透支额度异常,{}+{}>{},需要排查原因",vo.getSiteCode(),beforeRemainOverdrawAmount,beforeFrozenOverdrawAmount,beforeOverdrawAmount);
            return ResponseVO.fail(ResultCode.OVERDRAW_ERROR);
        }
        //保证金增加情况 会员存款、代理存款、增加保证金
        //会员人工提款  提款成功
        //代理人工提款  提款成功
        /**
         * 保证金	会员存款	会员存款	+
         * 剩余透支额度	会员存款	透支额度抵扣	+
         *  保证金	会员提款	提款失败	+
         * 冻结保证金	会员提款	提款失败	-
         *  保证金	代理存款	代理存款	+
         * 剩余透支额度	代理存款	透支额度抵扣	+
         * 保证金	代理提款	提款失败	+
         * 冻结保证金	代理提款	提款失败	-
         * 保证金	增加保证金	增加保证金	+
         * 剩余透支额度	增加保证金	透支额度抵扣	+
         * 保证金	减少保证金	减少保证金失败	+
         * 冻结保证金	减少保证金	减少保证金失败	-
         */
        SiteSecurityAmountDirectEnums frozenAmountDirect=SiteSecurityAmountDirectEnums.NONE;//冻结资金方向
        SiteSecurityAmountDirectEnums availableAmountDirect=SiteSecurityAmountDirectEnums.NONE;//可用余额资金方向
        SiteSecurityAmountDirectEnums overdrawAmountDirect=SiteSecurityAmountDirectEnums.NONE;//透支额度资金方向
        SiteSecurityAmountDirectEnums remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.NONE;//剩余透支额度资金方向
        SiteSecurityAmountDirectEnums frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.NONE;//冻结透支额度资金方向

        SiteSecurityCoinTypeEnums overdrawCoinTypeEnums=coinTypeEnums;//剩余透支额度 帐变类型

        LambdaUpdateWrapper<SiteSecurityBalancePO> lambdaUpdateWrapper=new LambdaUpdateWrapper<SiteSecurityBalancePO>();
        //存在透支额度抵扣情况
        //当 剩余透支+冻结透支>透支 时,存在异常
        //当 剩余透支+冻结透支==透支 时,不需要抵扣
        //当 剩余透支+冻结透支<透支 时 需要抵扣额度=透支-(剩余透支+冻结透支)
        if(
                SiteSecurityCoinTypeEnums.USER_DEPOSIT.getCode().equals(vo.getCoinType())||
                SiteSecurityCoinTypeEnums.AGENT_DEPOSIT.getCode().equals(vo.getCoinType())||
                SiteSecurityCoinTypeEnums.ADD_SECURITY_BALANCE.getCode().equals(vo.getCoinType())
        ){
            BigDecimal adjustAmount=vo.getAdjustAmount();//本次调整金额
            if(sumOverdrawAmount.compareTo(beforeOverdrawAmount)==0){
                log.info("透支额度平衡,不需要抵扣,{}+{}={}",beforeRemainOverdrawAmount,beforeFrozenOverdrawAmount,beforeOverdrawAmount);
                actualAdjustAmount=adjustAmount;
            }else if(sumOverdrawAmount.compareTo(beforeOverdrawAmount)<0){
                BigDecimal deductAmount=beforeOverdrawAmount.subtract(sumOverdrawAmount);//需要待抵扣额度
                log.info("本次调整金额:{},需要待抵扣额度:{}",adjustAmount,deductAmount);
                //正好抵扣完成 或 抵扣不足
                if(adjustAmount.compareTo(deductAmount)<=0){
                    actualOverdrawAdjustAmount=adjustAmount;
                }else {
                    //抵扣完成 还有剩余
                    actualOverdrawAdjustAmount=deductAmount;
                    actualAdjustAmount=vo.getAdjustAmount().subtract(actualOverdrawAdjustAmount);
                }
                log.info("透支实际抵扣:{},可用实际减少:{}",actualOverdrawAdjustAmount,actualAdjustAmount);
            }
            //先抵扣透支保证金
            if(actualOverdrawAdjustAmount.compareTo(BigDecimal.ZERO)>0){
                lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw+"+actualOverdrawAdjustAmount);
                remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;
                overdrawCoinTypeEnums=SiteSecurityCoinTypeEnums.DEDUCT_SECURITY_OVERDRAW;
                afterRemainOverdrawAmount=beforeRemainOverdrawAmount.add(actualOverdrawAdjustAmount);
            }

            if(actualAdjustAmount.compareTo(BigDecimal.ZERO)>0){
                lambdaUpdateWrapper.setSql("available_balance=available_balance+"+actualAdjustAmount);
                availableAmountDirect=SiteSecurityAmountDirectEnums.ADD;
                afterAmount=beforeAmount.add(actualAdjustAmount);
            }
        }
        //提款失败
        if( SiteSecurityCoinTypeEnums.WITHDRAW_FAIL.getCode().equals(vo.getCoinType()) ||
                SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE_FAIL.getCode().equals(vo.getCoinType())
        ){
            //按照原始订单查询实际冻结金额
            LambdaQueryWrapper<SiteSecurityChangeLogPO> orderLambdaQuery=new LambdaQueryWrapper<>();
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getSiteCode,securityBalancePODb.getSiteCode());
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getBalanceAccount,SiteSecurityBalanceAccountEnums.FROZEN.getCode());
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getSourceOrderNo,vo.getSourceOrderNo());
            SiteSecurityChangeLogPO  siteSecurityChangeLog= siteSecurityChangeLogRepository.selectOne(orderLambdaQuery);
            if(siteSecurityChangeLog!=null){
                actualAdjustAmount=siteSecurityChangeLog.getChangeAmount();
                lambdaUpdateWrapper.setSql("available_balance=available_balance+"+actualAdjustAmount);
                lambdaUpdateWrapper.setSql("frozen_balance=frozen_balance-"+actualAdjustAmount);
                afterFrozenAmount=beforeFrozenAmount.subtract(actualAdjustAmount);
                afterAmount=beforeAmount.add(actualAdjustAmount);
                frozenAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                availableAmountDirect=SiteSecurityAmountDirectEnums.ADD;
            }
            LambdaQueryWrapper<SiteSecurityChangeLogPO> orderOverdrawLambdaQuery=new LambdaQueryWrapper<>();
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getSiteCode,securityBalancePODb.getSiteCode());
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getBalanceAccount,SiteSecurityBalanceAccountEnums.OVERDRAW_FROZEN.getCode());
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getSourceOrderNo,vo.getSourceOrderNo());
            SiteSecurityChangeLogPO  overdrawChangeLog= siteSecurityChangeLogRepository.selectOne(orderOverdrawLambdaQuery);
            if(overdrawChangeLog!=null) {
                actualOverdrawAdjustAmount = overdrawChangeLog.getChangeAmount();
                lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw+"+actualOverdrawAdjustAmount);
                lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw-"+actualOverdrawAdjustAmount);
                afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.subtract(actualOverdrawAdjustAmount);
                frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                afterRemainOverdrawAmount=beforeRemainOverdrawAmount.add(actualOverdrawAdjustAmount);
                remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;
            }
        }

        //提款申请、余额冻结、透支额度冻结
        if(SiteSecurityCoinTypeEnums.USER_WITHDRAW.getCode().equals(vo.getCoinType())||
            SiteSecurityCoinTypeEnums.AGENT_WITHDRAW.getCode().equals(vo.getCoinType())
        ){
            BigDecimal adjustAmount=vo.getAdjustAmount();
            afterAmount=beforeAmount.subtract(adjustAmount);
            if(afterAmount.compareTo(BigDecimal.ZERO)>=0){
                //部分用完 或正好用完
                actualAdjustAmount=adjustAmount;
            }else {
                //全部用完 还不够用
                actualAdjustAmount=beforeAmount;
            }
            //计算实际抵扣额度
            if(beforeAmount.compareTo(BigDecimal.ZERO)>0){
                lambdaUpdateWrapper.setSql("available_balance=available_balance-"+actualAdjustAmount);
                lambdaUpdateWrapper.setSql("frozen_balance=frozen_balance+"+actualAdjustAmount);
                afterFrozenAmount=beforeFrozenAmount.add(actualAdjustAmount);
                frozenAmountDirect=SiteSecurityAmountDirectEnums.ADD;
                availableAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                afterAmount=beforeAmount.subtract(actualAdjustAmount);
            }
            //可用余额不足 需要扣除透支金额
            if(actualAdjustAmount.compareTo(adjustAmount)<0){
                actualOverdrawAdjustAmount=vo.getAdjustAmount().subtract(actualAdjustAmount.abs());
                afterRemainOverdrawAmount=beforeRemainOverdrawAmount.subtract(actualOverdrawAdjustAmount);
                afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.add(actualOverdrawAdjustAmount);
                lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw-"+actualOverdrawAdjustAmount);
                lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw+"+actualOverdrawAdjustAmount);
                remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;
            }
        }

        //减少保证金
        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE.getCode().equals(vo.getCoinType())){
            actualAdjustAmount=vo.getAdjustAmount();
            lambdaUpdateWrapper.setSql("available_balance=available_balance-"+actualAdjustAmount);
            lambdaUpdateWrapper.setSql("frozen_balance=frozen_balance+"+actualAdjustAmount);
            afterAmount=beforeAmount.subtract(actualAdjustAmount);
            afterFrozenAmount=beforeFrozenAmount.add(actualAdjustAmount);
            frozenAmountDirect=SiteSecurityAmountDirectEnums.ADD;
            availableAmountDirect=SiteSecurityAmountDirectEnums.SUB;
        }



        //提款成功  减少保证金成功
        if( SiteSecurityCoinTypeEnums.WITHDRAW_SUCCESS.getCode().equals(vo.getCoinType())||
                SiteSecurityCoinTypeEnums.SUB_SECURITY_BALANCE_SUCCESS.getCode().equals(vo.getCoinType())
        ){
            //余额是否增加
            boolean needBalanceAddFlag= SiteSecuritySourceCoinTypeEnums.USER_MANUAL_WITHDRAW.getCode().equals(vo.getSourceCoinType()) ||
                    SiteSecuritySourceCoinTypeEnums.AGENT_MANUAL_WITHDRAW.getCode().equals(vo.getSourceCoinType());

            //按照原始订单查询实际冻结金额
            LambdaQueryWrapper<SiteSecurityChangeLogPO> orderLambdaQuery=new LambdaQueryWrapper<>();
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getSiteCode,securityBalancePODb.getSiteCode());
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getBalanceAccount,SiteSecurityBalanceAccountEnums.FROZEN.getCode());
            orderLambdaQuery.eq(SiteSecurityChangeLogPO::getSourceOrderNo,vo.getSourceOrderNo());
            SiteSecurityChangeLogPO  siteSecurityChangeLog= siteSecurityChangeLogRepository.selectOne(orderLambdaQuery);
            if(siteSecurityChangeLog!=null){
                actualAdjustAmount=siteSecurityChangeLog.getChangeAmount();
                lambdaUpdateWrapper.setSql("frozen_balance=frozen_balance-"+actualAdjustAmount);
                afterFrozenAmount=beforeFrozenAmount.subtract(actualAdjustAmount);
                frozenAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                if(needBalanceAddFlag){
                    lambdaUpdateWrapper.setSql("available_balance=available_balance+"+actualAdjustAmount);
                    availableAmountDirect=SiteSecurityAmountDirectEnums.ADD;
                    afterAmount=beforeAmount.add(actualAdjustAmount);
                }
            }

            LambdaQueryWrapper<SiteSecurityChangeLogPO> orderOverdrawLambdaQuery=new LambdaQueryWrapper<>();
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getSiteCode,securityBalancePODb.getSiteCode());
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getBalanceAccount,SiteSecurityBalanceAccountEnums.OVERDRAW_FROZEN.getCode());
            orderOverdrawLambdaQuery.eq(SiteSecurityChangeLogPO::getSourceOrderNo,vo.getSourceOrderNo());
            SiteSecurityChangeLogPO  overdrawChangeLog= siteSecurityChangeLogRepository.selectOne(orderOverdrawLambdaQuery);
            if(overdrawChangeLog!=null){
                actualOverdrawAdjustAmount=overdrawChangeLog.getChangeAmount();
                lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw-"+actualOverdrawAdjustAmount);
                afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.subtract(actualOverdrawAdjustAmount);
                frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;
                if(needBalanceAddFlag){
                    lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw+"+actualOverdrawAdjustAmount);
                    remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;
                    afterRemainOverdrawAmount=beforeRemainOverdrawAmount.add(actualOverdrawAdjustAmount);
                }
            }
        }

        if(SiteSecurityCoinTypeEnums.ADD_SECURITY_OVERDRAW.getCode().equals(vo.getCoinType())){
            actualOverdrawAdjustAmount=vo.getAdjustAmount();
            lambdaUpdateWrapper.setSql("overdraw_amount=overdraw_amount+"+actualOverdrawAdjustAmount);
            lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw+"+actualOverdrawAdjustAmount);
            afterRemainOverdrawAmount=beforeRemainOverdrawAmount.add(actualOverdrawAdjustAmount);
            remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;

            afterOverdrawAmount=beforeOverdrawAmount.add(actualOverdrawAdjustAmount);
            overdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;

        }

        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW.getCode().equals(vo.getCoinType())){
            actualOverdrawAdjustAmount=vo.getAdjustAmount();
            lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw-"+actualOverdrawAdjustAmount);
            lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw+"+actualOverdrawAdjustAmount);

            afterRemainOverdrawAmount=beforeRemainOverdrawAmount.subtract(actualOverdrawAdjustAmount);
            remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;

            afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.add(actualOverdrawAdjustAmount);
            frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;

        }


        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW_FAIL.getCode().equals(vo.getCoinType())){
            actualOverdrawAdjustAmount=vo.getAdjustAmount();
            lambdaUpdateWrapper.setSql("remain_overdraw=remain_overdraw+"+actualOverdrawAdjustAmount);
            lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw-"+actualOverdrawAdjustAmount);

            afterRemainOverdrawAmount=beforeRemainOverdrawAmount.add(actualOverdrawAdjustAmount);
            remainOverdrawAmountDirect=SiteSecurityAmountDirectEnums.ADD;

            afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.subtract(actualOverdrawAdjustAmount);
            frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;
        }


        if(SiteSecurityCoinTypeEnums.SUB_SECURITY_OVERDRAW_SUCCESS.getCode().equals(vo.getCoinType())){
            actualOverdrawAdjustAmount=vo.getAdjustAmount();
            lambdaUpdateWrapper.setSql("overdraw_amount=overdraw_amount-"+actualOverdrawAdjustAmount);
            lambdaUpdateWrapper.setSql("frozen_overdraw=frozen_overdraw-"+actualOverdrawAdjustAmount);
            afterFrozenOverdrawAmount=beforeFrozenOverdrawAmount.subtract(actualOverdrawAdjustAmount);
            frozenOverdrawAmountDirect=SiteSecurityAmountDirectEnums.SUB;
        }

        String userName=vo.getUserName();
        //帐号名称=站点名称
        if(SiteSecurityUserTypeEnums.SITE.getCode().equals(sourceCoinTypeEnums.getUserTypeEnums().getCode())){
            userName=securityBalancePODb.getSiteName();
        }
        //计算账户状态
        Integer accountStatus = calAccountStatus(afterAmount,securityBalancePODb.getThresholdAmount(),securityBalancePODb.getOverdrawAmount(),securityBalancePODb.getRemainOverdraw());
        lambdaUpdateWrapper.set(SiteSecurityBalancePO::getAccountStatus,accountStatus);
        SiteSecurityChangeLogPO siteSecurityChangeLogPO=new SiteSecurityChangeLogPO();
        siteSecurityChangeLogPO.setId(SnowFlakeUtils.getSnowId());
        siteSecurityChangeLogPO.setSiteCode(vo.getSiteCode());
        siteSecurityChangeLogPO.setOrderNo(vo.getSourceOrderNo());
        siteSecurityChangeLogPO.setSiteName(securityBalancePODb.getSiteName());
        siteSecurityChangeLogPO.setCompany(securityBalancePODb.getCompany());
        siteSecurityChangeLogPO.setSiteType(securityBalancePODb.getSiteType());
        siteSecurityChangeLogPO.setCurrency(securityBalancePODb.getCurrency());
        siteSecurityChangeLogPO.setBalanceAccount(SiteSecurityBalanceAccountEnums.AVAILABLE.getCode());
        siteSecurityChangeLogPO.setUserType(sourceCoinTypeEnums.getUserTypeEnums().getCode());
        siteSecurityChangeLogPO.setUserId(vo.getUserId());
        siteSecurityChangeLogPO.setUserName(userName);
        siteSecurityChangeLogPO.setSourceOrderNo(vo.getSourceOrderNo());
        siteSecurityChangeLogPO.setSourceCoinType(vo.getSourceCoinType());
        siteSecurityChangeLogPO.setCoinType(vo.getCoinType());
        siteSecurityChangeLogPO.setAmountDirect(availableAmountDirect.getCode());
        siteSecurityChangeLogPO.setBeforeAmount(beforeAmount);
        siteSecurityChangeLogPO.setChangeAmount(actualAdjustAmount);
        siteSecurityChangeLogPO.setAfterAmount(afterAmount);
        siteSecurityChangeLogPO.setChangeTime(System.currentTimeMillis());
        siteSecurityChangeLogPO.setCreatedTime(System.currentTimeMillis());
        siteSecurityChangeLogPO.setCreator(operUserNo);
        siteSecurityChangeLogPO.setUpdater(operUserNo);
        siteSecurityChangeLogPO.setUpdatedTime(System.currentTimeMillis());
        if(!SiteSecurityAmountDirectEnums.NONE.getCode().equals(availableAmountDirect.getCode())){
            siteSecurityChangeLogRepository.insert(siteSecurityChangeLogPO);
            log.info("可用保证金金额记录成功:{}",siteSecurityChangeLogPO);
        }


        SiteSecurityChangeLogPO siteSecurityFrozenChangeLogPO=new SiteSecurityChangeLogPO();
        BeanUtils.copyProperties(siteSecurityChangeLogPO,siteSecurityFrozenChangeLogPO);
        siteSecurityFrozenChangeLogPO.setId(SnowFlakeUtils.getSnowId());
        siteSecurityFrozenChangeLogPO.setBalanceAccount(SiteSecurityBalanceAccountEnums.FROZEN.getCode());
        siteSecurityFrozenChangeLogPO.setAmountDirect(frozenAmountDirect.getCode());
        siteSecurityFrozenChangeLogPO.setBeforeAmount(beforeFrozenAmount);
        siteSecurityFrozenChangeLogPO.setChangeAmount(actualAdjustAmount);
        siteSecurityFrozenChangeLogPO.setAfterAmount(afterFrozenAmount);
        if(!SiteSecurityAmountDirectEnums.NONE.getCode().equals(frozenAmountDirect.getCode())) {
            siteSecurityChangeLogRepository.insert(siteSecurityFrozenChangeLogPO);
            log.info("保证金冻结金额记录成功:{}", siteSecurityFrozenChangeLogPO);
        }


        //保证金为负值时增加保证金,优先抵扣剩余保证金,剩余透支额度增加
        //当可用保证金小于0 剩余保证金透支额度= 保证金透支额度+可用保证金
        //当可用保证金大于0 剩余保证金透支额度= 保证金透支额度


        //记录剩余透支额度操作记录
        SiteSecurityChangeLogPO remainOverdrawLogPO=new SiteSecurityChangeLogPO();
        remainOverdrawLogPO.setId(SnowFlakeUtils.getSnowId());
        remainOverdrawLogPO.setSiteCode(securityBalancePODb.getSiteCode());
        remainOverdrawLogPO.setSiteName(securityBalancePODb.getSiteName());
        remainOverdrawLogPO.setCompany(securityBalancePODb.getCompany());
        remainOverdrawLogPO.setSiteType(securityBalancePODb.getSiteType());
        remainOverdrawLogPO.setCurrency(securityBalancePODb.getCurrency());
        remainOverdrawLogPO.setUserType(sourceCoinTypeEnums.getUserTypeEnums().getCode());
        remainOverdrawLogPO.setUserId(vo.getUserId());
        remainOverdrawLogPO.setUserName(userName);
        remainOverdrawLogPO.setBalanceAccount(SiteSecurityBalanceAccountEnums.OVERDRAW_AVAILABLE.getCode());
        remainOverdrawLogPO.setOrderNo(vo.getSourceOrderNo());
        remainOverdrawLogPO.setSourceOrderNo(vo.getSourceOrderNo());
        remainOverdrawLogPO.setSourceCoinType(sourceCoinTypeEnums.getCode());
        remainOverdrawLogPO.setCoinType(overdrawCoinTypeEnums.getCode());
        remainOverdrawLogPO.setBeforeAmount(securityBalancePODb.getRemainOverdraw());
        remainOverdrawLogPO.setAfterAmount(afterRemainOverdrawAmount);
        remainOverdrawLogPO.setChangeAmount(actualOverdrawAdjustAmount.abs());
        remainOverdrawLogPO.setAmountDirect(remainOverdrawAmountDirect.getCode());
        remainOverdrawLogPO.setChangeTime(System.currentTimeMillis());
        remainOverdrawLogPO.setCreator(operUserNo);
        remainOverdrawLogPO.setCreatedTime(System.currentTimeMillis());
        remainOverdrawLogPO.setUpdater(operUserNo);
        remainOverdrawLogPO.setUpdatedTime(System.currentTimeMillis());
        if(!remainOverdrawAmountDirect.equals(SiteSecurityAmountDirectEnums.NONE)){
            siteSecurityChangeLogRepository.insert(remainOverdrawLogPO);
            log.info("剩余透支额度记录成功:{}",vo);
        }

        //透支额度记录
        SiteSecurityChangeLogPO overdrawLogPO=new SiteSecurityChangeLogPO();
        BeanUtils.copyProperties(remainOverdrawLogPO,overdrawLogPO);
        overdrawLogPO.setId(SnowFlakeUtils.getSnowId());
        overdrawLogPO.setBalanceAccount(SiteSecurityBalanceAccountEnums.OVERDRAW.getCode());
        overdrawLogPO.setBeforeAmount(securityBalancePODb.getOverdrawAmount());
        overdrawLogPO.setChangeAmount(actualOverdrawAdjustAmount.abs());
        overdrawLogPO.setAfterAmount(afterOverdrawAmount);
        overdrawLogPO.setAmountDirect(overdrawAmountDirect.getCode());
        if(!overdrawAmountDirect.equals(SiteSecurityAmountDirectEnums.NONE)) {
            siteSecurityChangeLogRepository.insert(overdrawLogPO);
            log.info("透支额度记录成功:{}", vo);
        }

        //透支额度冻结记录
        SiteSecurityChangeLogPO overdrawFrozenLogPO=new SiteSecurityChangeLogPO();
        BeanUtils.copyProperties(remainOverdrawLogPO,overdrawFrozenLogPO);
        overdrawFrozenLogPO.setId(SnowFlakeUtils.getSnowId());
        overdrawFrozenLogPO.setBalanceAccount(SiteSecurityBalanceAccountEnums.OVERDRAW_FROZEN.getCode());
        overdrawFrozenLogPO.setBeforeAmount(securityBalancePODb.getFrozenOverdraw());
        overdrawFrozenLogPO.setChangeAmount(actualOverdrawAdjustAmount.abs());
        overdrawFrozenLogPO.setAfterAmount(afterFrozenOverdrawAmount);
        overdrawFrozenLogPO.setAmountDirect(frozenOverdrawAmountDirect.getCode());
        if(!frozenOverdrawAmountDirect.equals(SiteSecurityAmountDirectEnums.NONE)) {
            siteSecurityChangeLogRepository.insert(overdrawFrozenLogPO);
            log.info("透支额度冻结记录成功:{}", vo);
        }


        if(operUserNo!=null){
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getUpdatedTime,System.currentTimeMillis());
            lambdaUpdateWrapper.set(SiteSecurityBalancePO::getUpdater,operUserNo);
        }
        lambdaUpdateWrapper.eq(SiteSecurityBalancePO::getSiteCode,vo.getSiteCode());
        this.baseMapper.update(null,lambdaUpdateWrapper);
        log.info("设置可用保证金金额成功:{}",vo);
        return ResponseVO.success();
    }


    /**
     *  保证金账户状态 1:正常 2:预警 3:透支
     *  正常：可用保证金 > 预警额度
     *  预警：0< 可用保证金 <= 预警额度
     *  透支：可用保证金 =0 && 剩余透支额度<=透支额度
     * @param availableBalance 可用金额
     * @param thresholdAmount 预警额度
     * @return
     */
    private Integer calAccountStatus(BigDecimal availableBalance,BigDecimal thresholdAmount,BigDecimal overdrawAmount,BigDecimal remainOverdrawAmount) {
        Integer accountStatus= SiteSecurityAccountStatusEnums.NORMAL.getCode();
        if(availableBalance.compareTo(thresholdAmount)>0){
            accountStatus = SiteSecurityAccountStatusEnums.NORMAL.getCode();
        }
        if(availableBalance.compareTo(BigDecimal.ZERO)>0&&availableBalance.compareTo(thresholdAmount)<=0){
            accountStatus= SiteSecurityAccountStatusEnums.ALARM.getCode();
        }
        //透支： 可用保证金<=0
        if(availableBalance.compareTo(BigDecimal.ZERO)==0&&  remainOverdrawAmount.compareTo(overdrawAmount)<=0){
            accountStatus= SiteSecurityAccountStatusEnums.OVERDRAW.getCode();
        }
        return accountStatus;
    }

    public SiteSecurityBalancePO selectBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteSecurityBalancePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteSecurityBalancePO::getSiteCode,siteCode);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    public boolean isClosed(String siteCode) {
        LambdaQueryWrapper<SiteSecurityBalancePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteSecurityBalancePO::getSiteCode,siteCode);
        SiteSecurityBalancePO securityBalancePODb=this.baseMapper.selectOne(lambdaQueryWrapper);
        if(securityBalancePODb==null){
            log.error("站点保证金数据不存在:{}",siteCode);
            return true;
        }
        if(Objects.equals(SiteSecurityStatusEnums.CLOSE.getCode(), securityBalancePODb.getSecurityStatus())){
            log.error("站点保证金数据已关闭:{}",siteCode);
            return true;
        }
        return false;
    }

}
