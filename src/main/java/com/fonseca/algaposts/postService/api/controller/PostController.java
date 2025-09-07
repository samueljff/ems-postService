package com.fonseca.algaposts.postService.api.controller;

import com.fonseca.algaposts.postService.api.model.PostInput;
import com.fonseca.algaposts.postService.api.model.PostOutput;
import com.fonseca.algaposts.postService.api.model.PostSummaryOutput;
import com.fonseca.algaposts.postService.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostOutput> create(@RequestBody PostInput input) {
        PostOutput output = postService.create(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostOutput> findById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.findById(postId));
    }

    @GetMapping
    public PagedModel<PostSummaryOutput> list(@PageableDefault Pageable pageable) {
        return postService.list(pageable);
    }
}