package com.cloud.baowang.activity.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.ActivityRedemptionCodeApi;
import com.cloud.baowang.activity.api.enums.ActivityRedemptionCodeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.free.FreeGameReqVO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeBasePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeDetailPO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionCodeExchangePO;
import com.cloud.baowang.activity.po.SiteActivityRedemptionGenCodePO;
import com.cloud.baowang.activity.service.ActivityRedemptionCodeBaseService;
import com.cloud.baowang.activity.service.ActivityRedemptionCodeDetailService;
import com.cloud.baowang.activity.service.ActivityRedemptionCodeExchangeService;
import com.cloud.baowang.activity.service.ActivityRedemptionGenCodeSerivce;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.RandomStringUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositRecordApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordPageVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositRecordRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author brence
 * @desc 兑换码接口实现
 */

@RestController
@Slf4j
public class ActivityRedemptionCodeApiImpl implements ActivityRedemptionCodeApi {

    @Autowired
    private ActivityRedemptionCodeDetailService activityRedemptionCodeDetailService;
    @Autowired
    private ActivityRedemptionCodeBaseService activityRedemptionCodeBaseService;
    @Autowired
    private ActivityRedemptionGenCodeSerivce activityRedemptionGenCodeSerivce;
    @Autowired
    private ActivityRedemptionCodeExchangeService activityRedemptionCodeExchangeService;
    @Autowired
    private UserInfoApi userInfoApi;
    @Autowired
    private SystemDictConfigApi systemDictConfigApi;
    @Autowired
    private UserDepositRecordApi userDepositRecordApi;
    @Autowired
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    //兑换码生效前2天或2后,这段段时间内会员如有存款则可以参加兑换
    private int userRedemptionCodeDepositDays;
    private static final long _1_DAY_TIME = 1 * 24 * 3600 * 1000;
    @Override
    public ResponseVO<Boolean> save(ActivityRedemptionCodeConfigVO activityRedemptionCodeConfigVO) {
        SiteActivityRedemptionCodeBasePO basePO = BeanUtil.copyProperties(activityRedemptionCodeConfigVO,SiteActivityRedemptionCodeBasePO.class);
        log.info("save SiteActivityRedemptionCodeBasePO:{}",basePO);
        String orderNo = OrderNoUtils.generateOrderNo(CurrReqUtils.getAccount());
        basePO.setOrderNo(orderNo);
        //默认开启客户端开关
        basePO.setClientSwitch(CommonConstant.business_one);
        //兑换码详情列表
        List<SiteActivityRedemptionCodeDetailVO> detailVOS = activityRedemptionCodeConfigVO.getSiteActivityRedemptionCodeDetailVOS();
        BeanUtil.setFieldValue(detailVOS,"orderNo",orderNo);
        BeanUtil.setFieldValue(detailVOS,"activityId",Long.parseLong(basePO.getId()));
        List<SiteActivityRedemptionCodeDetailPO> detailPOS = BeanUtil.copyToList(detailVOS, SiteActivityRedemptionCodeDetailPO.class);
        log.info("save batch SiteActivityRedemptionCodeDetailPO:{}",detailPOS);
        this.activityRedemptionCodeBaseService.save(basePO);
        //批量保存兑换码详情
        this.activityRedemptionCodeDetailService.saveBatch(detailPOS);
        //根据配置信息生成兑换码
        List<SiteActivityRedemptionGenCodePO> genCodePOS = this.createCode(detailVOS);
        //批量保存生产的兑换码
        this.activityRedemptionGenCodeSerivce.saveBatch(genCodePOS);

        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Boolean> update(ActivityRedemptionCodeConfigVO activityRedemptionCodeConfigVO) {

        SiteActivityRedemptionCodeBasePO basePO = BeanUtil.copyProperties(activityRedemptionCodeConfigVO,SiteActivityRedemptionCodeBasePO.class);
        log.info("update SiteActivityRedemptionCodeBasePO:{}",basePO);
        //兑换码详情列表
        List<SiteActivityRedemptionCodeDetailVO> detailVOS = activityRedemptionCodeConfigVO.getSiteActivityRedemptionCodeDetailVOS();
        List<SiteActivityRedemptionCodeDetailPO> detailPOS = BeanUtil.copyToList(detailVOS, SiteActivityRedemptionCodeDetailPO.class);
        log.info("update batch SiteActivityRedemptionCodeDetailPO:{}",detailPOS);
        this.activityRedemptionCodeBaseService.saveOrUpdate(basePO);
        //批量保存兑换码详情
        this.activityRedemptionCodeDetailService.saveOrUpdateBatch(detailPOS);
        //根据配置信息生成兑换码
        List<SiteActivityRedemptionGenCodePO> genCodePOS = this.createCode(detailVOS);
        //批量保存生产的兑换码
        this.activityRedemptionGenCodeSerivce.saveBatch(genCodePOS);

        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Page<SiteActivityRedemptionCodeBaseRespVO>> redemptionCodeBasePageList(ActivityRedemptionCodeReqVO vo) {
        return ResponseVO.success(activityRedemptionCodeDetailService.pageList(vo));
    }

    @Override
    public ResponseVO<SiteActivityRedemptionCodeBaseRespVO> info(ActivityIdReqVO activityIdReqVO) {

        SiteActivityRedemptionCodeBasePO basePO = null;
        SiteActivityRedemptionCodeBaseRespVO baseVO;
        String activityId = activityIdReqVO.getId();
        long id ;
        if (StrUtil.isNotBlank(activityId)){
            id = Long.parseLong(activityId);
            basePO = activityRedemptionCodeBaseService.getById(id);
        }else {
            throw new BaowangDefaultException(ResultCode.ACTIVITY_NOT);
        }
        baseVO = BeanUtil.copyProperties(basePO, SiteActivityRedemptionCodeBaseRespVO.class);
        return ResponseVO.success(baseVO);
    }

    @Override
    public ResponseVO<Boolean> delete(String activityId) {

        LambdaUpdateWrapper<SiteActivityRedemptionCodeBasePO> updateWrapper = Wrappers.lambdaUpdate(SiteActivityRedemptionCodeBasePO.class);
        SiteActivityRedemptionGenCodeVO genCodeVO = SiteActivityRedemptionGenCodeVO.builder().build();
        SiteActivityRedemptionCodeDetailVO detailVO = SiteActivityRedemptionCodeDetailVO.builder().build();
        detailVO.setActivityId(Long.parseLong(activityId));
        SiteActivityRedemptionCodeDetailPO detailPO = this.activityRedemptionCodeDetailService.info(detailVO);
        genCodeVO.setActivityDetailId(Long.parseLong(detailPO.getId()));
        SiteActivityRedemptionGenCodeVO delGenCodeVO = this.activityRedemptionGenCodeSerivce.info(genCodeVO).getData();
        //删除之前先检查兑换表中是否有兑换记录,如已产生兑换记录,则不能禁用
        if (Objects.nonNull(delGenCodeVO)){
            throw new BaowangDefaultException("会员已兑换了本批次兑换码已,删除失败!");
        }
        updateWrapper.eq(SiteActivityRedemptionCodeBasePO::getId,Long.parseLong(activityId));
        //设置为禁用
        updateWrapper.set(SiteActivityRedemptionCodeBasePO::getStatus,CommonConstant.business_zero);
        this.activityRedemptionCodeBaseService.update(updateWrapper);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<SiteActivityRedemptionGenCodeVO>> export(String activityDetailId) {
        return null;
    }

    @Override
    public ResponseVO<Boolean> check(ActivityRedemptionCodeConfigVO vo) {

        if (Objects.isNull(vo) || StrUtil.isEmpty(vo.getPlatformOrFiatCurrency())
            || StrUtil.isEmpty(vo.getSiteCode())){
            throw new BaowangDefaultException(ResultCode.PARAM_NOT_VALID);
        }
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Boolean> exchange(String userId, String code) {

        boolean flag = false;
        Long startTime;
        Long endTime;
        Long firstDepositTime;
        String value;
        String message = "";
        int exchangeCondition;
        int category;
        int topLimit;

        ResponseVO<Boolean> responseVO = null;
        UserDepositRecordRespVO userDepositRecordRespVO;
        UserDepositRecordPageVO userDepositRecordPageVO;
        SiteActivityRedemptionGenCodePO genCodePO;
        LambdaUpdateWrapper<SiteActivityRedemptionGenCodePO> updateWrapper = Wrappers.lambdaUpdate(SiteActivityRedemptionGenCodePO.class);
        SiteActivityRedemptionCodeExchangePO po = new SiteActivityRedemptionCodeExchangePO();
        SiteActivityRedemptionGenCodeVO genCodeVO = this.getGenCodeByCode(code).getData();
        List<SiteActivityRedemptionCodeExchangePO> exchangePOS = this.activityRedemptionCodeExchangeService.list();
        SiteActivityRedemptionCodeDetailPO detailPO = this.activityRedemptionCodeDetailService.getById(genCodeVO.getActivityDetailId());
        UserInfoVO userInfo = userInfoApi.getByUserId(userId);

        //兑换码生效时间
        startTime = detailPO.getStartTime();
        //兑换码失效时间
        endTime = detailPO.getEndTime();
        // 首次存款时间
        firstDepositTime = userInfo.getFirstDepositTime();

        value = this.systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_REDEMPTION_CODE_DEPOSIT_DAYS.getCode(),userInfo.getSiteCode()).getData().getConfigParam();

        //当天存款用户记录
        userDepositRecordPageVO = new UserDepositRecordPageVO();
        userDepositRecordPageVO.setUserAccount(userInfo.getUserAccount());

        //获取兑换条件,1:无限制用户,2:存款用户,3:当天存款用户，4:三天内存款用户
        exchangeCondition = detailPO.getCondition();
        //兑换码类型,0:通用兑换码,1:唯一兑换码
        category = detailPO.getCategory();
        //兑换上限,兑换使用人数上限,兑换码类型=0，且top_limit=0时，表示为通用码，没有兑换人数限制；兑换码类型=1时，top_limit=1,1人1码
        topLimit = detailPO.getTopLimit();

        if (value == null){
            userRedemptionCodeDepositDays = 2;
        }else{
            userRedemptionCodeDepositDays = Integer.valueOf(value);
        }

        po.setCode(code);
        po.setAmount(detailPO.getAward());
        po.setCurrency(detailPO.getCurrency());
        po.setBatchNo(genCodeVO.getBatchNo());
        po.setUserId(userId);
        po.setCategory(category);
        po.setOrderNo(detailPO.getOrderNo());

        //兑换条件判断
        switch (exchangeCondition){
            //无限制用户
            case 1:
                //调用兑换逻辑
                flag = this.executeExchange(detailPO,po);
                break;
            //存款用户
            case 2:
                if(firstDepositTime != null && firstDepositTime > 0){
                    //调用兑换逻辑
                    flag = this.executeExchange(detailPO,po);
                }else {
                    message="兑换失败,会员:"+userId+"无充值记录!";
                }
                break;
            //当天存款用户
            case 3:
                userDepositRecordPageVO.setCreateStartTime(startTime);
                userDepositRecordPageVO.setCreateEndTime(startTime + _1_DAY_TIME);
                userDepositRecordRespVO = userDepositRecordApi.getUserDepositRecord(userDepositRecordPageVO);
                if (userDepositRecordRespVO.getTotalSuccessNum() > 0){
                    //调用兑换逻辑
                    flag = this.executeExchange(detailPO,po);
                }else{
                    message = "兑换失败,会员："+userId+"在兑换码生效当天无充值记录!";
                }
                break;
            //三天内存款用户
            case 4:
                userDepositRecordPageVO.setCreateStartTime(startTime - _1_DAY_TIME * userRedemptionCodeDepositDays );
                userDepositRecordPageVO.setCreateEndTime(startTime + _1_DAY_TIME * userRedemptionCodeDepositDays);
                userDepositRecordRespVO = userDepositRecordApi.getUserDepositRecord(userDepositRecordPageVO);
                if (userDepositRecordRespVO.getTotalSuccessNum() > 0){
                    //调用兑换逻辑
                    flag = this.executeExchange(detailPO,po);
                }else{
                    message = "兑换失败,会员："+userId+"在兑换码生效三天内无充值记录!";
                }
                break;
            default:
                //没有满足兑换条件,不兑换
                message = "兑换失败,没有找到匹配的兑换条件";
        }
        //兑换成功
        if (flag){
            //更新兑换码状态
            updateWrapper.eq(SiteActivityRedemptionGenCodePO::getId,genCodeVO.getId());
            updateWrapper.set(SiteActivityRedemptionGenCodePO::getStatus,CommonConstant.business_one);
            this.activityRedemptionGenCodeSerivce.update(updateWrapper);
            //兑换成功后发消息到会员福利中心
            responseVO = ResponseVO.success();
            //发消息
            pushRewardMessage(userInfo,detailPO);
        }else {
            responseVO = ResponseVO.fail(ResultCode.ACTIVITY_REDEMPTION_CODE_COMMON,message);
        }

        return responseVO;
    }

    @Override
    public ResponseVO<SiteActivityRedemptionGenCodeVO> getGenCodeByCode(String code) {

        return this.activityRedemptionGenCodeSerivce.getRedemptionGenCodeByCode(code);
    }

    @Override
    public ResponseVO<List<SiteActivityRedemptionGenCodeVO>> queryGenCodeList(String activityDetailId, String batchNo) {

        return null;
    }

    @Override
    public ResponseVO<SiteActivityRedemptionCodeDetailVO> getActivityRedemptionCodeDetailVOById(long activityDetailId) {

        SiteActivityRedemptionCodeDetailPO detailPO = this.activityRedemptionCodeDetailService.getById(activityDetailId);
        SiteActivityRedemptionCodeDetailVO detailVO = BeanUtil.copyProperties(detailPO,SiteActivityRedemptionCodeDetailVO.class);

        return ResponseVO.success(detailVO);
    }

    @Override
    public ResponseVO<SiteActivityRedemptionCodeExchangeVO> getRedemptionCodeExchangeVO(SiteActivityRedemptionCodeExchangeVO exchangeVO) {
        return this.activityRedemptionCodeExchangeService.info(exchangeVO);
    }

    @Override
    public ResponseVO<Boolean> clientSwitch(Integer flag) {

        if (Objects.isNull(flag)){
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        LambdaUpdateWrapper<SiteActivityRedemptionCodeBasePO> updateWrapper = Wrappers.lambdaUpdate(SiteActivityRedemptionCodeBasePO.class);
        updateWrapper.set(SiteActivityRedemptionCodeBasePO::getClientSwitch,flag);
        boolean isSuccess = this.activityRedemptionCodeBaseService.update(updateWrapper);
        if (isSuccess){
            return ResponseVO.success();
        }
        return ResponseVO.fail(ResultCode.UPDATE_ERROR);
    }

    @Override
    public ResponseVO<Long> countRedemption(ActivityRedemptionCodeReqVO vo) {
        LambdaQueryWrapper<SiteActivityRedemptionCodeDetailPO> queryWrapper = Wrappers.lambdaQuery(SiteActivityRedemptionCodeDetailPO.class);


        return null;
    }


    /**
     * 执行兑换操作
     * @param detailPO
     * @return
     */
    private boolean executeExchange(SiteActivityRedemptionCodeDetailPO detailPO,SiteActivityRedemptionCodeExchangePO po){

        boolean flag = false;
        SiteActivityRedemptionCodeExchangeVO exchangeVO = SiteActivityRedemptionCodeExchangeVO.builder().build();
        exchangeVO.setCode(po.getCode());
        //已兑换数量
        Long exchangedCount = this.activityRedemptionCodeExchangeService.countExchanged(exchangeVO);

        //通用兑换码
        if (detailPO.getCategory() == ActivityRedemptionCodeEnum.ACTIVITY_REDEMPTION_CODE_CATEGORY_COMMON.getCode()){
            //通用兑换码,没有兑换人数限制
            if (detailPO.getTopLimit() == CommonConstant.business_zero){
                flag = activityRedemptionCodeExchangeService.save(po);
                //判断是否可以兑换
            }else if (exchangedCount >= detailPO.getTopLimit()){
                throw new BaowangDefaultException("兑换失败,兑换码："+po.getCode()+"的兑换数量已用完");
                //可以兑换
            }else {
                flag = activityRedemptionCodeExchangeService.save(po);
            }
            //唯一验证码
        }else {
            //exchangedCount=0表示此兑换码未领取奖励,可以兑换
            if(exchangedCount < detailPO.getQuantity()){
                flag = activityRedemptionCodeExchangeService.save(po);
            }else{
                throw new BaowangDefaultException("兑换失败,兑换码："+po.getCode()+"的兑换数量已用完");
            }
        }
        return flag;
    }

    /**
     * 向会员福利中心发生兑换码奖励领取消息
     * @param userInfoVO
     * @param detailPO
     */
    private void pushRewardMessage(UserInfoVO userInfoVO,SiteActivityRedemptionCodeDetailPO detailPO){

        String processCurrency;
        BigDecimal rate;
        BigDecimal amount;

        Long activityId = detailPO.getActivityId();
        String siteCode = detailPO.getSiteCode();
        String userId = userInfoVO.getUserId();
        List<ActivitySendMqVO> sendMqVOList = Lists.newArrayList();
        ActivitySendMqVO sendMqVO = new ActivitySendMqVO();
        //站点币种汇率表
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(detailPO.getSiteCode());

        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("兑换码货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", detailPO.getSiteCode(), userInfoVO.getUserId(), currencyRateMap);
            return;
        }
        processCurrency = (("1".equals(detailPO.getCurrency()) ? detailPO.getCurrency():"WTC"));
        rate = currencyRateMap.get(processCurrency);
        //充值金额
        amount = AmountUtils.divide(detailPO.getAward(), rate);
        //生成消息订单号
        String orderNo = OrderNoUtils.genOrderNo(userId, activityId +"");
        sendMqVO.setOrderNo(orderNo);

        sendMqVO.setActivityId(activityId+"");
        sendMqVO.setUserId(userId);
        sendMqVO.setSiteCode(siteCode);
        sendMqVO.setDistributionType(1);
        sendMqVO.setActivityTemplate("ACTIVITY_REDEMPTION_CODE_EXCHANGE");
        sendMqVO.setReceiveStartTime(System.currentTimeMillis());
        //48小时失效
        sendMqVO.setReceiveEndTime(System.currentTimeMillis()+_1_DAY_TIME * userRedemptionCodeDepositDays);
        sendMqVO.setActivityAmount(amount);
        sendMqVO.setCurrencyCode(detailPO.getCurrency());
        sendMqVO.setRunningWater(detailPO.getWashRatio());
        sendMqVO.setRunningWaterMultiple(detailPO.getAward().multiply(detailPO.getWashRatio()));
        sendMqVO.setParticipationMode(0);
        //国际盘口模式
        sendMqVO.setHandicapMode(SiteHandicapModeEnum.Internacional.getCode());
        log.info("starting to send member redemption code exchange message:{}",sendMqVO);
        sendMqVOList.add(sendMqVO);
        ActivitySendListMqVO sendListMqVO = new ActivitySendListMqVO();
        sendListMqVO.setList(sendMqVOList);
        //发送奖金消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, sendListMqVO);
        log.info("finished send member redemption code exchange message:{}",sendMqVO);

    }

    /**
     *
     * @param detailVOS
     * @return
     */
    private List<SiteActivityRedemptionGenCodePO> createCode(List<SiteActivityRedemptionCodeDetailVO> detailVOS){

        String batchNo= RandomUtil.randomNumbers(CommonConstant.business_six);
        SiteActivityRedemptionGenCodePO  genCodePO = null;
        List<SiteActivityRedemptionGenCodePO> genCodePOS = new ArrayList<>();
        if (Objects.nonNull(detailVOS) && detailVOS.size() > CommonConstant.business_zero){
            for (SiteActivityRedemptionCodeDetailVO vo : detailVOS){
                //根据配置信息中的兑换码数量生成等量兑换码,按最大数量生成
                int quantity = vo.getQuantity() > vo.getTopLimit()?vo.getQuantity(): vo.getTopLimit();

                for (int i = 0;i<quantity;i++){
                    genCodePO = new SiteActivityRedemptionGenCodePO();
                    genCodePO.setCode(RandomStringUtil.generateRandomString(CommonConstant.business_eight));
                    genCodePO.setCurrency(vo.getCurrency());
                    genCodePO.setBatchNo(batchNo);
                    genCodePO.setActivityDetailId(vo.getId());
                    genCodePO.setStatus(CommonConstant.business_one);
                    genCodePOS.add(genCodePO);
                }
            }
        }
        return genCodePOS;
    }
}
