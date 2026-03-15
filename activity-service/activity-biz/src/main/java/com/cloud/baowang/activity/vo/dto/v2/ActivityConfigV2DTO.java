package com.cloud.baowang.activity.vo.dto.v2;

import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityCheckInPO;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityCheckInV2PO;
import lombok.Data;

/**
 *
 * @author: brence
 * @description: 活动配置
 * @date: 2025-10-17
 */
@Data
public class ActivityConfigV2DTO {

    private SiteActivityBaseV2PO basePO;

    private SiteActivityCheckInV2PO checkInPO;
}
