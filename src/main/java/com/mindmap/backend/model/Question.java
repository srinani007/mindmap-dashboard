package com.mindmap.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private String answer;

    private boolean starred;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @PrePersist
    public void setDefaultTimestamp() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
