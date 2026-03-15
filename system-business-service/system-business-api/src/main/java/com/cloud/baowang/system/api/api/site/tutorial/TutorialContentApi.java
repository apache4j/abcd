package com.cloud.baowang.system.api.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.content.TutorialContentRspVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteTutorialContentApi", value = ApiConstants.NAME)
@Tag(name = "教程内容配置 ")
public interface TutorialContentApi {
    String TUTORIAL_CONTENT_PREFIX = ApiConstants.PREFIX + "/tutorial-content/api/";

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "listPage")
    ResponseVO<Page<TutorialContentRspVO>> listPage(@RequestBody TutorialContentResVO vo);

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody TutorialContentAddVO vo);

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "edit")
    ResponseVO<Boolean> edit(@RequestBody TutorialContentAddVO vo);

    @GetMapping(TUTORIAL_CONTENT_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody TutorialContentRspVO vo);

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "getContentDownBox")
    List<CodeValueVO> getContentDownBox(@RequestBody TutorialDownBoxResVo resVo);

    @PostMapping(TUTORIAL_CONTENT_PREFIX + "sort")
    ResponseVO<Boolean> sort(@RequestBody List<TutorialCategoryRspVO> sourceList, @RequestParam("siteCode") String siteCode);

    @PostMapping(TUTORIAL_CONTENT_PREFIX+"showContent")
    List<TutorialClientShowVO> showContent(@RequestBody TutorialDownBoxResVo resVo);
    @GetMapping(TUTORIAL_CONTENT_PREFIX+"checkDataExist")
    Boolean checkDataExist();
}
