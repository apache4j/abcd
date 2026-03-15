package com.cloud.baowang.system.api.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigChangeLogApi;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogRespVO;
import com.cloud.baowang.system.service.dict.SystemDictConfigChangeLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemDictConfigChangeLogApiImpl implements SystemDictConfigChangeLogApi {
    private final SystemDictConfigChangeLogService logService;

    @Override
    public ResponseVO<Page<SystemDictConfigChangeLogRespVO>> pageQuery(SystemDictConfigChangeLogPageQueryVO queryVO) {
        return logService.pageQuery(queryVO);
    }
}
