package com.gbsoft.member.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConfig {

    private static DbConfig instance = null;
    public static String driver = null;
    public static String url = null;
    public static String user = null;
    public static String password = null;

    private DbConfig() {
    }

    public static DbConfig getInstance() {
        if (instance == null) {
            instance = new DbConfig();
        }
        return instance;
    }

    public Connection sqlLogin() {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DB 연결 실패. DB의 아이디 비밀번호가 Config 클래스와 일치하는지 확인해주세요.");
        }
        return conn;
    }
}