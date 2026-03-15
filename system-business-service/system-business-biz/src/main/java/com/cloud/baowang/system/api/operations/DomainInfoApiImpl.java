package com.cloud.baowang.system.api.operations;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.enums.DomainBindStatusEnum;
import com.cloud.baowang.system.api.vo.operations.*;
import com.cloud.baowang.system.service.operations.DomainInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class DomainInfoApiImpl implements DomainInfoApi {

    private final DomainInfoService domainInfoService;

    @Override
    public ResponseVO<Boolean> add(DomainAddVO domainAddVO) {
        return domainInfoService.add(domainAddVO);
    }

    @Override
    public ResponseVO<Page<DomainVO>> queryDomainPage(DomainRequestVO domainRequestVO) {
        return domainInfoService.queryDomainPage(domainRequestVO);
    }

    @Override
    public ResponseVO<Page<DomainVO>> queryUnBindDomainPage(DomainRequestVO domainRequestVO) {
        domainRequestVO.setBind(DomainBindStatusEnum.UN_BIND.getCode());
        String siteCode = domainRequestVO.getSiteCode();
        if (StringUtils.isBlank(siteCode)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return domainInfoService.queryUnBindDomainPage(domainRequestVO);
    }

    @Override
    public ResponseVO<Boolean> editDomainStatus(DomainEditVO domainEditVO) {
        return domainInfoService.editDomainStatus(domainEditVO);
    }

    @Override
    public ResponseVO<Boolean> delDomain(IdVO idVO) {
        return domainInfoService.delete(idVO);
    }

    @Override
    public DomainVO getDomainByAddress(String domain) {
        return domainInfoService.getDomainByAddress(domain);
    }

    @Override
    public ResponseVO<Boolean> unbind(DomainEditVO editVO) {
        return domainInfoService.unbind(editVO);
    }

    @Override
    public ResponseVO<Boolean> bind(DomainBindVO domainBindVO) {
        return domainInfoService.bind(domainBindVO);
    }

    @Override
    public DomainVO getDomainByType(DomainQueryVO domainQueryVO) {
        return domainInfoService.getDomainByType(domainQueryVO);
    }

    @Override
    public DomainVO getDomainbyAddressAndSitecode(DomainRequestVO domainRequestVO) {
        return domainInfoService.getDomainbyAddressAndSitecode(domainRequestVO);
    }
}
