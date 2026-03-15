package com.cloud.baowang.site.controller.rebate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.excel.ExcelUtil;
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.system.api.vo.site.rebate.SiteNonRebateExportVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.api.file.MinioUploadApi;
import com.cloud.baowang.system.api.api.file.UploadXlsxVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.system.api.api.site.rebate.SiteRebateApi;
import com.cloud.baowang.system.api.vo.site.rebate.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@Tag(name = "站点-游戏不返水配置")
@RequestMapping("/non-rebate/api/")
@AllArgsConstructor
@Slf4j
public class NonRebateConfigController {

    private final SiteRebateApi siteRebateApi;

    private final GameInfoApi gameInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final MinioUploadApi minioUploadApi;



    @PostMapping("listPage")
    @Operation(summary = "不返水配置分页查询")
    ResponseVO<Page<SiteNonRebateConfigVO>> listPage(@RequestBody SiteNonRebateQueryVO vo){
        log.error("不返水配置分页查询-siteCode-"+CurrReqUtils.getSiteCode());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRebateApi.listNonRebatePage(vo);
    }


    @PostMapping("saveNonRebateConfig")
    @Operation(summary = "新增不返水配置")
    ResponseVO saveNonRebateConfig(@RequestBody SiteNonRebateConfigAddVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRebateApi.saveNonRabate(vo);
    }
    @PostMapping("updateNonRebateConfig")
    @Operation(summary = "编辑不返水配置")
    ResponseVO updateNonRebateConfig(@RequestBody SiteNonRebateConfigAddVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRebateApi.updateNonRabate(vo);
    }

    @PostMapping("del")
    @Operation(summary = "编辑/新增不返水配置")
    ResponseVO del(@RequestBody SiteNonRebateConfigAddVO vo){
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return siteRebateApi.delNonRabate(vo);
    }

    @PostMapping("gameInfoList")
    @Operation(summary = "编辑/新增 查询游戏名称")
    ResponseVO<List<GameInfoRebateVO>> gameInfoList(@RequestBody SiteNonRebateQueryVO vo){
        List<GameInfoRebateVO> result = new ArrayList<>();
        if (StringUtils.isBlank(vo.getVenueType()) || StringUtils.isBlank(vo.getVenueCode())) {
            return  ResponseVO.success(result);
        }
        GameInfoRequestVO queryVo = GameInfoRequestVO.builder().venueCode(vo.getVenueCode()).build();
        ResponseVO<List<SiteGameInfoVO>> responseVO = gameInfoApi.siteGameInfoList(queryVo);
        if (responseVO.isOk()){
            List<SiteGameInfoVO> gameInfoList = responseVO.getData();
            if (gameInfoList != null && !gameInfoList.isEmpty()){
                gameInfoList.forEach(gameInfoVO -> {
                    GameInfoRebateVO gameInfoRebateVO = new GameInfoRebateVO();
                    gameInfoRebateVO.setGameId(gameInfoVO.getAccessParameters());
                    gameInfoRebateVO.setGameName(gameInfoVO.getGameName());
                    result.add(gameInfoRebateVO);
                });
            }
        }
        return ResponseVO.success(result);
    }



    @Operation(summary = "下拉框-查场馆名称 联动查询传参 只需赋值venueType")
    @PostMapping(value = "/getDownBox")
    public ResponseVO<Map<String, List<CodeValueVO>>> getDownBox(@RequestBody SiteNonRebateQueryVO downBoxVo) {
        //VENUE_CODE
        Map<String, List<CodeValueVO>> result = new HashMap<>();
        if (downBoxVo.getVenueType() != null){
            VenueInfoRequestVO paramVO = VenueInfoRequestVO.builder().venueType(Integer.valueOf(downBoxVo.getVenueType())).build();
            paramVO.setPageSize(-1);
            ResponseVO<Page<VenueInfoVO>> responseVO =  playVenueInfoApi.siteVenueInfoPage(paramVO);
//            ResponseVO<List<VenueInfoVO>> responseVO = playVenueInfoApi.venueInfoListByParam(VenueInfoRequestVO.builder().venueType(Integer.valueOf(downBoxVo.getVenueType())).build());
            if (responseVO.isOk()){
                List<CodeValueVO> venueTempList = new ArrayList<>();
                List<VenueInfoVO> venueInfoVOList = responseVO.getData().getRecords();
                venueInfoVOList.forEach(venueInfoVO -> {
                    CodeValueVO venueVO = new CodeValueVO();
                    venueVO.setCode(venueInfoVO.getVenueCode());
                    venueVO.setValue(venueInfoVO.getVenueName());
                    venueTempList.add(venueVO);
                });
                result.put(CommonConstant.VENUE_CODE,venueTempList);
            }
        }
        return ResponseVO.success(result);
    }


    @Operation(summary = "导出")
    @PostMapping("export")
    public ResponseVO export(@Valid @RequestBody SiteNonRebateQueryVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        String adminId = CurrReqUtils.getOneId();
        String uniqueKey = "no-RebateConfigExport::site::" + adminId;
        if (RedisUtil.isKeyExist(uniqueKey)) {
            long remain = RedisUtil.getRemainExpireTime(uniqueKey);
            return ResponseVO.failAppend(ResultCode.DOWNLOAD_EXPORT_NOTICE, String.valueOf(remain));
        } else {
            RedisUtil.setValue(uniqueKey, uniqueKey, CommonConstant.EXPORT_EXPIRE_TIME, TimeUnit.SECONDS);
        }

        ResponseVO<Long> resp = siteRebateApi.NonRebateCount(vo);
        if (!resp.isOk() || resp.getData() <= 0) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        vo.setPageSize(10000);
        Long count = resp.getData();
        // 生成导出文件，转换成字节数组
        byte[] byteArray = ExcelUtil.writeForParallel(
                SiteNonRebateExportVO.class,
                vo,
                1,
                ExcelUtil.getPages(vo.getPageSize(), count),
                param -> ConvertUtil.entityListToModelList(siteRebateApi.listExportPage(vo).getData().getRecords(),SiteNonRebateExportVO.class));

        // 把Excel对应的字节数组写入MINIO，并返回fileKey
        // 保存fileKey到文件导出表file_export
        return minioUploadApi.uploadXlsxAndFileExport(
                UploadXlsxVO.builder()
                        .bucket(ExcelUtil.BAOWANG_BUCKET)
                        .byteArray(byteArray)
                        .pageName(CommonConstant.NON_REBATE_CONFIG)
                        .siteCode(CurrReqUtils.getSiteCode())
                        .adminId(CurrReqUtils.getAccount())
                        .build());
    }

}
