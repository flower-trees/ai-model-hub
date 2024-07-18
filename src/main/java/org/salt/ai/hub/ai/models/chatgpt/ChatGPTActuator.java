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

package org.salt.ai.hub.ai.models.chatgpt;

import org.salt.ai.hub.ai.models.chatgpt.dto.ChatGPTRequest;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ChatGPTActuator implements AiChatActuator {

    @Value("${models.chatgpt.chat-url}")
    private String chatUrl;

    @Value("${models.chatgpt.chat-key}")
    private String chatKey;

    @Autowired
    HttpStreamClient chatGPTHttpClient;

    @Override
    public void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);

        ChatGPTRequest chatGPTRequest = convert(aiChatDto);

        chatGPTHttpClient.call(chatUrl, JsonUtil.toJson(chatGPTRequest), headers, List.of(new ChatGPTListener(aiChatDto, responder, callback)));
    }

    public static ChatGPTRequest convert(AiChatDto aiChatDto) {
        ChatGPTRequest chatGPTRequest = new ChatGPTRequest();
        chatGPTRequest.setModel(aiChatDto.getModel());
        chatGPTRequest.setStream(aiChatDto.isStream());

        List<ChatGPTRequest.Message> chatGPTMessages = aiChatDto.getMessages().stream()
                .map(ChatGPTActuator::convertMessage)
                .collect(Collectors.toList());
        chatGPTRequest.setMessages(chatGPTMessages);

        return chatGPTRequest;
    }

    private static ChatGPTRequest.Message convertMessage(AiChatDto.Message aiChatMessage) {
        ChatGPTRequest.Message chatGPTMessage = new ChatGPTRequest.Message();
        chatGPTMessage.setRole(aiChatMessage.getRole());
        chatGPTMessage.setContent(aiChatMessage.getContent());
        return chatGPTMessage;
    }
}
