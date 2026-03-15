package com.cloud.baowang.user.api.vip;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.vip.SiteVipChangeRecordCnApi;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnVO;
import com.cloud.baowang.user.api.vo.vip.SiteVipChangeRecordCnReqVO;
import com.cloud.baowang.user.service.SiteVipChangeRecordCnService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteVipChangeRecordCnApImpl implements SiteVipChangeRecordCnApi {
    private SiteVipChangeRecordCnService siteVipChangeRecordCnService;


    @Override
    public ResponseVO<Page<SiteVipChangeRecordCnVO>> getList(SiteVipChangeRecordCnReqVO vo) {
        return siteVipChangeRecordCnService.getList(vo);
    }

    /**
     *
     * @param userId 会员id
     * @param startDayTime  某一天的开始时间 00:00:00
     * @param endDayTime 某一天的最后时间 23:59:59
     * @return
     */
    @Override
    public Integer findVIPCodeByDay(String userId, long startDayTime, long endDayTime) {
        return  siteVipChangeRecordCnService.findVIPCodeByDay(userId,startDayTime,endDayTime);
    }
}
