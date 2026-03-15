package com.cloud.baowang.site.controller.tutorial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.SystemParamApi;
import com.cloud.baowang.system.api.api.language.LanguageManagerApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialCategoryApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialClassApi;
import com.cloud.baowang.system.api.enums.tutorial.TutorialGeneralEnum;
import com.cloud.baowang.system.api.vo.language.LanguageManagerListVO;
import com.cloud.baowang.system.api.vo.site.tutorial.*;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.category.TutorialCategoryRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassAddVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassResVO;
import com.cloud.baowang.system.api.vo.site.tutorial.classif.TutorialClassRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.tabs.TutorialTabsAddVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "教程分类配置")
@RestController
@RequestMapping(value = "/tutorial-class/api")
@AllArgsConstructor
@Validated
public class TutorialClassController {
    private final TutorialClassApi tutorialClassApi;
    private final TutorialCategoryApi tutorialCategoryApi;
    private final LanguageManagerApi languageManagerApi;
    private final SystemParamApi systemParamApi;

    @Operation(summary = "下拉框")
    @PostMapping("getDownBox")
    public ResponseVO<Map<String,List<CodeValueVO>>> getDownBox(@RequestBody  TutorialDownBoxResVo resVo) {
        resVo.setSiteCode(CurrReqUtils.getSiteCode());
        Map<String,List<CodeValueVO>> result = new HashMap<>();
        result.put(TutorialGeneralEnum.TUTORIAL_CATEGORY.getName(),tutorialCategoryApi.getCategoryDownBox(resVo));
        result.put(TutorialGeneralEnum.TUTORIAL_CLASS.getName(),tutorialClassApi.getClassDownBox(resVo));
        result.put(CommonConstant.ENABLE_DISABLE_TYPE,systemParamApi.getSystemParamByType(CommonConstant.ENABLE_DISABLE_TYPE).getData());
        return ResponseVO.success(result);
    }

    @Operation(summary = "教程分类列表-分页")
    @PostMapping("listPage")
    public ResponseVO<Page<TutorialClassRspVO>> listPage(@RequestBody @Validated TutorialClassResVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return tutorialClassApi.listPage(vo);
    }

    @Operation(summary = "教程分类-添加")
    @PostMapping("add")
    public ResponseVO<Boolean> add(@RequestBody @Validated TutorialClassAddVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        checkNameRules(vo);
        return tutorialClassApi.add(vo);
    }

    @Operation(summary = "教程分类-编辑")
    @PostMapping("edit")
    public ResponseVO<Boolean> edit(@RequestBody @Validated TutorialClassAddVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        checkNameRules(vo);
        return tutorialClassApi.edit(vo);
    }

    @Operation(summary = "教程分类-删除")
    @PostMapping("del")
    public ResponseVO<Boolean> del(@RequestBody TutorialTabsAddVO vo) {
        return tutorialClassApi.del(vo.getId());
    }

    @Operation(summary = "教程分类-禁用/启用")
    @PostMapping("enableAndDisAble")
    public ResponseVO<Boolean> enableAndDisAble(@RequestBody @Validated TutorialClassRspVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return tutorialClassApi.enableAndDisAble(vo);
    }

    @Operation(summary = "教程分类-排序")
    @PostMapping("sort")
    public ResponseVO<Boolean> sort(@RequestBody List<TutorialCategoryRspVO> sourceList) {
        if (sourceList != null && !sourceList.isEmpty()) {
            String siteCode = CurrReqUtils.getSiteCode();
            return tutorialClassApi.sort(sourceList, siteCode);
        }
        return ResponseVO.fail(ResultCode.PARAM_ERROR);
    }

    public void checkNameRules(TutorialClassAddVO vo){
        if(vo.getI18nMessages().stream().anyMatch(e -> e.getMessage().length() > 100)){
            throw  new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
    }

    public List<CodeValueVO> getSiteLanguageInfo(){
        List<CodeValueVO> languageInfo = new ArrayList<>();
        ResponseVO<List<LanguageManagerListVO>> list = languageManagerApi.list();
        List<LanguageManagerListVO> data = list.getData();
        data.stream().forEach(e -> {
            languageInfo.add(CodeValueVO.builder().code(e.getCode()).value(e.getName()).build());
        });
        return languageInfo;
    }
}
