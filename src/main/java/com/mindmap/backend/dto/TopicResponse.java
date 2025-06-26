package com.mindmap.backend.dto;

import java.util.List;

public record TopicResponse(
        Long id,
        String name,
        List<QuestionBrief> questions) {

    public record QuestionBrief(
            Long id,
            String question,
            String answer,      // 👈 ADD THIS
            boolean starred,
            String timestamp) {}
}
