package com.gbsoft.member.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class TokenFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("x-api-token");
        if (token != null) {
            if (token.equals("gbsoft")) {
                chain.doFilter(request, response);
            } else {
                response.getWriter().write("토큰 정보가 틀렸습니다.");
            }
        } else {
            response.getWriter().write("토큰 정보가 없습니다.");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}