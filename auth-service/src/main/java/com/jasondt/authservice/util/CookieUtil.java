package com.jasondt.authservice.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
    public static Cookie createJwtCookie(String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }
}