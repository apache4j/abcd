package com.cloud.baowang.user.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.VipLevelChangeRecordApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordPageQueryVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordRequestVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordVO;
import com.cloud.baowang.user.service.SiteVipChangeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteVipChangeRecordApiImpl implements VipLevelChangeRecordApi {

    private final SiteVipChangeRecordService recordService;

    @Override
    public ResponseVO<Page<SiteVipChangeRecordVO>> queryChangeRecordPage(SiteVipChangeRecordPageQueryVO requestVO) {
        return recordService.queryChangeRecordPage(requestVO);
    }
    @Override
    public ResponseVO<Long> getTotalCount(SiteVipChangeRecordPageQueryVO vo) {
        return recordService.getTotalCount(vo);
    }

    @Override
    public ResponseVO<Boolean> insertChangeInfo(SiteVipChangeRecordRequestVO changeRecordVO) {
        return recordService.insertChangeInfo(changeRecordVO);
    }

    @Override
    public ResponseVO<Boolean> insertChangeRecordList(List<SiteVipChangeRecordRequestVO> requestVOs) {
        return recordService.insertChangeRecordList(requestVOs);
    }


}
