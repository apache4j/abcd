package com.cloud.baowang.system.api.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryQueryVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteTutorialCategoryApi", value = ApiConstants.NAME)
@Tag(name = "教程大类配置 ")
public interface TutorialCategoryApi {
    String TUTORIAL_CATEGORY_PREFIX = ApiConstants.PREFIX + "/tutorial-category/api/";

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "listPage")
    ResponseVO<Page<TutorialCategoryRspVO>> listPage(@RequestBody TutorialCategoryQueryVO vo);

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody TutorialCategoryAddVO vo);

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "edit")
    ResponseVO<Boolean> edit(@RequestBody TutorialCategoryAddVO vo);

    @GetMapping(TUTORIAL_CATEGORY_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody TutorialCategoryRspVO vo);

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "getCategoryDownBox")
    List<CodeValueVO> getCategoryDownBox(@RequestBody TutorialDownBoxResVo resVo);

    @PostMapping(TUTORIAL_CATEGORY_PREFIX + "sort")
    ResponseVO<Boolean> sort(@RequestBody List<TutorialCategoryRspVO> sourceList, @RequestParam("siteCode") String siteCode);


    @PostMapping(TUTORIAL_CATEGORY_PREFIX+"getCategoryList")
    List<TutorialClientShowVO> getCategoryList();
}
