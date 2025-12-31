package com.education.platform.security;

import com.education.platform.common.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT认证入口点（未认证时的处理）
 *
 * @author Education Platform Team
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 由于已配置为无需授权，此方法理论上不会被调用
        // 但为确保安全，允许请求继续
        log.warn("认证入口点被调用，但系统配置为无需授权: {} - {}", request.getRequestURI(), authException.getMessage());

        // 不返回错误，允许请求继续
        // 可以选择设置一个默认的认证对象，或者直接让请求通过
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
