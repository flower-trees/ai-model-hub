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

package org.salt.ai.hub.ai.models.moonshot;

import org.salt.ai.hub.ai.models.moonshot.dto.MoonshotRequest;
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
public class MoonshotActuator implements AiChatActuator {

    @Value("${models.moonshot.chat-url}")
    private String chatUrl;

    @Value("${models.moonshot.chat-key}")
    private String chatKey;

    @Autowired
    HttpStreamClient commonHttpClient;

    @Override
    public void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + chatKey);

        MoonshotRequest request = convert(aiChatDto);

        commonHttpClient.call(chatUrl, JsonUtil.toJson(request), headers, List.of(new MoonshotListener(aiChatDto, responder, callback)));
    }

    public static MoonshotRequest convert(AiChatDto aiChatDto) {
        MoonshotRequest request = new MoonshotRequest();
        request.setModel(aiChatDto.getModel());
        request.setStream(aiChatDto.isStream());

        List<MoonshotRequest.Message> doubaoMessages = aiChatDto.getMessages().stream()
                .map(MoonshotActuator::convertMessage)
                .collect(Collectors.toList());
        request.setMessages(doubaoMessages);
        request.setTemperature(0.3);

        return request;
    }

    private static MoonshotRequest.Message convertMessage(AiChatDto.Message aiChatMessage) {
        MoonshotRequest.Message message = new MoonshotRequest.Message();
        message.setRole(aiChatMessage.getRole());
        message.setContent(aiChatMessage.getContent());
        return message;
    }
}
