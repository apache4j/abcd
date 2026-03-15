package com.cloud.baowang.system.service.business;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.system.api.enums.BusinessEnum;
import com.cloud.baowang.system.api.vo.business.BusinessConfigVO;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.system.po.AddConfigPO;
import com.cloud.baowang.system.po.JoinUsConfigPO;
import com.cloud.baowang.system.repositories.AddConfigRepository;
import com.cloud.baowang.system.repositories.JoinUsConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BusinessConfigService extends ServiceImpl<AddConfigRepository, AddConfigPO> {

    private final AddConfigRepository addConfigRepository;

    private final JoinUsConfigRepository joinUsConfigRepository;


    /**
     * @return 返回结果
     */
    public List<BusinessConfigVO> queryBusinessConfig() {
        try {
            LambdaQueryWrapper<AddConfigPO> queryWrapper = new LambdaQueryWrapper<>();
            // 查询
            queryWrapper.in(AddConfigPO::getAddType, List.of(BusinessEnum.SKYPE.getType(), BusinessEnum.TELEGRAM.getType(),
                    BusinessEnum.H5_ADDRESS.getType(), BusinessEnum.INVITE_CODE.getType()));
            List<AddConfigPO> list = addConfigRepository.selectList(queryWrapper);
            if (list == null || list.isEmpty()) {
                throw new BaowangDefaultException(ResultCode.QUERY_BUSINESS_CONFIG_ERR);
            }
            List<BusinessConfigVO> resultList1 = new ArrayList<>(ConvertUtil.convertListToList(list, new BusinessConfigVO()));
            // 加入我们链接
            LambdaQueryWrapper<JoinUsConfigPO> joinUsWrapper = new LambdaQueryWrapper<>();
            joinUsWrapper.in(JoinUsConfigPO::getAddType, List.of(BusinessEnum.JOIN_US_PC.getType(), BusinessEnum.JOIN_US_OTHER.getType()));

            List<JoinUsConfigPO> joinUsConfigPOList = joinUsConfigRepository.selectList(joinUsWrapper);
            List<BusinessConfigVO> resultList2 = new ArrayList<>(ConvertUtil.convertListToList(joinUsConfigPOList, new BusinessConfigVO()));
            if (CollUtil.isEmpty(resultList2)) {
                throw new BaowangDefaultException(ResultCode.QUERY_BUSINESS_CONFIG_ERR);
            }
            resultList1.addAll(resultList2);
            return resultList1;
        } catch (Exception e) {
            log.error("查询客户端地址配置异常", e);
            throw new BaowangDefaultException(ResultCode.QUERY_BUSINESS_CONFIG_ERR);
        }
    }

    /**
     * @return 返回minio
     */
    public String queryMinioDomain() {
        try {
            // 临时删除，缓存了无效地址
            String domain = RedisUtil.getValue(RedisConstants.MINIO_DOMAIN_URL);
            if (StringUtils.isBlank(domain)) {

                LambdaQueryWrapper<AddConfigPO> queryWrapper = new LambdaQueryWrapper<>();
                // 查询
                queryWrapper.in(AddConfigPO::getAddType, List.of(BusinessEnum.MINIO_DOMAIN.getType()));
                List<AddConfigPO> list = addConfigRepository.selectList(queryWrapper);
                if (list == null || list.size() != 1) {
                    return "";
                }
                domain = list.get(0).getAddress();
                //  放入redis
                RedisUtil.setValue(RedisConstants.MINIO_DOMAIN_URL, domain);
            }
            return domain;
        } catch (Exception e) {
            log.error("查询minio地址配置异常", e);
            return "";
        }
    }


}
