package com.cloud.baowang.play.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.vo.venue.GameClassStatusRequestUpVO;
import com.cloud.baowang.play.api.vo.venue.SiteVenueConfigVO;
import com.cloud.baowang.play.po.SiteGamePO;
import com.cloud.baowang.play.po.SiteVenuePO;
import com.cloud.baowang.play.po.VenueInfoPO;
import com.cloud.baowang.play.repositories.SiteVenueRepository;
import com.cloud.baowang.play.repositories.VenueInfoRepository;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@AllArgsConstructor
public class SiteVenueService extends ServiceImpl<SiteVenueRepository, SiteVenuePO> {

    private final SiteGameService siteGameService;
    private final VenueInfoRepository venueInfoRepository;

    /**
     * 根据站点查询场馆CODE
     */
    public List<String> getSiteVenueCodeListBySiteCode(String siteCode) {
        if (StringUtils.isBlank(siteCode)) {
            return Lists.newArrayList();
        }

        List<SiteVenueConfigVO> siteGameList = getSiteVenueListBySiteCode(siteCode);
        if (CollectionUtils.isNotEmpty(siteGameList)) {
            return siteGameList.stream().map(SiteVenueConfigVO::getVenueCode).toList();
        }

        return Lists.newArrayList();
    }


    /**
     * 根据站点查询场馆
     */
    public List<SiteVenueConfigVO> getSiteVenueListBySiteCode(String siteCode) {
        if (StringUtils.isBlank(siteCode)) {
            return Lists.newArrayList();
        }

        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_SITE_VENUE_CONFIG_LIST, siteCode);
        List<SiteVenueConfigVO> siteGameList = RedisUtil.getValue(key);
        if (CollectionUtils.isNotEmpty(siteGameList)) {
            return siteGameList;
        }


//        List<SiteVenuePO> list = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class).eq(SiteVenuePO::getSiteCode, siteCode));
//        if (CollectionUtils.isEmpty(list)) {
//            return Lists.newArrayList();
//        }
        List<SiteVenueConfigVO> list = baseMapper.querySiteVenueJoinConfigBySiteCode(siteCode);

        if (CollectionUtils.isNotEmpty(list)) {
            RedisUtil.setValue(key, list,5L, TimeUnit.MINUTES);
        }
        return list;
    }

    /**
     * 站点跟场馆查询站点场馆信息
     * @param siteCode 站点
     * @param venueCode 场馆
     */
    public SiteVenueConfigVO getSiteVenueBySiteCodeAndVenueCode(String siteCode, String venueCode) {
        List<SiteVenueConfigVO> siteVenuePOList = getSiteVenueListBySiteCode(siteCode);


        siteVenuePOList = siteVenuePOList.stream()
                .filter(item -> venueCode.equals(item.getVenueCode()))
                .toList();

        if (CollectionUtil.isEmpty(siteVenuePOList)) {
            return null;
        }

        return siteVenuePOList.get(0);
    }

    /**
     * 判断该站点是否有这个场馆
     */
    public Boolean getSiteVenueIdsBySiteCodeAndByVenueCode(String venueCode) {
        return getSiteVenueIdsBySiteCodeAndByVenueCode(CurrReqUtils.getSiteCode(), venueCode);
    }

    public Boolean getSiteVenueIdsBySiteCodeAndByVenueCode(String siteCode, String venueCode) {
        List<String> venueCodeList = getSiteVenueCodeListBySiteCode(siteCode);
        if (CollectionUtils.isEmpty(venueCodeList)) {
            return Boolean.FALSE;
        }

        return venueCodeList.contains(venueCode);
    }



    /**
     * 站点同步总控-场馆维护状态
     */
    public Boolean upSynAdminVenueInfoStatus(GameClassStatusRequestUpVO requestVO) {
        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getId, requestVO.getId()));
        if (CollectionUtils.isEmpty(venueInfoList)) {
            return Boolean.FALSE;
        }
        VenueInfoPO venueInfoPO = venueInfoList.get(0);
        if (!venueInfoPO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
            return Boolean.FALSE;
        }

        String siteCode = CurrReqUtils.getSiteCode();
        UpdateWrapper<SiteVenuePO> venueWrapper = new UpdateWrapper<>();
        venueWrapper.eq("site_code", siteCode)
                .eq("venue_code", venueInfoPO.getVenueCode())
                .eq("status", StatusEnum.MAINTAIN.getCode())
                .set("last_status", null)
                .set("maintenance_start_time", venueInfoPO.getMaintenanceStartTime())
                .set("maintenance_end_time", venueInfoPO.getMaintenanceEndTime())
                .set("remark", venueInfoPO.getRemark())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis());


        UpdateWrapper<SiteGamePO> siteGameWrapper = new UpdateWrapper<>();
        siteGameWrapper.eq("site_code", siteCode)
                .eq("venue_code", venueInfoPO.getVenueCode())
                .eq("status", StatusEnum.MAINTAIN.getCode())
                .set("last_status", null)
                .set("maintenance_start_time", venueInfoPO.getMaintenanceStartTime())
                .set("maintenance_end_time", venueInfoPO.getMaintenanceEndTime())
                .set("remark", venueInfoPO.getRemark())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis());


        siteGameService.getBaseMapper().update(null, siteGameWrapper);

//        RedisUtil.deleteKeysByPattern(RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_LOBBY_VENUE, siteCode));
//        RedisUtil.deleteKey(String.format(RedisConstants.VENUE_INFO_LIST, venueInfoPO.getVenueCode()));
//        RedisUtil.deleteKey(String.format(RedisConstants.VENUE_INFO_PLAT_MERCHANT, venueInfoPO.getVenuePlatform(),venueInfoPO.getMerchantNo()));
//        RedisUtil.deleteKeysByPattern(RedisConstants.getSiteCodeKeyConstant(String.format(RedisConstants.SITE_LOBBY_LABEL,"*")));

        LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
        return baseMapper.update(null, venueWrapper) > 0;
    }


    @Transactional(rollbackFor = {Exception.class})
    public Boolean upSiteVenueInfoStatus(GameClassStatusRequestUpVO requestVO) {
        String siteCode = CurrReqUtils.getSiteCode();
        if (ObjectUtil.isEmpty(StatusEnum.nameByCode(requestVO.getStatus()))) {
            return Boolean.FALSE;
        }

        List<VenueInfoPO> venueInfoList = venueInfoRepository.selectList(Wrappers.lambdaQuery(VenueInfoPO.class)
                .eq(VenueInfoPO::getId, requestVO.getId()));
        if (CollectionUtils.isEmpty(venueInfoList)) {
            return Boolean.FALSE;
        }
        VenueInfoPO venueInfoPO = venueInfoList.get(0);

        SiteVenuePO siteVenuePO = baseMapper.selectOne(Wrappers
                .lambdaQuery(SiteVenuePO.class)
                .eq(SiteVenuePO::getSiteCode, siteCode)
                .eq(SiteVenuePO::getVenueCode, venueInfoPO.getVenueCode()));
        if (ObjectUtil.isEmpty(siteVenuePO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }

        //重复请求
        if (siteVenuePO.getStatus().equals(requestVO.getStatus())) {
            return Boolean.TRUE;
        }

        //站点尝试开启场馆的游戏时,校验总台的状态
        if (requestVO.getStatus().equals(StatusEnum.OPEN.getCode())) {

            //点击开启当时候 站点场馆状态跟总台场馆状态都是维护的时候,返回前端弹出同步总控维护状态错误码
            if (siteVenuePO.getStatus().equals(StatusEnum.MAINTAIN.getCode())
                    && venueInfoPO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
                //只要站点跟场馆的维护时间不一样,就需要弹出同步维护时间
                if (!Objects.equals(venueInfoPO.getMaintenanceStartTime(), siteVenuePO.getMaintenanceStartTime())
                        || !Objects.equals(venueInfoPO.getMaintenanceEndTime(), siteVenuePO.getMaintenanceEndTime())
                || !Objects.equals(venueInfoPO.getRemark(),siteVenuePO.getRemark())) {
                    throw new BaowangDefaultException(ResultCode.ADMIN_VENUE_NOT_MAINTAIN);
                }
            }


            //总控场馆在维护
            if (venueInfoPO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_VENUE_MAINTAIN);
            }

            //总控已禁用场馆
            if (venueInfoPO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_VENUE_CLOSE);
            }
        }

        if (requestVO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {

            //站点场馆未开启,不允许维护
            if (!siteVenuePO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                throw new BaowangDefaultException(ResultCode.SITE_VENUE_NOT_MAINTAIN);
            }
        }

        UpdateWrapper<SiteVenuePO> updateWrapper = new UpdateWrapper<>();
        if (ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime())) {
            if (requestVO.getMaintenanceStartTime() >= requestVO.getMaintenanceEndTime()) {
                throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
            }
        }

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;
        if (Objects.equals(requestVO.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = requestVO.getMaintenanceEndTime();
            maintenanceStartTime = requestVO.getMaintenanceStartTime();
            remark = requestVO.getRemark();
        }

        updateWrapper
                .eq("id", siteVenuePO.getId())
                .set("site_last_status", null)
                .set("last_status", null)
                .set("status", requestVO.getStatus())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("updater", CurrReqUtils.getAccount())
                .set("remark", remark)
                .set("updated_time", System.currentTimeMillis());
        boolean bool = baseMapper.update(null, updateWrapper) > 0;

        if (!bool) {
            log.info("修改场馆失败,不向下继续修改");
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        List<SiteGamePO> siteGameList = siteGameService.getBaseMapper().selectList(Wrappers
                .lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getVenueCode, siteVenuePO.getVenueCode())
                .eq(SiteGamePO::getSiteCode, siteCode)
                .select(SiteGamePO::getId));
        if (CollectionUtils.isNotEmpty(siteGameList)) {
            List<String> ids = siteGameList.stream().map(SiteGamePO::getId).toList();
            GameClassStatusRequestUpVO requestUpVO = GameClassStatusRequestUpVO.builder()
                    .ids(ids)
                    .status(requestVO.getStatus())
                    .maintenanceStartTime(maintenanceStartTime)
                    .maintenanceEndTime(maintenanceEndTime)
                    .remark(remark)
                    .venueCode(siteVenuePO.getVenueCode())
                    .build();
            //站点向下改游戏
            siteGameService.upSiteVenueGameInfoStatus(requestUpVO);
        }
        CompletableFuture.runAsync(() -> {
            LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
        });
        return bool;
    }


    public void newAdminUpVenueStatus(GameClassStatusRequestUpVO request,String venueCode){
        //站点场馆
        UpdateWrapper<SiteVenuePO> siteVenueUpdateWrapper = new UpdateWrapper<>();

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (Objects.equals(request.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = request.getMaintenanceEndTime();
            maintenanceStartTime = request.getMaintenanceStartTime();
            remark = request.getRemark();
        }


        siteVenueUpdateWrapper
                .eq("venue_code",venueCode)
                .set("site_last_status", null)
                .set("updater", CommonConstant.ADMIN_NAME)
                .set("updated_time", System.currentTimeMillis())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("remark", remark);


        //总控选择开启
        if (StatusEnum.OPEN.getCode().equals(request.getStatus())) {
            //修改所有站点中,将上一次状态同步回去
            siteVenueUpdateWrapper.in("last_status",Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode(),StatusEnum.CLOSE.getCode()))
                    .in("status",Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .setSql("status = last_status")
                    .set("last_status", null);
        }else if (StatusEnum.MAINTAIN.getCode().equals(request.getStatus())) {// 总控修改状态为维护 ,将开启中的场馆修改
//            List<SiteVenuePO> siteVenueList = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
//                    .eq(SiteVenuePO::getVenueCode, request.getVenueCode())
//                    .eq(SiteVenuePO::getStatus, StatusEnum.OPEN.getCode())
//                    .select(SiteVenuePO::getId));
//            siteVenueIds = siteVenueList.stream().map(SiteVenuePO::getId).toList();
            siteVenueUpdateWrapper
                    .eq("status",StatusEnum.OPEN.getCode())
                    .set("status", request.getStatus())
                    .setSql("last_status = status");
        }else if (StatusEnum.CLOSE.getCode().equals(request.getStatus())) {// 总控修改状态为关闭场馆 修改所有站点中 未关闭的场馆
//            List<SiteVenuePO> siteVenueList = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
//                    .eq(SiteVenuePO::getVenueCode, request.getVenueCode())
//                    .in(SiteVenuePO::getStatus,Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
//                    .select(SiteVenuePO::getId));
//            siteVenueIds = siteVenueList.stream().map(SiteVenuePO::getId).toList();

            siteVenueUpdateWrapper
                    .in("status",Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .set("status", request.getStatus())
                    .set("last_status", null);
        }
        baseMapper.update(null, siteVenueUpdateWrapper);
    }

    /**
     * 总控改场馆 调用该方法,去改站点下的场馆与游戏
     *
     * @param request 参数对象
     */
    public void adminUpVenueStatus(GameClassStatusRequestUpVO request) {
        //站点场馆
        UpdateWrapper<SiteVenuePO> siteVenueUpdateWrapper = new UpdateWrapper<>();

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (Objects.equals(request.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = request.getMaintenanceEndTime();
            maintenanceStartTime = request.getMaintenanceStartTime();
            remark = request.getRemark();
        }


        siteVenueUpdateWrapper
                .set("site_last_status", null)
                .set("updater", CommonConstant.ADMIN_NAME)
                .set("updated_time", System.currentTimeMillis())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("remark", remark);

        List<String> siteVenueIds = Lists.newArrayList();

        //总控选择开启
        if (StatusEnum.OPEN.getCode().equals(request.getStatus())) {
            List<SiteVenuePO> siteVenueList = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
                    .eq(SiteVenuePO::getVenueCode, request.getVenueCode())
                    .isNotNull(SiteVenuePO::getLastStatus)
//                    .ne(SiteVenuePO::getStatus, StatusEnum.CLOSE.getCode())
                            .in(SiteVenuePO::getStatus,Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .select(SiteVenuePO::getId));

            siteVenueIds = siteVenueList.stream().map(SiteVenuePO::getId).toList();

            //修改所有站点中,将上一次状态同步回去
            siteVenueUpdateWrapper
                    .in("id", siteVenueIds)
                    .setSql("status = last_status")
                    .set("last_status", null);

        }

        // 总控修改状态为维护 ,将开启中的场馆修改
        if (StatusEnum.MAINTAIN.getCode().equals(request.getStatus())) {
            List<SiteVenuePO> siteVenueList = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
                    .eq(SiteVenuePO::getVenueCode, request.getVenueCode())
                    .eq(SiteVenuePO::getStatus, StatusEnum.OPEN.getCode())
                    .select(SiteVenuePO::getId));

            siteVenueIds = siteVenueList.stream().map(SiteVenuePO::getId).toList();

            siteVenueUpdateWrapper
//                    .eq("venue_code", request.getVenueCode())
//                    .eq("status", StatusEnum.OPEN.getCode())
                    .in("id", siteVenueIds)
                    .set("status", request.getStatus())
                    .setSql("last_status = status");

        }

        // 总控修改状态为关闭场馆 修改所有站点中 未关闭的场馆
        if (StatusEnum.CLOSE.getCode().equals(request.getStatus())) {
            List<SiteVenuePO> siteVenueList = baseMapper.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
                    .eq(SiteVenuePO::getVenueCode, request.getVenueCode())
//                    .ne(SiteVenuePO::getStatus, request.getStatus())
                            .in(SiteVenuePO::getStatus,Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
                    .select(SiteVenuePO::getId));

            siteVenueIds = siteVenueList.stream().map(SiteVenuePO::getId).toList();


            siteVenueUpdateWrapper
                    .in("id", siteVenueIds)
                    .set("status", request.getStatus())
                    .set("last_status", null);
        }

        if (CollectionUtils.isNotEmpty(siteVenueIds)) {
            baseMapper.update(null, siteVenueUpdateWrapper);
        }


        //总控向下改游戏
//        siteGameService.adminUpGameInfoStatus(request);


    }

}
