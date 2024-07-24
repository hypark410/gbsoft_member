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

/*
 * Member 수정 (이름, 연락처, 주소)
 * */
public class UpdateMemberServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");

        ServletInputStream inputStream = request.getInputStream();
        String str = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        MemberInputDto inputDto = objectMapper.readValue(str, MemberInputDto.class);

        String result = validation(inputDto);

        MemberDto member = new MemberDto(Long.parseLong(id), inputDto.getName(), null, null, null, null, null, null, null);
        MemberAdditionalInformationDto info = new MemberAdditionalInformationDto(null, null, inputDto.getContact(), inputDto.getAddress(), null, null, null, null);

        MemberDao.getInstance().updateMember(member, info);
        response.getWriter().write(result);
    }

    private String validation(MemberInputDto inputDto) {
        String result = "ok";
        String contact = inputDto.getContact();
        String contactRegex = "\\d{2,3}-\\d{3,4}-\\d{4}";

        if (contact != null || !contact.isEmpty()) {
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