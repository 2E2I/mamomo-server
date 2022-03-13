package com.hsu.mamomo.document;

import static org.springframework.restdocs.snippet.Attributes.key;

import org.springframework.restdocs.snippet.Attributes;

public interface DocumentFormatGenerator {

    static Attributes.Attribute getSortFormat() {
        return key("format").value("{정렬대상}, {정렬방법}");
    }

    static Attributes.Attribute getCategoryFormat() {
        return key("format").value("{카테고리 이름}");
    }


}
