package com.fonseca.algaposts.postService.domain.service;

import com.fonseca.algaposts.postService.api.model.*;
import com.fonseca.algaposts.postService.domain.model.Post;
import com.fonseca.algaposts.postService.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.fonseca.algaposts.postService.infrastructure.rabbitmq.RabbitConfig.POST_QUEUE;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final AmqpTemplate amqpTemplate;

    @Transactional
    public PostOutput create(PostInput input) {
        log.info("Criando novo post - título: '{}', autor: '{}'", input.getTitle(), input.getAuthor());
        UUID id = UUID.randomUUID();
        Post post = new Post();
        post.setId(id);
        post.setTitle(input.getTitle());
        post.setBody(input.getBody());
        post.setAuthor(input.getAuthor());
        postRepository.saveAndFlush(post);

        try {
            postRepository.saveAndFlush(post);
            log.debug("Post salvo no banco - ID: {}", id);

            amqpTemplate.convertAndSend(POST_QUEUE, new TextProcessingMessageRequest(id, input.getBody()));
            log.debug("Post enviado para processamento - ID: {}", id);

            log.info("Post criado com sucesso - ID: {}", id);
            return new PostOutput(id, post.getTitle(), post.getBody(), post.getAuthor(), null, null);

        } catch (Exception e) {
            log.error("Erro ao criar post - título: '{}', erro: {}", input.getTitle(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updatePostInfo(ProcessingResultMessage result) {
        log.info("Atualizando post processado - ID: {}, palavras: {}, valor: {}",
                result.getPostId(), result.getWordCount(), result.getCalculatedValue());
        try {
            Post post = postRepository.findById(result.getPostId())
                    .orElseThrow(() -> {
                        log.warn("Post não encontrado para atualização - ID: {}", result.getPostId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND);
                    });

            post.setWordCount(result.getWordCount());
            post.setCalculatedValue(result.getCalculatedValue());
            postRepository.save(post);

            log.info("Post atualizado com sucesso - ID: {}", result.getPostId());

        } catch (Exception e) {
            log.error("Erro ao atualizar post - ID: {}, erro: {}", result.getPostId(), e.getMessage(), e);
            throw e;
        }
    }

    public PostOutput findById(UUID id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new PostOutput(post.getId(), post.getTitle(), post.getBody(), post.getAuthor(), post.getWordCount(), post.getCalculatedValue());
    }

    public PagedModel<PostSummaryOutput> list(Pageable pageable) {
        return new PagedModel<>(postRepository.findAll(pageable).map(post -> new PostSummaryOutput(
                post.getId(),
                post.getTitle(),
                summarize(post.getBody()),
                post.getAuthor()
        )));
    }

    private String summarize(String body) {
        return Arrays.stream(body.split("\\n")).limit(3).collect(Collectors.joining("\n"));
    }
}
