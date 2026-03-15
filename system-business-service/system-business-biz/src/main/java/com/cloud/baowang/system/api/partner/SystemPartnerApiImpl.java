package com.cloud.baowang.system.api.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SystemPartnerApi;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerVO;
import com.cloud.baowang.system.service.partner.SystemPartnerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemPartnerApiImpl implements SystemPartnerApi {
    private final SystemPartnerService partnerService;

    @Override
    public ResponseVO<Page<SystemPartnerVO>> pageQuery(SystemPartnerPageQueryVO pageQueryVO) {
        return ResponseVO.success(partnerService.pageQuery(pageQueryVO));
    }

    @Override
    public ResponseVO<Boolean> add(SystemPartnerVO partnerVO) {
        return ResponseVO.success(partnerService.add(partnerVO));
    }

    @Override
    public ResponseVO<Boolean> upd(SystemPartnerVO partnerVO) {
        return ResponseVO.success(partnerService.upd(partnerVO));
    }

    @Override
    public ResponseVO<Boolean> del(String id) {
        return ResponseVO.success(partnerService.del(id));
    }

    @Override
    public ResponseVO<Boolean> enableAndDisAble(SystemPartnerVO partnerVO) {
        return ResponseVO.success(partnerService.enableAndDisAble(partnerVO));
    }

    @Override
    public ResponseVO<List<SystemPartnerVO>> listQuery() {
        return ResponseVO.success(partnerService.listQuery());
    }

    @Override
    public ResponseVO<SystemPartnerVO> detail(String id) {
        return ResponseVO.success(partnerService.detail(id));
    }
}
