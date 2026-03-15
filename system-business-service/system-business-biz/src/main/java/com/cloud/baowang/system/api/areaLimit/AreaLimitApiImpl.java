package com.cloud.baowang.system.api.areaLimit;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.areaLimit.AreaLimitApi;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerAddReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerEditReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerIdReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerStatusChangeReqVO;
import com.cloud.baowang.system.api.vo.areaLimit.AreaLimitManagerVO;
import com.cloud.baowang.system.service.areaLimit.AreaLimitService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AreaLimitApiImpl implements AreaLimitApi {

    private final AreaLimitService areaLimitService;

    @Override
    public ResponseVO<Page<AreaLimitManagerVO>> pageList(AreaLimitManagerReqVO vo) {
        return areaLimitService.pageList(vo);
    }

    @Override
    public ResponseVO<Void> edit(AreaLimitManagerEditReqVO vo) {
        return areaLimitService.edit(vo);
    }

    @Override
    public ResponseVO<Void> statusChange(AreaLimitManagerStatusChangeReqVO vo) {
        return areaLimitService.statusChange(vo);
    }

    @Override
    public ResponseVO<Void> del(AreaLimitManagerIdReqVO vo) {
        return areaLimitService.del(vo);
    }

    @Override
    public ResponseVO<AreaLimitManagerVO> info(AreaLimitManagerIdReqVO vo) {
        return areaLimitService.info(vo);
    }

    @Override
    public ResponseVO<Void> add(AreaLimitManagerAddReqVO vo) {
        return areaLimitService.add(vo);
    }
}

