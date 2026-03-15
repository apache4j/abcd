package com.cloud.baowang.system.api.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.operations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteDomainInfoApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - domainInfoApi")
public interface DomainInfoApi {

    String PREFIX = ApiConstants.PREFIX + "/domainInfo/api";

    @Operation(summary = "新增域名")
    @PostMapping(PREFIX + "/add")
    ResponseVO<Boolean> add(@RequestBody DomainAddVO domainAddVO);

    @Operation(summary = "查询域名列表")
    @PostMapping(PREFIX + "/queryDomainPage")
    ResponseVO<Page<DomainVO>> queryDomainPage(@RequestBody DomainRequestVO domainRequestVO);

    @Operation(summary = "查询未绑定域名列表-站点列表-域名设置查询使用")
    @PostMapping(PREFIX + "/queryUnBindDomainPage")
    ResponseVO<Page<DomainVO>> queryUnBindDomainPage(@RequestBody DomainRequestVO domainRequestVO);

    @Operation(summary = "修改域名信息")
    @PostMapping(PREFIX + "/editDomainStatus")
    ResponseVO<Boolean> editDomainStatus(@RequestBody DomainEditVO domainEditVO);

    @Operation(summary = "根据域名查询记录")
    @PostMapping(PREFIX + "/getDomainByAddress")
    DomainVO getDomainByAddress(@RequestParam("domain") String domain);

    @Operation(summary = "删除域名")
    @PostMapping(PREFIX + "/delDomain")
    ResponseVO<Boolean> delDomain(@RequestBody IdVO idVO);

    @Operation(summary = "解除域名绑定")
    @PostMapping(PREFIX + "/unbind")
    ResponseVO<Boolean> unbind(@RequestBody DomainEditVO editVO);

    @Operation(summary = "域名绑定")
    @PostMapping(PREFIX + "/bind")
    ResponseVO<Boolean> bind(@RequestBody DomainBindVO domainBindVO);

    @Operation(summary = "查询域名")
    @PostMapping(PREFIX + "/getDomainByType")
    DomainVO getDomainByType(@RequestBody DomainQueryVO domainQueryVO);

    @Operation(summary = "查询域名信息")
    @PostMapping(PREFIX + "/getDomainbyAddressAndSitecode")
    DomainVO getDomainbyAddressAndSitecode(@RequestBody DomainRequestVO domainRequestVO);
}
