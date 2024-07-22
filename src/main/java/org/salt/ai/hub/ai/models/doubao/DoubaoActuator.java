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

package org.salt.ai.hub.ai.models.doubao;

import org.salt.ai.hub.ai.models.doubao.dto.DoubaoRequest;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.model.AiChatActuator;
import org.salt.ai.hub.frame.chat.client.stream.HttpStreamClient;
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
public class DoubaoActuator implements AiChatActuator {

    @Value("${models.doubao.chat-url}")
    private String chatUrl;

    @Value("${models.doubao.chat-key}")
    private String chatKey;

    @Autowired
    HttpStreamClient commonHttpClient;

    @Override
    public void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);

        DoubaoRequest doubaoRequest = convert(aiChatDto);

        commonHttpClient.call(chatUrl, JsonUtil.toJson(doubaoRequest), headers, List.of(new DoubaoListener(aiChatDto, responder, callback)));
    }

    @Override
    public AiChatResponse pursueSyc(AiChatDto aiChatDto, Consumer<AiChatResponse> responder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);

        DoubaoRequest doubaoRequest = convert(aiChatDto);

        AtomicReference<AiChatResponse> r = new AtomicReference<>();
        commonHttpClient.request(chatUrl, JsonUtil.toJson(doubaoRequest), headers, List.of(new DoubaoListener(aiChatDto, responder, (aiChatDto1, aiChatResponse) -> { r.set(aiChatResponse); })));
        return r.get();
    }

    public static DoubaoRequest convert(AiChatDto aiChatDto) {
        DoubaoRequest doubaoRequest = new DoubaoRequest();
        doubaoRequest.setModel(aiChatDto.getModel());
        doubaoRequest.setStream(aiChatDto.isStream());

        List<DoubaoRequest.Message> doubaoMessages = aiChatDto.getMessages().stream()
                .map(DoubaoActuator::convertMessage)
                .collect(Collectors.toList());
        doubaoRequest.setMessages(doubaoMessages);

        return doubaoRequest;
    }

    private static DoubaoRequest.Message convertMessage(AiChatDto.Message aiChatMessage) {
        DoubaoRequest.Message doubaoMessage = new DoubaoRequest.Message();
        doubaoMessage.setRole(aiChatMessage.getRole());
        doubaoMessage.setContent(aiChatMessage.getContent());
        return doubaoMessage;
    }
}
