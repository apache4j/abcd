package com.cloud.baowang.play.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.cloud.baowang.play.api.vo.venue.*;
import com.cloud.baowang.play.po.*;
import com.cloud.baowang.play.repositories.GameInfoRepository;
import com.cloud.baowang.play.repositories.SiteGameRepository;
import com.cloud.baowang.play.repositories.SiteVenueRepository;
import com.cloud.baowang.play.repositories.VenueInfoRepository;
import com.cloud.baowang.play.util.LobbyCateUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class SiteGameService extends ServiceImpl<SiteGameRepository, SiteGamePO> {

    private final GameInfoRepository gameInfoRepository;

    private final SiteVenueRepository siteVenueRepository;


    public List<SiteGamePO> selectList(LambdaQueryWrapper<SiteGamePO> wrapper) {
        return baseMapper.selectList(wrapper);
    }

    public List<SiteGamePO> getSiteGameIdsBySiteCodeList(String siteCode) {
        if (ObjectUtil.isEmpty(siteCode) || CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode)) {
            return selectList(null);
        }
        String key = RedisConstants.getSiteCodeKeyConstant(RedisConstants.KEY_SITE_GAME_LIST,siteCode);

//        Set<SiteGamePO> siteGameList = RedisUtil.getSet(key);

        List<SiteGamePO> siteGameList = RedisUtil.getValue(key);
        if (CollectionUtils.isNotEmpty(siteGameList)) {
//            return new ArrayList<>(siteGameList);
            return siteGameList;
        }

        LambdaQueryWrapper<SiteGamePO> wrapper = Wrappers.lambdaQuery(SiteGamePO.class);
        wrapper.eq(SiteGamePO::getSiteCode, siteCode);
        List<SiteGamePO> list = baseMapper.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
//            RedisUtil.setSet(key, new HashSet<>(list), 5L, TimeUnit.MINUTES);
            RedisUtil.setValue(key, list, 5L, TimeUnit.MINUTES);
        }
        return list;
    }

    /**
     * 根据站点查询出游戏ID
     */
    public List<String> getSiteGameIdsBySiteCode(String siteCode) {
        if (StringUtils.isBlank(siteCode) || siteCode.equals(CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            return Lists.newArrayList();
        }

        List<SiteGamePO> list = getSiteGameIdsBySiteCodeList(siteCode);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(SiteGamePO::getGameInfoId).toList();
    }


    /**
     * 根据站点查询出游戏ID
     */
    public List<String> getSiteGameIdsBySiteCode(String siteCode, List<String> gameIds) {
//        if (StringUtils.isBlank(siteCode)) {
//            return Lists.newArrayList();
//        }

        if (CollectionUtils.isEmpty(gameIds)) {
            return Lists.newArrayList();
        }

        LambdaQueryWrapper<SiteGamePO> wrapper = Wrappers.lambdaQuery(SiteGamePO.class);
        if (!StringUtils.isBlank(siteCode) && !siteCode.equals(CommonConstant.ADMIN_CENTER_SITE_CODE)) {
            wrapper.eq(SiteGamePO::getSiteCode, siteCode);
        }

        wrapper.in(SiteGamePO::getGameInfoId, gameIds);

        List<SiteGamePO> list = baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return list.stream().map(SiteGamePO::getGameInfoId).toList();
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean addSiteGame(String siteCode, List<String> gameInfoIds) {

        if (ObjectUtil.isEmpty(siteCode) || CollectionUtil.isEmpty(gameInfoIds)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        List<GameInfoPO> list = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class).in(GameInfoPO::getId, gameInfoIds));
        if (CollectionUtil.isEmpty(list)) {
            throw new BaowangDefaultException(ResultCode.PARAM_NOT_VALID);
        }

        List<SiteGamePO> siteGamePOList = baseMapper.selectList(Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getSiteCode, siteCode)
        );

        //差值要删除
        if (CollectionUtil.isNotEmpty(siteGamePOList)) {
            List<String> oldGameInfoList = new ArrayList<>(siteGamePOList.stream().map(SiteGamePO::getGameInfoId).toList());
            oldGameInfoList.removeAll(gameInfoIds);
            if (CollectionUtil.isNotEmpty(oldGameInfoList)) {
                baseMapper.delete(Wrappers.lambdaQuery(SiteGamePO.class).eq(SiteGamePO::getSiteCode, siteCode).in(SiteGamePO::getGameInfoId, oldGameInfoList));
//                gameJoinClassRepository.delete(Wrappers.lambdaQuery(GameJoinClassPO.class).eq(GameJoinClassPO::getSiteCode, siteCode)
//                        .in(GameJoinClassPO::getGameId, oldGameInfoList));
//                RedisUtil.deleteKey(RedisConstants.getWildcardsKey(RedisConstants.KEY_GAME_JOIN_CLASS));
            }
        }


        Map<String, List<SiteGamePO>> siteGameMap = siteGamePOList.stream().collect(Collectors.groupingBy(SiteGamePO::getGameInfoId));

        int i = 0;
        for (GameInfoPO tmp : list) {
            i++;
            List<SiteGamePO> gameInfoList = siteGameMap.get(tmp.getId());
            if (CollectionUtil.isNotEmpty(gameInfoList)) {
                continue;
            }

            SiteGamePO gamePO = SiteGamePO.builder()
                    .gameInfoId(tmp.getId())
                    .venueCode(tmp.getVenueCode())
                    .status(StatusEnum.CLOSE.getCode())
                    .siteCode(siteCode)
                    .currencyCode(tmp.getCurrencyCode())
                    .homeHotSort(i)
                    .build();
            baseMapper.insert(gamePO);
        }
        return Boolean.TRUE;
    }


    public Boolean siteUpGameInfo(GameInfoAddOrUpdateRequest request) {
        List<String> ids = getSiteGameIdsBySiteCode(CurrReqUtils.getSiteCode());
        String siteCode = CurrReqUtils.getSiteCode();

        //商户登进来有站点,但是没查到站点信息代表没权限
        if (!CommonConstant.ADMIN_CENTER_SITE_CODE.equals(siteCode) && CollectionUtil.isEmpty(ids)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SiteGamePO po = SiteGamePO.builder()
                .gameInfoId(request.getId())
                .cornerLabels(request.getCornerLabels())
                .label(request.getLabel())
                .siteCode(CurrReqUtils.getSiteCode())
                .build();

        Boolean bool = baseMapper.update(po, Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getSiteCode, CurrReqUtils.getSiteCode())
                .eq(SiteGamePO::getGameInfoId, request.getId())) > 0;

        if (bool) {
            CompletableFuture.runAsync(() -> {
                LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
            });
        }

        return bool;
    }


    /**
     * 总控修改场馆状态,同步站点游戏状态
     */
    public void adminUpVenueToGameStatus(GameClassStatusRequestUpVO request,String venueCode){

        //站点游戏
        UpdateWrapper<SiteGamePO> siteGameUpdateWrapper = new UpdateWrapper<>();

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (Objects.equals(request.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = request.getMaintenanceEndTime();
            maintenanceStartTime = request.getMaintenanceStartTime();
            remark = request.getRemark();
        }

        siteGameUpdateWrapper
                .set("updater", CommonConstant.ADMIN_NAME)
                .set("updated_time", System.currentTimeMillis())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("site_last_status", null)
                .set("remark", remark)
                .eq("venue_code", venueCode);


        //总控选择开启
        if (StatusEnum.OPEN.getCode().equals(request.getStatus())) {
            //修改所有站点中游戏 ,将上一次状态同步回去
            siteGameUpdateWrapper.in("last_status", Lists.newArrayList(StatusEnum.OPEN.getCode(), StatusEnum.MAINTAIN.getCode(), StatusEnum.CLOSE.getCode()))
                    .in("status", Lists.newArrayList(StatusEnum.OPEN.getCode(), StatusEnum.MAINTAIN.getCode()))
                    .setSql("status = last_status")
                    .set("last_status", null);
        } else if (StatusEnum.MAINTAIN.getCode().equals(request.getStatus())) {
            // 总控修改状态为维护 ,将开启中的场馆修改
            //修改所有站点中游戏 ,将开启中的游戏修改

            siteGameUpdateWrapper.eq("status", StatusEnum.OPEN.getCode())
                    .set("status", request.getStatus())
                    .setSql("last_status = status");
        } else if (StatusEnum.CLOSE.getCode().equals(request.getStatus())) {
            // 总控修改状态为关闭场馆 修改所有站点中 未关闭的场馆
            //修改所有站点中 未关闭的游戏
            siteGameUpdateWrapper.in("status", Lists.newArrayList(StatusEnum.OPEN.getCode(), StatusEnum.MAINTAIN.getCode()))
                    .set("status", request.getStatus())
                    .set("last_status", null);
        }

        baseMapper.update(null, siteGameUpdateWrapper);
    }

    /**
     * 总控向下改游戏, 调用该方法,去改站点下的游戏
     *
     * @param request 参数对象
     */
    public void adminUpGameInfoStatus(GameClassStatusRequestUpVO request) {

        //站点游戏
        UpdateWrapper<SiteGamePO> siteGameUpdateWrapper = new UpdateWrapper<>();

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (Objects.equals(request.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = request.getMaintenanceEndTime();
            maintenanceStartTime = request.getMaintenanceStartTime();
            remark = request.getRemark();
        }

        LambdaQueryWrapper<SiteGamePO> queryWrapper = Wrappers.lambdaQuery(SiteGamePO.class)
                .in(CollectionUtils.isNotEmpty(request.getIds()), SiteGamePO::getGameInfoId, request.getIds())
                .eq(ObjectUtil.isNotEmpty(request.getVenueCode()), SiteGamePO::getVenueCode, request.getVenueCode())
                .select(SiteGamePO::getId);


        siteGameUpdateWrapper
                .set("updater", CommonConstant.ADMIN_NAME)
                .set("updated_time", System.currentTimeMillis())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("site_last_status", null)
                .set("remark", remark);


        List<String> siteGameIds = Lists.newArrayList();

        //总控选择开启
        if (StatusEnum.OPEN.getCode().equals(request.getStatus())) {
            queryWrapper.isNotNull(SiteGamePO::getLastStatus)
//                    .ne(SiteGamePO::getStatus, StatusEnum.CLOSE.getCode())
                    .in(SiteGamePO::getStatus, Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()))
            ;
            List<SiteGamePO> siteGameList = baseMapper.selectList(queryWrapper);

            siteGameIds = siteGameList.stream().map(SiteGamePO::getId).toList();

            //修改所有站点中游戏 ,将上一次状态同步回去
            siteGameUpdateWrapper
                    .setSql("status = last_status")
                    .set("last_status", null);
        }

        // 总控修改状态为维护 ,将开启中的场馆修改
        if (StatusEnum.MAINTAIN.getCode().equals(request.getStatus())) {
            queryWrapper.eq(SiteGamePO::getStatus, StatusEnum.OPEN.getCode());
            List<SiteGamePO> siteGameList = baseMapper.selectList(queryWrapper);
            siteGameIds = siteGameList.stream().map(SiteGamePO::getId).toList();
            //修改所有站点中游戏 ,将开启中的游戏修改
            siteGameUpdateWrapper
                    .set("status", request.getStatus())
                    .setSql("last_status = status");
        }

        // 总控修改状态为关闭场馆 修改所有站点中 未关闭的场馆
        if (StatusEnum.CLOSE.getCode().equals(request.getStatus())) {
//            queryWrapper.ne(SiteGamePO::getStatus, request.getStatus());
            queryWrapper.in(SiteGamePO::getStatus,Lists.newArrayList(StatusEnum.OPEN.getCode(),StatusEnum.MAINTAIN.getCode()));
            List<SiteGamePO> siteGameList = baseMapper.selectList(queryWrapper);
            siteGameIds = siteGameList.stream().map(SiteGamePO::getId).toList();

            //修改所有站点中 未关闭的游戏
            siteGameUpdateWrapper
                    .set("status", request.getStatus())
                    .set("last_status", null);
        }

        if (CollectionUtils.isNotEmpty(siteGameIds)) {
            siteGameUpdateWrapper.in("id", siteGameIds);
            baseMapper.update(null, siteGameUpdateWrapper);
        }
    }



    /**
     * 站点修改场馆-修改游戏状态
     */
    public Boolean upSiteVenueGameInfoStatus(GameClassStatusRequestUpVO request) {

        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (Objects.equals(request.getStatus(), StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = request.getMaintenanceEndTime();
            maintenanceStartTime = request.getMaintenanceStartTime();
            remark = request.getRemark();
        }

        //站点游戏
        UpdateWrapper<SiteGamePO> siteGameWrapper = new UpdateWrapper<>();
        siteGameWrapper.in("id", request.getIds())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis())
                .set("maintenance_start_time", maintenanceStartTime)
                .set("maintenance_end_time", maintenanceEndTime)
                .set("remark", remark);

        //站点选择开启场馆,将上一次状态同步回去
        if (StatusEnum.OPEN.getCode().equals(request.getStatus())) {
            siteGameWrapper
                    .isNotNull("site_last_status")
                    .ne("status", StatusEnum.CLOSE.getCode())
                    .setSql("status = site_last_status")
                    .set("site_last_status", null);

            //站点改总控场馆为启用的时候.要把站点下的游戏也同步启用,但是前提是总控的游戏是启用的
            List<GameInfoPO> gameInfoPOList = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                    .eq(GameInfoPO::getStatus, StatusEnum.OPEN.getCode())
                    .eq(GameInfoPO::getVenueCode, request.getVenueCode())
                    .select(GameInfoPO::getId));
            if (CollectionUtil.isEmpty(gameInfoPOList)) {
                return Boolean.FALSE;
            }
            List<String> gameInfoIdList = gameInfoPOList.stream().map(GameInfoPO::getId).toList();
            siteGameWrapper.in("game_info_id", gameInfoIdList);
        }

        // 站点维护场馆 ,将非禁用的的游戏进行维护
        if (StatusEnum.MAINTAIN.getCode().equals(request.getStatus())) {
            siteGameWrapper
                    .eq("status", StatusEnum.OPEN.getCode())
                    .setSql("site_last_status = status")
                    .set("status", request.getStatus());


            //原本就是维护的 只同步维护时间跟备注
            UpdateWrapper<SiteGamePO> maintainGame = new UpdateWrapper<>();
            maintainGame.in("id", request.getIds())
                    .eq("status", StatusEnum.MAINTAIN.getCode())
                    .set("updater", CurrReqUtils.getAccount())
                    .set("updated_time", System.currentTimeMillis())
                    .set(ObjectUtil.isNotEmpty(request.getMaintenanceStartTime()), "maintenance_start_time", request.getMaintenanceStartTime())
                    .set(ObjectUtil.isNotEmpty(request.getMaintenanceEndTime()), "maintenance_end_time", request.getMaintenanceEndTime())
                    .set(ObjectUtil.isNotEmpty(request.getRemark()), "remark", request.getRemark());
            baseMapper.update(null, maintainGame);

        }

        // 站点修改状态为关闭场馆 修改所有站点中 未关闭的游戏
        if (StatusEnum.CLOSE.getCode().equals(request.getStatus())) {
            siteGameWrapper
                    .ne("status", request.getStatus())
                    .set("site_last_status", null)
                    .set("status", request.getStatus());
        }

        baseMapper.update(null, siteGameWrapper);
        return true;
    }

    /**
     * 该方法是由站点后台改游戏状态 批量
     */
    public Boolean newUpGameInfoStatusBatch(BatchGameClassStatusRequestUpVO batchRequestVO) {
        String lock = String.format(RedisConstants.UP_SITE_GAME_STATUS_BATCH, CurrReqUtils.getSiteCode());
        String lockCode = RedisUtil.acquireImmediate(lock, 100L);
        if (lockCode == null) {
            log.info("站点批量修改游戏状态,太频繁");
            throw new BaowangDefaultException(ResultCode.PLEASE_TRY_AGAIN_LATER);
        }

        try {
            //因为批量修改如果出现异常不需要提示直接继续执行下一次
            return newUpSiteGameInfoStatusList(batchRequestVO);
        } catch (Exception e) {
            log.error("站点-批量修改游戏状态异常", e);
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("站点-批量修改游戏状态:{},执行结束,删除锁:{}", lock, release);
            }
        }
        return Boolean.TRUE;
    }


    /**
     * 站点修改游戏状态
     */
    public Boolean newUpSiteGameInfoStatusList(BatchGameClassStatusRequestUpVO requestVO) {
        if (CollectionUtil.isEmpty(requestVO.getIdBatch())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<String> idBatch = requestVO.getIdBatch();

        Integer status = requestVO.getStatus();

        String siteCode = CurrReqUtils.getSiteCode();

        LambdaQueryWrapper<SiteGamePO> wrapper = Wrappers.lambdaQuery(SiteGamePO.class)
                .in(SiteGamePO::getGameInfoId, idBatch)
                .eq(SiteGamePO::getSiteCode, siteCode);

        if (baseMapper.selectCount(wrapper) <= 0) {
            return false;
        }

        if (ObjectUtil.isNotEmpty(requestVO.getStatus())) {
            if (ObjectUtil.isEmpty(StatusEnum.nameByCode(requestVO.getStatus()))) {
                log.info("批量修改-站点修改游戏状态-请求参数异常:{}", requestVO);
                return Boolean.FALSE;
            }
        }

        if (ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime())) {
            if (requestVO.getMaintenanceStartTime() >= requestVO.getMaintenanceEndTime()) {
                log.info("批量修改-站点修改游戏状态-时间不对:{}", requestVO);
                return Boolean.FALSE;
            }
        }

        List<SiteGamePO> gamePOList = baseMapper.selectList(wrapper);

        List<String> venueCodeList = gamePOList.stream().map(SiteGamePO::getVenueCode).toList();
        venueCodeList = venueCodeList.stream().distinct().toList();


        UpdateWrapper<SiteGamePO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("game_info_id", idBatch)
                .set("status", status)
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis())
                .set("last_status", null);


        //批量修改游戏状态的前提是站点的场馆是开启的并且总控的游戏也是开启的
        if (requestVO.getStatus().equals(StatusEnum.OPEN.getCode())) {
            List<SiteVenuePO> siteVenuePOList = siteVenueRepository.selectList(Wrappers.lambdaQuery(SiteVenuePO.class)
                    .eq(SiteVenuePO::getSiteCode, siteCode)
                    .in(SiteVenuePO::getVenueCode, venueCodeList)
                    .eq(SiteVenuePO::getStatus, StatusEnum.OPEN.getCode())
            );

            if (CollectionUtil.isEmpty(siteVenuePOList)) {
                log.info("批量修改游戏状态为启用,没有场馆启用不允许启用游戏");
                return Boolean.FALSE;
            }


            List<GameInfoPO> gameInfoPOList = gameInfoRepository.selectList(Wrappers.lambdaQuery(GameInfoPO.class)
                    .in(GameInfoPO::getId, idBatch)
                    .eq(GameInfoPO::getStatus, StatusEnum.OPEN.getCode()));

            if (CollectionUtil.isEmpty(gameInfoPOList)) {
                log.info("批量修改游戏状态为启用,总控没有启用不允许启用游戏");
                return Boolean.FALSE;
            }

            List<String> gameIds = gameInfoPOList.stream().map(GameInfoPO::getId).toList();
            List<String> venueList = siteVenuePOList.stream().map(SiteVenuePO::getVenueCode).toList();
            venueList = venueList.stream().distinct().toList();

            updateWrapper.in("venue_code", venueList).in("game_info_id", gameIds);

        }

        if (!StatusEnum.MAINTAIN.getCode().equals(status)) {
            updateWrapper.set("maintenance_start_time", null)
                    .set("maintenance_end_time", null)
                    .set("remark", null);
        }


        //如果是维护只能将启用的改成维护
        if (StatusEnum.MAINTAIN.getCode().equals(status)) {
            updateWrapper.set("maintenance_start_time", requestVO.getMaintenanceStartTime())
                    .set("maintenance_end_time", requestVO.getMaintenanceEndTime())
                    .set("remark", requestVO.getRemark())
                    .eq("status", StatusEnum.OPEN.getCode());
        }

        if (StatusEnum.CLOSE.getCode().equals(requestVO.getStatus())) {
            updateWrapper.ne("status", requestVO.getStatus());
        }


        baseMapper.update(null, updateWrapper);
        CompletableFuture.runAsync(() -> {
            LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
        });
        return true;
    }


    /**
     * 站点修改游戏状态
     */
    public Boolean upSiteGameInfoStatus(GameClassStatusRequestUpVO requestVO) {
        if (ObjectUtil.isEmpty(requestVO.getId())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        String siteCode = CurrReqUtils.getSiteCode();
        LambdaQueryWrapper<SiteGamePO> wrapper = Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getGameInfoId, requestVO.getId())
                .eq(SiteGamePO::getSiteCode, siteCode);

        if (baseMapper.selectCount(wrapper) <= 0) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        if (ObjectUtil.isNotEmpty(requestVO.getStatus())) {
            if (ObjectUtil.isEmpty(StatusEnum.nameByCode(requestVO.getStatus()))) {
                log.info("请求参数异常:{}", requestVO);
                return Boolean.FALSE;
            }
        }

        if (ObjectUtil.isNotEmpty(requestVO.getMaintenanceStartTime()) && ObjectUtil.isNotEmpty(requestVO.getMaintenanceEndTime())) {
            if (requestVO.getMaintenanceStartTime() >= requestVO.getMaintenanceEndTime()) {
                throw new BaowangDefaultException(ResultCode.TIME_NOT_GOOD);
            }
        }


        Long maintenanceEndTime = null;
        Long maintenanceStartTime = null;
        String remark = null;

        if (requestVO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
            maintenanceEndTime = requestVO.getMaintenanceEndTime();
            maintenanceStartTime = requestVO.getMaintenanceStartTime();
            remark = requestVO.getRemark();
        }


        SiteGamePO siteGamePO = baseMapper.selectOne(Wrappers.lambdaQuery(SiteGamePO.class)
                .eq(SiteGamePO::getSiteCode, siteCode)
                .eq(SiteGamePO::getGameInfoId, requestVO.getId()));

        //重复操作
        if (siteGamePO.getStatus().equals(requestVO.getStatus())) {
            return Boolean.TRUE;
        }

        List<SiteVenuePO> siteVenueList = siteVenueRepository.selectList(Wrappers
                .lambdaQuery(SiteVenuePO.class)
                .eq(SiteVenuePO::getVenueCode, siteGamePO.getVenueCode())
                .eq(SiteVenuePO::getSiteCode, siteCode));

        if (ObjectUtil.isEmpty(siteVenueList)) {
            log.info("场馆不存在,siteCode:{},venueCode:{}", CurrReqUtils.getSiteCode(), siteGamePO.getVenueCode());
            throw new BaowangDefaultException(ResultCode.VENUE_NOT_OPEN);
        }

        GameInfoPO gameInfoPO = gameInfoRepository.selectById(siteGamePO.getGameInfoId());
        SiteVenuePO siteVenuePO = siteVenueList.get(0);

        //开启校验逻辑
        if (requestVO.getStatus().equals(StatusEnum.OPEN.getCode())) {

            if (!StatusEnum.OPEN.getCode().equals(siteVenuePO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.VENUE_NOT_OPEN);
            }


            //站点游戏从维护到开启情况下,总控游戏是维护的,这个错误码前端弹出同步总控的维护时间跟备注到站点
            if (gameInfoPO.getStatus().equals(StatusEnum.MAINTAIN.getCode())
                    && siteGamePO.getStatus().equals(StatusEnum.MAINTAIN.getCode())) {
                //只要站点跟场馆的维护时间不一样,就需要弹出同步维护时间
                if (!Objects.equals(gameInfoPO.getMaintenanceStartTime(), siteGamePO.getMaintenanceStartTime())
                        || !Objects.equals(gameInfoPO.getMaintenanceEndTime(), siteGamePO.getMaintenanceEndTime())) {
                    throw new BaowangDefaultException(ResultCode.ADMIN_GAME_MAINTAIN_SYN);
                }
            }

            //总控游戏禁用 站点尝试开启
            if (StatusEnum.CLOSE.getCode().equals(gameInfoPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_GAME_CLOSE);
            }

            //总控游戏禁用 站点尝试开启
            if (StatusEnum.MAINTAIN.getCode().equals(gameInfoPO.getStatus())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_GAME_NOT_OPEN);
            }

        } else if (Objects.equals(requestVO.getStatus(), StatusEnum.MAINTAIN.getCode())) {//维护

            //站点场馆禁用的情况不允许维护
            if (siteVenuePO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
                throw new BaowangDefaultException(ResultCode.SITE_VENUE_CLOSE);
            }

            //总台游戏禁用的情况不允许维护
            if (gameInfoPO.getStatus().equals(StatusEnum.CLOSE.getCode())) {
                throw new BaowangDefaultException(ResultCode.ADMIN_GAME_CLOSE);
            }

            //站点游戏如果不是开启状态情况不允许维护
            if (!siteGamePO.getStatus().equals(StatusEnum.OPEN.getCode())) {
                throw new BaowangDefaultException(ResultCode.SITE_GAME_NOT_MAINTAIN);
            }

        }

        List<SiteGamePO> gameInfoPOList = baseMapper.selectList(wrapper);

        if (CollectionUtil.isNotEmpty(gameInfoPOList)) {
            List<String> gameIds = gameInfoPOList.stream().map(SiteGamePO::getId).toList();
            UpdateWrapper<SiteGamePO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id", gameIds)
                    .set("status", requestVO.getStatus())
                    .set("maintenance_start_time", maintenanceStartTime)
                    .set("maintenance_end_time", maintenanceEndTime)
                    .set("updater", CurrReqUtils.getAccount())
                    .set("remark", remark)
                    .set("updated_time", System.currentTimeMillis());
            baseMapper.update(null, updateWrapper);
        }
        CompletableFuture.runAsync(() -> {
            LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
        });
        return true;
    }


    /**
     * 站点同步总控游戏维护信息
     */
    public Boolean upSynAdminGameInfoStatus(GameClassStatusRequestUpVO requestVO) {
        GameInfoPO gameInfoPo = gameInfoRepository.selectById(requestVO.getId());
        if (ObjectUtil.isEmpty(gameInfoPo)) {
            return Boolean.FALSE;
        }

        String siteCode = CurrReqUtils.getSiteCode();
        UpdateWrapper<SiteGamePO> gameWrapper = new UpdateWrapper<>();
        gameWrapper.eq("site_code", siteCode)
                .eq("game_info_id", gameInfoPo.getId())
                .eq("status", StatusEnum.MAINTAIN.getCode())
                .set("last_status", null)
                .set("maintenance_start_time", gameInfoPo.getMaintenanceStartTime())
                .set("maintenance_end_time", gameInfoPo.getMaintenanceEndTime())
                .set("remark", gameInfoPo.getRemark())
                .set("updater", CurrReqUtils.getAccount())
                .set("updated_time", System.currentTimeMillis());

        Boolean result = baseMapper.update(null, gameWrapper) > 0;

        CompletableFuture.runAsync(() -> {
            LobbyCateUtil.deleteLobbySiteGameInfo(siteCode);
        });
        return result;
    }

}
