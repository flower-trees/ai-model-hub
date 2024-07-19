/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.salt.ai.hub.ai.models.aliyun;

import org.salt.ai.hub.ai.models.aliyun.dto.AliyunRequest;
import org.salt.ai.hub.frame.chat.client.stream.HttpStreamClient;
import org.salt.ai.hub.frame.chat.model.AiChatActuator;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class AliyunActuator implements AiChatActuator {

    @Value("${models.aliyun.chat-url}")
    private String chatUrl;

    @Value("${models.aliyun.chat-key}")
    private String chatKey;

    @Autowired
    HttpStreamClient commonHttpClient;

    @Override
    public void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);
        headers.put("X-DashScope-SSE", "enable");

        AliyunRequest aliyunRequest = convert(aiChatDto);

        commonHttpClient.call(chatUrl, JsonUtil.toJson(aliyunRequest), headers, List.of(new AliyunListener(aiChatDto, responder, callback)));
    }

    @Override
    public AiChatResponse pursueSyc(AiChatDto aiChatDto, Consumer<AiChatResponse> responder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);
        headers.put("X-DashScope-SSE", "enable");

        AliyunRequest aliyunRequest = convert(aiChatDto);

        AtomicReference<AiChatResponse> r = new AtomicReference<>();
        commonHttpClient.request(chatUrl, JsonUtil.toJson(aliyunRequest), headers, List.of(new AliyunListener(aiChatDto, responder, (aiChatDto1, aiChatResponse) -> { r.set(aiChatResponse); })));
        return r.get();
    }

    public static AliyunRequest convert(AiChatDto aiChatDto) {
        AliyunRequest request = new AliyunRequest();
        request.setModel(aiChatDto.getModel());
        request.setInput(new AliyunRequest.Input());

        List<AliyunRequest.Message> messages = aiChatDto.getMessages().stream()
                .map(AliyunActuator::convertMessage)
                .collect(Collectors.toList());
        request.getInput().setMessages(messages);

        request.setParameters(new AliyunRequest.Parameters());
        request.getParameters().setIncrementalOutput(true);

        return request;
    }

    private static AliyunRequest.Message convertMessage(AiChatDto.Message aiChatMessage) {
        AliyunRequest.Message message = new AliyunRequest.Message();
        message.setRole(aiChatMessage.getRole());
        message.setContent(aiChatMessage.getContent());
        return message;
    }
}
