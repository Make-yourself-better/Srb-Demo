package com.xxl.core;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@SpringBootTest
class CoreApplicationTests {
    //过期时间，毫秒，24小时
    private static long tokenExpiration = 24*60*60*1000;
    //秘钥
    private static String tokenSignKey = "123456";
    @Test
    void contextLoads() throws InterruptedException {
        String token = Jwts.builder().setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "H2256")
                .setSubject("user")
                .setIssuer("xxl")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)) //过期时间
                .setNotBefore(new Date(System.currentTimeMillis() + 20 * 1000)) //20秒后可用
                .setId(UUID.randomUUID().toString())
                .claim("nickname", "xxls")
                .claim("avatar", "1.jpg")
                .signWith(SignatureAlgorithm.HS256, tokenSignKey)
                .compact();//转换成字符串
        System.out.println(token);


    }
    //解析token
    @Test
    public void parse(){
        String token ="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoieHhsIiwiaWF0IjoxNjY0NzY1NzAyLCJleHAiOjE2NjQ4NTIxMDIsIm5iZiI6MTY2NDc2NTcyMiwianRpIjoiNzA4YjRjYWQtZjVkZC00ZGIxLWI4ZDUtNjhlMGI1MjU4NTM2Iiwibmlja25hbWUiOiJ4eGxzIiwiYXZhdGFyIjoiMS5qcGcifQ.K651sByl49IwLw5IAjiMkm_mTYr36b6D3aPVzBNIido";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String nickname = (String) body.get("nickname");
        String avatar = (String) body.get("avatar");
        String subject = body.getSubject();
        System.out.println(subject);
    }
    @Test
    public void uuid(){
        String string = UUID.randomUUID().toString();
        String replaceAll = string.replaceAll("-", "");
        System.out.println("uuid"+replaceAll);
    }

}
