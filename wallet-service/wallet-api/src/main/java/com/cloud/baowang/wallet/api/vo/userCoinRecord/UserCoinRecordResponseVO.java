package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "会员账变记录返回对象")
@I18nClass
public class UserCoinRecordResponseVO{

    /**
     * 小计
     */
    private UserCoinRecordVO currentPage;

    /**
     * 总计
     */
    private UserCoinRecordVO totalPage;
    /**
     * 分页数据
     */
    private Page<UserCoinRecordVO> userCoinRecordVOPage;
}
