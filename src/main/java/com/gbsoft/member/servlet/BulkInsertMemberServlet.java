package com.gbsoft.member.servlet;

import com.gbsoft.member.config.UtilClass;
import com.gbsoft.member.dao.MemberDao;
import com.gbsoft.member.dto.GenderEnum;
import com.gbsoft.member.dto.MemberAdditionalInformationDto;
import com.gbsoft.member.dto.MemberDto;
import com.gbsoft.member.dto.MemberInputDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * 엑셀 파일을 이용한 Member 생성
 * */
public class BulkInsertMemberServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        UtilClass util = new UtilClass();
        util.setDbConfig(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        String userId = (String) session.getAttribute("userId");
        String userId = "hypark";
        String result = "ok";

        List<MemberDto> memberList = new ArrayList<>();
        List<MemberAdditionalInformationDto> memberInfoList = new ArrayList<>();
        MemberDto member = null;
        MemberAdditionalInformationDto memberInfo = null;

        InputStream is = request.getPart("bulk_insert.xlsx").getInputStream();
//        InputStream is = request.getPart("bulk_insert_fail.xlsx").getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum == 0 || rowNum == 1) continue;
            String nameStr = row.getCell(0).getStringCellValue();
            if (row != null && !nameStr.equals("")) {
                java.util.Date dateCellValue = row.getCell(1).getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String birthStr = sdf.format(dateCellValue);
                String genderStr = row.getCell(2).getStringCellValue();
                String contactStr = row.getCell(3).getStringCellValue();
                String addressStr = row.getCell(4).getStringCellValue();

                MemberInputDto inputDto = new MemberInputDto();
                inputDto.setName(nameStr);
                inputDto.setBirth(birthStr);
                inputDto.setGender(genderStr);
                inputDto.setContact(contactStr);
                inputDto.setAddress(addressStr);

                result = new MemberServlet().validation(inputDto);
                if (result.equals("ok")) {
                    member = new MemberDto();
                    member.setName(nameStr);
                    member.setBirth(Date.valueOf(birthStr));
                    member.setGender(GenderEnum.getCodeByDescription(genderStr));
                    member.setIsDeleted(0);
                    memberInfo = new MemberAdditionalInformationDto();
                    memberInfo.setContact(contactStr);
                    memberInfo.setAddress(addressStr);

                    memberList.add(member);
                    memberInfoList.add(memberInfo);
                } else {
                    result = rowNum + 1 + "행의 " + result;
                    break;
                }
            }
        }

        if (result.equals("ok")) {
            for (int i = 0; i < memberList.size(); i++) {
                try {
                    MemberDao.getInstance().createMember(memberList.get(i), memberInfoList.get(i), userId);
                } catch (SQLException e) {
                    result = e.getMessage();
                }
            }
        }

        is.close();
        workbook.close();
        response.getWriter().write(result);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}