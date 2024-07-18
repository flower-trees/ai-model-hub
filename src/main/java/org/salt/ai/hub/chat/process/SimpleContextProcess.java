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

package org.salt.ai.hub.chat.process;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.salt.ai.hub.ai.models.enums.VendorType;
import org.salt.ai.hub.data.service.AgentService;
import org.salt.ai.hub.data.service.ChatHisService;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.service.SessionService;
import org.salt.ai.hub.data.vo.AgentVo;
import org.salt.ai.hub.data.vo.ChatHisVo;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.data.vo.SessionVo;
import org.salt.ai.hub.frame.chat.process.ChatProcess;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.enums.MessageType;
import org.salt.ai.hub.frame.chat.structs.enums.RoleType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.IdsUtil;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class SimpleContextProcess implements ChatProcess<String> {

    @Autowired
    AgentService agentService;

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatHisService chatHisService;

    @Override
    public AiChatDto executeUp(AiChatRequest aiChatRequest) {

        AiChatDto aiChatDto = new AiChatDto();

        aiChatDto.setId(aiChatRequest.getId());
        aiChatDto.setSession(aiChatRequest.getSession());
        aiChatDto.setAgent(aiChatRequest.getAgent());

        //Get the model manufacturer and model name, default chatgpt
        aiChatDto.setVendor(VendorType.CHATGPT.getCode());
        aiChatDto.setModel("gpt-3.5-turbo");
        if (StringUtils.isNotBlank(aiChatDto.getAgent())) {
            AgentVo agentVo = agentService.load(aiChatDto.getAgent());
            if (agentVo != null && StringUtils.isNotBlank(agentVo.getConfigs())) {
                AgentVo.Configs configs = JsonUtil.fromJson(agentVo.getConfigs(), AgentVo.Configs.class);
                if (configs != null) {
                    aiChatDto.setVendor(configs.getVendor());
                    aiChatDto.setModel(configs.getModel());
                }
            }
        }

        aiChatDto.setMessages(new ArrayList<>());

        //Add Context
        List<ChatVo> chatVoList = chatService.queryLastList(aiChatRequest.getSession(), 3);
        if (!CollectionUtils.isEmpty(chatVoList)) {
            chatVoList.forEach(chatVo -> {
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
            });
        }

        //Add user questions
        AiChatDto.Message message = new AiChatDto.Message();
        message.setRole(RoleType.USER.getCode());
        message.setContent(aiChatRequest.getContent());
        aiChatDto.getMessages().add(message);

        aiChatDto.setStream(true);

        return aiChatDto;
    }

    @Override
    public void executeDown(AiChatDto aiChatDto, AiChatResponse aiChatResponse) {
        if (!AiChatCode.ERROR.equalsV(aiChatResponse.getCode())) {

            //Save conversation records
            SessionVo sessionVo = sessionService.load(aiChatDto.getSession());
            if (sessionVo == null) {
                sessionVo = new SessionVo();
                sessionVo.setSessionId(aiChatResponse.getSession());
                sessionVo.setAgentId(aiChatResponse.getAgent());
                sessionService.create(sessionVo);
            }

            assert CollectionUtils.isEmpty(aiChatDto.getMessages());
            String question = aiChatDto.getMessages().get(aiChatDto.getMessages().size() - 1).getContent();
            String answer = JsonUtil.toJson(aiChatResponse.getMessages());

            ChatVo chatVo = chatService.load(aiChatResponse.getId());
            if (chatVo == null) {
                chatVo = new ChatVo();
                chatVo.setSessionId(aiChatResponse.getSession());
                chatVo.setChatId(aiChatResponse.getId());
                chatVo.setQuestion(question);
                chatVo.setAnswer(answer);
                chatService.create(chatVo);
            } else {
                chatVo.setQuestion(question);
                chatVo.setAnswer(answer);
                chatService.update(chatVo);
            }

            ChatHisVo chatHisVo = new ChatHisVo();
            chatHisVo.setSessionId(aiChatResponse.getSession());
            chatHisVo.setChatId(aiChatResponse.getId());
            chatHisVo.setChatHisId(IdsUtil.newChatId());
            chatHisVo.setQuestion(question);
            chatHisVo.setAnswer(answer);
            chatHisService.create(chatHisVo);
        }
    }
}
