package com.gbsoft.member.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gbsoft.member.dao.MemberDao;
import com.gbsoft.member.dto.GenderEnum;
import com.gbsoft.member.dto.MemberAdditionalInformationDto;
import com.gbsoft.member.dto.MemberDto;
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
import java.text.SimpleDateFormat;

/*
 * 엑셀 파일을 이용한 Member 생성
 * */
public class BulkInsertMemberServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        String userId = (String) session.getAttribute("userId");
        String userId = "hypark";
        String result = "ok";

        MemberDto member = null;
        MemberAdditionalInformationDto memberInfo = null;

//        String filePath = "C:\\test\\bulk_insert.xlsx";
//        FileInputStream is = new FileInputStream(filePath);
        InputStream is = request.getPart("bulk_insert.xlsx").getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            if (row.getRowNum() == 0 || row.getRowNum() == 1) continue;
            String nameValue = row.getCell(0).getStringCellValue();
            if (row != null && !nameValue.equals("")) {
                // TODO validation 추가
                member = new MemberDto();
                member.setName(nameValue);
                java.util.Date dateCellValue = row.getCell(1).getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String str = sdf.format(dateCellValue);
                member.setBirth(Date.valueOf(str));
                String genderStr = row.getCell(2).getStringCellValue();
                member.setGender(GenderEnum.getCodeByDescription(genderStr));
                member.setIsDeleted(0);
                memberInfo = new MemberAdditionalInformationDto();
                memberInfo.setContact(row.getCell(3).getStringCellValue());
                memberInfo.setAddress(row.getCell(4).getStringCellValue());

                MemberDao.getInstance().createMember(member, memberInfo, userId); // TODO 수정
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