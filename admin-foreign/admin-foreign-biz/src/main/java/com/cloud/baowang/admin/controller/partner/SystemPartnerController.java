package com.cloud.baowang.admin.controller.partner;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.partner.SystemPartnerApi;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerPageQueryVO;
import com.cloud.baowang.system.api.vo.partner.SystemPartnerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "合作赞助商")
@RequestMapping("/systemPartner/api")
@AllArgsConstructor
public class SystemPartnerController {
    private final SystemPartnerApi partnerApi;

    @PostMapping("pageQuery")
    @Operation(summary = "赞助商分页列表")
    public ResponseVO<Page<SystemPartnerVO>> pageQuery(@RequestBody SystemPartnerPageQueryVO pageQueryVO) {
        return partnerApi.pageQuery(pageQueryVO);
    }

    @GetMapping("detail")
    @Operation(summary = "详情")
    public ResponseVO<SystemPartnerVO> detail(@RequestParam("id") String id) {
        return partnerApi.detail(id);
    }

    @PostMapping("add")
    @Operation(summary = "新增赞助商")
    public ResponseVO<Boolean> add(@RequestBody @Validated SystemPartnerVO partnerVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        partnerVO.setCreator(currentUserAccount);
        partnerVO.setCreatedTime(time);
        partnerVO.setUpdater(currentUserAccount);
        partnerVO.setUpdatedTime(time);
        return partnerApi.add(partnerVO);
    }

    @PostMapping("upd")
    @Operation(summary = "修改赞助商")
    public ResponseVO<Boolean> upd(@RequestBody @Validated SystemPartnerVO partnerVO) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        partnerVO.setUpdater(currentUserAccount);
        partnerVO.setUpdatedTime(time);
        return partnerApi.upd(partnerVO);
    }

    @GetMapping("del")
    @Operation(summary = "删除赞助商")
    public ResponseVO<Boolean> del(@RequestParam("id") String id) {
        return partnerApi.del(id);
    }

    @GetMapping("enableAndDisAble")
    @Operation(summary = "停用/启用")
    public ResponseVO<Boolean> enableAndDisAble(@RequestParam("id") String id, @RequestParam("status") Integer status) {
        String currentUserAccount = CurrReqUtils.getAccount();
        long time = System.currentTimeMillis();
        SystemPartnerVO partnerVO = new SystemPartnerVO();
        partnerVO.setId(id);
        partnerVO.setStatus(status);
        partnerVO.setUpdater(currentUserAccount);
        partnerVO.setUpdatedTime(time);
        return partnerApi.enableAndDisAble(partnerVO);
    }
}
