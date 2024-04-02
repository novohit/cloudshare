package com.cloudshare.server.openai.web.controller;

import cn.hutool.core.util.RandomUtil;
import com.cloudshare.server.model.FileDocument;
import com.cloudshare.server.openai.api.OpenAiWebClient;
import com.cloudshare.server.openai.listener.OpenAISubscriber;
import com.cloudshare.server.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author niuxiangqian
 * @version 1.0
 * @date 2023/3/21 16:18
 **/
@Slf4j
@RestController
@RequestMapping("/openai")
@RequiredArgsConstructor
public class OpenAiController {
    private final OpenAiWebClient openAiWebClient;

    @Value("${authorization}")
    private String authorization;

    /**
     * 流式返回
     *
     * @param prompt 提示词
     * @param user   用户
     * @return
     */
    @GetMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletions(String prompt, String user) {
        Assert.hasLength(user, "user不能为空");
        Assert.hasLength(prompt, "prompt不能为空");
        checkContent(prompt);

        return Flux.create(emitter -> {
            OpenAISubscriber subscriber = new OpenAISubscriber(emitter);
            Flux<String> openAiResponse =
                    openAiWebClient.getChatResponse(authorization, user, prompt, null, null, null);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    @PostMapping(value = "/completions/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletions(@RequestBody String prompt) {
        Assert.hasLength(prompt, "prompt不能为空");
        checkContent(prompt);

        return Flux.create(emitter -> {
            OpenAISubscriber subscriber = new OpenAISubscriber(emitter);
            Flux<String> openAiResponse =
                    openAiWebClient.getChatResponse(authorization, RandomUtil.randomString(16), prompt, null, null, null);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    @PostMapping(value = "/analyze-file", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> analyze(@RequestBody String content) {

        String prompt = """
                        我希望你担任高级数据分析人员。
                         现在我给出一段文本，详细的概括出这段文本的主要内容，
                               分点回复我，每一点换一行
                               文本是：               
                """ + content;

        Assert.hasLength(prompt, "prompt不能为空");
        checkContent(prompt);
        return Flux.create(emitter -> {
            OpenAISubscriber subscriber = new OpenAISubscriber(emitter);
            Flux<String> openAiResponse =
                    openAiWebClient.getChatResponse(authorization, RandomUtil.randomString(16), prompt, null, null, null);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }


    public void checkContent(String prompt) {
        Assert.isTrue(Boolean.FALSE.equals(openAiWebClient.checkContent(authorization, prompt).block()), "您输入的内容违规");
    }

}
