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

package org.salt.ai.hub.chat.flow.nodes.data;

import com.fasterxml.jackson.core.type.TypeReference;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.MessageType;
import org.salt.ai.hub.frame.chat.structs.enums.RoleType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@NodeIdentity(nodeId = "contextBuilder")
public class ContextBuilder extends FlowNodeWithReturn<AiChatDto> {

    @Autowired
    ChatService chatService;

    @Override
    public AiChatDto doProcess(IContextBus iContextBus) {

        //Init param
        AiChatRequest aiChatRequest = (AiChatRequest) iContextBus.getParam();
        AiChatDto aiChatDto = (AiChatDto) iContextBus.getTransmitInfo(AiChatDto.class.getName());

        //Query and Add Context
        List<ChatVo> chatVoList = chatService.queryLastList(aiChatRequest.getSession(), 3);
        if (!CollectionUtils.isEmpty(chatVoList)) {
            Collections.reverse(chatVoList);
            chatVoList.forEach(chatVo -> {
                if (!chatVo.getChatId().equals(aiChatDto.getId())) {
                    AiChatDto.Message messageUser = new AiChatDto.Message();
                    messageUser.setRole(RoleType.USER.getCode());
                    messageUser.setContent(chatVo.getQuestion());
                    aiChatDto.getMessages().add(messageUser);

                    List<AiChatResponse.Message> messages = JsonUtil.fromJson(chatVo.getAnswer(), new TypeReference<>() {});
                    if (!CollectionUtils.isEmpty(messages)) {
                        AiChatResponse.Message messageMarkdown = messages.stream().filter(message -> MessageType.MARKDOWN.equalsV(message.getType())).findFirst().orElse(null);
                        if (messageMarkdown != null) {
                            AiChatDto.Message message = new AiChatDto.Message();
                            message.setRole(RoleType.ASSISTANT.getCode());
                            message.setContent((String) messageMarkdown.getContent());
                            aiChatDto.getMessages().add(message);
                        }
                    }
                }
            });
        }

        //Add user questions
        AiChatDto.Message message = new AiChatDto.Message();
        message.setRole(RoleType.USER.getCode());
        message.setContent(aiChatRequest.getContent());
        aiChatDto.getMessages().add(message);

        return aiChatDto;
    }
}
