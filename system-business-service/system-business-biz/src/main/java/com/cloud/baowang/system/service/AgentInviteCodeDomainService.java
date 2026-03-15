package com.cloud.baowang.system.service;

import com.cloud.baowang.common.core.enums.DomainInfoTypeEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.DomainBindStatusEnum;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainQueryVO;
import com.cloud.baowang.system.api.vo.AgentInviteCodeDomainVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.system.repositories.AgentInviteCodeDomainRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class AgentInviteCodeDomainService {
    private final AgentInviteCodeDomainRepository codeDomainRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final SiteService siteService;
    private static final String DOMAIN_VISIT_REDIS_KEY = "domain_visit";//所有访问量

    public ResponseVO<AgentInviteCodeDomainVO> getDomainAndInCode(AgentInviteCodeDomainQueryVO queryVO) {
        String shortUrl = queryVO.getShortUrl();
        log.info("根据短链接获取站点网页端地址,当前短链接:{}", shortUrl);
        AgentInviteCodeDomainVO domainVO = codeDomainRepository.getDomainAndInCode(shortUrl, DomainInfoTypeEnum.WEB_PORTAL.getType(), DomainBindStatusEnum.BIND.getCode());
        if (domainVO != null) {
            String siteCode = domainVO.getSiteCode();
            ResponseVO<SiteVO> siteResp = siteService.getSiteInfo(siteCode);
            SiteVO siteVO = siteResp.getData();
            String timezone = siteVO.getTimezone();
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            //站点对应某天的时间字符串
            String key = shortUrl + ":" + zonedDateTime.toLocalDate().toString();
            redisTemplate.opsForHash().increment(DOMAIN_VISIT_REDIS_KEY, key, 1L);
        }
        //没有查询到,不是以www.开头的,拼接一级域名查询再查询一次
        if (domainVO == null && !shortUrl.startsWith("www.")) {
            domainVO = codeDomainRepository.getDomainAndInCode("www." + shortUrl, DomainInfoTypeEnum.WEB_PORTAL.getType(), DomainBindStatusEnum.BIND.getCode());
            if (domainVO != null) {
                String siteCode = domainVO.getSiteCode();
                ResponseVO<SiteVO> siteResp = siteService.getSiteInfo(siteCode);
                SiteVO siteVO = siteResp.getData();
                String timezone = siteVO.getTimezone();
                ZoneId zoneId = ZoneId.of(timezone);
                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
                //站点对应某天的时间字符串
                String key = shortUrl + ":" + zonedDateTime.toLocalDate().toString();
                //埋点统计还是使用不带www.的域名进行埋点
                redisTemplate.opsForHash().increment(DOMAIN_VISIT_REDIS_KEY, key, 1L);
            }
        } else if (domainVO == null && shortUrl.startsWith("www.")) {
            //没有查询到,以www.开头的,移除一级域名查询再查询一次
            domainVO = codeDomainRepository.getDomainAndInCode(shortUrl.replace("www.", ""), DomainInfoTypeEnum.WEB_PORTAL.getType(), DomainBindStatusEnum.BIND.getCode());
            if (domainVO != null) {
                String siteCode = domainVO.getSiteCode();
                ResponseVO<SiteVO> siteResp = siteService.getSiteInfo(siteCode);
                SiteVO siteVO = siteResp.getData();
                String timezone = siteVO.getTimezone();
                ZoneId zoneId = ZoneId.of(timezone);
                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
                //站点对应某天的时间字符串
                String key = shortUrl + ":" + zonedDateTime.toLocalDate().toString();
                //埋点统计还是使用不带www.的域名进行埋点
                redisTemplate.opsForHash().increment(DOMAIN_VISIT_REDIS_KEY, key, 1L);
            }
        }
        return ResponseVO.success(domainVO);
    }
}
