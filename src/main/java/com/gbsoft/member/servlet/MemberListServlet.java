package com.gbsoft.member.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsoft.member.config.UtilClass;
import com.gbsoft.member.dao.MemberDao;
import com.gbsoft.member.dto.MemberInputDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/*
 * Member 전체 조회 (페이징, 이름/나이순 정렬, 이름/성별 검색)
 * */
public class MemberListServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        UtilClass util = new UtilClass();
        util.setDbConfig(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 페이징
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        int offset = (pageNumber - 1) * pageSize;

        // 정렬
        int colNum = Integer.parseInt(request.getParameter("col"));
        int sortingNum = Integer.parseInt(request.getParameter("sorting"));
        String col = null;
        switch (colNum) {
            case 1:
                col = "name";
                break;
            case 2:
                col = "birth";
                break;
        }
        String sorting = null;
        switch (sortingNum) {
            case 1:
                sorting = "asc";
                break;
            case 2:
                sorting = "desc";
                break;
        }

        // 검색
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");

        List<MemberInputDto> memberList = MemberDao.getInstance().getMemberList(pageSize, offset, col, sorting, name, gender);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(memberList));
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}