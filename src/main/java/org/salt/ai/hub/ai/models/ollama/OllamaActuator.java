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

package org.salt.ai.hub.ai.models.ollama;

import org.salt.ai.hub.ai.models.ollama.dto.OllamaRequest;
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
public class OllamaActuator implements AiChatActuator {

    @Value("${models.ollama.chat-url}")
    private String chatUrl;

    @Value("${models.ollama.chat-key}")
    private String chatKey;

    @Autowired
    HttpStreamClient commonHttpClient;

    @Override
    public void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);

        OllamaRequest request = convert(aiChatDto);

        commonHttpClient.call(chatUrl, JsonUtil.toJson(request), headers, List.of(new OllamaListener(aiChatDto, responder, callback)));
    }

    public static OllamaRequest convert(AiChatDto aiChatDto) {
        OllamaRequest request = new OllamaRequest();
        request.setModel(aiChatDto.getModel());
        request.setStream(aiChatDto.isStream());

        List<OllamaRequest.Message> doubaoMessages = aiChatDto.getMessages().stream()
                .map(OllamaActuator::convertMessage)
                .collect(Collectors.toList());
        request.setMessages(doubaoMessages);
        OllamaRequest.Options options = new OllamaRequest.Options();
        options.setTemperature(0.3);
        request.setOptions(options);

        return request;
    }

    private static OllamaRequest.Message convertMessage(AiChatDto.Message aiChatMessage) {
        OllamaRequest.Message message = new OllamaRequest.Message();
        message.setRole(aiChatMessage.getRole());
        message.setContent(aiChatMessage.getContent());
        return message;
    }
}
