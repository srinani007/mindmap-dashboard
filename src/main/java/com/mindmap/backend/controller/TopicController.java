package com.mindmap.backend.controller;

import java.util.List;

import com.mindmap.backend.dto.QuestionDTO;
import com.mindmap.backend.dto.TopicDTO;
import com.mindmap.backend.dto.TopicResponse;
import com.mindmap.backend.model.Topic;
import com.mindmap.backend.repository.QuestionRepository;
import com.mindmap.backend.repository.TopicRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "http://localhost:5173") // Adjust origin in production
public class TopicController {

    private final TopicRepository topicRepo;
    private final QuestionRepository questionRepo;

    public TopicController(TopicRepository topicRepo, QuestionRepository questionRepo) {
        this.topicRepo = topicRepo;
        this.questionRepo = questionRepo;
    }

    /** GET all topics with summarized questions */
    @GetMapping
    public List<TopicResponse> getAllTopics() {
        return topicRepo.findAll().stream()
                .map(t -> new TopicResponse(
                        t.getId(),
                        t.getName(),
                        t.getQuestions().stream()
                                .map(q -> new TopicResponse.QuestionBrief(
                                        q.getId(),
                                        q.getQuestion(),
                                        q.getAnswer(),
                                        q.isStarred(),
                                        q.getTimestamp() != null ? q.getTimestamp().toString() : ""
                                )).toList()
                )).toList();
    }

    /** POST a new topic */
    @PostMapping
    public TopicResponse createTopic(@RequestBody TopicDTO dto) {
        Topic topic = new Topic();
        topic.setName(dto.name);
        Topic saved = topicRepo.save(topic);
        System.out.println("🧪 Authenticated user: " + SecurityContextHolder.getContext().getAuthentication());
        return new TopicResponse(saved.getId(), saved.getName(), List.of());
    }

    @GetMapping("/{id}/questions")
    public List<QuestionDTO> getQuestionsByTopic(@PathVariable Long id) {
        return questionRepo.findByTopicId(id).stream()
                .map(q -> new QuestionDTO(
                        q.getId(),               // ✅ Question ID
                        id,                      // ✅ Topic ID
                        q.getQuestion(),
                        q.getAnswer(),
                        q.isStarred(),
                        q.getTimestamp()
                ))
                .toList();
    }


    /** PUT to update a topic name */
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(@PathVariable Long id, @RequestBody TopicDTO dto) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Topic not found"));

        topic.setName(dto.name);
        Topic updated = topicRepo.save(topic);
        return ResponseEntity.ok(new TopicResponse(updated.getId(), updated.getName(), List.of()));
    }

    /** DELETE a topic only if no questions exist */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("❌ Topic not found"));

        if (!topic.getQuestions().isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Cannot delete topic with existing questions.");
        }

        topicRepo.delete(topic);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/topics")
    public ResponseEntity<?> deleteAllTopics() {
        topicRepo.deleteAll();
        return ResponseEntity.ok("🗑️ All topics deleted successfully");
    }
}
