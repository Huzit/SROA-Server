package com.project.sroa.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long evaluationNum;
    private Timestamp writeDate;
    private String content;
    private Integer score;

    @OneToOne
    @JoinColumn(name = "scheduleNum")
    private Schedule schedule;

    @Builder
    public Evaluation(Timestamp writeDate, String content, Integer score, Schedule schedule) {
        this.writeDate = writeDate;
        this.content = content;
        this.score = score;
        this.schedule = schedule;
    }
}
