package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.AgentDateEnum;
import com.cloud.baowang.agent.api.enums.ShortORLongUrlEnums;
import com.cloud.baowang.agent.api.vo.PromotionDomainRespVO;
import com.cloud.baowang.agent.api.vo.domian.AddVisCountVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainPageQueryVO;
import com.cloud.baowang.agent.api.vo.domian.AgentDomainShortVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.repositories.AgentDomainRepository;
import com.cloud.baowang.agent.repositories.AgentInfoRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.operations.DomainInfoApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.operations.DomainRequestVO;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 推广链接
 */
@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PromotionDomainService {

    private final AgentDomainRepository agentDomainRepository;
    private final AgentInfoRepository agentInfoRepository;
    private final SiteApi siteApi;
    private final RedisTemplate<String, String> redisTemplate;
    private final DomainInfoApi domainInfoApi;
    private static final String initCode = "?inviteCode=";
    private static final String DOMAIN_VISIT_REDIS_KEY = "domain_visit";//所有访问量
    private static final String DOMAIN_VISIT_TYPE_REDIS_KEY = "domain_type_visit:";//按设备类型访问量


    public Page<PromotionDomainRespVO> getPromotionDomainList(AgentDomainPageQueryVO queryVO) {
        LambdaQueryWrapper<AgentInfoPO> query = Wrappers.lambdaQuery();
        query.eq(AgentInfoPO::getSiteCode, queryVO.getSiteCode()).eq(AgentInfoPO::getAgentAccount, queryVO.getAgentAccount());
        AgentInfoPO agentInfoPO = agentInfoRepository.selectOne(query);
        if (agentInfoPO == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        Page<PromotionDomainRespVO> page = new Page<>(queryVO.getPageNumber(), queryVO.getPageSize());
        page = agentDomainRepository.getPromotionDomainAndSiteDomainPage(page, queryVO);
        Integer dateNum = queryVO.getDateNum();
        if (dateNum == null) {
            queryVO.setDateNum(9999);
        }

        if (queryVO.getDateNum().equals(AgentDateEnum.THIRTY_DAY.getCode())
                || queryVO.getDateNum().equals(AgentDateEnum.SEVEN_DAY.getCode())
                || queryVO.getDateNum().equals(AgentDateEnum.TODAY.getCode())) {
            String timezone = queryVO.getTimezone();  // 获取时区
            ZoneId zoneId = ZoneId.of(timezone);  // 根据时区获取 ZoneId
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            ZonedDateTime startDateTime = now.plusDays(queryVO.getDateNum()).toLocalDate().atStartOfDay(zoneId);
            ZonedDateTime endDateTime = now.toLocalDate().atTime(23, 59, 59, 999999999).atZone(zoneId);

            Long startTime = startDateTime.toInstant().toEpochMilli();
            Long endTime = endDateTime.toInstant().toEpochMilli();

            queryVO.setStartTime(startTime);
            queryVO.setEndTime(endTime);
        } else {
            if (queryVO.getStartTime() == null || queryVO.getEndTime() == null) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }

        List<PromotionDomainRespVO> records = page.getRecords();
        String inviteCode = agentInfoPO.getInviteCode();
        if (CollectionUtil.isNotEmpty(records)) {
            for (PromotionDomainRespVO record : records) {
                if (ShortORLongUrlEnums.SHORT_URL.getType().equals(record.getDomainType())) {
                    record.setDomainName(record.getDomainName().replaceAll("www.",""));
                }
                if (ShortORLongUrlEnums.LONG_URL.getType().equals(record.getShortType())) {
                    record.setDomainName(record.getDomainName() + initCode + inviteCode);
                }
                Long visitCounts = this.getVisitCounts(queryVO, record.getDomainName(), record.getDomainType());
                record.setLongUrlVisitCount(visitCounts);
            }
        }
        return page;
    }

    /**
     * 短链接访问量统计
     *
     * @param agentDomainShortVO
     * @return
     */
    public ResponseVO<AgentDomainShortVO> getPromotionDomain(final AgentDomainShortVO agentDomainShortVO) {
        try {
            // todo test 添加打印日志
            AgentDomainShortVO result = new AgentDomainShortVO();
            agentDomainShortVO.setSiteCode(CurrReqUtils.getSiteCode());
            AgentDomainShortVO vo = agentDomainRepository.getPromotionDomain(agentDomainShortVO);
            // 因为一个代理可以配置多个
            /*AgentInfoPO po = agentInfoRepository.selectOne(new LambdaQueryWrapper<AgentInfoPO>()
                    .eq(AgentInfoPO::getShortUrl, agentDomainShortVO.getDomainName()));*/
            AgentInfoPO po = agentInfoRepository.selectOne(new LambdaQueryWrapper<AgentInfoPO>()
                    .apply("FIND_IN_SET('" + agentDomainShortVO.getDomainName() + "', short_url) > 0"));
            if (null != vo) {
                result.setDomainName(vo.getDomainName());
                result.setDomainType(vo.getDomainType());
            }
            if (null != po) {
                result.setInviteCode(po.getInviteCode());
                //统计短链接访问量
                String hashKey = LocalDateTimeUtil.formatNormal(LocalDate.now()) + ":" + agentDomainShortVO.getDomainName();
                redisTemplate.opsForHash().increment(DOMAIN_VISIT_REDIS_KEY, hashKey, 1L);
                if (null != vo) {
                    redisTemplate.opsForHash().increment(DOMAIN_VISIT_TYPE_REDIS_KEY + ":" + vo.getDomainType(), hashKey, 1L);
                }
            }
            return ResponseVO.success(result);
        } catch (Exception e) {
            log.error("查询该域名:{} 长链接发生异常", agentDomainShortVO.getDomainName(), e);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
    }


    /**
     * 查询链接访问量
     *
     * @return
     */
    public Long getVisitCounts(AgentDomainPageQueryVO vo, String domain, Integer domainType) {

        LocalDate startDate = Instant.ofEpochMilli(vo.getStartTime()).atZone(ZoneId.of(vo.getTimezone())).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(vo.getEndTime()).atZone(ZoneId.of(vo.getTimezone())).toLocalDate();
        endDate = endDate.plusDays(1);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<String> dateList = startDate.datesUntil(endDate)
                .map(format::format)
                .toList();
        domain = StringUtils.isNotEmpty(domain) && domain.indexOf("/?") != -1 ? domain.replace("/?","?"):domain;
        //批量查询并统计访问量
        String finalDomain = domain;
        List<Object> values = redisTemplate.opsForHash().multiGet(DOMAIN_VISIT_REDIS_KEY, dateList.stream()
                .map(dateKey -> finalDomain + ":" + dateKey)
                .collect(Collectors.toList()));
        return values.stream()
                .filter(Objects::nonNull)
                .map(value -> Long.parseLong(value.toString()))
                .mapToLong(Long::longValue)
                .sum();
    }

    /**
     * 长链接访问量++
     *
     * @param countVO
     * @return
     */
    public ResponseVO<Boolean> addVisCount(AddVisCountVO countVO) {
        DomainRequestVO domainRequestVO = DomainRequestVO.builder().domainAddr(countVO.getDomainName()).build();
        DomainVO domainVO = domainInfoApi.getDomainbyAddressAndSitecode(domainRequestVO);
        if (domainVO != null) {
            String siteCode = domainVO.getSiteCode();
            ResponseVO<SiteVO> siteResp = siteApi.getSiteInfo(siteCode);
            if (siteResp.isOk()) {
                SiteVO data = siteResp.getData();
                String timezone = data.getTimezone();
                ZoneId zoneId = ZoneId.of(timezone);
                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
                //站点对应某天的时间字符串
                String key = countVO.getDomainName() + initCode + countVO.getInviteCode() + ":" + zonedDateTime.toLocalDate().toString();
                redisTemplate.opsForHash().increment(DOMAIN_VISIT_REDIS_KEY, key, 1L);
            }
        }

        return ResponseVO.success();
    }
}
