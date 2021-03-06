package com.hsu.mamomo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "banner")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "banner_id")
    @NonNull
    private String bannerId;

    @Column(name = "img")
    @NonNull
    private String img;

    @Column(name = "original_img")
    @NonNull
    private String originalImg;

    @Column(name = "url")
    private String url;

    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date;

    @Column(name = "site_type")
    @NonNull
    private String siteType;

    @Column(name = "title")
    @NonNull
    private String title;

    @Column(name = "info")
    @NonNull
    private String info;

    @Column(name = "width")
    @NonNull
    private String width;

    @Column(name = "height")
    @NonNull
    private String height;

    @Column(name = "bg_color1")
    @NonNull
    private String bgColor1;

    @Column(name = "bg_color2")
    @NonNull
    private String bgColor2;

    @Column(name = "text_color1")
    @NonNull
    private String textColor1;

    @Column(name = "text_color2")
    @NonNull
    private String textColor2;

    @Column(name = "text_color3")
    @NonNull
    private String textColor3;

    @Column(name = "text_font1")
    @NonNull
    private String textFont1;

    @Column(name = "text_font2")
    @NonNull
    private String textFont2;

    @Column(name = "text_font3")
    @NonNull
    private String textFont3;

}
