package com.smarthire.repository;

import com.smarthire.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByConversationId(String conversationId);
    List<Conversation> findByRecruiterEmailOrderByUpdatedAtDesc(String recruiterEmail);
    List<Conversation> findAllByOrderByUpdatedAtDesc();
    void deleteByConversationId(String conversationId);
}
