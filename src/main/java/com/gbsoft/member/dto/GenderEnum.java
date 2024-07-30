package com.gbsoft.member.dto;

import java.util.Arrays;

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
        return Arrays.stream(values())
                .filter(genderEnum -> genderEnum.getCode().equals(code))
                .findFirst()
                .map(GenderEnum::getDescription)
                .orElse(null);
    }

    public static String getCodeByDescription(String description) {
        return Arrays.stream(values())
                .filter(genderEnum -> genderEnum.getDescription().equals(description))
                .findFirst()
                .map(GenderEnum::getCode)
                .orElse(null);
    }
}
