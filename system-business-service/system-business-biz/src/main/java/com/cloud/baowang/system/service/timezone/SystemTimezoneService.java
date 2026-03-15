package com.cloud.baowang.system.service.timezone;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.timezone.SystemTimezoneVO;
import com.cloud.baowang.system.po.timezone.SystemTimezonePO;
import com.cloud.baowang.system.repositories.SystemTimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemTimezoneService extends ServiceImpl<SystemTimezoneRepository, SystemTimezonePO> {
    private final SystemTimezoneRepository repository;

    public ResponseVO<List<SystemTimezoneVO>> getAll() {
        List<SystemTimezonePO> list = this.list();
        return ResponseVO.success(BeanUtil.copyToList(list,SystemTimezoneVO.class));
    }
}
