package com.gbsoft.member.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsoft.member.dao.MemberDao;
import com.gbsoft.member.dto.Member;
import com.gbsoft.member.dto.MemberAdditionalInformation;
import com.gbsoft.member.dto.MemberInputDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

//@WebServlet(name = "memberServlet", value = "/member")
public class MemberServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        int offset = (pageNumber - 1) * pageSize;

        int colNum = Integer.parseInt(request.getParameter("col"));
        String sorting = request.getParameter("sorting");
        String col = null;
        switch (colNum) {
            case 1:
                col = "name";
                break;
            case 2:
                col = "birth";
                break;
        }

        String name = request.getParameter("name");
        String gender = request.getParameter("gender");
        String id = request.getParameter("id");

        List<Member> memberList = MemberDao.getInstance().getMemberList(pageSize, offset, col, sorting, name, gender, id);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(memberList));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String str = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        MemberInputDto inputDto = objectMapper.readValue(str, MemberInputDto.class);

        String result = validation(inputDto);
        if (result.equals("ok")) {
            Date date = Date.valueOf(inputDto.getBirth());
            String createdBy = inputDto.getCreatedBy();
            String modifiedBy = inputDto.getModifiedBy();
            Member member = new Member(null, inputDto.getName(), date, inputDto.getGender(), null, createdBy, null, modifiedBy, 0);
            MemberAdditionalInformation memberInfo = new MemberAdditionalInformation(null, null, inputDto.getContact(), inputDto.getAddress(), null, createdBy, null, modifiedBy);

            MemberDao.getInstance().createMember(member, memberInfo);
        }
        response.getWriter().write(result);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private String validation(MemberInputDto inputDto) {
        String result = "ok";
        String name = inputDto.getName();
        String birth = inputDto.getBirth();
        String regex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
        String gender = inputDto.getGender();
        String createdBy = inputDto.getCreatedBy();
        String modifiedBy = inputDto.getModifiedBy();

        if (name == null || name.isEmpty()) {
            result = "이름을 입력하세요.";
        } else if (birth == null || birth.isEmpty()) {
            result = "생일을 입력하세요.";
        } else if (!birth.matches(regex)) {
            result = "생일은 1900-01-01 형태로 입력하세요.";
        } else if (gender == null || gender.isEmpty()) {
            result = "성별을 입력하세요.";
        } else if (!gender.equals("F") && !gender.equals("M")) {
            result = "성별은 F 혹은 M으로 입력하세요.";
        } else if (createdBy == null || createdBy.isEmpty()) {
            result = "createdBy를 입력하세요.";
        } else if (modifiedBy == null || modifiedBy.isEmpty()) {
            result = "modifiedBy을 입력하세요.";
        }

        return result;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
