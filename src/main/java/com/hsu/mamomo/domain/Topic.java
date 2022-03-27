package com.hsu.mamomo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Topic")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Topic {

    @Id
    private Long id;

    @Column(name = "topic_name")
    @NonNull
    private String topicName;

}
