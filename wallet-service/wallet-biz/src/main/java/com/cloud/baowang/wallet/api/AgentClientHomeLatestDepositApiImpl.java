package com.cloud.baowang.wallet.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.wallet.api.vo.agent.DepositRecordResponseVO;
import com.cloud.baowang.wallet.api.vo.agent.LatestDepositParam;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.api.AgentClientHomeLatestDepositApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.DepositRecordParam;
import com.cloud.baowang.wallet.service.UserDepositRecordService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentClientHomeLatestDepositApiImpl implements AgentClientHomeLatestDepositApi {

    private UserDepositRecordService userDepositRecordService;

    private SiteCurrencyInfoApi siteCurrencyInfoApi;


    /**
     * 查询当前代理 下会员充值最新五条数据
     * @param vo
     * @return
     */
    @Override
    public ResponseVO<List<DepositRecordResponseVO>> latestDeposit(LatestDepositParam vo) {
        String currentAgent=vo.getCurrentAgent();
         String siteCode=vo.getSiteCode();
        DepositRecordParam param = new DepositRecordParam();
        param.setSiteCode(siteCode);
        param.setAgentAccount(currentAgent);
        param.setPageNumber(CommonConstant.business_one);
        param.setPageSize(CommonConstant.business_five);
        //非平台币 进行过滤
        if(!CommonConstant.PLAT_CURRENCY_CODE.equals(vo.getCurrencyCode())){
            param.setCurrencyCode(vo.getCurrencyCode());
        }
        Page<DepositRecordResponseVO> page = userDepositRecordService.depositRecord(param);
        List<DepositRecordResponseVO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return ResponseVO.success(Lists.newArrayList());
        }
        Map<String,BigDecimal> siteCurrencyRate=siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if(CommonConstant.PLAT_CURRENCY_CODE.equals(vo.getCurrencyCode())){
            for(DepositRecordResponseVO depositRecordResponseVO:records){
                //币种汇率转换为平台币
                BigDecimal rate=siteCurrencyRate.get(depositRecordResponseVO.getCurrencyCode());
                BigDecimal targetAmount=AmountUtils.divide(depositRecordResponseVO.getArriveAmount(),rate);
                depositRecordResponseVO.setArriveAmount(targetAmount);
                depositRecordResponseVO.setCurrencyCode(vo.getCurrencyCode());
            }
        }
        if (null != vo.getOrderField() && null != vo.getOrderType()) {
            if (CommonConstant.business_one.equals(vo.getOrderField())
                    && CommonConstant.business_one.equals(vo.getOrderType())) {
                // 1按照存款金额排序 1升序
                records = records.stream().sorted(Comparator.comparing(DepositRecordResponseVO::getArriveAmount)).toList();
            } else if (CommonConstant.business_one.equals(vo.getOrderField())
                    && CommonConstant.business_two.equals(vo.getOrderType())) {
                // 1按照存款金额排序 2降序
                records = records.stream().sorted(Comparator.comparing(DepositRecordResponseVO::getArriveAmount).reversed()).toList();
            } else if (CommonConstant.business_two.equals(vo.getOrderField())
                    && CommonConstant.business_one.equals(vo.getOrderType())) {
                // 2按照时间排序 1升序
                records = records.stream().sorted(Comparator.comparing(DepositRecordResponseVO::getUpdatedTime)).toList();
            } else if (CommonConstant.business_two.equals(vo.getOrderField())
                    && CommonConstant.business_two.equals(vo.getOrderType())) {
                // 2按照时间排序 2降序
                records = records.stream().sorted(Comparator.comparing(DepositRecordResponseVO::getUpdatedTime).reversed()).toList();
            }
        }
        return ResponseVO.success(records);
    }
}
