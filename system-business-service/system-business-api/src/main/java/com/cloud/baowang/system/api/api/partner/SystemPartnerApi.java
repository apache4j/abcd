package com.cloud.baowang.system.api.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "systemPartnerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - SystemPartner")
public interface SystemPartnerApi {

    String SYSTEM_PARTNER_PREFIX = ApiConstants.PREFIX + "/system-partner/api/";

    @PostMapping(SYSTEM_PARTNER_PREFIX+"pageQuery")
    ResponseVO<Page<SystemPartnerVO>> pageQuery(@RequestBody SystemPartnerPageQueryVO pageQueryVO);

    @PostMapping(SYSTEM_PARTNER_PREFIX+"add")
    ResponseVO<Boolean> add(@RequestBody SystemPartnerVO partnerVO);

    @PostMapping(SYSTEM_PARTNER_PREFIX+"upd")
    ResponseVO<Boolean> upd(@RequestBody SystemPartnerVO partnerVO);

    @GetMapping(SYSTEM_PARTNER_PREFIX+"del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(SYSTEM_PARTNER_PREFIX+"enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody SystemPartnerVO partnerVO);

    @GetMapping(SYSTEM_PARTNER_PREFIX+"listQuery")
    ResponseVO<List<SystemPartnerVO>> listQuery();

    @GetMapping(SYSTEM_PARTNER_PREFIX+"detail")
    ResponseVO<SystemPartnerVO> detail(@RequestParam("id") String id);
}
