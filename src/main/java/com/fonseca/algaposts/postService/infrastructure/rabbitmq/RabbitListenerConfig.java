package com.fonseca.algaposts.postService.infrastructure.rabbitmq;

import com.fonseca.algaposts.postService.api.model.ProcessingResultMessage;
import com.fonseca.algaposts.postService.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitListenerConfig {

    private final PostService postService;

    @RabbitListener(queues = RabbitConfig.RESULT_QUEUE)
    public void consumeProcessingMessage(@Payload ProcessingResultMessage result) {
        postService.updatePostInfo(result);
    }

}
