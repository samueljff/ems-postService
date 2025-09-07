package com.fonseca.algaposts.postService.domain.repository;

import com.fonseca.algaposts.postService.domain.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}
