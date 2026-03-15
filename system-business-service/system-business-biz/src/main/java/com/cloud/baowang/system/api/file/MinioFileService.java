package com.cloud.baowang.system.api.file;

import com.cloud.baowang.system.api.api.SystemConfigApi;
import com.cloud.baowang.system.service.SystemParamService;
import com.cloud.baowang.system.service.business.BusinessConfigService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class MinioFileService {

    @Autowired
    private  BusinessConfigService businessConfigService;

    /**
     * 查询地址配置中的 MINIO对外访问的域名
     * @return String
     */
    public String getMinioDomain() {
        return businessConfigService.queryMinioDomain();
    }

    /**
     * 获取文件访问路径
     */
    public String getFileUrlByKey(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return null;
        }
        return getMinioDomain() + "/" + fileKey;
    }

    /**
     * 批量获取文件访问路径
     */
    public List<String> getFileUrlByKeys(List<String> fileKeys) {
        if (CollectionUtils.isEmpty(fileKeys)) {
            return Lists.newArrayList();
        }
        List<String> urlFileList = Lists.newLinkedList();
        for (String fileKey : fileKeys) {
            urlFileList.add(getMinioDomain() + "/" + fileKey);
        }
        return urlFileList;
    }
}
