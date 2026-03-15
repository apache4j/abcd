package com.cloud.baowang.wallet.service;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.NetWorkTypeEnum;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.ECDSAUtil;
import com.cloud.baowang.common.core.utils.HttpClientUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.pay.api.api.VirtualCurrencyPayApi;
import com.cloud.baowang.pay.api.vo.HotWalletAddressRequestVO;
import com.cloud.baowang.pay.api.vo.HotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.recharge.CryptoCurrencyDepositAddressVO;
import com.cloud.baowang.wallet.api.vo.recharge.GenHotWalletAddressReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.HotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AddressBalanceMessageVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.AgentHotWalletAddressVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.BatchCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.SingleCollectVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressRequestVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressResponseVO;
import com.cloud.baowang.wallet.api.vo.userCoinRecord.UserHotWalletAddressVO;
import com.cloud.baowang.wallet.po.HotWalletAddressPO;
import com.cloud.baowang.wallet.repositories.HotWalletAddressRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HotWalletAddressService extends ServiceImpl<HotWalletAddressRepository, HotWalletAddressPO> {


    private final VirtualCurrencyPayApi virtualCurrencyPayApi;

    @Value("${common.config.jvPayDomain}")
    private String jvPayDomainUrl;

    @Value("${common.config.jvPayPrivateKey}")
    private String jvPayPrivateKey;

    @Autowired
    public HotWalletAddressService(VirtualCurrencyPayApi virtualCurrencyPayApi) {
        this.virtualCurrencyPayApi = virtualCurrencyPayApi;
    }


    @DistributedLock(name = RedisConstants.RECHARGE_GEN_HOT_WALLET_ADDRESS, unique = "#genHotWalletAddressReqVO.oneId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<String> getHotWalletAddress(GenHotWalletAddressReqVO genHotWalletAddressReqVO){
        String oneId = genHotWalletAddressReqVO.getOneId();
        String oneAccount = genHotWalletAddressReqVO.getOneAccount();
        String networkType = genHotWalletAddressReqVO.getNetworkType();
        String siteCode = genHotWalletAddressReqVO.getSiteCode();
        String ownerUserType = genHotWalletAddressReqVO.getOwnerUserType();
        String currencyCode = genHotWalletAddressReqVO.getCurrencyCode();
        String extractParam = genHotWalletAddressReqVO.getExtractParam();
        LambdaQueryWrapper<HotWalletAddressPO> hotLqw = new LambdaQueryWrapper<>();
        String chanType = NetWorkTypeEnum.nameOfCode(networkType).getType();
        String outAddressNo = chanType+siteCode+oneId+ownerUserType+currencyCode;
        hotLqw.eq(HotWalletAddressPO::getOutAddressNo,outAddressNo);
        hotLqw.eq(HotWalletAddressPO::getUserId,oneId);

        hotLqw.eq(HotWalletAddressPO::getChainType,chanType) ;
        HotWalletAddressPO hotWalletAddressPO = this.baseMapper.selectOne(hotLqw);
        CryptoCurrencyDepositAddressVO currencyDepositAddressVO = new CryptoCurrencyDepositAddressVO();
        HotWalletAddressRequestVO hotWalletAddressRequestVO = new HotWalletAddressRequestVO();
        hotWalletAddressRequestVO.setChainType(chanType);
        hotWalletAddressRequestVO.setOutAddressNo(outAddressNo);
        hotWalletAddressRequestVO.setCurrencyCode(currencyCode);
        hotWalletAddressRequestVO.setExtractParam(extractParam);
        hotWalletAddressRequestVO.setOwnerUserId(oneId);
        hotWalletAddressRequestVO.setOwnerUserType(ownerUserType);
        hotWalletAddressRequestVO.setPlatNo(siteCode);
        ResponseVO<HotWalletAddressResponseVO> responseVO = virtualCurrencyPayApi.createHotWalletAddress(hotWalletAddressRequestVO);
        if(responseVO.isOk() ){
            HotWalletAddressResponseVO hotWalletAddressResponseVO = responseVO.getData();
            currencyDepositAddressVO.setChinaType(chanType);
            currencyDepositAddressVO.setUserId(hotWalletAddressResponseVO.getOwnerUserId());
            currencyDepositAddressVO.setAddress(hotWalletAddressResponseVO.getAddressNo());
            if(null ==  hotWalletAddressPO){
                HotWalletAddressPO hotWalletAddressPOInsert =new HotWalletAddressPO();
                hotWalletAddressPOInsert.setChainType(chanType);
                hotWalletAddressPOInsert.setOutAddressNo(outAddressNo);
                hotWalletAddressPOInsert.setCurrencyCode(currencyCode);
                hotWalletAddressPOInsert.setExtractParam(extractParam);
                hotWalletAddressPOInsert.setUserId(oneId);
                hotWalletAddressPOInsert.setAddress(hotWalletAddressResponseVO.getAddressNo());
                hotWalletAddressPOInsert.setSiteCode(siteCode);
                hotWalletAddressPOInsert.setNetworkType(networkType);
                hotWalletAddressPOInsert.setUserAccount(oneAccount);
                hotWalletAddressPOInsert.setWalletUserType(ownerUserType);
                this.baseMapper.insert(hotWalletAddressPOInsert);
            }else {
                if(!hotWalletAddressPO.getAddress().equals(hotWalletAddressResponseVO.getAddressNo())){
                    hotWalletAddressPO.setAddress(hotWalletAddressResponseVO.getAddressNo());
                    this.baseMapper.updateById(hotWalletAddressPO);
                }
                currencyDepositAddressVO = ConvertUtil.entityToModel(hotWalletAddressPO,CryptoCurrencyDepositAddressVO.class);
            }
            return ResponseVO.success(currencyDepositAddressVO.getAddress());
        }else{
            return ResponseVO.fail(responseVO.getCode());
        }
    }


    public HotWalletAddressVO queryHotWalletAddress(String address) {
        HotWalletAddressPO hotWalletAddressPO =  this.baseMapper.selectOne(new LambdaQueryWrapper<HotWalletAddressPO>().eq(HotWalletAddressPO::getAddress,address));
        return ConvertUtil.entityToModel(hotWalletAddressPO,HotWalletAddressVO.class);
    }

    public List<HotWalletAddressVO> queryHotWalletAddressByUserId(String userId) {
        List<HotWalletAddressPO> hotWalletAddressPOList = this.baseMapper
                .selectList(new LambdaQueryWrapper<HotWalletAddressPO>().eq(HotWalletAddressPO::getUserId, userId));
        return ConvertUtil.entityListToModelList(hotWalletAddressPOList,HotWalletAddressVO.class);
    }

    public ResponseVO<UserHotWalletAddressResponseVO> listUserHotAddress(UserHotWalletAddressRequestVO vo) {
        Page<HotWalletAddressPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<UserHotWalletAddressVO> userHotWalletAddressVOPage = this.baseMapper.listUserHotAddress(page,vo);
        UserHotWalletAddressResponseVO userHotWalletAddressResponseVO = new UserHotWalletAddressResponseVO();
        userHotWalletAddressResponseVO.setUserHotWalletAddressVOPage(userHotWalletAddressVOPage);

        //汇总小计
        userHotWalletAddressResponseVO.setCurrentPage(getSubtotal(userHotWalletAddressVOPage.getRecords()));

        //汇总总计
        userHotWalletAddressResponseVO.setTotalPage(getTotal(vo));
        return ResponseVO.success(userHotWalletAddressResponseVO);
    }

    /**
     * 会员小计
     * @param userHotWalletAddressVOList
     * @return
     */
    public UserHotWalletAddressVO getSubtotal(List<UserHotWalletAddressVO> userHotWalletAddressVOList) {
        //汇总小计
        BigDecimal sumTrcBalance = userHotWalletAddressVOList.stream()
                .map(UserHotWalletAddressVO::getTrcAddressBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumErcBalance = userHotWalletAddressVOList.stream()
                .map(UserHotWalletAddressVO::getErcAddressBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        UserHotWalletAddressVO userHotWalletAddressVO = new UserHotWalletAddressVO();
        userHotWalletAddressVO.setUserId("小计");
        userHotWalletAddressVO.setTrcAddressBalance(sumTrcBalance);
        userHotWalletAddressVO.setErcAddressBalance(sumErcBalance);

        return userHotWalletAddressVO;
    }

    public Long userHotWalletAddressPageCount(UserHotWalletAddressRequestVO vo) {
        return this.baseMapper.userHotWalletAddressPageCount(vo);
    }

    public UserHotWalletAddressVO getTotal(UserHotWalletAddressRequestVO vo) {
        return this.baseMapper.sumUserHotWalletAddress(vo);
    }




    public ResponseVO<AgentHotWalletAddressResponseVO> listAgentHotAddress(AgentHotWalletAddressRequestVO vo) {
        Page<HotWalletAddressPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        Page<AgentHotWalletAddressVO> agentHotWalletAddressVOPage = this.baseMapper.listAgentHotAddress(page,vo);
        AgentHotWalletAddressResponseVO agentHotWalletAddressResponseVO = new AgentHotWalletAddressResponseVO();
        agentHotWalletAddressResponseVO.setAgentHotWalletAddressVOPage(agentHotWalletAddressVOPage);

        //汇总小计
        agentHotWalletAddressResponseVO.setCurrentPage(getAgentSubtotal(agentHotWalletAddressVOPage.getRecords()));

        //汇总总计
        agentHotWalletAddressResponseVO.setTotalPage(getAgentTotal(vo));
        return ResponseVO.success(agentHotWalletAddressResponseVO);
    }

    public AgentHotWalletAddressVO getAgentSubtotal(List<AgentHotWalletAddressVO> agentHotWalletAddressVOList) {
        //汇总小计
        BigDecimal sumTrcBalance = agentHotWalletAddressVOList.stream()
                .map(AgentHotWalletAddressVO::getTrcAddressBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumErcBalance = agentHotWalletAddressVOList.stream()
                .map(AgentHotWalletAddressVO::getErcAddressBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        AgentHotWalletAddressVO agentHotWalletAddressVO = new AgentHotWalletAddressVO();
        agentHotWalletAddressVO.setAgentAccount("小计");
        agentHotWalletAddressVO.setTrcAddressBalance(sumTrcBalance);
        agentHotWalletAddressVO.setErcAddressBalance(sumErcBalance);

        return agentHotWalletAddressVO;
    }

    public Long agentHotWalletAddressPageCount(AgentHotWalletAddressRequestVO vo) {
        return this.baseMapper.agentHotWalletAddressPageCount(vo);
    }

    public AgentHotWalletAddressVO getAgentTotal(AgentHotWalletAddressRequestVO vo) {
        return this.baseMapper.sumAgentHotWalletAddress(vo);
    }

    public void updateAddressBalance(AddressBalanceMessageVO addressBalanceMessageVO) {
        LambdaUpdateWrapper<HotWalletAddressPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(HotWalletAddressPO::getChainType,addressBalanceMessageVO.getChainType());
        updateWrapper.eq(HotWalletAddressPO::getAddress,addressBalanceMessageVO.getAddressNo());
        updateWrapper.set(HotWalletAddressPO::getBalance,addressBalanceMessageVO.getBalance());
        this.baseMapper.update(null,updateWrapper);
    }

    public HotWalletAddressVO queryHotWalletAddressByOutAddressNo(String outAddressNo) {
        LambdaQueryWrapper<HotWalletAddressPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(HotWalletAddressPO::getOutAddressNo,outAddressNo);
        HotWalletAddressPO hotWalletAddressPO = this.baseMapper.selectOne(lqw);

        return  ConvertUtil.entityToModel(hotWalletAddressPO,HotWalletAddressVO.class);
    }

    public ResponseVO<Void> singleCollect(SingleCollectVO singleCollectVO) {

        try {
            final String apiUrl = jvPayDomainUrl + "/api/balance/manualSingleCollect";
            String paramJson = JSON.toJSONString(singleCollectVO);
            Map<String,String> headerMap = buildHeadMap(paramJson);
            String result = HttpClientUtil.doPostJson(apiUrl, paramJson,headerMap);
            log.info("单个归集提交商户号结果：" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("code");
            if (success != null && success.equals("200")) {
                log.error("http request success:{1}", jsonObject.get("code"));
                return ResponseVO.success();
            }
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        } catch (Exception e) {
            log.error("http request error:{0}", e.getMessage());
        }
        return ResponseVO.fail(ResultCode.PARAM_ERROR);
    }

    public ResponseVO<Void> batchCollect(BatchCollectVO batchCollectVO){
        try {
            final String apiUrl = jvPayDomainUrl + "/api/balance/manualBatchCollect";
            String paramJson = JSON.toJSONString(batchCollectVO);
            Map<String,String> headerMap = buildHeadMap(paramJson);
            String result = HttpClientUtil.doPostJson(apiUrl, paramJson,headerMap);
            log.info("批量归集提交商户号结果：" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            Object success = jsonObject.get("code");
            if (success != null && success.equals("200")) {
                log.error("http request success:{1}", jsonObject.get("code"));
                return ResponseVO.success();
            }
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        } catch (Exception e) {
            log.error("http request error:{0}", e.getMessage());
        }
        return ResponseVO.fail(ResultCode.PARAM_ERROR);
    }

    /**
     * 报文加密
     * @param paramJson 请求参数
     * @return
     */
    private Map<String, String> buildHeadMap(String paramJson) {
        String  timestamp=System.currentTimeMillis()+"";
        String   random= RandomStringUtils.randomAlphabetic(6);
        String signVal="";
        if(JSON.isValidArray(paramJson)){
            JSONArray bodyJsonArray=JSONArray.parseArray(paramJson);
            signVal= ECDSAUtil.signParam(timestamp,random, bodyJsonArray,jvPayPrivateKey);
        }else {
            signVal=ECDSAUtil.signParam(timestamp,random, JSON.parseObject(paramJson),jvPayPrivateKey);
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(WalletConstants.HEAD_TIMESTAMP,timestamp);
        headerMap.put(WalletConstants.HEAD_RANDOM,random);
        headerMap.put(WalletConstants.HEAD_SIGN_NAME,signVal);
        return headerMap;
    }
}
