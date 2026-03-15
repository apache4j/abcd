package com.cloud.baowang.system.api.api.site.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.enums.ApiConstants;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassRspVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "siteTutorialClassApi", value = ApiConstants.NAME)
@Tag(name = "教程分类配置 ")
public interface TutorialClassApi {
    String TUTORIAL_CLASS_PREFIX = ApiConstants.PREFIX + "/tutorial-class/api/";

    @PostMapping(TUTORIAL_CLASS_PREFIX + "listPage")
    ResponseVO<Page<TutorialClassRspVO>> listPage(@RequestBody TutorialClassResVO vo);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "add")
    ResponseVO<Boolean> add(@RequestBody TutorialClassAddVO vo);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "edit")
    ResponseVO<Boolean> edit(@RequestBody TutorialClassAddVO vo);

    @GetMapping(TUTORIAL_CLASS_PREFIX + "del")
    ResponseVO<Boolean> del(@RequestParam("id") String id);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "enableAndDisAble")
    ResponseVO<Boolean> enableAndDisAble(@RequestBody TutorialClassRspVO vo);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "getClassDownBox")
    List<CodeValueVO> getClassDownBox(@RequestBody TutorialDownBoxResVo resVo);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "sort")
    ResponseVO<Boolean> sort(@RequestBody List<TutorialCategoryRspVO> sourceList, @RequestParam("siteCode") String siteCode);

    @PostMapping(TUTORIAL_CLASS_PREFIX + "getClassList")
    List<TutorialClientShowVO> getClassList(@RequestParam("id") String id);
}
