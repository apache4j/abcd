package com.cloud.baowang.activity.service.redbag;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.po.SiteActivityRedBagConfigPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SiteActivityRedBagConfigService extends ServiceImpl<SiteActivityRedBagConfigRepository, SiteActivityRedBagConfigPO> {

    public List<SiteActivityRedBagConfigPO> getByBaseId(String baseId) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagConfigPO::getBaseId, baseId)
                .orderByAsc(SiteActivityRedBagConfigPO::getSort)
                .list();
    }

    public void deleteByBaseId(String baseId) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagConfigPO::getBaseId, baseId)
                .remove();
    }
}