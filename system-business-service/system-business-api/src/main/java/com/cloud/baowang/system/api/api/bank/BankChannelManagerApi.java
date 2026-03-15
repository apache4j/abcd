package com.cloud.baowang.system.api.api.bank;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.bank.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(contextId = "bank-code-manager", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 站点banner相关")
public interface BankChannelManagerApi {
    String PREFIX = "/bankCodeManager/api";

    @Operation(summary = "银行卡管理分页查询")
    @PostMapping(PREFIX + "/pageList")
    ResponseVO<Page<BankChannelInfoRspVO>> pageList(@RequestBody BankCodeListReqVO vo);

    @Operation(summary = "通道银行编码配置-新增")
    @PostMapping(PREFIX + "/add")
    ResponseVO<Boolean> add(@RequestBody BankChannelManageAddVO vo);

    @Operation(summary = "通道银行编码配置-删除")
    @PostMapping(PREFIX+"/deleteBankChannelConfig/{id}")
    ResponseVO<Void> deleteBankChannelConfig(@PathVariable("id") String id);


    @Operation(summary = "列表删除-删除")
    @PostMapping(PREFIX+"/deleteChannelInfo/{id}")
    ResponseVO<Void> deleteChannelInfo(@PathVariable("id") String id);

    @Operation(summary = "页面-编辑-获取通道银行编码集合")
    @PostMapping(PREFIX+"/queryChannelBankRelation/{id}")
    ResponseVO<List<BankChannelManageRspVO>> queryChannelBankRelation(@PathVariable("id") String id);

    @Operation(summary = "通道银行编码配置-修改")
    @PostMapping(PREFIX+"/edit")
    ResponseVO<Void> edit(@RequestBody BankChannelManageAddVO editVO);



    @Operation(summary = "根据通道id,bankCode查对应关系")
    @PostMapping(PREFIX + "/getSystemChannelBankRelation")
    ResponseVO<BankChannelManageRspVO> getSystemChannelBankRelation(@RequestBody @Validated ChannelBankRelationReqVO reqVO);

    @Operation(summary = "获取总控全部银行编码")
    @PostMapping(PREFIX+"/queryAllChannelBankRelation")
    ResponseVO<Set<BankInfoAdminRspVO>> queryAllChannelBankRelation(@RequestBody @Validated BankChannelRelationQueryVO req);
}
