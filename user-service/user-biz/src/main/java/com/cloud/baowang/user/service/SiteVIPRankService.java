package com.cloud.baowang.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.YesOrNoEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.ValidateUtil;
import com.cloud.baowang.common.core.utils.tool.vo.Comparison;
import com.cloud.baowang.common.core.vo.base.*;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.SiteHandicapModeEnum;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.vip.*;
import com.cloud.baowang.user.enums.ChangeOperationEnum;
import com.cloud.baowang.user.po.*;
import com.cloud.baowang.user.repositories.SiteVIPRankRepository;
import com.cloud.baowang.user.repositories.VIPGradeRepository;
import com.cloud.baowang.user.util.MinioFileService;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawConfigApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import com.cloud.baowang.wallet.api.vo.withdraw.UserWithdrawConfigAddOrUpdateVO;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author : 小智
 * @Date : 2024/8/2 14:47
 * @Version : 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteVIPRankService extends ServiceImpl<SiteVIPRankRepository, SiteVIPRankPO> {

    private final I18nApi i18nApi;
    private final SystemParamApi systemParamApi;
    private final UserWithdrawConfigApi userWithdrawConfigApi;

    private final SiteVIPRankRepository siteVIPRankRepository;
    private final VIPGradeRepository vipGradeRepository;
    private final MinioFileService minioFileService;

    private final SiteCurrencyInfoApi currencyInfoApi;
    private final VipRankService systemVipRankService;
    private final SiteVipRankCurrencyConfigService siteVipRankCurrencyConfigService;
    private final SiteVipSportService siteVipSportService;
    private final SiteVIPGradeService siteVIPGradeService;
    private VIPOperationService vipOperationService;
    private SiteVIPBenefitService siteVIPBenefitService;
    private SiteVIPVenueExeService siteVIPVenueExeService;
    private SiteMedalInfoService siteMedalInfoService;
    private SiteVipOptionService siteVipOptionService;
    private SiteApi siteApi;

    public ResponseVO<Page<SiteVIPRankVO>> queryVIPRankPage(PageVO pageVO, String siteCode) {
        Page<SiteVIPRankPO> page = new Page<>(pageVO.getPageNumber(), pageVO.getPageSize());
        Page<SiteVIPRankVO> resultPage = siteVIPRankRepository.queryVIPRankPage(page, siteCode);
        //获取当前站点全部vip等级
        List<SiteVIPGradePO> list = siteVIPGradeService.list(Wrappers.lambdaQuery(SiteVIPGradePO.class).eq(SiteVIPGradePO::getSiteCode, siteCode));
        //批量封装
        Map<Integer, String> map = list.stream()
                .collect(Collectors.toMap(SiteVIPGradePO::getVipGradeCode, SiteVIPGradePO::getVipGradeName));


        String minioDomain = minioFileService.getMinioDomain();
        resultPage.convert(item -> {

            String vipGradeCodes = item.getVipGradeCodes();
            if (StringUtils.isNotBlank(vipGradeCodes)) {
                String[] codes = vipGradeCodes.split(",");
                List<String> gradeCodeArr = Arrays.asList(codes);
                int minCode = Integer.parseInt(gradeCodeArr.get(0));
                String minGradeName = vipGradeRepository.getVipGradeNameBySiteCode(siteCode, minCode);
                item.setMinVipGrade(minCode);
                item.setMinVipGradeName(minGradeName);
                int maxCode = Integer.parseInt(codes[codes.length - 1]);
                String maxGradeName = vipGradeRepository.getVipGradeNameBySiteCode(siteCode, maxCode);
                item.setMaxVipGrade(maxCode);
                item.setMaxVipGradeName(maxGradeName);
                //codes转integer数组
                // 使用流将字符串数组转换为整数列表
                List<Integer> codesArr = Arrays.stream(codes)
                        .map(Integer::parseInt)
                        .collect(Collectors.toCollection(ArrayList::new));
                item.setVipGradeList(codesArr);

                List<String> gradeCodes = Arrays.asList(vipGradeCodes.split(CommonConstant.COMMA));
                String vipGradeCodesName = gradeCodes.stream()
                        .filter(gradeCode -> map.containsKey(Integer.parseInt(gradeCode)))
                        .map(gradeCode -> map.get(Integer.parseInt(gradeCode)))
                        .collect(Collectors.joining(CommonConstant.COMMA));
                item.setVipGradeCodesName(vipGradeCodesName);
            }
            String vipIcon = item.getVipIcon();
            if (StringUtils.isNotBlank(vipIcon)) {
                item.setVipIconImage(minioDomain + "/" + vipIcon);
            }
            return item;
        });
        return ResponseVO.success(resultPage);
    }

    /**
     * 根据id获取vip段位详情，包含周体育流水礼金信息（如果有）
     *
     * @param id 段位id
     * @return vo
     */
    public ResponseVO<SiteVIPRankVO> queryVIPRankDetailById(String id) {
        SiteVIPRankPO siteVIPRankPO = siteVIPRankRepository.selectById(id);
        log.info("vipRank id:{}查询", id);
        if (siteVIPRankPO == null) {
            log.info("vipRank id:{}查询为空", id);
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        log.info("获取到数据库中vip段位信息:id:{},结果:{}", id, JSON.toJSONString(siteVIPRankPO));
        SiteVIPRankVO vo = BeanUtil.copyProperties(siteVIPRankPO, SiteVIPRankVO.class);
        log.info("转换后vip段位信息id:{},结果:{}", id, JSON.toJSONString(vo));
        Integer weekSportFlag = vo.getWeekSportFlag();
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(weekSportFlag))) {
            LambdaQueryWrapper<SiteVipSportPO> query = Wrappers.lambdaQuery();
            query.eq(SiteVipSportPO::getRankId, id);
            List<SiteVipSportPO> list = siteVipSportService.list(query);
            List<SiteVipSportVo> siteVipSportVos = BeanUtil.copyToList(list, SiteVipSportVo.class);
            vo.setSportVos(siteVipSportVos);
        }

        String vipGradeCodes = vo.getVipGradeCodes();
        if (StringUtils.isNotBlank(vipGradeCodes)) {
            LambdaQueryWrapper<SiteVIPGradePO> gradeQuery = Wrappers.lambdaQuery();
            gradeQuery.eq(SiteVIPGradePO::getSiteCode, siteVIPRankPO.getSiteCode()).in(SiteVIPGradePO::getVipGradeCode, Arrays.asList(vipGradeCodes.split(CommonConstant.COMMA)));
            List<SiteVIPGradePO> list = siteVIPGradeService.list(gradeQuery);
            if (CollectionUtil.isNotEmpty(list)) {
                String gradeNames = list.stream()
                        .map(SiteVIPGradePO::getVipGradeName) // 假设有一个 getVipGradeName 方法
                        .collect(Collectors.joining(CommonConstant.COMMA));
                vo.setVipGradeCodesName(gradeNames);
            }
        }
        String vipIcon = vo.getVipIcon();
        if (StringUtils.isNotBlank(vipIcon)) {
            String minioDomain = minioFileService.getMinioDomain();
            vo.setVipIconImage(minioDomain + "/" + vipIcon);
        }
        String siteCode = siteVIPRankPO.getSiteCode();
        //获取下当前站点的币种
        List<SiteCurrencyInfoRespVO> currencyList = currencyInfoApi.getBySiteCode(siteCode);
        LambdaQueryWrapper<SiteVipRankCurrencyConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipRankCurrencyConfigPO::getSiteCode, siteVIPRankPO.getSiteCode())
                .eq(SiteVipRankCurrencyConfigPO::getVipRankCode, siteVIPRankPO.getVipRankCode());
        if (CollectionUtil.isNotEmpty(currencyList)) {
            List<String> currencyCodes = currencyList.stream()
                    .map(SiteCurrencyInfoRespVO::getCurrencyCode) // 获取 platCurrencyCode
                    .toList();
            query.in(SiteVipRankCurrencyConfigPO::getCurrencyCode, currencyCodes);
        }
        List<SiteVipRankCurrencyConfigPO> configPOS = siteVipRankCurrencyConfigService.list(query);
        if (CollectionUtil.isNotEmpty(configPOS)) {
            //根据币种分组
            List<SiteVipRankCurrencyConfigVO> result = new ArrayList<>();
            Map<String, List<SiteVipRankCurrencyConfigPO>> currencyRankMap = configPOS.stream().collect(Collectors.groupingBy(SiteVipRankCurrencyConfigPO::getCurrencyCode));

            for (String currencyCode : currencyRankMap.keySet()) {
                //当前币种对应全部提款方式的配置数据
                List<SiteVipRankCurrencyConfigPO> currencyRankArr = currencyRankMap.get(currencyCode);
                SiteVipRankCurrencyConfigPO vipRankCurrencyConfigVO = currencyRankArr.get(0);
                SiteVipRankCurrencyConfigVO configVO = new SiteVipRankCurrencyConfigVO();
                configVO.setSiteCode(vipRankCurrencyConfigVO.getSiteCode());
                configVO.setVipRankCode(vipRankCurrencyConfigVO.getVipRankCode());
                configVO.setCurrencyCode(currencyCode);
                configVO.setDailyWithdrawals(vipRankCurrencyConfigVO.getDailyWithdrawals());
                configVO.setDayWithdrawLimit(vipRankCurrencyConfigVO.getDayWithdrawLimit());
                configVO.setDailyWithdrawalNumsLimit(vipRankCurrencyConfigVO.getDailyWithdrawalNumsLimit());
                configVO.setDailyWithdrawAmountLimit(vipRankCurrencyConfigVO.getDailyWithdrawAmountLimit());
                List<SiteVipRankCurrencyWithdrawConfigVO> configVOS = new ArrayList<>();
                for (SiteVipRankCurrencyConfigPO siteVipRankCurrencyConfigPO : currencyRankArr) {
                    SiteVipRankCurrencyWithdrawConfigVO config = new SiteVipRankCurrencyWithdrawConfigVO();
                    config.setWithdrawWayId(siteVipRankCurrencyConfigPO.getWithdrawWayId());
                    config.setWithdrawFeeType(siteVipRankCurrencyConfigPO.getWithdrawFeeType());
                    config.setWithdrawFee(siteVipRankCurrencyConfigPO.getWithdrawFee());
                    configVOS.add(config);
                }
                configVO.setWithdrawConfigVOS(configVOS);
                result.add(configVO);
            }
            vo.setCurrencyConfigVOS(result);
        }
        setIsShowByVipRank(vo, siteCode);
        return ResponseVO.success(vo);
    }

    private void setIsShowByVipRank(SiteVIPRankVO rank, String siteCode) {
        //没有配置过多语言,说明是第一次进来,找到比当前段位低的段位,看看有没有配置过是否条件
        LambdaQueryWrapper<SiteVIPRankPO> preQuery = Wrappers.lambdaQuery();
        preQuery.eq(SiteVIPRankPO::getSiteCode, siteCode)
                .lt(SiteVIPRankPO::getVipRankCode, rank.getVipRankCode())
                .isNotNull(SiteVIPRankPO::getVipRankNameI18nCode)
                .orderByDesc(SiteVIPRankPO::getVipRankCode)
                .last("limit 0,1");
        SiteVIPRankPO previousVipRankPO = siteVIPRankRepository.selectOne(preQuery);
        LambdaQueryWrapper<SiteVIPRankPO> lastQuery = Wrappers.lambdaQuery();
        lastQuery.eq(SiteVIPRankPO::getSiteCode, siteCode)
                .gt(SiteVIPRankPO::getVipRankCode, rank.getVipRankCode())
                .isNotNull(SiteVIPRankPO::getVipRankNameI18nCode)
                .orderByAsc(SiteVIPRankPO::getVipRankCode)
                .last("limit 0,1");
        SiteVIPRankPO lastVipRankPO = siteVIPRankRepository.selectOne(lastQuery);
        Integer yesCode = Integer.parseInt(YesOrNoEnum.YES.getCode());
        Integer noCode = Integer.parseInt(YesOrNoEnum.NO.getCode());
        //一共四种情况
        if (previousVipRankPO != null && lastVipRankPO != null) {
            //包含上一个段位和下一个段位数据,根据上一个段位设置参与的勾选,根据下一个段位设置不参与的勾选
            // 转盘
            if (previousVipRankPO.getLuckFlag().equals(yesCode)) {
                rank.setLuckFlag(yesCode);
                rank.setLuckFlagIsShow(noCode);
            } else {
                rank.setLuckFlagIsShow(yesCode);
            }
            // 周流水
            if (previousVipRankPO.getWeekAmountFlag().equals(yesCode)) {
                rank.setWeekAmountFlag(yesCode);
                rank.setWeekAmountFlagIsShow(noCode);
            } else {
                rank.setWeekAmountFlagIsShow(yesCode);
            }
            // 月流水
            if (previousVipRankPO.getMonthAmountFlag().equals(yesCode)) {
                rank.setMonthAmountFlag(yesCode);
                rank.setMonthAmountFlagIsShow(noCode);
            } else {
                rank.setMonthAmountFlagIsShow(yesCode);
            }

            // 周体育
            if (previousVipRankPO.getWeekSportFlag().equals(yesCode)) {
                rank.setWeekSportFlag(yesCode);
                rank.setWeekSportFlagIsShow(noCode);
            } else {
                rank.setWeekSportFlagIsShow(yesCode);
            }

            // 手续费
           /* if (previousVipRankPO.getEncryCoinFee().equals(yesCode)) {
                rank.setEncryCoinFee(yesCode);
                rank.setEncryCoinFeeIsShow(noCode);
            } else {
                rank.setEncryCoinFeeIsShow(yesCode);
            }*/

            // svip
            if (previousVipRankPO.getSvipWelfare().equals(yesCode)) {
                rank.setSvipWelfare(yesCode);
                rank.setSvipWelfareIsShow(noCode);
            } else {
                rank.setSvipWelfareIsShow(yesCode);
            }

            // 赠品
            if (previousVipRankPO.getLuxuriousGifts().equals(yesCode)) {
                rank.setLuxuriousGifts(yesCode);
                rank.setLuxuriousGiftsIsShow(noCode);
            } else {
                rank.setLuxuriousGiftsIsShow(yesCode);
            }
            // VIP反水特权配置
            if (previousVipRankPO.getRebateConfig().equals(yesCode)) {
                rank.setRebateConfig(yesCode);
                rank.setRebateConfigIsShow(noCode);
            } else {
                rank.setRebateConfigIsShow(yesCode);
            }
            // 对于下一个段位的处理，设置不参与的勾选
            // 转盘
            if (lastVipRankPO.getLuckFlag()!=null && lastVipRankPO.getLuckFlag().equals(noCode)) {
                rank.setLuckFlag(noCode);
                rank.setLuckFlagIsShow(noCode);
            }

            // 周流水
            if (lastVipRankPO.getWeekAmountFlag()!=null && lastVipRankPO.getWeekAmountFlag().equals(noCode)) {
                rank.setWeekAmountFlag(noCode);
                rank.setWeekAmountFlagIsShow(noCode);
            }

            // 月流水
            if (lastVipRankPO.getMonthAmountFlag() !=null && lastVipRankPO.getMonthAmountFlag().equals(noCode)) {
                rank.setMonthAmountFlag(noCode);
                rank.setMonthAmountFlagIsShow(noCode);
            }

            // 周体育
            if (lastVipRankPO.getWeekSportFlag() !=null && lastVipRankPO.getWeekSportFlag().equals(noCode)) {
                rank.setWeekSportFlag(noCode);
                rank.setWeekSportFlagIsShow(noCode);
            }

            // 手续费
            /*if (lastVipRankPO.getEncryCoinFee().equals(noCode)) {
                rank.setEncryCoinFee(noCode);
                rank.setEncryCoinFeeIsShow(noCode);
            }*/

            // svip
            if (lastVipRankPO.getSvipWelfare() !=null &&lastVipRankPO.getSvipWelfare().equals(noCode)) {
                rank.setSvipWelfare(noCode);
                rank.setSvipWelfareIsShow(noCode);
            }

            // 赠品
            if (lastVipRankPO.getLuxuriousGifts() != null && lastVipRankPO.getLuxuriousGifts().equals(noCode)) {
                rank.setLuxuriousGifts(noCode);
                rank.setLuxuriousGiftsIsShow(noCode);
            }

            // VIP反水特权配置
            if (lastVipRankPO.getRebateConfig() != null && lastVipRankPO.getRebateConfig().equals(noCode)) {
                rank.setRebateConfig(noCode);
                rank.setRebateConfigIsShow(noCode);
            }

            //手续费改为手动配置
            rank.setEncryCoinFeeIsShow(yesCode);
        } else if (previousVipRankPO == null && lastVipRankPO == null) {
            //上一个段位和下一个段位都不包含,当前段位为第一条,都允许勾选
            rank.setLuckFlagIsShow(yesCode);
            rank.setWeekAmountFlagIsShow(yesCode);
            rank.setMonthAmountFlagIsShow(yesCode);
            rank.setWeekSportFlagIsShow(yesCode);
            rank.setEncryCoinFeeIsShow(yesCode);
            rank.setSvipWelfareIsShow(yesCode);
            rank.setLuxuriousGiftsIsShow(yesCode);
            // VIP反水特权配置
            rank.setRebateConfig(yesCode);
            rank.setRebateConfigIsShow(yesCode);

        } else if (previousVipRankPO != null) {
            //只有上一个段位,勾选参与的
            //转盘
            if (previousVipRankPO.getLuckFlag() != null && previousVipRankPO.getLuckFlag().equals(yesCode)) {
                rank.setLuckFlag(yesCode);
                rank.setLuckFlagIsShow(noCode);
            } else {
                rank.setLuckFlagIsShow(yesCode);
            }
            //周流水
            if (previousVipRankPO.getWeekAmountFlag() !=null && previousVipRankPO.getWeekAmountFlag().equals(yesCode)) {
                rank.setWeekAmountFlag(yesCode);
                rank.setWeekAmountFlagIsShow(noCode);
            } else {
                rank.setWeekAmountFlagIsShow(yesCode);
            }
            //月流水
            if (previousVipRankPO.getMonthAmountFlag() != null && previousVipRankPO.getMonthAmountFlag().equals(yesCode)) {
                rank.setMonthAmountFlag(yesCode);
                rank.setMonthAmountFlagIsShow(noCode);
            } else {
                rank.setMonthAmountFlagIsShow(yesCode);
            }
            //周体育
            if (previousVipRankPO.getWeekSportFlag() != null && previousVipRankPO.getWeekSportFlag().equals(yesCode)) {
                rank.setWeekSportFlag(yesCode);
                rank.setWeekSportFlagIsShow(noCode);
            } else {
                rank.setWeekSportFlagIsShow(yesCode);
            }
            //手续费
            /*if (previousVipRankPO.getEncryCoinFee().equals(yesCode)) {
                rank.setEncryCoinFee(yesCode);
                rank.setEncryCoinFeeIsShow(noCode);
            } else {
                rank.setEncryCoinFeeIsShow(yesCode);
            }*/
            rank.setEncryCoinFeeIsShow(yesCode);
            //svip
            if (previousVipRankPO.getSvipWelfare() != null && previousVipRankPO.getSvipWelfare().equals(yesCode)) {
                rank.setSvipWelfare(yesCode);
                rank.setSvipWelfareIsShow(noCode);
            } else {
                rank.setSvipWelfareIsShow(yesCode);
            }
            //赠品
            if (previousVipRankPO.getLuxuriousGifts() != null && previousVipRankPO.getLuxuriousGifts().equals(yesCode)) {
                rank.setLuxuriousGifts(yesCode);
                rank.setLuxuriousGiftsIsShow(noCode);
            } else {
                rank.setLuxuriousGiftsIsShow(yesCode);
            }
            // VIP反水特权配置
            if (previousVipRankPO.getRebateConfig() != null && previousVipRankPO.getRebateConfig().equals(yesCode)) {
                rank.setRebateConfig(yesCode);
                rank.setRebateConfigIsShow(noCode);
            }else {
                rank.setRebateConfigIsShow(yesCode);
            }
        } else {
            //只有下一个段位,勾选不参与的
            //转盘
            if (lastVipRankPO.getLuckFlag() != null && lastVipRankPO.getLuckFlag().equals(noCode)) {
                rank.setLuckFlag(noCode);
                rank.setLuckFlagIsShow(noCode);
            } else {
                rank.setLuckFlagIsShow(yesCode);
            }
            //周流水
            if (lastVipRankPO.getWeekAmountFlag() != null && lastVipRankPO.getWeekAmountFlag().equals(noCode)) {
                rank.setWeekAmountFlag(noCode);
                rank.setWeekAmountFlagIsShow(noCode);
            } else {
                rank.setWeekAmountFlagIsShow(yesCode);
            }
            //月流水
            if (lastVipRankPO.getMonthAmountFlag() != null && lastVipRankPO.getMonthAmountFlag().equals(noCode)) {
                rank.setMonthAmountFlag(noCode);
                rank.setMonthAmountFlagIsShow(noCode);
            } else {
                rank.setMonthAmountFlagIsShow(yesCode);
            }
            //周体育
            if (lastVipRankPO.getWeekSportFlag() != null && lastVipRankPO.getWeekSportFlag().equals(noCode)) {
                rank.setWeekSportFlag(noCode);
                rank.setWeekSportFlagIsShow(noCode);
            } else {
                rank.setWeekSportFlagIsShow(yesCode);
            }
            //手续费
            /*if (lastVipRankPO.getEncryCoinFee().equals(noCode)) {
                rank.setEncryCoinFee(noCode);
                rank.setEncryCoinFeeIsShow(noCode);
            } else {
                rank.setEncryCoinFeeIsShow(yesCode);
            }*/
            rank.setEncryCoinFeeIsShow(yesCode);
            //svip
            if (lastVipRankPO.getSvipWelfare() != null && lastVipRankPO.getSvipWelfare().equals(noCode)) {
                rank.setSvipWelfare(noCode);
                rank.setSvipWelfareIsShow(noCode);
            } else {
                rank.setSvipWelfareIsShow(yesCode);
            }
            //赠品
            if (lastVipRankPO.getLuxuriousGifts() != null && lastVipRankPO.getLuxuriousGifts().equals(noCode)) {
                rank.setLuxuriousGifts(noCode);
                rank.setLuxuriousGiftsIsShow(noCode);
            } else {
                rank.setLuxuriousGiftsIsShow(yesCode);
            }
            // VIP反水特权配置
            if (lastVipRankPO.getRebateConfig() != null && lastVipRankPO.getRebateConfig().equals(noCode)) {
                rank.setRebateConfig(noCode);
                rank.setRebateConfigIsShow(noCode);
            }else {
                rank.setRebateConfigIsShow(yesCode);
            }
        }


    }

    /**
     * 根据站点获取所有vip段位（包含周体育流水记录信息，如果有）
     *
     * @param siteCode 站点code
     * @return listVo
     */
    public List<SiteVIPRankVO> getVipRankListBySiteCode(String siteCode) {
        List<SiteVIPRankVO> rList = RedisUtil.getList(RedisConstants.KEY_VIP_RANK_CONFIG + siteCode);
        //缓存中没有获取到，从数据库中组装一次，同步到缓存
        if (ObjectUtils.isEmpty(rList)) {
            //查询当前站点所有vip段位数据
            List<SiteVIPRankPO> list = this.lambdaQuery().eq(SiteVIPRankPO::getSiteCode, siteCode).list();
            if (CollectionUtil.isNotEmpty(list)) {
                //关联vo数据
                List<SiteVIPRankVO> siteVIPRankVOS = processCache(siteCode, list);
                if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
                    rList.addAll(siteVIPRankVOS);
                }
            }
        }

        return rList;
    }

    /**
     * 根据站点获取所有vip段位（包含周体育流水记录信息，如果有）
     *
     * @param siteCode    站点code
     * @param vipRankCode vip段位代码
     * @return listVo
     */
    public SiteVIPRankVO getVipRankListBySiteCodeAndCode(String siteCode, Integer vipRankCode) {
        List<SiteVIPRankVO> vipRankListBySiteCodes = getVipRankListBySiteCode(siteCode);
        return vipRankListBySiteCodes
                .stream()
                .filter(e -> Objects.equals(vipRankCode, e.getVipRankCode()))
                .findFirst()
                .orElse(null);
    }

    public List<CodeValueNoI18VO> getVipRank() {
        LambdaQueryWrapper<VipRankPO> query = new LambdaQueryWrapper<>();
        query.select(VipRankPO::getVipRankCode, VipRankPO::getVipRankName);
        List<VipRankPO> vipRankPOS = systemVipRankService.list(query);
        return vipRankPOS.stream()
                .map(vipRankPO -> new CodeValueNoI18VO(CommonConstant.VIP_RANK, String.valueOf(vipRankPO.getVipRankCode()), vipRankPO.getVipRankName()))
                .collect(Collectors.toList());
    }


    @Transactional
    public boolean batchVIPRank(final String siteCode, List<String> currency,Integer handicapMode) {
        try {
            LambdaQueryWrapper<SiteVIPGradePO> query = Wrappers.lambdaQuery();
            query.eq(SiteVIPGradePO::getSiteCode, siteCode);
            if (siteVIPGradeService.count(query) > 0) {
                //已存在编辑过的站点会员等级数据，保留
                return true;
            }
            // 清空VIP相关站点数据
            this.remove(new LambdaQueryWrapper<SiteVIPRankPO>().eq(SiteVIPRankPO::getSiteCode, siteCode));
            siteVIPGradeService.remove(new LambdaQueryWrapper<SiteVIPGradePO>().eq(SiteVIPGradePO::getSiteCode,
                    siteCode));
            siteVIPBenefitService.remove(new LambdaQueryWrapper<SiteVIPBenefitPO>()
                    .eq(SiteVIPBenefitPO::getSiteCode, siteCode));

            // vip等级初始化数据
            List<VIPGradePO> gradePOList = vipGradeRepository.selectList(new LambdaQueryWrapper<>());
            List<SiteVIPGradePO> siteVIPGradePOS = Lists.newArrayList();
            List<SiteVIPBenefitPO> siteVIPBenefitPOList = Lists.newArrayList();
            //做一个map,包含默认等级所属段位的vip等级全部记录下来,用于初始化站点vip段位信息
            Map<Integer, List<Integer>> vipRankCodeGradeArrMap = new HashMap<>();
            for (VIPGradePO po : gradePOList) {
                Integer defaultRankCode = po.getDefaultRankCode();
                if (defaultRankCode != null) {
                    if (vipRankCodeGradeArrMap.containsKey(defaultRankCode)) {
                        vipRankCodeGradeArrMap.get(defaultRankCode).add(po.getVipGradeCode());
                    } else {
                        ArrayList<Integer> gradeCodeArr = new ArrayList<>();
                        gradeCodeArr.add(po.getVipGradeCode());
                        vipRankCodeGradeArrMap.put(defaultRankCode, gradeCodeArr);
                    }
                }
                siteVIPGradePOS.add(SiteVIPGradePO.builder()
                        .vipGradeCode(po.getVipGradeCode())
                        .vipGradeName(po.getVipGradeName()).siteCode(siteCode)
                        //站点vip所属段位,初始化时使用默认配置
                        .vipRankCode(defaultRankCode)
                        .build()
                );
                siteVIPBenefitPOList.add(SiteVIPBenefitPO.builder().siteCode(siteCode)
                        .vipGradeCode(po.getVipGradeCode()).build());
            }
            siteVIPGradeService.saveBatch(siteVIPGradePOS);


            // vip段位初始化数据
            List<VipRankPO> vipRankPOList = systemVipRankService.lambdaQuery().list();
//            ResponseVO<List<CodeValueVO>> rankPOResponseVO = systemParamApi.getSystemParamByType(CommonConstant.VIP_RANK);
            List<SiteVIPRankPO> siteVIPRankPOS = Lists.newArrayList();
            if (ObjectUtils.isNotEmpty(vipRankPOList)) {
                for (VipRankPO po : vipRankPOList) {
                    String gradeArrStr = "";
                    //存在默认vip等级中,配置了所属段位的数据
                    if (vipRankCodeGradeArrMap.containsKey(po.getVipRankCode())) {
                        List<Integer> vipGradeArr = vipRankCodeGradeArrMap.get(po.getVipRankCode());
                        gradeArrStr = CollectionUtil.join(vipGradeArr, CommonConstant.COMMA);
                    }
                    siteVIPRankPOS.add(SiteVIPRankPO.builder()
                            .vipRankCode(po.getVipRankCode())
                            .vipGradeCodes(gradeArrStr)
                            .vipRankName(po.getVipRankName())
                            .siteCode(siteCode).build());
                }
            }

            this.saveBatch(siteVIPRankPOS);
            //初始化会员提款设置数据
            List<VIPGradeVO> list= siteVipOptionService.getInitVIPGrade();
            initWithdrawConfig(currency, siteVIPRankPOS,list, siteCode,handicapMode);

            // vip权益初始化数据
            siteVIPBenefitService.saveBatch(siteVIPBenefitPOList);
            // 各游戏大类经验值初始化
            List<SiteVIPVenueExePO> siteVIPVenueExePOList = Lists.newArrayList();
            Map<String, String> venueType = systemParamApi.getSystemParamMapInner(CommonConstant.VENUE_TYPE);
            for (Map.Entry<String, String> map : venueType.entrySet()) {
                SiteVIPVenueExePO siteVIPVenueExePO = new SiteVIPVenueExePO();
                siteVIPVenueExePO.setSiteCode(siteCode);
                siteVIPVenueExePO.setVenueType(Integer.valueOf(map.getKey()));
                siteVIPVenueExePO.setExperience(BigDecimal.ONE);
                siteVIPVenueExePOList.add(siteVIPVenueExePO);
            }
            if (CollectionUtil.isNotEmpty(siteVIPVenueExePOList)) {
                LambdaQueryWrapper<SiteVIPVenueExePO> del = Wrappers.lambdaQuery();
                del.eq(SiteVIPVenueExePO::getSiteCode, siteCode);
                siteVIPVenueExeService.remove(del);
                siteVIPVenueExeService.saveBatch(siteVIPVenueExePOList);
            }

            // 勋章初始化
            siteMedalInfoService.init(siteCode);
        } catch (Exception e) {
            log.error("init siteCode:{} vip data error", siteCode, e);
            return false;
        }
        return true;
    }

    /**
     * 初始化会员提款设置数据
     *
     * @param currency       支持币种
     * @param siteVIPRankPOS 会员段位初始化列表
     * @param siteCode       站点
     */
    public void initWithdrawConfig(List<String> currency, List<SiteVIPRankPO> siteVIPRankPOS,List<VIPGradeVO> list, String siteCode,Integer handicapMode) {
        log.info("开始初始化会员提款设置，当前币种列表:{}，所属站点:{}", JSON.toJSONString(currency), siteCode);
        if (CollectionUtil.isNotEmpty(currency) && CollectionUtil.isNotEmpty(siteVIPRankPOS)) {
            List<UserWithdrawConfigAddOrUpdateVO> withdrawConfigVos = new ArrayList<>();
            for (String currencyCode : currency) {
                if (SiteHandicapModeEnum.China.getCode().equals(handicapMode)){
                    for (VIPGradeVO vipGradeVO:list){
                        UserWithdrawConfigAddOrUpdateVO vo = new UserWithdrawConfigAddOrUpdateVO();
                        vo.setCurrencyCode(currencyCode);
                        vo.setSiteCode(siteCode);
                        vo.setVipGradeCode(vipGradeVO.getVipGradeCode());
                        vo.setSingleDayWithdrawCount(0);
                        vo.setSingleMaxWithdrawAmount(null);
                        withdrawConfigVos.add(vo);
                    }
                }else{
                    for (SiteVIPRankPO siteVIPRankPO : siteVIPRankPOS) {
                        UserWithdrawConfigAddOrUpdateVO vo = new UserWithdrawConfigAddOrUpdateVO();
                        vo.setCurrencyCode(currencyCode);
                        vo.setSiteCode(siteCode);
                        vo.setVipRankCode(siteVIPRankPO.getVipRankCode());
                        vo.setSingleDayWithdrawCount(0);
                        vo.setSingleMaxWithdrawAmount(null);
                        withdrawConfigVos.add(vo);
                    }
                }

            }
            userWithdrawConfigApi.initUserWithdrawConfigData(withdrawConfigVos);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<?> updateVIPRank(VIPRankUpdateVO vipRankUpdateVO) {
        String operator = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        //校验参数
        checkParam(vipRankUpdateVO);
        //获取老vip段位信息（用作记录）
        SiteVIPRankPO oldVipRankPO = this.getOne(new LambdaQueryWrapper<SiteVIPRankPO>()
                .eq(SiteVIPRankPO::getSiteCode, siteCode).eq(SiteVIPRankPO::getVipRankCode,
                        vipRankUpdateVO.getVipRankCode()));
        if (oldVipRankPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        String vipRankNameI18Code;
        if (StringUtils.isBlank(oldVipRankPO.getVipRankNameI18nCode())) {
            //为空说明是第一次编辑，创建i18code
            vipRankNameI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.VIP_RANK_NAME.getCode());
        } else {
            //不为空，说明之前有修改过多语言，这里使用原来的i18
            vipRankNameI18Code = oldVipRankPO.getVipRankNameI18nCode();
        }
        SiteVIPRankPO newVipRankPo = BeanUtil.copyProperties(vipRankUpdateVO, SiteVIPRankPO.class);
        newVipRankPo.setSiteCode(siteCode);
        newVipRankPo.setId(oldVipRankPO.getId());
        String newVipGradeCodes = StringUtils
                .join(vipRankUpdateVO.getVipGradeCode(), ",");
        newVipRankPo.setVipGradeCodes(newVipGradeCodes);
        newVipRankPo.setVipRankNameI18nCode(vipRankNameI18Code);
        //修改段位
        this.updateById(newVipRankPo);
        //修改vip等级对应段位数据
        siteVIPGradeService.processGrade(newVipRankPo);
        //修改段位对应币种配置信息
        List<SiteVipRankCurrencyConfigVO> currencyConfigVOS = vipRankUpdateVO.getCurrencyConfigVOS();
        processCurrencyConfig(siteCode, newVipRankPo.getVipRankCode(), currencyConfigVOS);


        //处理周体育信息
        processSport(oldVipRankPO, vipRankUpdateVO);

        //同步更新下会员提款设置对应单日提款总次数，单日最高提款总额
        currencyConfigVOS.forEach(item -> {
            item.setSiteCode(siteCode);
            item.setVipRankCode(vipRankUpdateVO.getVipRankCode());
        });
        updUserWithdraw(siteCode, vipRankUpdateVO.getVipRankCode(), currencyConfigVOS);

        // 插入国际化信息(更新方法是删除后新增，这里每次直接调用update吧)
        Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                vipRankNameI18Code, vipRankUpdateVO.getVipRankNameI18nCodeList());
        i18nApi.update(i18nData);

        // 刷新redis缓存
        refreshVIPRank(null, siteCode);

        // 记录VIP操作日志
        recordOperation(newVipRankPo, oldVipRankPO, operator, vipRankUpdateVO.getVipRankCode(), siteCode);

        return ResponseVO.success();
    }

    /**
     * 修改段位信息对应币种配置信息-先删后增
     *
     * @param siteCode          站点code
     * @param vipRankCode       段位code
     * @param currencyConfigVOS 新的币种配置信息
     */
    private void processCurrencyConfig(String siteCode, Integer vipRankCode, List<SiteVipRankCurrencyConfigVO> currencyConfigVOS) {
        LambdaQueryWrapper<SiteVipRankCurrencyConfigPO> del = Wrappers.lambdaQuery();
        del.eq(SiteVipRankCurrencyConfigPO::getSiteCode, siteCode).eq(SiteVipRankCurrencyConfigPO::getVipRankCode, vipRankCode);
        siteVipRankCurrencyConfigService.remove(del);
        if (CollectionUtil.isNotEmpty(currencyConfigVOS)) {
            List<SiteVipRankCurrencyConfigPO> configPOS = new ArrayList<>();
            for (SiteVipRankCurrencyConfigVO currencyConfigVO : currencyConfigVOS) {
                paramValidCheck(currencyConfigVO);
                List<SiteVipRankCurrencyWithdrawConfigVO> withdrawConfigVOS = currencyConfigVO.getWithdrawConfigVOS();
                //可能会存在没有提款方式的某个币种的配置信息
                if (CollectionUtil.isNotEmpty(withdrawConfigVOS)) {
                    for (SiteVipRankCurrencyWithdrawConfigVO withdrawConfigVO : withdrawConfigVOS) {
                        SiteVipRankCurrencyConfigPO po = new SiteVipRankCurrencyConfigPO();
                        po.setSiteCode(siteCode);
                        po.setVipRankCode(vipRankCode);
                        po.setCurrencyCode(currencyConfigVO.getCurrencyCode());
                        po.setDailyWithdrawals(currencyConfigVO.getDailyWithdrawals());
                        po.setDayWithdrawLimit(currencyConfigVO.getDayWithdrawLimit());
                        po.setDailyWithdrawalNumsLimit(currencyConfigVO.getDailyWithdrawalNumsLimit());
                        po.setDailyWithdrawAmountLimit(currencyConfigVO.getDailyWithdrawAmountLimit());
                        po.setWithdrawWayId(withdrawConfigVO.getWithdrawWayId());
                        po.setWithdrawFeeType(withdrawConfigVO.getWithdrawFeeType());
                        po.setWithdrawFee(withdrawConfigVO.getWithdrawFee());
                        configPOS.add(po);
                    }
                } else {
                    SiteVipRankCurrencyConfigPO po = new SiteVipRankCurrencyConfigPO();
                    po.setSiteCode(siteCode);
                    po.setVipRankCode(vipRankCode);
                    po.setCurrencyCode(currencyConfigVO.getCurrencyCode());
                    po.setDailyWithdrawals(currencyConfigVO.getDailyWithdrawals());
                    po.setDayWithdrawLimit(currencyConfigVO.getDayWithdrawLimit());
                    po.setDailyWithdrawalNumsLimit(currencyConfigVO.getDailyWithdrawalNumsLimit());
                    po.setDailyWithdrawAmountLimit(currencyConfigVO.getDailyWithdrawAmountLimit());
                    configPOS.add(po);
                }
            }
            siteVipRankCurrencyConfigService.saveBatch(configPOS);
        }
    }

    /**
     * 输入参数校验
     * @param currencyConfigVO
     */
    public void paramValidCheck(SiteVipRankCurrencyConfigVO currencyConfigVO){
        Integer freeNums = currencyConfigVO.getDailyWithdrawals();
        Integer totalNums = currencyConfigVO.getDailyWithdrawalNumsLimit();
        BigDecimal freeAmount = currencyConfigVO.getDayWithdrawLimit();
        BigDecimal totalAmount = currencyConfigVO.getDailyWithdrawAmountLimit();

        if (isPositiveInteger(freeNums) || isPositiveInteger(totalNums) || isPositiveBigDecimal(freeAmount) || isPositiveBigDecimal(totalAmount)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (totalAmount.compareTo(freeAmount) < 0 ) {
            throw new BaowangDefaultException(ResultCode.FREE_AMOUNT_GT_DAY_AMOUNT);
        }
        if (totalNums < freeNums ) {
            throw new BaowangDefaultException(ResultCode.FREE_NUM_GT_DAY_NUM);
        }

    }

    public  boolean isPositiveInteger(Integer number) {
        return number == null || number < 0 || number >= 1000;
    }
    public  boolean isPositiveBigDecimal(BigDecimal number) {
        if (number == null) {
            return false;
        }
        String length = number.toBigInteger().toString();
        return  number.compareTo(BigDecimal.ZERO) < 0 || number.scale() > 2 || length.length()>9;
    }

    /**
     * 处理段位对应周体育流水礼金信息
     *
     * @param oldVipRankPO    历史段位信息，需要根据此id删除原有周体育信息
     * @param vipRankUpdateVO 当前修改的段位信息
     */
    private void processSport(SiteVIPRankPO oldVipRankPO, VIPRankUpdateVO vipRankUpdateVO) {
        //先删除一下周体育流水礼金
        String rankId = oldVipRankPO.getId();
        LambdaQueryWrapper<SiteVipSportPO> del = Wrappers.lambdaQuery();
        del.eq(SiteVipSportPO::getRankId, rankId);
        siteVipSportService.remove(del);

        //如果是包含了周体育流水礼金的，添加关联记录
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(vipRankUpdateVO.getWeekSportFlag()))) {
            List<SiteVipSportReqVo> sportReqVos = vipRankUpdateVO.getSportReqVos();
            if (CollectionUtil.isNotEmpty(sportReqVos)) {
                validateSportReqVos(sportReqVos);
                List<SiteVipSportPO> siteVipSportPOS = BeanUtil.copyToList(sportReqVos, SiteVipSportPO.class);
                //获取一下周体育倍数,如果有多条,每条都赋值一下
                BigDecimal weekSportMultiple = siteVipSportPOS.get(0).getWeekSportMultiple();
                String siteCode = oldVipRankPO.getSiteCode();
                for (SiteVipSportPO siteVipSportPO : siteVipSportPOS) {
                    siteVipSportPO.setSiteCode(siteCode);
                    siteVipSportPO.setRankCode(oldVipRankPO.getVipRankCode());
                    siteVipSportPO.setRankId(Long.parseLong(rankId));
                    siteVipSportPO.setWeekSportMultiple(weekSportMultiple);
                }
                siteVipSportService.saveBatch(siteVipSportPOS);
            }


        }
    }

    /**
     * 校验周体育流水
     *
     * @param sportReqVos 周体育流水数组
     */
    private void validateSportReqVos(List<SiteVipSportReqVo> sportReqVos) {
        if (sportReqVos == null || sportReqVos.size() < 2) {
            return;
        }
        for (int i = 1; i < sportReqVos.size(); i++) {
            SiteVipSportReqVo current = sportReqVos.get(i);
            SiteVipSportReqVo previous = sportReqVos.get(i - 1);
            if (current.getWeekSportBetAmount().compareTo(previous.getWeekSportBetAmount()) <= 0) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
    }

    /**
     * @param siteCode          站点
     * @param vipRankCode       段位code
     * @param currencyConfigVOS 币种段位配置信息
     */
    private void updUserWithdraw(String siteCode, Integer vipRankCode, List<SiteVipRankCurrencyConfigVO> currencyConfigVOS) {
        List<UserWithdrawConfigAddOrUpdateVO> updateVOS = new ArrayList<>();
        for (SiteVipRankCurrencyConfigVO currencyConfigVO : currencyConfigVOS) {
            UserWithdrawConfigAddOrUpdateVO updateVO = new UserWithdrawConfigAddOrUpdateVO();
            updateVO.setSiteCode(siteCode);
            updateVO.setVipRankCode(vipRankCode);
            updateVO.setCurrencyCode(currencyConfigVO.getCurrencyCode());
            //单日免费提款总次数-
            updateVO.setSingleDayWithdrawCount(currencyConfigVO.getDailyWithdrawals());
            //单日免费提款额度
            updateVO.setSingleMaxWithdrawAmount(currencyConfigVO.getDayWithdrawLimit());

            //单日提款上限次数
            updateVO.setDailyWithdrawalNumsLimit(currencyConfigVO.getDailyWithdrawalNumsLimit());
            //单日提款金额上限
            updateVO.setDailyWithdrawAmountLimit(currencyConfigVO.getDailyWithdrawAmountLimit());

            updateVOS.add(updateVO);
        }

        userWithdrawConfigApi.updateBySiteCodeAndRankCode(siteCode, vipRankCode, updateVOS);
    }


    /**
     * 校验入参
     */
    private void checkParam(VIPRankUpdateVO updateVO) {
        //轮盘活动
        Integer luckFlag = updateVO.getLuckFlag();
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(luckFlag))) {
            if (updateVO.getLuck() == null) {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
        //周流水奖励是否参加
        Integer weekAmountFlag = updateVO.getWeekAmountFlag();
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(weekAmountFlag))) {
            if (updateVO.getWeekAmountProp1() == null
                    || updateVO.getWeekAmountProp2() == null
                    || updateVO.getWeekAmountMultiple() == null) {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
        //月流水奖励是否参加
        Integer monthAmountFlag = updateVO.getMonthAmountFlag();
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(monthAmountFlag))) {
            if (updateVO.getMonthAmountProp1() == null
                    || updateVO.getMonthAmountProp2() == null
                    || updateVO.getMonthAmountLimit() == null
                    || updateVO.getMonthAmountMultiple() == null) {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
        //是否参加周体育奖励
        Integer weekSportFlag = updateVO.getWeekSportFlag();
        if (YesOrNoEnum.YES.getCode().equals(String.valueOf(weekSportFlag))) {
            if (CollectionUtil.isEmpty(updateVO.getSportReqVos())) {
                throw new BaowangDefaultException(ConstantsCode.PARAM_ERROR);
            }
        }
    }

    private void refreshVIPRank(List<SiteVIPRankPO> list, String siteCode) {
        // 刷新redis缓存
        if (ObjectUtils.isEmpty(list)) {
            list = this.lambdaQuery()
                    .eq(SiteVIPRankPO::getSiteCode, siteCode).list();
        }
        RList<SiteVIPRankVO> rList = RedisUtil.getList(RedisConstants.KEY_VIP_RANK_CONFIG + siteCode);
        //清空缓存
        rList.clear();
        if (CollectionUtil.isNotEmpty(list)) {
            //关联vo数据
            List<SiteVIPRankVO> siteVIPRankVOS = processCache(siteCode, list);
            if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
                rList.addAll(siteVIPRankVOS);
            }
        }
        //清空一下vip等级缓存
        RSet<SiteVIPGradeVO> vipGradeVOS = RedisUtil.getSet(RedisConstants.KEY_VIP_GRADE_CONFIG + siteCode);
        //清空一下缓存
        vipGradeVOS.clear();
    }

    private List<SiteVIPRankVO> processCache(String siteCode, List<SiteVIPRankPO> poList) {
        //组装一下vip等级信息
        //批量获取一下周体育流水
        List<String> ids = poList.stream()
                .filter(item -> YesOrNoEnum.YES.getCode().equals(String.valueOf(item.getWeekSportFlag()))) // 过滤条件
                .map(SiteVIPRankPO::getId)
                .toList();
        //获取周体育流水信息转map
        Map<Long, List<SiteVipSportVo>> rankIdSportList = new HashMap<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            LambdaQueryWrapper<SiteVipSportPO> sportQuery = Wrappers.lambdaQuery();
            sportQuery.in(SiteVipSportPO::getRankId, ids);
            List<SiteVipSportPO> sportPOS = siteVipSportService.list(sportQuery);
            List<SiteVipSportVo> sportVos = BeanUtil.copyToList(sportPOS, SiteVipSportVo.class);
            //转map用作封装，key是rankId,值是对应的周体育流水数组
            for (SiteVipSportVo sportVo : sportVos) {
                if (rankIdSportList.containsKey(sportVo.getRankId())) {
                    rankIdSportList.get(sportVo.getRankId()).add(sportVo);
                } else {
                    List<SiteVipSportVo> sportVoArr = new ArrayList<>();
                    sportVoArr.add(sportVo);
                    rankIdSportList.put(sportVo.getRankId(), sportVoArr);
                }
            }
        }

        //获取段位币种配置信息
        List<Integer> vipRankCodeArr = poList.stream()
                .map(SiteVIPRankPO::getVipRankCode)
                .toList();
        LambdaQueryWrapper<SiteVipRankCurrencyConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipRankCurrencyConfigPO::getSiteCode, siteCode).in(SiteVipRankCurrencyConfigPO::getVipRankCode, vipRankCodeArr);
        //币种信息转map,一个rankCode,多个币种信息
        Map<Integer, List<SiteVipRankCurrencyConfigVO>> currencyConfigMap = new HashMap<>();
        List<SiteVipRankCurrencyConfigPO> configPOS = siteVipRankCurrencyConfigService.list(query);
        List<SiteVipRankCurrencyConfigVO> configVOS = BeanUtil.copyToList(configPOS, SiteVipRankCurrencyConfigVO.class);
        for (SiteVipRankCurrencyConfigVO configVO : configVOS) {
            if (currencyConfigMap.containsKey(configVO.getVipRankCode())) {
                currencyConfigMap.get(configVO.getVipRankCode()).add(configVO);
            } else {
                List<SiteVipRankCurrencyConfigVO> configVoArr = new ArrayList<>();
                configVoArr.add(configVO);
                currencyConfigMap.put(configVO.getVipRankCode(), configVoArr);
            }
        }

        //封装vo
        return poList.stream().map(obj -> {
            SiteVIPRankVO vo = new SiteVIPRankVO();
            BeanUtils.copyProperties(obj, vo);
            //组装币种信息
            if (currencyConfigMap.containsKey(vo.getVipRankCode())) {
                vo.setCurrencyConfigVOS(currencyConfigMap.get(vo.getVipRankCode()));
            }
            //组装周体育信息
            long rankId = Long.parseLong(vo.getId());
            if (rankIdSportList.containsKey(rankId)) {
                vo.setSportVos(rankIdSportList.get(rankId));
            }
            //组装最小，最大等级，等级ids转数组
            String vipGradeCodes = obj.getVipGradeCodes();
            if (StringUtils.isNotBlank(vipGradeCodes)) {
                String[] codes = vipGradeCodes.split(",");
                List<String> gradeCodeArr = Arrays.asList(codes);

                int minCode = Integer.parseInt(gradeCodeArr.get(0));
                //组装vip等级信息
                LambdaQueryWrapper<SiteVIPGradePO> gradeQuery = Wrappers.lambdaQuery();
                gradeQuery.eq(SiteVIPGradePO::getSiteCode, siteCode).in(SiteVIPGradePO::getVipGradeCode, gradeCodeArr);
                List<SiteVIPGradePO> list = siteVIPGradeService.list(gradeQuery);
                if (CollectionUtil.isNotEmpty(list)) {
                    vo.setVipGradeVoList(BeanUtil.copyToList(list, SiteVIPGradeVO.class));
                }
                String minGradeName = vipGradeRepository.getVipGradeNameBySiteCode(siteCode, minCode);
                vo.setMinVipGrade(minCode);
                vo.setMinVipGradeName(minGradeName);
                int maxCode = Integer.parseInt(codes[codes.length - 1]);
                String maxGradeName = vipGradeRepository.getVipGradeNameBySiteCode(siteCode, maxCode);
                vo.setMaxVipGrade(maxCode);
                vo.setMaxVipGradeName(maxGradeName);
                //codes转integer数组
                // 使用流将字符串数组转换为整数列表
                List<Integer> codesArr = Arrays.stream(codes)
                        .map(Integer::parseInt)
                        .collect(Collectors.toCollection(ArrayList::new));
                vo.setVipGradeList(codesArr);
            }
            return vo;
        }).toList();
    }


    private void recordOperation(SiteVIPRankPO newSiteVIPRankPO, SiteVIPRankPO oldVipRankPO,
                                 String operator, Integer vipRank, String siteCode) {
        List<Comparison> list = Lists.newArrayList();
        List<Comparison> compareList = ValidateUtil.compareObj(oldVipRankPO, newSiteVIPRankPO);
        if (ObjectUtils.isNotEmpty(compareList)) {
            list.addAll(compareList);
        }
        List<SiteVIPOperationPO> vipOperationPOS = list.stream().map(obj -> {
            SiteVIPOperationPO po = new SiteVIPOperationPO();
            po.setOperationType(ChangeOperationEnum.VIP_RANK.getCode());
            ImmutableList<CodeValueVO> paramVOS = ChangeOperationEnum.VIP_RANK.getList();
            String changeField = paramVOS.stream().filter(vo -> obj.getField()
                    .equals(vo.getCode())).findFirst().orElse(new CodeValueVO()).getCode();
            if (ObjectUtil.isEmpty(changeField)) {
                return null;
            }
            po.setOperationItem(changeField);
            po.setSiteCode(siteCode);
            po.setAdjustLevel(String.valueOf(vipRank));
            po.setOperationBefore(null == obj.getBefore() ? null : obj.getBefore().toString());
            po.setOperationAfter(null == obj.getAfter() ? null : obj.getAfter().toString());
            po.setOperationTime(System.currentTimeMillis());
            // 操作人
            po.setOperator(operator);
            return po;
        }).filter(ObjectUtil::isNotEmpty).toList();
        vipOperationService.saveBatch(vipOperationPOS);
    }

    public List<SiteVIPRankVO> getVipRankListBySiteCodeAndCodes(String siteCode, List<Integer> vipRankCodes) {
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode).in(SiteVIPRankPO::getVipRankCode, vipRankCodes);
        List<SiteVIPRankPO> siteVIPRankPOS = siteVIPRankRepository.selectList(query);
        return BeanUtil.copyToList(siteVIPRankPOS, SiteVIPRankVO.class);
    }

    public Boolean initSystemVipRank() {
        Map<Integer, List<I18nMsgFrontVO>> map = new HashMap<>();

        // 0: VIP0 段位
        List<I18nMsgFrontVO> vip0Messages = new ArrayList<>();
        vip0Messages.add(new I18nMsgFrontVO("zh-CN", "VIP0"));
        vip0Messages.add(new I18nMsgFrontVO("en-US", "VIP0"));
        vip0Messages.add(new I18nMsgFrontVO("pt-BR", "VIP0"));
        vip0Messages.add(new I18nMsgFrontVO("vi-VN", "VIP0"));
        map.put(0, vip0Messages);

        // 1: 青铜段位
        List<I18nMsgFrontVO> bronzeMessages = new ArrayList<>();
        bronzeMessages.add(new I18nMsgFrontVO("zh-CN", "青铜"));
        bronzeMessages.add(new I18nMsgFrontVO("en-US", "Bronze"));
        bronzeMessages.add(new I18nMsgFrontVO("pt-BR", "Bronze"));
        bronzeMessages.add(new I18nMsgFrontVO("vi-VN", "Đồng thau"));
        map.put(1, bronzeMessages);

        // 2: 白银段位
        List<I18nMsgFrontVO> silverMessages = new ArrayList<>();
        silverMessages.add(new I18nMsgFrontVO("zh-CN", "白银"));
        silverMessages.add(new I18nMsgFrontVO("en-US", "Silver"));
        silverMessages.add(new I18nMsgFrontVO("pt-BR", "Prata"));
        silverMessages.add(new I18nMsgFrontVO("vi-VN", "Bạc"));
        map.put(2, silverMessages);

        // 3: 黄金段位
        List<I18nMsgFrontVO> goldMessages = new ArrayList<>();
        goldMessages.add(new I18nMsgFrontVO("zh-CN", "黄金"));
        goldMessages.add(new I18nMsgFrontVO("en-US", "Gold"));
        goldMessages.add(new I18nMsgFrontVO("pt-BR", "Ouro"));
        goldMessages.add(new I18nMsgFrontVO("vi-VN", "Vàng"));
        map.put(3, goldMessages);

        // 4: 铂金1段位
        List<I18nMsgFrontVO> platinum1Messages = new ArrayList<>();
        platinum1Messages.add(new I18nMsgFrontVO("zh-CN", "铂金1"));
        platinum1Messages.add(new I18nMsgFrontVO("en-US", "Platinum 1"));
        platinum1Messages.add(new I18nMsgFrontVO("pt-BR", "Platina 1"));
        platinum1Messages.add(new I18nMsgFrontVO("vi-VN", "Bạch kim 1"));
        map.put(4, platinum1Messages);

        // 5: 铂金2段位
        List<I18nMsgFrontVO> platinum2Messages = new ArrayList<>();
        platinum2Messages.add(new I18nMsgFrontVO("zh-CN", "铂金2"));
        platinum2Messages.add(new I18nMsgFrontVO("en-US", "Platinum 2"));
        platinum2Messages.add(new I18nMsgFrontVO("pt-BR", "Platina 2"));
        platinum2Messages.add(new I18nMsgFrontVO("vi-VN", "Bạch kim 2"));
        map.put(5, platinum2Messages);

        // 6: 钻石1段位
        List<I18nMsgFrontVO> diamond1Messages = new ArrayList<>();
        diamond1Messages.add(new I18nMsgFrontVO("zh-CN", "钻石1"));
        diamond1Messages.add(new I18nMsgFrontVO("en-US", "Diamond 1"));
        diamond1Messages.add(new I18nMsgFrontVO("pt-BR", "Diamante 1"));
        diamond1Messages.add(new I18nMsgFrontVO("vi-VN", "Kim cương 1"));
        map.put(6, diamond1Messages);

        // 7: 钻石2段位
        List<I18nMsgFrontVO> diamond2Messages = new ArrayList<>();
        diamond2Messages.add(new I18nMsgFrontVO("zh-CN", "钻石2"));
        diamond2Messages.add(new I18nMsgFrontVO("en-US", "Diamond 2"));
        diamond2Messages.add(new I18nMsgFrontVO("pt-BR", "Diamante 2"));
        diamond2Messages.add(new I18nMsgFrontVO("vi-VN", "Kim cương 2"));
        map.put(7, diamond2Messages);

        // 8: 钻石3段位
        List<I18nMsgFrontVO> diamond3Messages = new ArrayList<>();
        diamond3Messages.add(new I18nMsgFrontVO("zh-CN", "钻石3"));
        diamond3Messages.add(new I18nMsgFrontVO("en-US", "Diamond 3"));
        diamond3Messages.add(new I18nMsgFrontVO("pt-BR", "Diamante 3"));
        diamond3Messages.add(new I18nMsgFrontVO("vi-VN", "Kim cương 3"));
        map.put(8, diamond3Messages);

        List<VipRankPO> list = systemVipRankService.list();
        for (VipRankPO rankPO : list) {
            String vipRankNameI18Code = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.SYSTEM_VIP_RANK_NAME.getCode());
            List<I18nMsgFrontVO> i18nMsgFrontVOS = map.get(rankPO.getVipRankCode());
            // 插入国际化信息(更新方法是删除后新增，这里每次直接调用update吧)
            Map<String, List<I18nMsgFrontVO>> i18nData = Map.of(
                    vipRankNameI18Code, i18nMsgFrontVOS);
            i18nApi.update(i18nData);
            rankPO.setVipRankNameI18nCode(vipRankNameI18Code);
        }
        systemVipRankService.updateBatchById(list);
        return true;
    }

    public VIPRankVO getVipRankByCode(Integer vipRankCode) {
        LambdaQueryWrapper<VipRankPO> query = Wrappers.lambdaQuery();
        query.eq(VipRankPO::getVipRankCode, vipRankCode);
        VipRankPO one = systemVipRankService.getOne(query);
        return BeanUtil.copyProperties(one, VIPRankVO.class);
    }

    public List<VIPRankVO> getVipRankList() {
        List<VipRankPO> list = systemVipRankService.list();
        return BeanUtil.copyToList(list, VIPRankVO.class);
    }

    public SiteVipFeeRateVO getVipRankSiteCodeAndCurrency(String siteCode, Integer vipRankCode, String currencyCode, String withdrawWayId) {
        LambdaQueryWrapper<SiteVipRankCurrencyConfigPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVipRankCurrencyConfigPO::getSiteCode, siteCode)
                .eq(SiteVipRankCurrencyConfigPO::getVipRankCode, vipRankCode)
                .eq(SiteVipRankCurrencyConfigPO::getCurrencyCode, currencyCode)
                .eq(SiteVipRankCurrencyConfigPO::getWithdrawWayId, withdrawWayId);
        SiteVipRankCurrencyConfigPO siteVipRankCurrencyConfigPO = siteVipRankCurrencyConfigService.getOne(query);
        SiteVipFeeRateVO siteVipFeeRateVO = ConvertUtil.entityToModel(siteVipRankCurrencyConfigPO, SiteVipFeeRateVO.class);


        LambdaQueryWrapper<SiteVIPRankPO> vipRankLqw = Wrappers.lambdaQuery();
        vipRankLqw.eq(SiteVIPRankPO::getSiteCode, siteCode);
        vipRankLqw.eq(SiteVIPRankPO::getVipRankCode, vipRankCode);
        SiteVIPRankPO siteVIPRankPO = siteVIPRankRepository.selectOne(vipRankLqw);
        if (ObjectUtil.isNotEmpty(siteVIPRankPO)) {
            siteVipFeeRateVO.setEncryCoinFee(siteVIPRankPO.getEncryCoinFee());
        }
        return siteVipFeeRateVO;
    }

    public Map<String, List<SiteVIPRankVO>> getVipRankListBySiteCodes(List<String> siteCodeList) {
        Map<String, List<SiteVIPRankVO>> resultMap = Maps.newHashMap();
        for (String siteCode : siteCodeList) {
            List<SiteVIPRankVO> rList = RedisUtil.getList(RedisConstants.KEY_VIP_RANK_CONFIG + siteCode);
            //缓存中没有获取到，从数据库中组装一次，同步到缓存
            if (ObjectUtils.isEmpty(rList)) {
                //查询当前站点所有vip段位数据
                List<SiteVIPRankPO> list = this.lambdaQuery().eq(SiteVIPRankPO::getSiteCode, siteCode).list();
                if (CollectionUtil.isNotEmpty(list)) {
                    //关联vo数据
                    List<SiteVIPRankVO> siteVIPRankVOS = processCache(siteCode, list);
                    if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
                        rList.addAll(siteVIPRankVOS);
                    }
                }
            }
            resultMap.put(siteCode, rList);
        }
        return resultMap;
    }

    public SiteVIPRankVO getFirstVipRankBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode).orderByAsc(SiteVIPRankPO::getVipRankCode).last("limit 0,1");
        SiteVIPRankPO one = this.getOne(query);
        return BeanUtil.copyProperties(one, SiteVIPRankVO.class);
    }

    public Map<String, List<CodeValueNoI18VO>> getVIPRankGradeList(String siteCode) {
        Map<String, List<CodeValueNoI18VO>> map = Maps.newHashMap();
        if(SiteHandicapModeEnum.China.getCode().equals(CurrReqUtils.getHandicapMode())){
            List<VIPGradeVO> vipGradeList= siteVipOptionService.getInitVIPGrade();
            List<CodeValueNoI18VO> vipGrade = Lists.newArrayList();
            vipGradeList.forEach(obj -> vipGrade.add(CodeValueNoI18VO.builder().code(obj.getVipGradeCode().toString()).value(obj.getVipGradeName()).build()));
            map.put(CommonConstant.VIP_GRADE, vipGrade);
        }else{
            List<SiteVIPRankVO> rankVOList = getVipRankListBySiteCode(siteCode);
            List<CodeValueNoI18VO> vipGrade = Lists.newArrayList();
            for (SiteVIPRankVO rankVO : rankVOList) {
                List<SiteVIPGradeVO> gradeVOList = siteVIPGradeService.getSiteVipGradeList(siteCode,
                        rankVO.getVipRankCode().toString());
                for (SiteVIPGradeVO gradeVO : gradeVOList) {
                    vipGrade.add(CodeValueNoI18VO.builder().type(rankVO.getVipRankCode().toString())
                            .code(gradeVO.getVipGradeCode().toString())
                            .value(gradeVO.getVipGradeName()).build());
                }
            }
            List<CodeValueNoI18VO> vipRank = Lists.newArrayList();
            rankVOList.forEach(obj -> vipRank.add(CodeValueNoI18VO.builder().type(CommonConstant.VIP_RANK)
                    .code(obj.getVipRankCode().toString()).value(I18nMessageUtil.getI18NMessageInAdvice(obj.getVipRankNameI18nCode())).build()));
            map.put(CommonConstant.VIP_RANK, vipRank);
            map.put(CommonConstant.VIP_GRADE, vipGrade);
        }
        return map;
    }

    public List<SiteVIPRankPO> getBetweenAndVipRank(Integer beforeVipRank, Integer vipRankCode, String siteCode) {
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode).ge(SiteVIPRankPO::getVipRankCode, beforeVipRank)
                .le(SiteVIPRankPO::getVipRankCode, vipRankCode);
        return this.list(query);
    }

    public Map<String, List<SiteVIPRankVO>> getAllSiteVipRank() {
        List<SiteVIPRankPO> list = this.list();
        List<SiteVIPRankVO> siteVIPRankVOS = BeanUtil.copyToList(list, SiteVIPRankVO.class);
        Map<String, List<SiteVIPRankVO>> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(siteVIPRankVOS)) {
            map = siteVIPRankVOS.stream()
                    .collect(Collectors.groupingBy(SiteVIPRankVO::getSiteCode));
        }
        return map;
    }

    public SiteVIPRankPO getVipRankBySiteCodeAndCode(String siteCode, Integer vipRank) {
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode).eq(SiteVIPRankPO::getVipRankCode, vipRank);
        SiteVIPRankPO po = this.getOne(query);
        return po;
    }

    public List<SiteVIPRankRabateVO> getVipRankBySiteCode(String siteCode){
        LambdaQueryWrapper<SiteVIPRankPO> query = Wrappers.lambdaQuery();
        query.eq(SiteVIPRankPO::getSiteCode, siteCode);
        List<SiteVIPRankPO> list=this.getBaseMapper().selectList(query);
        return BeanUtil.copyToList(list, SiteVIPRankRabateVO.class);
    }



    public Boolean initUserWithdrawConfig(String siteCode,List<String> currency,Integer handicapMode) {
        // vip段位初始化数据
        LambdaQueryWrapper<SiteVIPRankPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteVIPRankPO::getSiteCode, siteCode);
        List<SiteVIPRankPO> siteVIPRankPOS =   this.getBaseMapper().selectList(queryWrapper);
        List<VIPGradeVO> list= siteVipOptionService.getInitVIPGrade();
        //初始化会员提款设置数据
        initWithdrawConfig(currency, siteVIPRankPOS,list, siteCode,handicapMode);
        return true;
    }


}
