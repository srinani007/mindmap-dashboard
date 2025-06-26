package com.mindmap.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class QuestionDTO {
    public Long id;
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    public boolean starred;
    public LocalDateTime timestamp;

    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Answer is required")
    private String answer;




    public QuestionDTO(Long id, Long topicId, String question, String answer, boolean starred, LocalDateTime timestamp) {
        this.id = id;
        this.topicId = topicId;
        this.question = question;
        this.answer = answer;
        this.starred = starred;
        this.timestamp = timestamp;
    }

    public QuestionDTO() {
        // Default constructor for deserialization
    }

}
