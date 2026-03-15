package com.cloud.baowang.admin.controller.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainAddVO;
import com.cloud.baowang.system.api.vo.operations.DomainEditVO;
import com.cloud.baowang.system.api.vo.operations.DomainRequestVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "域名管理")
@RestController
@RequestMapping("/domain/api")
@AllArgsConstructor
public class DomainController {

    private final DomainInfoApi domainInfoApi;

    @Operation(summary = "查询域名列表")
    @PostMapping("/queryDomainPage")
    public ResponseVO<Page<DomainVO>> queryDomainPage(@RequestBody DomainRequestVO domainRequestVO) {
        return domainInfoApi.queryDomainPage(domainRequestVO);
    }

    @Operation(summary = "解除绑定")
    @PostMapping("/unbind")
    public ResponseVO<Boolean> unbind(@Valid @RequestBody DomainEditVO domainEditVO){
        domainEditVO.setOperator(CurrReqUtils.getAccount());
        return domainInfoApi.unbind(domainEditVO);
    }

    @Operation(summary = "新增域名")
    @PostMapping("/addDomain")
    public ResponseVO<Boolean> queryDomainList(@RequestBody DomainAddVO domainAddVO) {
        domainAddVO.setOperator(CurrReqUtils.getAccount());
        return domainInfoApi.add(domainAddVO);
    }

    @Operation(summary = "删除域名")
    @PostMapping("/delDomain")
    public ResponseVO<Boolean> delDomain(@RequestBody IdVO idVO) {
        return domainInfoApi.delDomain(idVO);
    }

    @Operation(summary = "修改域名信息")
    @PostMapping("/editDomain")
    public ResponseVO<?> editDomain(@RequestBody DomainEditVO domainEditVO) {
        domainEditVO.setOperator(CurrReqUtils.getAccount());
        return domainInfoApi.editDomainStatus(domainEditVO);
    }
}
