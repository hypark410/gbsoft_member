package com.gbsoft.member.dao;

import com.gbsoft.member.config.DbConfig;
import com.gbsoft.member.dto.Member;
import com.gbsoft.member.dto.MemberAdditionalInformation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {
    private static MemberDao instance = null;

    public MemberDao() {
    }

    public static MemberDao getInstance() {
        if (instance == null) {
            instance = new MemberDao();
        }
        return instance;
    }

    public List<Member> getMemberList() {
        List<Member> memberList = new ArrayList<>();
        Connection conn = DbConfig.getInstance().sqlLogin();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("select * from member");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong(1));
                member.setName(rs.getString(2));

                member.setBirth(rs.getDate(3));
                member.setGender(rs.getString(4));
                member.setCreatedAt(rs.getTimestamp(5));
                member.setCreatedBy(rs.getString(6));
                member.setModifiedAt(rs.getTimestamp(7));
                member.setModifiedBy(rs.getString(8));
                member.setIsDeleted(rs.getInt(9));
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return memberList;
    }

    //    public void createMember(Member member, MemberAdditionalInformation memberInfo) {
//        Connection conn = DbConfig.getInstance().sqlLogin();
//        PreparedStatement pstmt = null;
//        String memberSql = "INSERT INTO member VALUES (NEXTVAL(member_seq),?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,0)";
//
//        try {
//            pstmt = conn.prepareStatement(memberSql);
//            pstmt.setString(1, member.getName());
//            pstmt.setDate(2, member.getBirth());
//            pstmt.setString(3, member.getGender());
//            pstmt.setString(4, member.getCreatedBy());
//            pstmt.setString(5, member.getModifiedBy());
//            pstmt.executeUpdate();
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                pstmt.close();
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//    public void createMemberInfo(MemberAdditionalInformation memberInfo) {
//        Connection conn = DbConfig.getInstance().sqlLogin();
//        PreparedStatement pstmt = null;
//        String memberInfoSql = "INSERT INTO member_additional_information VALUES (NEXTVAL(member_info_seq),?,?,?,?,?,?,?)";
//
//        try {
//            pstmt = conn.prepareStatement(memberInfoSql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                pstmt.close();
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

    public void createMember(Member member, MemberAdditionalInformation memberInfo) {
        try (Connection connection = DbConfig.getInstance().sqlLogin()) {
            connection.setAutoCommit(false);

            String memberSql = "INSERT INTO member (name, birth, gender, created_at, created_by, modified_at, modified_by, is_deleted) VALUES (?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,0)";

            try (PreparedStatement psMember = connection.prepareStatement(memberSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psMember.setString(1, member.getName());
                psMember.setDate(2, member.getBirth());
                psMember.setString(3, member.getGender());
                psMember.setString(4, member.getCreatedBy());
                psMember.setString(5, member.getModifiedBy());
                psMember.executeUpdate();

                try (ResultSet rs = psMember.getGeneratedKeys()) {
                    if (rs.next()) {
                        long memberId = rs.getLong(1);
                        String memberInfoSql = "INSERT INTO member_additional_information (member_id, contact, address, created_at, created_by, modified_at, modified_by) VALUES (?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?)";

                        try (PreparedStatement psMemberInfo = connection.prepareStatement(memberInfoSql)) {
                            psMemberInfo.setLong(1, memberId);
                            psMemberInfo.setString(2, memberInfo.getContact());
                            psMemberInfo.setString(3, memberInfo.getAddress());
                            psMemberInfo.setString(4, memberInfo.getCreatedBy());
                            psMemberInfo.setString(5, memberInfo.getModifiedBy());
                            psMemberInfo.executeUpdate();
                        }
                        connection.commit();
                    } else {
                        connection.rollback();
                        throw new SQLException("member 테이블 ID 생성 실패");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
