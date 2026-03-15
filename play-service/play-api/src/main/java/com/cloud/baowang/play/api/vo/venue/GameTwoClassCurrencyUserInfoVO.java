package com.cloud.baowang.play.api.vo.venue;

import cn.hutool.core.collection.CollectionUtil;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author qiqi
 */
@Data
@Schema(description = "二级分类添加币种游戏请求对象")
public class GameTwoClassCurrencyUserInfoVO {

    @Schema(description = "币种")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    @Schema(description = "游戏列表 调用 游戏管理页面 游戏信息-列表接口", required = true)
    private List<GameClassInfoSetSortDetailVO> gameIds;


    public static void main(String[] args) {
        List<String> allGameList = List.of("A", "B", "C", "D"); // 旧数据
        List<String> newAllGameList = List.of("B", "C", "E", "F"); // 新数据

        // 转成 Set 方便做差集运算
        Set<String> oldSet = new HashSet<>(allGameList);
        Set<String> newSet = new HashSet<>(newAllGameList);

        // 找出旧数据中存在、新数据中不存在的（需要删除的）
        List<String> delAllGameList = new ArrayList<>(oldSet);
        delAllGameList.removeAll(newSet);

        // 找出新数据中存在、旧数据中不存在的（需要新增的）
        List<String> addAllGameList = new ArrayList<>(newSet);
        addAllGameList.removeAll(oldSet);

        // 打印结果
        System.out.println("需要删除的：" + delAllGameList);
        System.out.println("需要新增的：" + addAllGameList);
    }
}
