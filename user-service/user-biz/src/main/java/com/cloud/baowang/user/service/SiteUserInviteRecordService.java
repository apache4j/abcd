package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.agent.api.vo.agent.winLoss.UserWinLossParamVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.vo.ValidInviteUserRechargeMqVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.vo.operations.DomainQueryVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.user.api.vo.user.invite.*;
import com.cloud.baowang.user.po.SiteUserInviteConfigPO;
import com.cloud.baowang.user.po.SiteUserInviteRecordPO;
import com.cloud.baowang.user.repositories.SiteUserInviteRecordRepository;
import com.cloud.baowang.user.util.MinioFileService;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 23:46
 * @description:
 */
@Service
@AllArgsConstructor
@Slf4j
public class SiteUserInviteRecordService extends ServiceImpl<SiteUserInviteRecordRepository, SiteUserInviteRecordPO> {

    private final UserInfoApi userInfoApi;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final SiteUserInviteConfigService siteUserInviteConfigService;
    private final SiteUserInviteRecordRepository siteUserInviteRecordRepository;
    private final DomainInfoApi domainInfoApi;
    private final MinioFileService minioFileService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;


    /**
     * 需求变更 兼容线上数据
     *
     * @param siteCode
     * @return
     */
    public ResponseVO<Void> validInviteRecoup(String siteCode, boolean isInit) {
        log.info("***************** 有效邀请 validInviteRecoup begin ***************** siteCode : " + siteCode);
        SiteUserInviteRecordReqVO reqVO = new SiteUserInviteRecordReqVO();
        reqVO.setSiteCode(siteCode);
        Integer page = 1;
        Integer size = 50;
        List<SiteUserInviteRecordPO> records;
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(reqVO.getSiteCode());
        reqVO.setPageSize(size);
        do {
            reqVO.setPageNumber(page);
            reqVO.setPageSize(size);
            Page<SiteUserInviteRecordPO> inviteRecordPage = getInviteRecordPageTemp(reqVO);
            records = inviteRecordPage.getRecords();
            if (ObjectUtil.isNotEmpty(records)) {
                //首存
                List<SiteUserInviteRecordPO> pos = new ArrayList<>(records.size());
                List<String> targetUserIdList = records.stream().map(SiteUserInviteRecordPO::getTargetUserId).toList();
                List<UserInfoVO> userList = userInfoApi.getUserInfoByUserIds(targetUserIdList);
                Map<String, UserInfoVO> userMap = userList.stream().collect(Collectors.toMap(UserInfoVO::getUserId, p -> p, (k1, k2) -> k2));
                //总存提
                ReportUserWinLossParamVO userWinLossParamVO = new ReportUserWinLossParamVO();
                userWinLossParamVO.setUserIds(targetUserIdList);
                List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByUserIds(userWinLossParamVO);
                Map<String, BigDecimal> depMap = depList.stream().collect(Collectors.toMap(ReportUserAmountVO::getUserId, ReportUserAmountVO::getRechargeAmount, (k1, k2) -> k2));

                SiteUserInviteConfigPO configPO = siteUserInviteConfigService.getInviteConfigBySiteCode(reqVO.getSiteCode());
                if (isInit) {
                    for (SiteUserInviteRecordPO po : records) {
                        po.setFirstDepositAmount(userMap.get(po.getTargetUserId()).getFirstDepositAmount());
                        po.setDepositAmountTotal(depMap.get(po.getTargetUserId()) == null ? BigDecimal.ZERO : depMap.get(po.getTargetUserId()));
                        po.setFirstDepositTime(userMap.get(po.getTargetUserId()).getFirstDepositTime());
                        BigDecimal rate = currencyRateMap.get(po.getCurrency());
                        BigDecimal firstDepositAmount = AmountUtils.divide(po.getFirstDepositAmount(), rate);
                        BigDecimal DepositAmountTotal = AmountUtils.divide(po.getDepositAmountTotal(), rate);
                        po.setValidFirstDeposit(firstDepositAmount.compareTo(configPO.getFirstDepositAmount()) >= 0 ? 1 : 0);
                        po.setValidTotalDeposit(DepositAmountTotal.compareTo(configPO.getDepositAmountTotal()) >= 0 ? 1 : 0);
                        pos.add(po);
                    }
                } else {
                    //修改配置,更新累计存款是否达标
                    for (SiteUserInviteRecordPO po : records) {
                        if (po.getValidFirstDeposit() == 1 && po.getValidTotalDeposit() == 1) {
                            continue;
                        }
                        BigDecimal rate = currencyRateMap.get(po.getCurrency());
                        BigDecimal DepositAmountTotal = AmountUtils.divide(po.getDepositAmountTotal(), rate);
                        po.setValidTotalDeposit(DepositAmountTotal.compareTo(configPO.getDepositAmountTotal()) >= 0 ? 1 : 0);
                        pos.add(po);
                    }
                }
                this.updateBatchById(pos);
            }
            page++;
        } while (!records.isEmpty());
        return ResponseVO.success();
    }

    public Page<SiteUserInviteRecordPO> getInviteRecordPageTemp(SiteUserInviteRecordReqVO reqVO) {
        Page<SiteUserInviteRecordPO> page = new Page<>(reqVO.getPageNumber(), reqVO.getPageSize());
        LambdaQueryWrapper<SiteUserInviteRecordPO> query = Wrappers.lambdaQuery();
        LambdaUpdateWrapper<SiteUserInviteRecordPO> update = Wrappers.lambdaUpdate();
        query.ge(ObjUtil.isNotEmpty(reqVO.getStartTime()), SiteUserInviteRecordPO::getRegisterTime, reqVO.getStartTime());
        query.le(ObjUtil.isNotEmpty(reqVO.getStartTime()), SiteUserInviteRecordPO::getRegisterTime, reqVO.getEndTime());
        query.eq(StringUtils.isNotBlank(reqVO.getTargetAccount()), SiteUserInviteRecordPO::getTargetAccount, reqVO.getTargetAccount());
        query.eq(StringUtils.isNotBlank(reqVO.getUserAccount()), SiteUserInviteRecordPO::getUserAccount, reqVO.getUserAccount());
        query.eq(StringUtils.isNotBlank(reqVO.getInviteCode()), SiteUserInviteRecordPO::getInviteCode, reqVO.getInviteCode());
        query.eq(SiteUserInviteRecordPO::getSiteCode, reqVO.getSiteCode());
        return this.page(page, query);
    }

    /**
     * 更新存款信息
     */
    public void updateDepositInfo(ValidInviteUserRechargeMqVO reqVO) {

        LambdaQueryWrapper<SiteUserInviteRecordPO> query = Wrappers.lambdaQuery();
        query.eq(SiteUserInviteRecordPO::getTargetUserId, reqVO.getUserId());
        query.eq(SiteUserInviteRecordPO::getSiteCode, reqVO.getSiteCode());
        SiteUserInviteRecordPO po = this.baseMapper.selectOne(query);
        if (po != null) {
            Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(reqVO.getSiteCode());
            SiteUserInviteConfigPO configPO = siteUserInviteConfigService.getInviteConfigBySiteCode(reqVO.getSiteCode());
            BigDecimal rate = currencyRateMap.get(po.getCurrency());
            if (reqVO.getIsFirstDeposit() == 1) {
                //首存
                po.setFirstDepositAmount(reqVO.getAmount());
                po.setDepositAmountTotal(reqVO.getAmount());
                po.setFirstDepositTime(System.currentTimeMillis());
                if (configPO != null) {
                    BigDecimal firstDepositWtc = AmountUtils.divide(reqVO.getAmount(), rate);
                    po.setValidFirstDeposit(firstDepositWtc.compareTo(configPO.getFirstDepositAmount()) >= 0 ? 1 : 0);
                    po.setValidTotalDeposit(firstDepositWtc.compareTo(configPO.getDepositAmountTotal()) >= 0 ? 1 : 0);
                }
            } else {
                BigDecimal totalDeposit = po.getDepositAmountTotal();
                totalDeposit = totalDeposit.add(reqVO.getAmount());
                po.setDepositAmountTotal(totalDeposit);
                if (configPO != null) {
                    if (po.getValidTotalDeposit() != 1) {
                        BigDecimal totalWtc = AmountUtils.divide(totalDeposit, rate);
                        po.setValidTotalDeposit(totalWtc.compareTo(configPO.getDepositAmountTotal()) >= 0 ? 1 : 0);
                    }
                }
            }
            this.baseMapper.updateById(po);
        }
    }

    public Page<SiteUserInviteRecordResVO> getInviteRecordPage(SiteUserInviteRecordReqVO reqVO) {
        Page<SiteUserInviteRecordPO> page = getInviteRecordPageTemp(reqVO);
        return ConvertUtil.toConverPage(page.convert(item -> {
            SiteUserInviteRecordResVO vo = BeanUtil.copyProperties(item, SiteUserInviteRecordResVO.class);
            vo.setValidInvite(item.getValidFirstDeposit() == 1 && item.getValidTotalDeposit() == 1 ? CommonConstant.business_one_str : CommonConstant.business_zero_str);
            return vo;
        }));

       /* Page<SiteUserInviteRecordResVO> pageVO = siteUserInviteRecordRepository.getInviteRecordPage(page, reqVO);
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(reqVO.getSiteCode());

        List<SiteUserInviteRecordResVO> records = pageVO.getRecords();
        if (ObjectUtil.isNotEmpty(records)) {
            //首存
            List<String> targetUserIdList = records.stream().map(SiteUserInviteRecordResVO::getTargetUserId).toList();
            List<UserInfoVO> userList = userInfoApi.getUserInfoByUserIds(targetUserIdList);
            Map<String, UserInfoVO> userMap = userList.stream().collect(Collectors.toMap(UserInfoVO::getUserId,p->p,(k1, k2)->k2));
            //总存提
            UserWinLossParamVO userWinLossParamVO = new UserWinLossParamVO();
            userWinLossParamVO.setUserIds(targetUserIdList);
            List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByUserIds(userWinLossParamVO);
            Map<String, BigDecimal> depMap = depList.stream().collect(Collectors.toMap(ReportUserAmountVO::getUserId,ReportUserAmountVO::getRechargeAmount,(k1, k2)->k2));
            //fixme  是否转为主货币或者平台币

            SiteUserInviteConfigPO configPO = siteUserInviteConfigService.getInviteConfigBySiteCode(reqVO.getSiteCode());
            for (SiteUserInviteRecordResVO vo : records) {
                vo.setFirstDepositAmount(userMap.get(vo.getTargetUserId()).getFirstDepositAmount());
                vo.setDepositAmountTotal(depMap.get(vo.getTargetUserId()) == null ? BigDecimal.ZERO : depMap.get(vo.getTargetUserId()));
                vo.setFirstDepositTime(userMap.get(vo.getTargetUserId()).getFirstDepositTime());
                vo.setValidInvite("0");
                BigDecimal rate = currencyRateMap.get(vo.getCurrency());
                BigDecimal firstDepositAmount = AmountUtils.divide(vo.getFirstDepositAmount(), rate);
                BigDecimal DepositAmountTotal = AmountUtils.divide(vo.getDepositAmountTotal(), rate);
                if (firstDepositAmount.compareTo(configPO.getFirstDepositAmount()) >= 0 &&
                        DepositAmountTotal.compareTo(configPO.getDepositAmountTotal()) >= 0) {
                    vo.setValidInvite("1");
                }
            }
        }


        return pageVO;*/
    }

    public Long getInviteRecordCount(SiteUserInviteRecordReqVO reqVO) {
        LambdaQueryWrapper<SiteUserInviteRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(reqVO.getUserAccount()), SiteUserInviteRecordPO::getUserAccount, reqVO.getUserAccount());
        queryWrapper.eq(StrUtil.isNotBlank(reqVO.getInviteCode()), SiteUserInviteRecordPO::getInviteCode, reqVO.getInviteCode());
        queryWrapper.eq(ObjUtil.isNotEmpty(reqVO.getTargetAccount()), SiteUserInviteRecordPO::getTargetAccount, reqVO.getTargetAccount());
        queryWrapper.gt(ObjUtil.isNotEmpty(reqVO.getStartTime()), SiteUserInviteRecordPO::getRegisterTime, reqVO.getStartTime());
        queryWrapper.lt(ObjUtil.isNotEmpty(reqVO.getEndTime()), SiteUserInviteRecordPO::getRegisterTime, reqVO.getEndTime());
        return siteUserInviteRecordRepository.selectCount(queryWrapper);
    }

    public Long getCountByUserId(String userId) {
        LambdaQueryWrapper<SiteUserInviteRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteUserInviteRecordPO::getTargetUserId, userId);
        return siteUserInviteRecordRepository.selectCount(queryWrapper);
    }

    public UserInviteResVO inviteFriend(String userId, String siteCode) {
        Integer deviceType = CurrReqUtils.getReqDeviceType();
        DomainQueryVO domainQueryVO = new DomainQueryVO();
        domainQueryVO.setSiteCode(siteCode);
        domainQueryVO.setStatus(1);
        domainQueryVO.setDomainType(2);

        //获取域名
        DomainVO domainVO = domainInfoApi.getDomainByType(domainQueryVO);
        if (domainVO == null) {
            throw new BaowangDefaultException(ResultCode.DOMAIN_NULL);
        }

        GetByUserAccountVO userInfoVO = userInfoApi.getByUserInfoId(userId);

        UserInviteResVO resVO = new UserInviteResVO();
        resVO.setSiteCode(siteCode);
        resVO.setInviteCode(userInfoVO.getFriendInviteCode());
        String url = CurrReqUtils.getBizCustom();
        log.info("邀请链接获取的bizCustom:{}", url);
        if (url == null || deviceType == null || deviceType == 3 || deviceType == 5) {
            url = domainVO.getDomainAddr();
        }
        if (url.startsWith("http")) {
            if (deviceType == null || deviceType == 1 || deviceType == 0) {
                resVO.setInviteUrl(url);
            } else {
                resVO.setInviteUrl(url);
            }
        } else {
            if (deviceType == null || deviceType == 1 || deviceType == 0) {
                resVO.setInviteUrl("http://" + url);
            } else {
                resVO.setInviteUrl("http://" + url);
            }

        }

        //获取背景图
        String minioDomain = minioFileService.getMinioDomain();
        String language = CurrReqUtils.getLanguage();
        String device = "2";
        if (deviceType == null || deviceType == 1) {
            device = "1";
        }
        String iconUrl = siteUserInviteConfigService.getIconUrlByLanguage(siteCode, language, device);
        if (!ObjectUtil.isEmpty(iconUrl)) {
            resVO.setIconUrl(minioDomain + "/" + iconUrl);
        } else {
            resVO.setIconUrl(null);
        }
        return resVO;
    }

    public List<SiteUserInviteRecordTaskResVO> getInviteRecord(SiteUserInviteRecordTaskReqVO reqVO) {
        List<SiteUserInviteRecordTaskResVO> result = siteUserInviteRecordRepository.getInviteRecord(reqVO);
        return result;
    }
}
