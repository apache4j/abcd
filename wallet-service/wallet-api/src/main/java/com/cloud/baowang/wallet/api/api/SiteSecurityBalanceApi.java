package com.cloud.baowang.wallet.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.wallet.api.ApiConstants;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityApplyReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceChangeRecordReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceInitReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceOverdrawAmountReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalancePageReqVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceRespVO;
import com.cloud.baowang.wallet.api.vo.siteSecurity.SiteSecurityBalanceThresholdAmountReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 保证金余额相关接口
 */
@FeignClient(contextId = "remoteSiteSecurityBalanceApi",value = ApiConstants.NAME)
@Tag(name = "RPC服务-保证金余额api")
public interface SiteSecurityBalanceApi {

    String PREFIX = ApiConstants.PREFIX + "/site/SiteSecurityBalance/";

    @PostMapping(value = PREFIX + "init")
    @Operation(summary = "初始化保证金余额")
    ResponseVO<Void> init(@RequestBody SiteSecurityBalanceInitReqVO securityBalanceInitReqVO);

    //分页查询接口
    @PostMapping(value = PREFIX + "listPage")
    @Operation(summary = "分页查询接口")
    ResponseVO<Page<SiteSecurityBalanceRespVO>> listPage(@RequestBody SiteSecurityBalancePageReqVO siteSecurityBalancePageReqVO);

    /**
     * 调整保证金金额
     * @param siteSecurityApplyReqVO 参数
     * @return
     */
    @PostMapping(value = PREFIX + "adjustAmount")
    @Operation(summary = "调整保证金金额")
    ResponseVO<Void> adjustAmount(@RequestBody SiteSecurityApplyReqVO siteSecurityApplyReqVO);


    @PostMapping(value = PREFIX + "adminSetOverdrawAmount")
    @Operation(summary = "保证金透支额度设置接口")
    ResponseVO<Void> adminSetOverdrawAmount(@RequestBody SiteSecurityBalanceOverdrawAmountReqVO siteSecurityBalanceOverdrawAmountReqVO);

    //预警设置接口
    @PostMapping(value = PREFIX + "adminSetThresholdAmount")
    @Operation(summary = "预警阀值设置接口")
    ResponseVO<Void> adminSetThresholdAmount(@RequestBody SiteSecurityBalanceThresholdAmountReqVO siteSecurityBalanceThresholdAmountReqVO);




    /**
     * 会员代理充值成功后调用
     * 会员代理提现申请、失败后调用
     *
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
    @PostMapping(value = PREFIX + "afterDepositOrWithdraw")
    @Operation(summary = "保证金变更")
    ResponseVO<Void> afterDepositOrWithdraw(@RequestBody SiteSecurityBalanceChangeRecordReqVO vo);


    @PostMapping(value = PREFIX + "isClosed")
    @Operation(summary = "保证金是否被关闭")
    boolean isClosed(@RequestBody String siteCode);

    @PostMapping(value = PREFIX + "findBySiteCode")
    @Operation(summary = "按照站点查询保证金")
    SiteSecurityBalanceRespVO findBySiteCode(@RequestBody String siteCode);
}
