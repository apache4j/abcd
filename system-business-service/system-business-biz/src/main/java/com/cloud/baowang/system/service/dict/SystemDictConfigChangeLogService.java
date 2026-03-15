package com.cloud.baowang.system.service.dict;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigChangeLogRespVO;
import com.cloud.baowang.system.po.dict.SystemDictConfigChangeLogPO;
import com.cloud.baowang.system.repositories.dict.SystemDictConfigChangeLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemDictConfigChangeLogService extends ServiceImpl<SystemDictConfigChangeLogMapper, SystemDictConfigChangeLogPO> {

    public ResponseVO<Page<SystemDictConfigChangeLogRespVO>> pageQuery(SystemDictConfigChangeLogPageQueryVO queryVO) {
        Page<SystemDictConfigChangeLogPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        LambdaQueryWrapper<SystemDictConfigChangeLogPO> query = Wrappers.lambdaQuery();
        Integer configCategory = queryVO.getConfigCategory();
        String updater = queryVO.getUpdater();
        if (configCategory != null) {
            query.eq(SystemDictConfigChangeLogPO::getConfigCategory, configCategory);
        }
        String siteCode = queryVO.getSiteCode();
        if (StringUtils.isNotBlank(siteCode)) {
            query.eq(SystemDictConfigChangeLogPO::getSiteCode, siteCode);
        }
        if (StringUtils.isNotBlank(updater)) {
            query.eq(SystemDictConfigChangeLogPO::getUpdater, updater);
        }
        query.orderByDesc(SystemDictConfigChangeLogPO::getUpdatedTime);
        page = this.page(page, query);
        return ResponseVO.success(ConvertUtil.toConverPage(page.convert(item -> BeanUtil.copyProperties(item, SystemDictConfigChangeLogRespVO.class))));
    }
}
