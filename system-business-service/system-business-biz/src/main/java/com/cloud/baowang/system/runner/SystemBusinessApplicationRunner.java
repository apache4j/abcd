package com.cloud.baowang.system.runner;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.api.vo.operations.DomainVO;
import com.cloud.baowang.system.service.member.BusinessRoleService;
import com.cloud.baowang.system.service.operations.DomainInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLocalCachedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 初始化 system-business 模块对应业务数据
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SystemBusinessApplicationRunner implements ApplicationRunner {

//    @Value("${auth.roleCacheEnabled}")
    private boolean roleCacheEnabled = false;

    private final BusinessRoleService businessRoleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化角色数据
        if(roleCacheEnabled){
            //获取所有角色及角色对应的菜单url
            Map<String,Map<String,String>> map = businessRoleService.getAllRoleMenuUrls();
            for(Map.Entry<String, Map<String,String>> roleMenuMap : map.entrySet()){
                String roleId = roleMenuMap.getKey();
                RedisUtil.setLocalCachedMap(CacheConstants.KEY_ADMIN_AUTH_INFO_KEY, roleId, roleMenuMap.getValue());
            }
        }
    }

}
