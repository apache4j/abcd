package com.cloud.baowang.user.api.medal;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.api.medal.SiteMedalInfoApi;
import com.cloud.baowang.user.api.vo.IdReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoCondReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoDetailRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoStatusReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoUpdateReqVO;
import com.cloud.baowang.user.service.SiteMedalInfoService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desciption: 勋章信息相关
 * @Author: Ford
 * @Date: 2024/7/29 10:01
 * @Version: V1.0
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteMedalInfoApiImpl implements SiteMedalInfoApi {
    @Resource
    private SiteMedalInfoService siteMedalInfoService;

    @Override
    public ResponseVO<Page<SiteMedalInfoRespVO>> listPage(SiteMedalInfoReqVO siteMedalInfoReqVO) {
        return siteMedalInfoService.selectPage(siteMedalInfoReqVO);
    }

    @Override
    public ResponseVO<List<SiteMedalInfoRespVO>> listAllBySort(String siteCode) {
        return siteMedalInfoService.listAllBySort(siteCode);
    }

    @Override
    public ResponseVO<List<SiteMedalInfoRespVO>> selectBySiteCode(String siteCode) {
        return siteMedalInfoService.selectBySiteCode(siteCode);
    }

    @Override
    public ResponseVO<SiteMedalInfoRespVO> selectByCond(SiteMedalInfoCondReqVO siteMedalInfoCondReqVO) {
        return siteMedalInfoService.selectByCond(siteMedalInfoCondReqVO);
    }

    @Override
    public ResponseVO<Boolean> init(String siteCode) {
        return siteMedalInfoService.init(siteCode);
    }


    @Override
    public ResponseVO<Void> update(SiteMedalInfoUpdateReqVO siteMedalInfoUpdateReqVO) {
        return siteMedalInfoService.updateByInfo(siteMedalInfoUpdateReqVO);
    }

    @Override
    public ResponseVO<Void> enableOrDisable(SiteMedalInfoStatusReqVO siteMedalInfoStatusReqVO) {
        return siteMedalInfoService.enableOrDisable(siteMedalInfoStatusReqVO);
    }

    @Override
    public ResponseVO<SiteMedalInfoDetailRespVO>  info(IdReqVO idReqVO) {
        return siteMedalInfoService.info(idReqVO.getId());
    }
}
