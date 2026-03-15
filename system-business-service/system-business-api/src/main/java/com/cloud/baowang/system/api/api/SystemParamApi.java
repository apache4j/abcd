package com.cloud.baowang.system.api.api;

import com.cloud.baowang.common.core.vo.base.CodeValueResVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteSystemParamApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - systemParam")
public interface SystemParamApi {

    String SYSTEM_PARAM_PREFIX = ApiConstants.PREFIX + "/system-param/api/";

    /**
     * 根据type获取系统参数
     * @param type type类型
     * @return 系统参数结果列表
     */
    @GetMapping(value = SYSTEM_PARAM_PREFIX + "{type}")
    @Operation(summary = "根据类型获取系统参数响应结果")
    ResponseVO<List<CodeValueVO>> getSystemParamByType(@PathVariable("type") String type) ;

    /**
     * 根据type获取系统参数
     * @param list type类型列表
     * @return Map<String,List<CodeValueVO>> key ==> type, value ==> 系统参数结果列表
     */
    @PostMapping(value = SYSTEM_PARAM_PREFIX + "getSystemParamsByList")
    @Operation(summary = "根据类型集合获取系统参数响应结果")
    ResponseVO<Map<String, List<CodeValueVO>>> getSystemParamsByList(@RequestBody List<String> list);

    /**
     * 根据type获取系统参数
     * @param list type类型列表
     * @return Map<String,List<CodeValueVO>> key ==> type, value ==> 系统参数结果列表
     */
    @PostMapping(value = SYSTEM_PARAM_PREFIX + "getSystemParamsByListVO")
    @Operation(summary = "根据类型集合获取系统参数响应结果")
    ResponseVO<List<CodeValueResVO>> getSystemParamsByListVO(@RequestBody List<String> list);

    /**
     * 根据类型获取系统参数响应结果
     * @param type type类型
     * @return Map<String,String>
     *     key ==> 结果code, value ==> 结果value 例：param：{"code":"1",value:"v1","type":"t1"} => map值:{1:v1}
     */
    @GetMapping(SYSTEM_PARAM_PREFIX+"getSystemParamMap")
    @Schema(description = "根据类型获取系统参数响应结果")
    ResponseVO<Map<String, String>> getSystemParamMap(@RequestParam("type") String type);

    /**
     * 根据类型列表获取系统参数响应结果
     * @param list type类型列表
     * @return Map<String, Map<String, String>> key  ==> type, vkey ==> 结果code, vValue ==> 结果value
     * 示例：param：[{"code":"1",value:"v1","type":"t1"},{"code":"2",value:"v2","type":"t2"},{"code":"3",value:"v3","type":"t2"}]
     * => map值:{t1:{1:v1},t2:{{2,v2},{3,v3}}}
     */
    @PostMapping(value = SYSTEM_PARAM_PREFIX + "getSystemParamsMapByList")
    @Operation(summary = "根据类型集合获取系统参数响应结果")
    ResponseVO<Map<String, Map<String, String>>> getSystemParamsMapByList(@RequestBody List<String> list);


    /************************带缓存接口 ****************************/
    @GetMapping(value = SYSTEM_PARAM_PREFIX + "/cache/{type}")
    @Operation(summary = "根据类型获取系统参数响应结果-带缓存")
    ResponseVO<List<CodeValueVO>> getSystemParamByTypeCaChe(@PathVariable("type") String type);

    @GetMapping(SYSTEM_PARAM_PREFIX+"getSystemParamMapInner")
    @Schema(description = "根据类型获取系统参数响应结果(服务内部调用)")
    Map<String, String> getSystemParamMapInner(@RequestParam("type") String type);



}