package com.cloud.baowang.system.service.versions;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.vo.version.*;
import com.cloud.baowang.system.po.versions.SystemVersionChangeRecordPO;
import com.cloud.baowang.system.po.versions.SystemVersionManagerPO;
import com.cloud.baowang.system.repositories.versions.SystemVersionChangeRecordMapper;
import com.cloud.baowang.system.repositories.versions.SystemVersionManagerMapper;
import com.cloud.baowang.system.api.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class SystemVersionChangeRecordService extends ServiceImpl<SystemVersionChangeRecordMapper, SystemVersionChangeRecordPO> {

    public ResponseVO<Page<SystemVersionChangeRecordRespVO>> pageQuery(SystemVersionChangeRecordPageQueryVO queryVO) {
        Page<SystemVersionChangeRecordPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<SystemVersionChangeRecordPO> query = Wrappers.lambdaQuery();
        String siteCode = queryVO.getSiteCode();
        Integer deviceTerminal = queryVO.getDeviceTerminal();
        String updater = queryVO.getUpdater();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(SystemVersionChangeRecordPO::getSiteCode, siteCode);
        }
        if (deviceTerminal != null) {
            query.eq(SystemVersionChangeRecordPO::getDeviceTerminal, deviceTerminal);
        }
        if (StringUtils.isNotBlank(updater)) {
            query.eq(SystemVersionChangeRecordPO::getUpdater, updater);
        }
        page = this.page(page, query);
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, SystemVersionChangeRecordRespVO.class))));
    }
}
