package com.cloud.baowang.user.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.enums.ApiConstants;
import com.cloud.baowang.user.api.vo.userlabel.*;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "remoteSiteUserLabelConfigApi", value = ApiConstants.NAME)
@Tag(name = "RPC会员标签服务")
public interface SiteUserLabelConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/siteUserLabelConfig/api/";

    @Operation(summary = "查询所有会员标签")
    @PostMapping(value = PREFIX + "getAllUserLabel")
    List<GetAllUserLabelVO> getAllUserLabel(@RequestParam("siteCode")String siteCode);

    @Operation(summary = "按照主键查询会员标签")
    @PostMapping(value = PREFIX + "getInfoById")
    public GetUserLabelByIdsVO getInfoById(@RequestParam("id")Long id);

    @Operation(summary = "根据id查询标签")
    @PostMapping(value = PREFIX + "getByLabelId")
    GetAllUserLabelVO getByLabelId(@RequestBody IdVO idVO);

    @Operation(summary = "根据ids查询标签集合")
    @PostMapping(value = PREFIX + "getUserLabelByIds")
    List<GetUserLabelByIdsVO> getUserLabelByIds(@RequestBody List<String> ids);

    @Operation(summary = "获取单个会员下全部标签")
    @PostMapping(value = PREFIX + "getUserLabel")
    List<GetUserLabelByIdsVO> getUserLabel(@RequestBody UserLabelIdReqVO userLabelIdReqVO);

    @Operation(summary = "新增标签")
    @PostMapping(value = PREFIX + "addLabel")
    ResponseVO addLabel(@RequestBody UserLabelAddRequestVO vo);

    @Operation(summary = "会员标签配置分页查询")
    @PostMapping(value = PREFIX + "getLabelConfigPage")
    Page<UserLabelConfigPageResponseVO> getLabelConfigPage(@RequestBody UserLabelConfigPageRequestVO vo);

    @Operation(summary = "编辑标签")
    @PostMapping(value = PREFIX + "editLabel")
    ResponseVO editLabel(@RequestBody UserLabelEditRequestVO vo);

    @Operation(summary = "删除标签")
    @PostMapping(value = PREFIX + "delLabel")
    ResponseVO delLabel(@RequestBody UserLabelDelRequestVO vo);

    @Operation(summary = "标签对应的会员 分页查询")
    @PostMapping(value = PREFIX + "getUserPageByLabelId")
    Page<GetUserPageByLabelIdVO> getUserPageByLabelId(@RequestBody GetUserPageByLabelIdRequestVO vo);

    @Operation(summary = "会员标签配置 总量")
    @PostMapping(value = PREFIX + "getTotalCount")
    ResponseVO<Long> getTotalCount(@RequestBody UserLabelConfigPageRequestVO reqVO);

    @Operation(summary = "是否存在某个会员标签")
    @PostMapping(value = PREFIX + "existsLabelId")
    Boolean existsLabelId(@RequestParam("ids")String ids, @RequestParam("labelId")String labelId);
}
