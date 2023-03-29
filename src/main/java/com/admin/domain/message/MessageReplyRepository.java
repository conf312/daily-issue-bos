package com.admin.domain.message;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReplyRepository extends JpaRepository<Message, Long> {
}
