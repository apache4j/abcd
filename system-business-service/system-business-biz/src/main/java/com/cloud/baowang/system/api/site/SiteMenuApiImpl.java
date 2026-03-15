package com.cloud.baowang.system.api.site;

import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.api.site.SiteMenuApi;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuAddVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuQueryVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuUpdateVO;
import com.cloud.baowang.system.api.vo.site.admin.SiteMenuVO;
import com.cloud.baowang.system.service.site.SiteMenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class SiteMenuApiImpl implements SiteMenuApi {


    private final SiteMenuService siteMenuService;

    @Override
    public String addMenu(SiteMenuAddVO siteMenuAddVO) {
       return siteMenuService.addMenu(siteMenuAddVO);
    }

    @Override
    public String updateMenu(SiteMenuUpdateVO siteMenuUpdateVO) {
        return siteMenuService.updateMenu(siteMenuUpdateVO);
    }

    @Override
    public Integer deleteMenu(IdVO idVO) {
        return siteMenuService.deleteMenu(idVO);
    }
    @Override
    public List<SiteMenuVO> listTreeMenu(SiteMenuQueryVO siteMenuQueryVO) {
        return siteMenuService.listTreeMenu(siteMenuQueryVO);
    }
    @Override
    public List<SiteMenuVO> listTreeMenuByAdminId(String adminId) {
        return siteMenuService.listTreeMenuByAdminId(adminId);
    }
    @Override
    public boolean checkMenuKeyUnique(String menuKey) {
        return siteMenuService.checkMenuKeyUnique(menuKey);
    }
}
