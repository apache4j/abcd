package com.cloud.baowang.system.api.versions;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.versions.SystemVersionChangeRecordApi;
import com.cloud.baowang.system.api.api.versions.SystemVersionManagerApi;
import com.cloud.baowang.system.api.vo.version.*;
import com.cloud.baowang.system.po.versions.SystemVersionChangeRecordPO;
import com.cloud.baowang.system.service.versions.SystemVersionChangeRecordService;
import com.cloud.baowang.system.service.versions.SystemVersionManagerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SystemVersionChangeRecordImpl implements SystemVersionChangeRecordApi {
    private final SystemVersionChangeRecordService recordService;


    @Override
    public ResponseVO<Page<SystemVersionChangeRecordRespVO>> pageQuery(SystemVersionChangeRecordPageQueryVO queryVO) {

        return recordService.pageQuery(queryVO);
    }
}
