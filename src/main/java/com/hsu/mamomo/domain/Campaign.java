package com.hsu.mamomo.domain;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.lang.Nullable;

@Getter
@Setter
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

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @ElementCollection
    @Field(name = "category")
    private List<String> category;

    @ElementCollection
    @Field(name = "tags")
    private List<String> tags;

    @Field(name = "body", type = FieldType.Text)
    private String body;

    @Field(name = "organization_name", type = FieldType.Keyword)
    private String organizationName;

    @Field(name = "thumbnail", type = FieldType.Keyword)
    private String thumbnail;

    // 편의상 String으로 설정
    @Field(name = "due_date", type = FieldType.Date)
    private LocalDate dueDate;

    @Field(name = "start_date", type = FieldType.Date)
    private LocalDate startDate;

    @Field(name = "target_price", type = FieldType.Long)
    private Long targetPrice;

    @Field(name = "status_price", type = FieldType.Long)
    private Long statusPrice;

    @Field(name = "percent", type = FieldType.Integer)
    private Integer percent;

    @Field(name = "heart_count", type = FieldType.Integer)
    private Integer heartCount;

    private Boolean isHeart = false;

}
