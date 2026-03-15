package com.cloud.baowang.system.api.api.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigPageQueryVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigReqVO;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "systemDictConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 系统字典配置")
public interface SystemDictConfigApi {
    String PREFIX = ApiConstants.PREFIX + "/systemDictConfig/api/";

    @PostMapping(PREFIX + "pageQuery")
    @Operation(summary = "分页查询字典配置列表")
    ResponseVO<Page<SystemDictConfigRespVO>> pageQuery(@RequestBody SystemDictConfigPageQueryVO queryVO);

    @PostMapping(PREFIX + "upd")
    @Operation(summary = "编辑字典")
    ResponseVO<Boolean> upd(@RequestBody SystemDictConfigReqVO reqVO);

    /**
     * {@link com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums}
     *
     * @param dictCode code来源自枚举
     * @return vo
     */
    @GetMapping(PREFIX + "getByCode")
    @Operation(summary = "根据code获取字典配置信息")
    ResponseVO<SystemDictConfigRespVO> getByCode(@RequestParam("dictCode") Integer dictCode,
                                                 @RequestParam("siteCode") String siteCode);

    @GetMapping(PREFIX + "getByCodes")
    @Operation(summary = "根据codes获取字典配置信息")
    ResponseVO<List<SystemDictConfigRespVO>> getByCodes(@RequestParam("dictCodes") List<Integer> dictCodes,
                                                 @RequestParam("siteCode") String siteCode);

    /**
     * {@link com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums}
     *
     * @param dictCode code来源自枚举
     * @return List<Vo>
     */
    @GetMapping(PREFIX + "getListByCode")
    @Operation(summary = "根据code批量获取配置信息,不区分站点")
    ResponseVO<List<SystemDictConfigRespVO>> getListByCode(@RequestParam("dictCode") Integer dictCode);

    @GetMapping(PREFIX + "initSiteDictConfig")
    @Operation(summary = "初始化站点字典配置信息")
    void initSiteDictConfig(@RequestParam("siteCode") String siteCode,
                            @RequestParam("operator") String operator);

    @GetMapping(PREFIX + "queryWithdrawSwitch")
    @Operation(summary = "查询当前站点推送提款开关")
    ResponseVO<SystemDictConfigRespVO> queryWithdrawSwitch(@RequestParam("siteCode") String siteCode,
                                                           @RequestParam("dictCode") Integer code);
}
