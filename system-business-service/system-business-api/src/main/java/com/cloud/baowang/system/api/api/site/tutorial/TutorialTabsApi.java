package com.cloud.baowang.system.api.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsRspVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteTutorialTabsApi", value = ApiConstants.NAME)
@Tag(name = "教程页签配置 ")
public interface TutorialTabsApi {
    String TUTORIAL_TABS_PREFIX = ApiConstants.PREFIX + "/tutorial-tabs/api/";

    @PostMapping(TUTORIAL_TABS_PREFIX + "listPage")
    ResponseVO<Page<TutorialTabsRspVO>> listPage(@RequestBody TutorialTabsResVO vo);

    @PostMapping(TUTORIAL_TABS_PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody TutorialTabsAddVO vo);

    @PostMapping(TUTORIAL_TABS_PREFIX + "edit")
    ResponseVO<Boolean> edit(@RequestBody TutorialTabsAddVO vo);

    @GetMapping(TUTORIAL_TABS_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(TUTORIAL_TABS_PREFIX + "enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody TutorialTabsRspVO vo);

    @PostMapping(TUTORIAL_TABS_PREFIX + "getTabsDownBox")
    List<CodeValueVO> getTabsDownBox(@RequestBody TutorialDownBoxResVo resVo);

    @PostMapping(TUTORIAL_TABS_PREFIX + "sort")
    ResponseVO<Boolean> sort(@RequestBody List<TutorialCategoryRspVO> sourceList, @RequestParam("siteCode") String siteCode);

    @PostMapping(TUTORIAL_TABS_PREFIX + "getTabsList")
    List<TutorialClientShowVO> getTabsList(@RequestBody TutorialDownBoxResVo resVo);
}
