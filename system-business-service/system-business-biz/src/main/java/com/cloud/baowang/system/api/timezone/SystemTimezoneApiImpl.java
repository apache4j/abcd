package com.cloud.baowang.system.api.timezone;

import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.timezone.SystemTimezoneApi;
import com.cloud.baowang.system.api.vo.timezone.SystemTimezoneVO;
import com.cloud.baowang.system.service.timezone.SystemTimezoneService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class SystemTimezoneApiImpl implements SystemTimezoneApi {
    private final SystemTimezoneService timezoneService;

    @Override
    public ResponseVO<List<SystemTimezoneVO>> getAll() {
        return timezoneService.getAll();
    }
}
