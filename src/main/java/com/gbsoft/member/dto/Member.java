package com.gbsoft.member.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class Member {
    private Long id;
    private String name;
    private Date birth;
    private String gender;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp modifiedAt;
    private String modifiedBy;
    private Integer isDeleted;

    public Member() {
    }

    public Member(Long id, String name, Date birth, String gender, Timestamp createdAt, String createdBy, Timestamp modifiedAt, String modifiedBy, Integer isDeleted) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.isDeleted = isDeleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Timestamp modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birth=" + birth +
                ", gender='" + gender + '\'' +
                ", createdAt=" + createdAt +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedAt=" + modifiedAt +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
