package com.cloud.baowang.user.controller.helpcenter;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.api.api.site.agreement.HelpCenterManageApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialCategoryApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialClassApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialContentApi;
import com.cloud.baowang.system.api.api.site.tutorial.TutorialTabsApi;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowRspVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialClientShowVO;
import com.cloud.baowang.system.api.vo.site.tutorial.TutorialDownBoxResVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "客户端教程显示")
@RestController
@RequestMapping("/tutorial-display/api")
@AllArgsConstructor
@Slf4j
public class TutorialDisplayController {
    private final TutorialCategoryApi tutorialCategoryApi;
    private final TutorialClassApi tutorialClassApi;
    private final TutorialTabsApi tutorialTabsApi;
    private final TutorialContentApi tutorialContentApi;
    private final HelpCenterManageApi helpCenterManageApi;


    @Operation(summary = "一层显示")
    @PostMapping("showTutorialPreLayer")
    public ResponseVO<List<TutorialClientShowRspVO>> showTutorialPreLayer() {
        return helpCenterManageApi.getHelpCenterInfo();
    }

    @Operation(summary = "二层显示")
    @PostMapping("showTutorialTurnLayer")
    public ResponseVO<List<TutorialClientShowRspVO>> showTutorialTurnLayer(@RequestBody TutorialDownBoxResVo resVo) {
        resVo.setSiteCode(CurrReqUtils.getSiteCode());
        List<TutorialClientShowRspVO> rspList = new ArrayList<>();
        List<TutorialClientShowVO> tabsList = tutorialTabsApi.getTabsList(resVo);
        for (TutorialClientShowVO tabs : tabsList) {
            resVo.setTabsId(String.valueOf(Long.valueOf(tabs.getId())));
            resVo.setTabsName(tabs.getName());
            List<TutorialClientShowVO> contentList = tutorialContentApi.showContent(resVo);
            TutorialClientShowRspVO resultVO = new TutorialClientShowRspVO();
            resultVO.setId(tabs.getId());
            resultVO.setName(tabs.getName());
            resultVO.setSubset(contentList);
            rspList.add(resultVO);
        }
        return ResponseVO.success(rspList);
    }


//    @Operation(summary = "显示教程大类")
//    @PostMapping("showCategory")
//    public ResponseVO<List<CodeValueVO>> showCategory(@RequestBody @Validated TutorialDownBoxResVo resVo) {
//        return ResponseVO.success(tutorialCategoryApi.getCategoryDownBox(resVo));
//    }
//
//    @Operation(summary = "显示分类")
//    @PostMapping("showClass")
//    public ResponseVO<List<CodeValueVO>> showClass(@RequestBody TutorialDownBoxResVo resVo)  {
//        return ResponseVO.success(tutorialClassApi.getClassDownBox(resVo));
//    }
//
//    @Operation(summary = "显示页签")
//    @PostMapping("showTabs")
//    public ResponseVO<List<CodeValueVO>> showTabs(@RequestBody TutorialDownBoxResVo resVo)  {
//        return ResponseVO.success(tutorialTabsApi.getTabsDownBox(resVo));
//    }
//
//    @Operation(summary = "显示内容,1页签对应n内容")
//    @PostMapping("showContent")
//    public ResponseVO<List<CodeValueVO>> showContent(@RequestBody TutorialDownBoxResVo resVo)  {
//        return ResponseVO.success(tutorialContentApi.showContent(resVo));
//    }
}
