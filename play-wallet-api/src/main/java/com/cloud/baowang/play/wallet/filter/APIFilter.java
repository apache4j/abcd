package com.cloud.baowang.play.wallet.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cloud.baowang.play.api.enums.FTGDefaultException;
import com.cloud.baowang.play.api.enums.ftg.FTGResultCodeEnums;
import com.cloud.baowang.play.api.enums.venue.VenueEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@Service
public class APIFilter extends OncePerRequestFilter {

    @Autowired
    private PlayVenueInfoApi playVenueInfoApi;

    public static String removeBearerPrefix(String input) {
        // 检查输入字符串是否以 "bearer " 开头
        if (input != null && input.toLowerCase().startsWith("bearer ")) {
            // 去除 "bearer " 前缀并返回剩余部分
            return input.substring(7); // "bearer " 的长度是 7
        }
        // 如果不以 "bearer " 开头，则返回原字符串
        return input;
    }

    private static Boolean ftgVerifierAuthorization(String authorization, String aesKey) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(aesKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(removeBearerPrefix(authorization));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain chain) throws ServletException, IOException {

        String url = request.getRequestURI();
        if (url.startsWith("/sbCall/api")) {//沙巴体育
            String conentEncoding = request.getHeader("Content-Encoding");
            if (("gzip".equalsIgnoreCase(conentEncoding) || "deflate".equalsIgnoreCase(conentEncoding))) {
                log.info("Content-Encoding: {}", conentEncoding);
                chain.doFilter(new SBSportRequestWrapper(request), response);
                return;
            }
        } else if (url.startsWith("/callback/ftg")) {//FTG
            String authorization = request.getHeader("Authorization");
            if (StringUtils.isBlank(authorization)) {
                log.info("FTG解密异常,authorization 为空");
                throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, "");
            }
            ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.venueInfoByVenueCode(VenueEnum.FTG.getVenueCode(),"");
            if(!responseVO.isOk()){
                log.info("FTG,调用失败:{}", authorization);
                throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, "");
            }
            VenueInfoVO venueInfoVO = responseVO.getData();
            //解密
            if (!ftgVerifierAuthorization(authorization, venueInfoVO.getAesKey())) {
                log.info("FTG解密异常,authorization:{}", authorization);
                throw new FTGDefaultException(FTGResultCodeEnums.WRONG_TYPES, "");
            }
        }
        chain.doFilter(request, response);
    }
}
