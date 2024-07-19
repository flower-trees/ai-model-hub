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

import org.salt.ai.hub.data.service.ChatHisService;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.service.SessionService;
import org.salt.ai.hub.data.vo.ChatHisVo;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.data.vo.SessionVo;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.IdsUtil;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.springframework.beans.factory.annotation.Autowired;

@NodeIdentity(nodeId = "chatSaver")
public class ChatSaver  extends FlowNodeWithReturn<AiChatResponse> {

    @Autowired
    SessionService sessionService;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatHisService chatHisService;

    @Override
    public AiChatResponse doProcess(IContextBus iContextBus) {

        //Init param
        AiChatResponse aiChatResponse = (AiChatResponse) iContextBus.getPreResult();
        AiChatDto aiChatDto = (AiChatDto) iContextBus.getTransmitInfo(AiChatDto.class.getName());

        if (!AiChatCode.ERROR.equalsV(aiChatResponse.getCode())) {

            //Save conversation records
            SessionVo sessionVo = sessionService.load(aiChatResponse.getSession());
            if (sessionVo == null) {
                sessionVo = new SessionVo();
                sessionVo.setSessionId(aiChatResponse.getSession());
                sessionVo.setAgentId(aiChatResponse.getAgent());
                sessionService.create(sessionVo);
            }

            String question = aiChatDto.getMessages().get(aiChatDto.getMessages().size() - 1).getContent();
            String answer = JsonUtil.toJson(aiChatResponse.getMessages());

            //Save chat records
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

            //Save chat his records
            ChatHisVo chatHisVo = new ChatHisVo();
            chatHisVo.setSessionId(aiChatResponse.getSession());
            chatHisVo.setChatId(aiChatResponse.getId());
            chatHisVo.setChatHisId(IdsUtil.newChatId());
            chatHisVo.setQuestion(question);
            chatHisVo.setAnswer(answer);
            chatHisService.create(chatHisVo);
        }

        return aiChatResponse;
    }
}
