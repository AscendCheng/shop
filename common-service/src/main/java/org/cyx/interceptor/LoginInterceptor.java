package org.cyx.interceptor;

import io.jsonwebtoken.Claims;
import org.cyx.enums.BizCodeEnum;
import org.cyx.model.LoginUser;
import org.cyx.util.CommonUtil;
import org.cyx.util.JWTUtil;
import org.cyx.util.JsonData;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description LoginInterceptor
 * @Author cyx
 * @Date 2021/2/22
 **/
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("token");
        if (token == null) {
            CommonUtil.sengMsg(response, JsonData.buildError(BizCodeEnum.ACCOUNT_NOT_LOGIN.getMessage()));
            return false;
        } else {
            Claims claims = JWTUtil.checkJWT(token);
            if (claims == null) {
                CommonUtil.sengMsg(response, JsonData.buildError(BizCodeEnum.ACCOUNT_NOT_LOGIN.getMessage()));
                return false;
            }

            Long id = Long.valueOf(claims.get("id").toString());
            String mail = claims.get("mail").toString();
            String headImg = claims.get("head_img").toString();
            String name = claims.get("name").toString();

            LoginUser loginUser = LoginUser.builder().id(id).mail(mail).headImg(headImg).name(name).build();

            // 通过attribute存储用户信息
            request.setAttribute("loginUser",loginUser);

            threadLocal.set(loginUser);
            return true;
        }
    }
}
