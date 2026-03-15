package com.cloud.baowang.system.service.site;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.BaseReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.system.po.site.SiteHomeAllButtonEntrancePO;
import com.cloud.baowang.system.repositories.site.SiteHomeAllButtonEntranceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 站点后台-快捷入口(全部功能) 服务类
 * </p>
 *
 * @author ford
 * @since 2024-10-31
 */
@Slf4j
@Service
public class SiteHomeAllButtonEntranceService extends ServiceImpl<SiteHomeAllButtonEntranceRepository, SiteHomeAllButtonEntrancePO> {

    public String getAllButtonEntranceByType(Integer pcOrH5) {
        List<SiteHomeAllButtonEntrancePO> list =
                this.lambdaQuery().eq(SiteHomeAllButtonEntrancePO::getPcOrH5, pcOrH5).list();
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        list = list.subList(0, 9);
        return JSONObject.toJSONString(list);
    }

    public ResponseVO<Boolean> init(BaseReqVO baseReqVO) {
        String siteCode=baseReqVO.getSiteCode();
        Long countNum=this.lambdaQuery().eq(SiteHomeAllButtonEntrancePO::getSiteCode, siteCode).count();
        if(countNum<=0){
            List<SiteHomeAllButtonEntrancePO> list =this.lambdaQuery().eq(SiteHomeAllButtonEntrancePO::getSiteCode, CommonConstant.ADMIN_CENTER_SITE_CODE).list();
            for(SiteHomeAllButtonEntrancePO agentHomeAllButtonEntrancePO:list){
                agentHomeAllButtonEntrancePO.setId(null);
                agentHomeAllButtonEntrancePO.setSiteCode(siteCode);
                agentHomeAllButtonEntrancePO.setCreator(baseReqVO.getAdminId());
                agentHomeAllButtonEntrancePO.setCreatedTime(System.currentTimeMillis());
                agentHomeAllButtonEntrancePO.setUpdater(baseReqVO.getAdminId());
                agentHomeAllButtonEntrancePO.setUpdatedTime(System.currentTimeMillis());
            }
            this.saveBatch(list);
        }
        return  ResponseVO.success(Boolean.TRUE);
    }
}
