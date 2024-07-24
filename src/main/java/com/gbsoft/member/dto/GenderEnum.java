package com.gbsoft.member.dto;

public enum GenderEnum {
    FEMALE("F", "여자"),
    MALE("M", "남자");

    private final String code;
    private final String description;

    GenderEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(String code) {
        for (GenderEnum genderEnum : GenderEnum.values()) {
            if (genderEnum.getCode().equals(code)) {
                return genderEnum.getDescription();
            }
        }
        return null;
    }
}
