package com.cloud.baowang.system.service.site.rebate;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.vo.site.rebate.*;
import com.cloud.baowang.system.po.site.rebate.SiteNonRebateConfigPO;
import com.cloud.baowang.system.repositories.site.rebate.SiteNonRebateConfigRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SiteNonRebateConfigService extends ServiceImpl<SiteNonRebateConfigRepository, SiteNonRebateConfigPO> {

    private final SiteNonRebateConfigRepository repository;


    /**
     * 不返水配置列表
     * @param vo
     * @return
     */
    public Page<SiteNonRebateConfigVO> listPage(SiteNonRebateQueryVO vo) {
        log.error("SiteNonRebateConfigService.listPage request : "+vo);
        Page<SiteNonRebateConfigPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = repository.listPage(page,vo);
        return  ConvertUtil.toConverPage(page.convert(item -> {
            SiteNonRebateConfigVO siteRebateConfigVO = BeanUtil.copyProperties(item, SiteNonRebateConfigVO.class,"gameInfo");
            String gameInfo = item.getGameInfo();
            if (StringUtils.isNotBlank(gameInfo)) {
                siteRebateConfigVO.setGameInfo(JSON.parseArray(gameInfo, GameInfoRebateVO.class));
            }
            return siteRebateConfigVO;
        }));
    }

    /**
     * 编辑不返水列表
     * @param vo
     * @return
     */
    public ResponseVO saveNonRabate(SiteNonRebateConfigAddVO vo) {
        String venueType = vo.getVenueType();
        String venueCode = vo.getVenueCode();
        List<GameInfoRebateVO> gameInfo = vo.getGameInfo();
        LambdaQueryWrapper<SiteNonRebateConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteNonRebateConfigPO::getSiteCode, vo.getSiteCode());
        wrapper.eq(SiteNonRebateConfigPO::getVenueType, venueType);
        wrapper.eq(SiteNonRebateConfigPO::getVenueCode, venueCode);
        Long count = repository.selectCount(wrapper);
        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.EXISTS_VENUE_NON_REBATE_CONFIG);
        }

        SiteNonRebateConfigPO configPO = BeanUtil.copyProperties(vo, SiteNonRebateConfigPO.class,"gameInfo");
        if (gameInfo != null && !gameInfo.isEmpty()){
            String gameInfoStr = JSONArray.toJSONString(gameInfo);
            configPO.setGameInfo(gameInfoStr);
        }
        configPO.setUpdatedTime(System.currentTimeMillis());
        configPO.setUpdater(CurrReqUtils.getAccount());
        this.save(configPO);
        return ResponseVO.success();
    }


    public ResponseVO updateNonRabate(SiteNonRebateConfigAddVO vo) {
        String venueType = vo.getVenueType();
        String venueCode = vo.getVenueCode();
        List<GameInfoRebateVO> gameInfo = vo.getGameInfo();
        LambdaQueryWrapper<SiteNonRebateConfigPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteNonRebateConfigPO::getSiteCode, vo.getSiteCode());
        wrapper.eq(SiteNonRebateConfigPO::getVenueType, venueType);
        wrapper.eq(SiteNonRebateConfigPO::getVenueCode, venueCode);
        wrapper.ne(SiteNonRebateConfigPO::getId, vo.getId());
        Long count = repository.selectCount(wrapper);
        if (count > 0) {
            throw new BaowangDefaultException(ResultCode.EXISTS_VENUE_NON_REBATE_CONFIG);
        }

        SiteNonRebateConfigPO configPO = BeanUtil.copyProperties(vo, SiteNonRebateConfigPO.class,"gameInfo");
        if (gameInfo != null && !gameInfo.isEmpty()){
            String gameInfoStr = JSONArray.toJSONString(gameInfo);
            configPO.setGameInfo(gameInfoStr);
        }
        configPO.setUpdatedTime(System.currentTimeMillis());
        configPO.setUpdater(CurrReqUtils.getAccount());
        this.updateById(configPO);
        return ResponseVO.success();
    }

    public Long NonRebateCount(SiteNonRebateQueryVO vo) {
        Page<SiteNonRebateConfigPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = repository.listPage(page,vo);
        return page.getTotal();
    }


    public Page<SiteNonRebateExportVO> listExportPage(SiteNonRebateQueryVO vo) {
        Page<SiteNonRebateConfigPO> page = new Page<>(vo.getPageNumber(), vo.getPageSize());
        page = repository.listPage(page,vo);
        return  ConvertUtil.toConverPage(page.convert(item -> {
            SiteNonRebateExportVO exportVo = BeanUtil.copyProperties(item, SiteNonRebateExportVO.class);
            String gameInfo = item.getGameInfo();
            if (StringUtils.isNotBlank(gameInfo)) {
                exportVo.setGameInfo(JSON.parseArray(gameInfo, GameInfoRebateVO.class).stream().map(GameInfoRebateVO::getGameName).toList().toString());
                exportVo.setUpdatedTime(item.getUpdatedTime() == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(item.getUpdatedTime(), CurrReqUtils.getTimezone()));
            }
            return exportVo;
        }));
    }



}
