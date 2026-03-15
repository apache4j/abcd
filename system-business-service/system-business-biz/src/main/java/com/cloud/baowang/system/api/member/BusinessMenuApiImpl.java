package com.cloud.baowang.system.api.member;

import com.cloud.baowang.system.api.vo.business.BusinessUserMenuRespVO;
import com.cloud.baowang.system.api.api.member.BusinessMenuApi;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuAddVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuQueryVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuUpdateVO;
import com.cloud.baowang.system.api.vo.member.BusinessMenuVO;
import com.cloud.baowang.system.service.member.BusinessMenuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class BusinessMenuApiImpl implements BusinessMenuApi {


    private final BusinessMenuService businessMenuService;

    @Override
    public String addMenu(BusinessMenuAddVO businessMenuAddVO) {
       return businessMenuService.addMenu(businessMenuAddVO);
    }

    @Override
    public String updateMenu(BusinessMenuUpdateVO businessMenuUpdateVO) {
        return businessMenuService.updateMenu(businessMenuUpdateVO);
    }

    @Override
    public Integer deleteMenu(IdVO idVO) {
        return businessMenuService.deleteMenu(idVO);
    }
    @Override
    public List<BusinessMenuVO> listTreeMenu(BusinessMenuQueryVO businessMenuQueryVO) {
        return businessMenuService.listTreeMenu(businessMenuQueryVO);
    }
    @Override
    public List<BusinessMenuVO> listTreeMenuByAdminId(String adminId) {
        return businessMenuService.listTreeMenuByAdminId(adminId);
    }
    @Override
    public boolean checkMenuKeyUnique(String menuKey) {
        return businessMenuService.checkMenuKeyUnique(menuKey);
    }

    @Override
    public List<BusinessUserMenuRespVO> listAllMenuByAdminId(String adminId) {
        return businessMenuService.listAllMenuByAdminId(adminId);
    }


}
