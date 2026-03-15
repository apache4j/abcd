package com.cloud.baowang.user.repositories;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.user.api.vo.site.GetStatisticsBySiteCodeVO;
import com.cloud.baowang.user.api.vo.site.IPTop10ResVO;
import com.cloud.baowang.user.api.vo.site.VisitFromResVO;
import com.cloud.baowang.user.api.vo.user.UserAccountListVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoCountVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.user.api.vo.user.UserLoginRequestVO;
import com.cloud.baowang.user.po.UserLoginInfoPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Author 小智
 * @Date 15/5/23 10:57 AM
 * @Version 1.0
 */
@Mapper
public interface UserLoginInfoRepository extends BaseMapper<UserLoginInfoPO> {


    Page<UserLoginInfoVO> selectUserLoginPage(Page<UserLoginInfoPO> page,
                                              @Param("vo") UserLoginRequestVO requestVO);


    UserLoginInfoCountVO selectUserLoginAll(@Param("vo") UserLoginRequestVO requestVO);

    Long getTotalCount(@Param("vo") UserLoginRequestVO vo);

    List<UserLoginInfoPO> getLatestLoginInfoByAccountList(@Param("vo") UserAccountListVO vo);

    List<UserLoginInfoPO> getLatestLoginInfoByUserIds(@Param("vo") List<String> userIds);

    UserLoginInfoPO getLatestLoginInfoByUserIdForTask(@Param("userId") String userId);

    List<GetStatisticsBySiteCodeVO> getLogStatisticsBySiteCode(@Param("start") Long start,
                                                               @Param("end") Long end,
                                                               @Param("siteCode") String siteCode,
                                                               @Param("dbZone") String dbZone
    );


    List<IPTop10ResVO> getDomainNameRanking(@Param("start") Long start,
                                            @Param("end") Long end,
                                            @Param("siteCode") String siteCode);

    List<VisitFromResVO> getVisitFrom(@Param("start") Long start,
                                      @Param("end") Long end,
                                      @Param("siteCode") String siteCode);


    List<IPTop10ResVO> getVisitFromByIp(@Param("start") Long start,
                                        @Param("end") Long end,
                                        @Param("siteCode") String siteCode);
}
