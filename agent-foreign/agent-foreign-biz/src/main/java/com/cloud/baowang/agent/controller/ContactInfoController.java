package com.cloud.baowang.agent.controller;

import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.vo.site.ContactInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "加入我们")
@RestController
@RequestMapping("/agent-contact-info/api")
@AllArgsConstructor
@Slf4j
public class ContactInfoController {
    private final HelpCenterManageApi helpCenterManageApi;

    @PostMapping("getContactInfo")
    @Operation(summary = "加入我们")
    public ResponseVO<List<ContactInfoVO>> getContactInfo() {
        return helpCenterManageApi.getContactInfo();
    }

}
