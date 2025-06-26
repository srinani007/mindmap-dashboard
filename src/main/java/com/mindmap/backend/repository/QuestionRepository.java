package com.mindmap.backend.repository;

import com.mindmap.backend.model.Question;
import com.mindmap.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopicId(Long topicId);

    interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);
    }
}
