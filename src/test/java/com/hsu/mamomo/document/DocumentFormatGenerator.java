package com.hsu.mamomo.document;

import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.snippet.Attributes;

public interface DocumentFormatGenerator {

    static Attributes.Attribute getSortFormat() {
        return key("format").value("{정렬대상}, {정렬방법}");
    }

    static Attributes.Attribute getCategoryFormat() {
        return key("format").value("{카테고리 id}");
    }

    static Attributes.Attribute getUserEmailFormat() {
        return key("format").value("이메일 형식을 따라야 함");
    }

    static Attributes.Attribute getUserPasswordFormat() {
        return key("format").value("아직 형식 미정");
    }

    static Attributes.Attribute getUserNicknameFormat() {
        return key("format").value("닉네임은 한글, 영문, 숫자만 가능합니다.");
    }

    static Attributes.Attribute getUserSexFormat() {
        return key("format").value("성별은 M 또는 F 중 하나여야 합니다.");
    }

    static Attributes.Attribute getUserBirthFormat() {
        return key("format").value("날짜 형식은 [yyyy-mm-dd]입니다.");
    }

    static Attributes.Attribute getUserFavTopicFormat() {
        return key("format").value("1~10 사이의 Integer 배열");
    }


}
