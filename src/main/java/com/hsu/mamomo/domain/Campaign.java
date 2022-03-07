package com.hsu.mamomo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Primary;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
@Document(indexName = "campaigns")
@Entity
public class Campaign {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    private String siteType;
    private String url;
    private String title;
    @ElementCollection
    private List<String> tags;
    private String body;
    private String organizationName;
    private String thumbnail;

    // 편의상 String으로 설정
    // 자료형 후에 반드시 변경할것
    private String dueDate;
    private String startDate;
    private String targetPrice;
    private String statusPrice;
    private String percent;

}
