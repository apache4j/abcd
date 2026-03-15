package com.cloud.baowang.system.service.verify;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.api.vo.verify.*;
import com.cloud.baowang.system.po.verify.ChannelSendingStatisticPO;
import com.cloud.baowang.system.repositories.verify.ChannelSendingStatisticRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class ChannelSendingStatisticService extends ServiceImpl<ChannelSendingStatisticRepository, ChannelSendingStatisticPO> {

    private final ChannelSendingStatisticRepository repository;
    private final SiteApi siteApi;


    public void addSendingInfo(ChannelSendingStatisticPO po) {
        ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(po.getSiteCode());
        po.setSiteName(siteInfo.getData().getSiteName());
        po.setCreatedTime(System.currentTimeMillis());
        this.save(po);
    }


    public ChannelSendStatisticRspVO pageQuery(ChannelSendStatisticQueryVO queryVO) {
        if (StringUtils.isBlank(queryVO.getChannelName())){
            queryVO.setChannelName(null);
        }
        if (StringUtils.isBlank(queryVO.getChannelCode())){
            queryVO.setChannelCode(null);
        }
        ChannelSendStatisticRspVO result = new ChannelSendStatisticRspVO();
        Page<ChannelSendingStatisticPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        System.out.println("ChannelSendingStatisticService.pageQuery+++++++++++++++++++");
        Page<ChannelSendingStatisticVO> resultPage = null;
        if(queryVO.getChannelType().equals(CommonConstant.business_two_str)){
            resultPage = repository.queryChannelEmailStatistic(page, queryVO);
        }else{
            resultPage = repository.queryChannelStatistic(page, queryVO);
        }
        //Page<ChannelSendingStatisticVO> resultPage = repository.queryChannelEmailStatistic(page, queryVO);
        List<ChannelSendingStatisticVO> records = resultPage.getRecords();
        if (records.isEmpty()) {
            return result;
        }
        result.setPages(resultPage);
        long curCount = records.stream().mapToLong(ChannelSendingStatisticVO::getSendCount).sum();
        result.setCurRecord(ChannelSendingStatisticVO.builder().sendCount(curCount).build());
        buildVO(queryVO);
        Long sendCount = repository.queryChannelStatisticCount(queryVO);
        result.setTotalRecord(ChannelSendingStatisticVO.builder().sendCount(sendCount).build());
        return result;
    }

    public ResponseVO<Long> sendCount(ChannelSendStatisticQueryVO vo) {
        log.info(" ChannelSendStatisticQueryVO vo : "+vo);
        buildVO(vo);
        Long count = repository.queryChannelStatisticCount(vo);
        return ResponseVO.success(count);
    }

    public ChannelSendStatisticQueryVO buildVO(ChannelSendStatisticQueryVO vo){
        if (StringUtils.isBlank(vo.getSiteCode())){
            vo.setSiteCode(null);
        }
        if (StringUtils.isBlank(vo.getChannelName())){
            vo.setChannelName(null);
        }
        if (StringUtils.isBlank(vo.getChannelCode())){
            vo.setChannelCode(null);
        }
        return vo;
    }

    public ChannelSendDetailsTotalRspVO getChannelSendDetails(SiteInfoVO queryVO) {
        if (StringUtils.isBlank(queryVO.getSiteCode())){
            queryVO.setSiteCode(null);
        }
        if (StringUtils.isBlank(queryVO.getSiteName())){
            queryVO.setSiteName(null);
        }
        if (queryVO.getChannelId()==null){
            if (StringUtils.isBlank(queryVO.getChannelName())){
                queryVO.setChannelName(null);
            }
            if (StringUtils.isBlank(queryVO.getChannelCode())){
                queryVO.setChannelCode(null);
            }
        }else {
            queryVO.setChannelName(null);
            queryVO.setChannelCode(null);
        }
        ChannelSendDetailsTotalRspVO result = new ChannelSendDetailsTotalRspVO();
        Page<ChannelSendingStatisticPO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        Page<ChannelSendDetailsRspVO> details = repository.getChannelSendDetails(page, queryVO);
        result.setPage(details);
        long count = details.getRecords().stream().mapToLong(ChannelSendDetailsRspVO::getSendCount).sum();
        result.setCurData(ChannelSendDetailsRspVO.builder().sendCount(count).build());

        long total =  repository.queryChannelSendCount(queryVO);
        result.setTotalData(ChannelSendDetailsRspVO.builder().sendCount(total).build());
        return result;
    }
}
