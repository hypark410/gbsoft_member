package com.gbsoft.member.dao;

import com.gbsoft.member.config.DbConfig;
import com.gbsoft.member.dto.Member;
import com.gbsoft.member.dto.MemberAdditionalInformation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Member> getMemberList(int limit, int offset, String col, String sorting, String name, String gender, String id) {
        List<Member> memberList = new ArrayList<>();

        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            String sql = String.format("select * from member order by %s %s limit ? offset ?", col, sorting);
            if (!name.equals("") && !gender.equals("")) {
                sql = "select * from member where name like \'%" + name + "%\' and gender = \'" + gender + "\' order by " + col + " " + sorting + " limit ? offset ?";
            } else if (!name.equals("")) {
                sql = "select * from member where name like \'%" + name + "%\' order by " + col + " " + sorting + " limit ? offset ?";
            } else if (!gender.equals("")) {
                sql = String.format("select * from member where gender = '%s' order by %s %s limit ? offset ?", gender, col, sorting);
            } else if (!id.equals("")) {
                sql = String.format("select * from member where id = %d", Integer.parseInt(id));
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                ps.setInt(2, offset);

                try (ResultSet rs = ps.executeQuery()) {
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
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return memberList;
    }

    public void createMember(Member member, MemberAdditionalInformation memberInfo) {
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            conn.setAutoCommit(false);

            String memberSql = "INSERT INTO member (name, birth, gender, created_at, created_by, modified_at, modified_by, is_deleted) VALUES (?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,0)";

            try (PreparedStatement psMember = conn.prepareStatement(memberSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

                        try (PreparedStatement psMemberInfo = conn.prepareStatement(memberInfoSql)) {
                            psMemberInfo.setLong(1, memberId);
                            psMemberInfo.setString(2, memberInfo.getContact());
                            psMemberInfo.setString(3, memberInfo.getAddress());
                            psMemberInfo.setString(4, memberInfo.getCreatedBy());
                            psMemberInfo.setString(5, memberInfo.getModifiedBy());
                            psMemberInfo.executeUpdate();
                        }
                        conn.commit();
                    } else {
                        conn.rollback();
                        throw new SQLException("member 테이블 ID 생성 실패");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
