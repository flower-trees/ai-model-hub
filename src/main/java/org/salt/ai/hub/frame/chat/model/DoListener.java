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

package org.salt.ai.hub.frame.chat.model;

import org.apache.commons.lang3.StringUtils;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.enums.MessageType;
import org.salt.ai.hub.frame.chat.structs.enums.RoleType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class DoListener implements ListenerStrategy {

    protected AiChatDto aiChatDto;
    
    protected AiChatResponse response;
    protected StringBuilder msgCache = new StringBuilder();

    protected Consumer<AiChatResponse> responder;
    protected BiConsumer<AiChatDto, AiChatResponse> callback;

    protected boolean result = true;
    protected Throwable throwable;

    public DoListener(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {
        this.responder = responder;
        this.callback = callback;
        this.aiChatDto = aiChatDto;
        this.response = initResponse();
    }

    @Override
    public void onMessage(String msg) {
        //Convert msg to AiChatResponse
        AiChatResponse aiChatResponse = convertMsg(msg);

        //Collect spliced streaming answers
        if (!CollectionUtils.isEmpty(aiChatResponse.getMessages())) {
            AiChatResponse.Message message = aiChatResponse.getMessages().stream().filter(m -> MessageType.MARKDOWN.equalsV(m.getType())).findAny().orElse(null);
            if (message != null && StringUtils.isNotBlank((CharSequence) message.getContent())) {
                msgCache.append(message.getContent());
            }
        }

        //Send AiChatResponse back
        responder.accept(aiChatResponse);
    }

    protected abstract AiChatResponse convertMsg(String msg);

    public void onError(Throwable throwable) {

        this.result = false;
        this.throwable = throwable;

        AiChatResponse aiChatResponse = new AiChatResponse();
        aiChatResponse.setCode(AiChatCode.ERROR.getCode());
        aiChatResponse.setMessage(AiChatCode.ERROR.getMessage());
        this.responder.accept(aiChatResponse);

        response.setCode(AiChatCode.ERROR.getCode());
        response.setMessage(AiChatCode.ERROR.getMessage());
    }

    public void onComplete() {

        if (!AiChatCode.ERROR.equalsV(response.getCode())) {
            AiChatResponse aiChatResponse = new AiChatResponse();
            aiChatResponse.setCode(AiChatCode.COMPLETE.getCode());
            aiChatResponse.setMessage(AiChatCode.COMPLETE.getMessage());
            this.responder.accept(aiChatResponse);
        }

        if (!msgCache.isEmpty()) {
            AiChatResponse.Message message = new AiChatResponse.Message();
            message.setRole(RoleType.ASSISTANT.getCode());
            message.setType(MessageType.MARKDOWN.getCode());
            message.setContent(msgCache.toString());
            response.getMessages().add(message);
        }
        this.callback.accept(aiChatDto, response);
    }

    private AiChatResponse initResponse() {
        AiChatResponse response = new AiChatResponse();
        response.setId(aiChatDto.getId());
        response.setSession(aiChatDto.getSession());
        response.setAgent(aiChatDto.getAgent());
        //response.setMessages(List.of(new AiChatResponse.Message()));
        response.setMessages(new ArrayList<>());
        response.setCode(AiChatCode.MESSAGE.getCode());
        response.setMessage(AiChatCode.MESSAGE.getMessage());
        return response;
    }
}
