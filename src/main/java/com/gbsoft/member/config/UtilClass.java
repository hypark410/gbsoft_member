package com.gbsoft.member.config;

import jakarta.servlet.ServletContext;

public class UtilClass {

    public void setDbConfig(ServletContext sc) {
        DbConfig.driver = sc.getInitParameter("driver");
        DbConfig.url = sc.getInitParameter("url");
        DbConfig.user = sc.getInitParameter("user");
        DbConfig.password = sc.getInitParameter("password");
    }

}