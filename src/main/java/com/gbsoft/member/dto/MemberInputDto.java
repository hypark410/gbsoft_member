package com.gbsoft.member.dto;

public class MemberInputDto {
    private String name;
    private String birth;
    private String gender;
    private String contact;
    private String address;
    private String createdBy;
    private String modifiedBy;

    public MemberInputDto() {
    }

    public MemberInputDto(String name, String birth, String gender, String contact, String address, String createdBy, String modifiedBy) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "MemberInputDto{" +
                "name='" + name + '\'' +
                ", birth='" + birth + '\'' +
                ", gender='" + gender + '\'' +
                ", contact='" + contact + '\'' +
                ", address='" + address + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }
}
