package com.gbsoft.member.servlet;

import com.gbsoft.member.config.UtilClass;
import com.gbsoft.member.dao.MemberDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/*
* Member 단일 삭제 (soft)
* */
public class SoftDeleteMemberServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        UtilClass util = new UtilClass();
        util.setDbConfig(getServletContext());
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String result = "ok";

        String ids = request.getParameter("ids");
        int delResult = MemberDao.getInstance().SoftDeleteMember(ids);

        if(delResult == 0) {
            result = "삭제를 실패하였습니다.";
        }

        response.getWriter().write(result);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
