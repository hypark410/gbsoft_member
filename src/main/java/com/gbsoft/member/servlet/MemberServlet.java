package com.gbsoft.member.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsoft.member.dao.MemberDao;
import com.gbsoft.member.dto.MemberAdditionalInformationDto;
import com.gbsoft.member.dto.MemberDto;
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

/*
 * Member 단일 조회
 * Member 생성
 * Member 단일 삭제 (hard)
 * */
//@WebServlet(name = "memberServlet", value = "/member")
public class MemberServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MemberInputDto member = MemberDao.getInstance().getMember(Long.parseLong(request.getParameter("id")));
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(member));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        String userId = (String) session.getAttribute("userId");
        String userId = "hypark";

        ServletInputStream inputStream = request.getInputStream();
        String str = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        MemberInputDto inputDto = objectMapper.readValue(str, MemberInputDto.class);

        String result = validation(inputDto);
        if (result.equals("ok")) {
            Date date = Date.valueOf(inputDto.getBirth());

            MemberDto member = new MemberDto(null, inputDto.getName(), date, inputDto.getGender(), null, null, null, null, 0);
            MemberAdditionalInformationDto memberInfo = new MemberAdditionalInformationDto(null, null, inputDto.getContact(), inputDto.getAddress(), null, null, null, null);

            MemberDao.getInstance().createMember(member, memberInfo, userId);
        }
        response.getWriter().write(result);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        String userId = (String) session.getAttribute("userId");
        String userId = "hypark";

        String result = "ok";
        int delResult = MemberDao.getInstance().HardDeleteMember(Long.parseLong(request.getParameter("id")), userId);

        if (delResult == 0) {
            result = "삭제를 실패하였습니다.";
        }

        response.getWriter().write(result);
    }

    private String validation(MemberInputDto inputDto) {
        String result = "ok";
        String name = inputDto.getName();
        String birth = inputDto.getBirth();
        String birthRegex = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
        String gender = inputDto.getGender();
        String contact = inputDto.getContact();
        String contactRegex = "\\d{2,3}-\\d{3,4}-\\d{4}";

        if (name == null || name.isEmpty()) {
            result = "이름을 입력하세요.";
        } else if (birth == null || birth.isEmpty()) {
            result = "생일을 입력하세요.";
        } else if (!birth.matches(birthRegex)) {
            result = "생일은 1900-01-01 형태로 입력하세요.";
        } else if (gender == null || gender.isEmpty()) {
            result = "성별을 입력하세요.";
        } else if (!gender.equals("F") && !gender.equals("M")) {
            result = "성별은 F 혹은 M으로 입력하세요.";
        } else if (contact != null || !contact.isEmpty()) {
            if (!contact.matches(contactRegex)) {
                result = "전화번호는 000-0000-0000 형태로 입력하세요.";
            }
        }

        return result;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
