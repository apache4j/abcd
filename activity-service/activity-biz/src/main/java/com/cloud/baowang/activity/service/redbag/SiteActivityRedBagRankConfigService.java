package com.cloud.baowang.activity.service.redbag;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.po.SiteActivityRedBagRankConfigPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagRankConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteActivityRedBagRankConfigService extends ServiceImpl<SiteActivityRedBagRankConfigRepository, SiteActivityRedBagRankConfigPO> {

    public List<SiteActivityRedBagRankConfigPO> getByBaseId(String baseId) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRankConfigPO::getBaseId, baseId)
                .orderByAsc(SiteActivityRedBagRankConfigPO::getSort)
                .list();
    }

    public void deleteByBaseId(String baseId) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagRankConfigPO::getBaseId, baseId)
                .remove();
    }
}
