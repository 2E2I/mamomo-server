package com.hsu.mamomo.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
@Document(indexName = "campaigns")
@Entity
public class Campaign {

    @Nullable
    @Id
    private String id;

    @Field(name = "site_type", type = FieldType.Keyword)
    private String siteType;

    @Field(name = "url", type = FieldType.Keyword)
    private String url;

    @Field(name = "title", type = FieldType.Keyword)
    private String title;

    @ElementCollection
    @Field(name = "tags", type = FieldType.Keyword)
    private List<String> tags;

    @Field(name = "body", type = FieldType.Keyword)
    private String body;

    @Field(name = "organization_name", type = FieldType.Keyword)
    private String organizationName;

    @Field(name = "thumbnail", type = FieldType.Keyword)
    private String thumbnail;

    // 편의상 String으로 설정
    @Field(name = "due_date", type = FieldType.Keyword)
    private String dueDate;

    @Field(name = "start_date", type = FieldType.Keyword)
    private String startDate;

    @Field(name = "target_price", type = FieldType.Keyword)
    private String targetPrice;

    @Field(name = "status_price", type = FieldType.Keyword)
    private String statusPrice;

    @Field(name = "percent", type = FieldType.Keyword)
    private String percent;

}
