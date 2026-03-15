package com.cloud.baowang.admin.controller.recharge;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.admin.vo.CurrencyCodeReqVO;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.wallet.ChannelTypeEnum;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.SiteVipOptionApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.VIPGradeVO;
import com.cloud.baowang.wallet.api.api.SystemRechargeChannelApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeTypeApi;
import com.cloud.baowang.wallet.api.api.SystemRechargeWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SortNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelNewReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelRespVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelStatusReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeChannelUpdateReqVO;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 17:25
 * @Version: V1.0
 **/
@RestController
@Tag(name = "金流-充值配置-充值通道")
@RequestMapping("/exchange/rechargeChannel")
@AllArgsConstructor
public class SystemRechargeChannelController {

    private final SystemRechargeChannelApi systemRechargeChannelApi;

    private final SystemRechargeTypeApi systemRechargeTypeApi;

    private final SystemRechargeWayApi systemRechargeWayApi;

    private final VipRankApi vipRankApi;

    private final SiteVipOptionApi siteVipOptionApi;


    @PostMapping("selectPage")
    @Operation(summary = "充值通道分页查询")
    ResponseVO<Page<SystemRechargeChannelRespVO>> selectPage(@RequestBody @Validated SystemRechargeChannelReqVO systemRechargeChannelReqVO){
        return systemRechargeChannelApi.selectPage(systemRechargeChannelReqVO);
    }
    //充值通道、提现数据 开发人员进行初始化,运营人员只能修改;
    @PostMapping("insert")
    @Operation(summary = "充值通道新增")
    ResponseVO<Void> insert(@RequestBody @Validated SystemRechargeChannelNewReqVO systemRechargeChannelReqNewVO){
        ResponseVO responseVO=paramValid(systemRechargeChannelReqNewVO);
        if(!responseVO.isOk()){
            return responseVO;
        }
        systemRechargeChannelReqNewVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeChannelApi.insert(systemRechargeChannelReqNewVO);
    }


    /**
     * 通道配置参数校验
     * @param systemRechargeChannelReqNewVO
     * @return
     */
    private ResponseVO<Void> paramValid(SystemRechargeChannelNewReqVO systemRechargeChannelReqNewVO) {
        if(ChannelTypeEnum.THIRD.getCode().equals(systemRechargeChannelReqNewVO.getChannelType())){
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getChannelCode())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getChannelName())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getChannelCode())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getMerNo())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getPubKey())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(systemRechargeChannelReqNewVO.getRechargeMin()==null){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(systemRechargeChannelReqNewVO.getRechargeMax()==null){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            if(!StringUtils.hasText(systemRechargeChannelReqNewVO.getPubKey())){
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
        }
        return ResponseVO.success();
    }

    @PostMapping("update")
    @Operation(summary = "充值通道修改")
    ResponseVO<Void> update(@RequestBody @Validated SystemRechargeChannelUpdateReqVO systemRechargeChannelUpdateReqVO){
        ResponseVO responseVO=paramValid(systemRechargeChannelUpdateReqVO);
        if(!responseVO.isOk()){
            return responseVO;
        }
        systemRechargeChannelUpdateReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeChannelApi.update(systemRechargeChannelUpdateReqVO);
    }

    @PostMapping("selectBySort")
    @Operation(summary = "充值通道排序")
    ResponseVO<List<SystemRechargeChannelRespVO>> selectBySort(@RequestBody @Validated SystemRechargeChannelReqVO systemRechargeChannelReqVO){
        return systemRechargeChannelApi.selectBySort(systemRechargeChannelReqVO);
    }

    @PostMapping("batchSave")
    @Operation(summary = "批量保存充值通道")
    ResponseVO<Boolean> batchSave(@RequestBody List<SortNewReqVO> sortNewReqVOS){
        return systemRechargeChannelApi.batchSave(CurrReqUtils.getAccount(),sortNewReqVOS);
    }


    @PostMapping("enableOrDisable")
    @Operation(summary = "充值通道启用禁用")
    ResponseVO<Void> enableOrDisable(@RequestBody @Validated SystemRechargeChannelStatusReqVO systemRechargeChannelStatusReqVO){
        systemRechargeChannelStatusReqVO.setOperatorUserNo(CurrReqUtils.getAccount());
        return systemRechargeChannelApi.enableOrDisable(systemRechargeChannelStatusReqVO);
    }

    @Operation(summary = "下拉框 类型从公共下拉框获取: CHANNEL_TYPE ENABLE_DISABLE_TYPE 启用禁用状态")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody @Validated  CurrencyCodeReqVO currencyCodeReqVO) {
        List<CodeValueVO> rechargeWayEnums = Lists.newArrayList();
        ResponseVO<List<SystemRechargeWayRespVO>> listResponseVO=systemRechargeWayApi.selectAll();
        if(listResponseVO.isOk()){
            List<SystemRechargeWayRespVO> systemRechargeWayRespVOS=listResponseVO.getData();
            systemRechargeWayRespVOS=systemRechargeWayRespVOS.stream().filter(o->o.getCurrencyCode().equals(currencyCodeReqVO.getCurrencyCode())).collect(Collectors.toUnmodifiableList());
            for(SystemRechargeWayRespVO systemRechargeWayRespVO:systemRechargeWayRespVOS){
                CodeValueVO codeValueVO=new CodeValueVO();
                codeValueVO.setType(systemRechargeWayRespVO.getCurrencyCode());
                codeValueVO.setCode(systemRechargeWayRespVO.getId());
                codeValueVO.setValue(systemRechargeWayRespVO.getRechargeWayI18());
                rechargeWayEnums.add(codeValueVO);
            }
        }

        Map<String, List<CodeValueVO>> result = Maps.newHashMap();
        result.put("rechargeWayEnums", rechargeWayEnums);

        return ResponseVO.success(result);
    }
    @Operation(summary = "获取系统vip段位下拉框 和大陆盘VIP等级下拉框")
    @GetMapping(value = "/getVipRankDownBox")
    public ResponseVO<Map<String, List<CodeValueNoI18VO>>> getVipRankDownBox() {
        Map<String, List<CodeValueNoI18VO>> result = Maps.newHashMap();
        ResponseVO<List<CodeValueNoI18VO>> vipRankResp=vipRankApi.getVipRank();
        if(vipRankResp.isOk()){
            result.put("vipRanks", vipRankResp.getData());
        }
        ResponseVO<List<VIPGradeVO>> responseVO = siteVipOptionApi.getCnVipGradeList();
        if(responseVO.isOk()){
            List<VIPGradeVO> list = responseVO.getData();
            List<CodeValueNoI18VO> codeValueNoI18VOList = new ArrayList<>();
            for (VIPGradeVO vipGradeVO:list) {
                CodeValueNoI18VO vo = new CodeValueNoI18VO();
                vo.setCode(String.valueOf(vipGradeVO.getVipGradeCode()));
                vo.setValue(vipGradeVO.getVipGradeName());
                codeValueNoI18VOList.add(vo);
            }
            result.put("vipGrades", codeValueNoI18VOList);
        }
        return ResponseVO.success(result);
    }



}
