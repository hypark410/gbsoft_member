package com.gbsoft.member.filter;

import com.gbsoft.member.dao.MemberDao;
import jakarta.servlet.*;

import java.io.IOException;

public class AuditFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        MemberDao.getInstance().updateTime(Long.parseLong(request.getParameter("id")));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}