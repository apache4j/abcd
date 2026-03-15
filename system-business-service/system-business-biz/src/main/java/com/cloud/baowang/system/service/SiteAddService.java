package com.cloud.baowang.system.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.WayFeeTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.IPUtil;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.GameInfoApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.system.api.api.verify.ChannelSiteLinkApi;
import com.cloud.baowang.system.api.enums.SiteOptionModelNameEnum;
import com.cloud.baowang.system.api.enums.SiteOptionStatusEnum;
import com.cloud.baowang.system.api.enums.SiteOptionTypeEnum;
import com.cloud.baowang.system.api.vo.operations.SkinResVO;
import com.cloud.baowang.system.api.vo.site.*;
import com.cloud.baowang.system.api.vo.site.change.SiteInfoChangeRecordReqVO;
import com.cloud.baowang.system.po.site.SitePO;
import com.cloud.baowang.system.repositories.SiteRepository;
import com.cloud.baowang.system.service.operations.SiteCustomerService;
import com.cloud.baowang.system.service.operations.SkinInfoService;
import com.cloud.baowang.system.service.site.SiteAdminService;
import com.cloud.baowang.system.service.site.change.SiteInfoChangeRecordService;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.vo.recharge.SystemRechargeWayRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SystemWithdrawWayResponseVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteAddService {

    private final PlayVenueInfoApi playVenueInfoApi;
    private final GameInfoApi gameInfoApi;

    private final SiteRepository siteRepository;

    private final SiteAdminService siteAdminService;

    private final SiteRechargeApi siteRechargeApi;

    private final SiteWithdrawApi siteWithdrawApi;

    private final ChannelSiteLinkApi channelSiteLinkApi;

    private final SiteCustomerService siteCustomerService;

    private final VipRankApi vipRankApi;

    private final SiteService siteService;

    private final SystemWithdrawWayApi systemWithdrawWayApi;
    private final SystemWithdrawChannelApi systemWithdrawChannelApi;

    private final SystemRechargeWayApi systemRechargeWayApi;
    private final SystemRechargeChannelApi systemRechargeChannelApi;

    private final SkinInfoService skinInfoService;
    private final SiteInfoChangeRecordService siteInfoChangeRecordService;

    @Value("${admin.pwd:aa123456}")
    private String pwd;


    public ResponseVO<?> judgeAndAddSite(SiteAddVO siteAddVO) {
        Integer step = siteAddVO.getLastStep();
        return switch (step) {
            case 1 -> siteBasic(siteAddVO.getSiteCode(), siteAddVO.getSiteBasic());
            case 2 -> siteConfig(siteAddVO.getSiteCode(), siteAddVO.getSiteConfig());
            case 3 -> siteVenue(siteAddVO.getSiteVenue());
            case 4 -> siteDeposit(siteAddVO.getSiteDeposit());
            case 5 -> siteWithdraw(siteAddVO.getSiteWithdraw());
            case 6 -> siteSms();
            case 7 -> siteEmail();
            case 8 -> siteCustomer(siteAddVO.getSiteCode(), siteAddVO);
            default -> null;
        };
    }

    public ResponseVO<Boolean> updateSiteInfo(SiteAddVO siteAddVO) {
        ResponseVO<Boolean> data=null;
        try {
            data=siteService.updateSiteInfo(siteAddVO);
            if (data.getCode()!= ResultCode.SUCCESS.getCode()){
                SiteInfoChangeRecordReqVO sd=siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(null,null, SiteOptionModelNameEnum.site.getname(),
                        CurrReqUtils.getReqIp(),null, SiteOptionTypeEnum.DataUpdate.getCode(),
                        SiteOptionStatusEnum.fail.getCode(),CurrReqUtils.getAccount());
                siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            }
        }catch (BaowangDefaultException e){
            SiteInfoChangeRecordReqVO sd=siteInfoChangeRecordService.initSiteInfoChangeRecordReqVO(null,null,SiteOptionModelNameEnum.site.getname(),
                    CurrReqUtils.getReqIp(),null,SiteOptionTypeEnum.DataUpdate.getCode(),
                    SiteOptionStatusEnum.fail.getCode(),CurrReqUtils.getAccount());
            siteInfoChangeRecordService.addSiteInfoChangeRequestVO(sd);
            throw new BaowangDefaultException(e.getMessage());
        }
        return data;
    }

    private ResponseVO<?> siteEmail() {
        try {
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("update site email have error, siteCode:{}", e.getMessage());
            return ResponseVO.fail(ResultCode.UPDATE_SITE_EMAIL_ERROR);
        }
    }

    private ResponseVO<?> siteSms() {
        try {
            return ResponseVO.success();
        } catch (Exception e) {
            log.error("update site sms have error, siteCode:{}", e.getMessage());
            return ResponseVO.fail(ResultCode.UPDATE_SITE_MESSAGE_ERROR);
        }
    }

    /**
     * 保存站点
     *
     * @param siteAddVO
     * @return
     */
    public ResponseVO<SiteResVO> siteCustomer(String siteCode, final SiteAddVO siteAddVO) {
        ResponseVO<SitePO> responseVO = siteService.saveAndUpdateSite(siteCode, siteAddVO, pwd);
        if (responseVO.isOk()) {
            SiteBasicVO siteBasic = siteAddVO.getSiteBasic();
            SitePO sitePO = responseVO.getData();
            SiteResVO vo = new SiteResVO();
            vo.setSiteName(sitePO.getSiteName());
            vo.setSiteAdminAccount(sitePO.getSiteAdminAccount());
            vo.setPwd(pwd);
            vo.setAllowIps(siteBasic.getAllowIps());
            return ResponseVO.success(vo);
        }
        return ResponseVO.fail(responseVO.getCode());
    }

    private ResponseVO<?> siteWithdraw(List<SiteWithdrawVO> siteWithdrawList) {
//        if (CollectionUtil.isEmpty(siteWithdrawList)) {
//            return ResponseVO.fail(ResultCode.PARAM_ERROR);
//        }

        Map<String, SiteWithdrawVO> siteWithdrawMap = siteWithdrawList.stream()
                .collect(toMap(SiteWithdrawVO::getWithdrawWayId, item -> item));
        // 手续费费率不能低于场馆费
        List<SystemWithdrawWayResponseVO> withdrawResult = systemWithdrawWayApi.queryWithdrawWayList();

        for (SystemWithdrawWayResponseVO vo : withdrawResult) {
            if (siteWithdrawMap.containsKey(vo.getId())) {
                SiteWithdrawVO siteWithdraw = siteWithdrawMap.get(vo.getId());
                //总台金额配置
                BigDecimal systemWayFee = vo.getWayFee();
                BigDecimal systemWayFeeFixedAmount = vo.getWayFeeFixedAmount();

                //当前传入配置
                BigDecimal siteWayFee = siteWithdraw.getWithdrawFee();
                BigDecimal siteWayFeeFixedAmount = siteWithdraw.getWayFeeFixedAmount();

                if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0 || siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                }
            }
        }

        return ResponseVO.success();
    }

    private ResponseVO<?> siteDeposit(final List<SiteDepositVO> siteDeposit) {
//        if (CollectionUtil.isEmpty(siteDeposit)) {
//            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
//        }

        // 手续费费率不能低于场馆费
        List<SystemRechargeWayRespVO> rechargeResult = systemRechargeWayApi.queryRechargeWayList();
        //校验前端传入存款方式是否存在
        List<String> systemRechargeIds = rechargeResult.stream()
                .map(SystemRechargeWayRespVO::getId)
                .toList();
        for (SiteDepositVO siteDepositVO : siteDeposit) {
            if (!systemRechargeIds.contains(siteDepositVO.getRechargeWayId())) {
                throw new BaowangDefaultException(ResultCode.DEPOSIT_CHOOSE_ERROR);
            }
        }

        Map<String, SiteDepositVO> siteDepositMap = siteDeposit.stream()
                .collect(toMap(SiteDepositVO::getRechargeWayId, siteDepositVO -> siteDepositVO));
        //校验值是否小于总台配置
        for (SystemRechargeWayRespVO vo : rechargeResult) {
            if (siteDepositMap.containsKey(vo.getId())) {
                SiteDepositVO siteDepositVO = siteDepositMap.get(vo.getId());
                //总台金额配置
                BigDecimal systemWayFee = vo.getWayFee();
                BigDecimal systemWayFeeFixedAmount = vo.getWayFeeFixedAmount();

                //当前传入配置
                BigDecimal siteWayFee = siteDepositVO.getDepositFee();
                BigDecimal siteWayFeeFixedAmount = siteDepositVO.getWayFeeFixedAmount();

                if (WayFeeTypeEnum.PERCENTAGE.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                } else if (WayFeeTypeEnum.PERCENTAGE_FIXED_AMOUNT.getCode().equals(String.valueOf(vo.getFeeType()))) {
                    if (siteWayFee.compareTo(systemWayFee) < 0 || siteWayFeeFixedAmount.compareTo(systemWayFeeFixedAmount) < 0) {
                        throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
                    }
                }
            }
        }
        return ResponseVO.success();
    }

    private ResponseVO<?> siteVenue(final List<SiteVenueAuthorizeVO> venueAuthorizeVOS) {
        if (CollectionUtil.isEmpty(venueAuthorizeVOS)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        // 手续费费率不能低于场馆费
        ResponseVO<List<VenueInfoVO>> venueResult = playVenueInfoApi.venueInfoList();
        Map<String, BigDecimal> map = venueAuthorizeVOS.stream().collect(toMap(SiteVenueAuthorizeVO::getVenueCode, SiteVenueAuthorizeVO::getHandlingFee));
        boolean validFlag = false;
        if (venueResult.isOk() && ObjectUtil.isNotEmpty(venueResult.getData())) {
            List<VenueInfoVO> list = venueResult.getData();
            for (VenueInfoVO vo : list) {
                if (map.containsKey(vo.getVenueCode()) && map.get(vo.getVenueCode())
                        .compareTo(vo.getVenueProportion()) < 0) {
                    validFlag = true;
                }
            }
        }

        List<String> venueCodeIds = venueAuthorizeVOS.stream().map(SiteVenueAuthorizeVO::getVenueCode).toList();
        List<VenueInfoVO> data=playVenueInfoApi.getSystemVenuesByIds(venueCodeIds).getData();
        if (CollectionUtil.isEmpty(data)) {
            throw new BaowangDefaultException(ResultCode.PARAM_NOT_VALID);
        }
        Map<Integer, List<VenueInfoVO>> checkSprot = data.stream()
                .filter(venue -> venue.getVenueType() == VenueTypeEnum.SPORTS.getCode()) // 过滤条件
                .collect(Collectors.groupingBy(VenueInfoVO::getVenueJoinType));

        if (ObjectUtils.isNotEmpty(checkSprot) && checkSprot.size()>=2){
            throw new BaowangDefaultException(ResultCode.VENUE_JOIN_TYPE_INVITE_CODE_ERROR);
        }

        if (validFlag) {
            throw new BaowangDefaultException(ResultCode.SET_HANDING_FEE_ERROR);
        }
        return ResponseVO.success();
    }

    private ResponseVO<?> siteConfig(String siteCode, final SiteConfigVO siteConfig) {
        if (StringUtils.isBlank(siteCode)) {
            if (siteConfig.getBkName().length() > 20) {
                return ResponseVO.fail(ResultCode.BK_NAME_LENGTH_MORE);
            }
            List<SitePO> bkSite = siteService.lambdaQuery()
                    .eq(SitePO::getBkName, siteConfig.getBkName()).list();
            if (!bkSite.isEmpty()) {
                return ResponseVO.fail(ResultCode.BK_NAME_IS_EXIST);
            }
            // 查询皮肤释放被禁用
            if (StringUtils.isNotEmpty(siteConfig.getSkin())) {
                SkinResVO skinResVO = skinInfoService.querySkinOne(siteConfig.getSkin());
                if (Objects.nonNull(skinResVO) && skinResVO.getStatus().equals(EnableStatusEnum.DISABLE.getCode())) {
                    return ResponseVO.fail(ResultCode.SKIN_DISABLED);
                }
            }
        }
        return ResponseVO.success();
    }

    private ResponseVO<?> siteBasic(String siteCode, final SiteBasicVO siteBasic) {
        String siteName = siteBasic.getSiteName();
        // 正则表达式：检查是否包含中文字符
        String regex = "^[a-zA-Z]+$";
        if (!siteName.matches(regex)) {
            //包含中文
            return ResponseVO.fail(ResultCode.SITE_NAME_NOT_CHINESE);
        }
        // 站点名称不可重复
        if (StringUtils.isBlank(siteCode)) {
            if (!siteService.lambdaQuery()
                    .eq(SitePO::getSiteName, siteBasic.getSiteName()).list().isEmpty()) {
                return ResponseVO.fail(ResultCode.SITE_NAME_IS_EXIST);
            }
            // 站点前缀不可重复
            if (!siteService.lambdaQuery()
                    .eq(SitePO::getSitePrefix, siteBasic.getSitePrefix()).list().isEmpty()) {
                return ResponseVO.fail(ResultCode.SITE_PREFIX_IS_EXIST);
            }
        }
        String allowIps = siteBasic.getAllowIps();
        if (StringUtils.isBlank(allowIps)) {
            return ResponseVO.fail(ResultCode.PARAM_ERROR);
        }
        //校验ip格式是否正确
        IPUtil.validateIPs(allowIps);
        return ResponseVO.success();
    }


}