package org.cyx.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cyx.enums.BizCodeEnum;
import org.cyx.model.LoginUser;

import java.util.Date;

/**
 * @Description JWTUtil
 * @Author cyx
 * @Date 2021/2/19
 **/
@Slf4j
public class JWTUtil {
    /**
     * 过期时间7天
     */
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7;

    /**
     * 密钥
     */
    private static final String SECURITY = "cyx.shop888";

    /**
     * token前缀
     */
    private static final String TOKEN_PREFIX = "cyxshop";

    /**
     * 颁发者
     */
    private static final String SUBJECT = "cyx";

    public static String generationJWT(LoginUser loginUser) {
        if (loginUser == null) {
            throw new NullPointerException("登录对象为空");
        }
        String token = Jwts.builder()
                .setSubject(SUBJECT)
                .claim("head_img", loginUser.getHeadImg())
                .claim("id", loginUser.getId())
                .claim("mail", loginUser.getMail())
                .claim("name", loginUser.getName()).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECURITY)
                .compact();
        token = TOKEN_PREFIX + token;
        return token;
    }

    public static Claims checkJWT(String token) {
        try {
            if (StringUtils.isNotBlank(token) && token.startsWith(TOKEN_PREFIX)) {
                final Claims claims = Jwts.parser().setSigningKey(SECURITY)
                        .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                        .getBody();
                if (claims != null) {
                    return claims;
                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error(BizCodeEnum.OPS_FAILE.getMessage());
        }
        return null;
    }
}
