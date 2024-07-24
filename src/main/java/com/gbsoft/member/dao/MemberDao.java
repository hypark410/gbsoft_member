package com.gbsoft.member.dao;

import com.gbsoft.member.config.DbConfig;
import com.gbsoft.member.dto.GenderEnum;
import com.gbsoft.member.dto.MemberAdditionalInformationDto;
import com.gbsoft.member.dto.MemberDto;
import com.gbsoft.member.dto.MemberInputDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {
    private static MemberDao instance = null;
    private static String baseSql = "select\n" +
            "                m.id, m.name, m.birth, m.gender, mai.contact, mai.address, m.created_at, m.created_by, m.modified_at, m.modified_by\n" +
            "            from member m inner join member_additional_information mai on m.id = mai.member_id\n" +
            "            where m.is_deleted = 0";

    public MemberDao() {
    }

    public static MemberDao getInstance() {
        if (instance == null) {
            instance = new MemberDao();
        }
        return instance;
    }

    public List<MemberInputDto> getMemberList(int limit, int offset, String col, String sorting, String name, String gender) {
        List<MemberInputDto> memberList = new ArrayList<>();

        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            String sql = String.format(baseSql + " order by %s %s limit ? offset ?", col, sorting);
            if (!name.equals("") && !gender.equals("")) {
                sql = baseSql + " and m.name like \'%" + name + "%\' and m.gender = \'" + gender + "\' order by " + col + " " + sorting + " limit ? offset ?";
            } else if (!name.equals("")) {
                sql = baseSql + " and m.name like \'%" + name + "%\' order by " + col + " " + sorting + " limit ? offset ?";
            } else if (!gender.equals("")) {
                sql = String.format(baseSql + " and m.gender = '%s' order by %s %s limit ? offset ?", gender, col, sorting);
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                ps.setInt(2, offset);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        memberList.add(setMember(rs));
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

    public MemberInputDto getMember(Long id) {
        MemberInputDto member = null;
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            String sql = String.format(baseSql + " and m.id = %d", id);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        member = setMember(rs);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }

    private MemberInputDto setMember(ResultSet rs) throws SQLException {
        MemberInputDto member = new MemberInputDto();
        member.setId(rs.getLong(1));
        member.setName(rs.getString(2));
        member.setBirth(rs.getString(3));
        String s = rs.getString(4);
        member.setGender(GenderEnum.getDescriptionByCode(s));
        member.setContact(rs.getString(5));
        member.setAddress(rs.getString(6));
        member.setCreatedAt(rs.getTimestamp(7));
        member.setCreatedBy(rs.getString(8));
        member.setModifiedAt(rs.getTimestamp(9));
        member.setModifiedBy(rs.getString(10));
        return member;
    }

    public void createMember(MemberDto member, MemberAdditionalInformationDto memberInfo) {
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
                conn.commit();
                try (ResultSet rs = psMember.getGeneratedKeys()) {
                    long memberId = 0L;
                    if (rs.next()) {
                        memberId = rs.getLong(1);

                        MemberDto insertMember = getTimestamp(memberId);
                        if (insertMember == null) {
                            HardDeleteMember(memberId);
                            return;
                        }

                        Timestamp createdAt = insertMember.getCreatedAt();
                        String memberInfoSql = "INSERT INTO member_additional_information (member_id, contact, address, created_at, created_by, modified_at, modified_by) VALUES (?,?,?,?,?,?,?)";
                        try (PreparedStatement psMemberInfo = conn.prepareStatement(memberInfoSql)) {
                            psMemberInfo.setLong(1, memberId);
                            psMemberInfo.setString(2, memberInfo.getContact());
                            psMemberInfo.setString(3, memberInfo.getAddress());
                            psMemberInfo.setTimestamp(4, createdAt);
                            psMemberInfo.setString(5, memberInfo.getCreatedBy());
                            psMemberInfo.setTimestamp(6, createdAt);
                            psMemberInfo.setString(7, memberInfo.getModifiedBy());
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

    private MemberDto getTimestamp(Long id) {
        MemberDto member = null;
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            String sql = String.format("select * from member where id = %d", id);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        member = new MemberDto();
                        member.setId(rs.getLong(1));
                        member.setName(rs.getString(2));
                        member.setBirth(rs.getDate(3));
                        String s = rs.getString(4);
                        member.setGender(GenderEnum.getDescriptionByCode(s));
                        member.setCreatedAt(rs.getTimestamp(5));
                        member.setCreatedBy(rs.getString(6));
                        member.setModifiedAt(rs.getTimestamp(7));
                        member.setModifiedBy(rs.getString(8));
                        member.setIsDeleted(rs.getInt(9));
                        return member;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return member;
    }
    
    public void updateMember(MemberDto member, MemberAdditionalInformationDto memberInfo) {
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            Long id = member.getId();

            String name = member.getName();
            if (name != null) {
                conn.setAutoCommit(false);
                String sql = "update member set name = ? where id = ?";
                updateQuery(conn, sql, name, id);
            }

            String contact = memberInfo.getContact();
            if (contact != null) {
                conn.setAutoCommit(false);
                String sql = "update member_additional_information set contact = ? where member_id = ?";
                updateQuery(conn, sql, contact, id);
            }

            String address = memberInfo.getAddress();
            if (address != null) {
                conn.setAutoCommit(false);
                String sql = "update member_additional_information set address = ? where member_id = ?";
                updateQuery(conn, sql, address, id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateQuery(Connection conn, String sql, String value, Long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setLong(2, id);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    public void updateTime(Long id) {
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            conn.setAutoCommit(false);
            String sql = "update member m, member_additional_information mai set m.modified_at = CURRENT_TIMESTAMP, mai.modified_at = CURRENT_TIMESTAMP where m.id = ? and mai.member_id = ?;";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setLong(2, id);
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int HardDeleteMember(Long id) {
        int result = 0;
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            conn.setAutoCommit(false);

            String sql = "delete from member where id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                result = ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int SoftDeleteMember(Long id) {
        int result = 0;
        try (Connection conn = DbConfig.getInstance().sqlLogin()) {
            conn.setAutoCommit(false);

            String sql = "";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                result = ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
