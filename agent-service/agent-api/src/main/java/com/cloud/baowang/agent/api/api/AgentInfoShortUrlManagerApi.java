package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerAddVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerPageQueryVO;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentShortUrlManagerRespVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "agentInfoShortUrlManagerApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理短链接管理 服务")
public interface AgentInfoShortUrlManagerApi {
    String PREFIX = ApiConstants.PREFIX + "/agentInfoShortUrlManager/api/";

    @PostMapping(PREFIX + "pageQuery")
    ResponseVO<Page<AgentShortUrlManagerRespVO>> pageQuery(@RequestBody AgentShortUrlManagerPageQueryVO queryVO);

    @PostMapping(PREFIX + "pageCount")
    Long pageCount(@RequestBody AgentShortUrlManagerPageQueryVO queryVO);

    @PostMapping(PREFIX + "addShortUrl")
    ResponseVO<Boolean> addShortUrl(@RequestBody AgentShortUrlManagerAddVO agentShortUrlManagerAddVO);

    @GetMapping(PREFIX + "deleteShortUrl")
    ResponseVO<Boolean> deleteShortUrl(@RequestParam("id") String id);
}
