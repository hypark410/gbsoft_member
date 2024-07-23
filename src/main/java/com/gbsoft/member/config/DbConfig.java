package com.gbsoft.member.config;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;

public class DbConfig {

    private static DbConfig instance = null;

    private DbConfig() {
    }

    public static DbConfig getInstance() {
        if (instance == null) {
            instance = new DbConfig();
        }
        return instance;
    }

    private Connection conn = null;
    private String url = "jdbc:mariadb://localhost:3306/test_db?user=root&password=root"; // TODO user, pw 수정

    public Connection sqlLogin() {



        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DB 연결 실패. DB의 아이디 비밀번호가 Config 클래스와 일치하는지 확인해주세요.");
        }
        return conn;
    }
}