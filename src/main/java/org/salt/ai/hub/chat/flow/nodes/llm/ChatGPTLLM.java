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

package org.salt.ai.hub.chat.flow.nodes.llm;

import org.salt.ai.hub.ai.models.chatgpt.ChatGPTActuator;
import org.salt.ai.hub.ai.models.enums.VendorType;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.springframework.beans.factory.annotation.Autowired;

@NodeIdentity(nodeId = "chatGPTLLM")
public class ChatGPTLLM extends FlowNodeWithReturn<AiChatResponse> {

    @Autowired
    ChatGPTActuator chatGPTActuator;

    @Override
    public AiChatResponse doProcess(IContextBus iContextBus) {

        //Init param
        AiChatRequest aiChatRequest = (AiChatRequest) iContextBus.getParam();
        AiChatDto aiChatDto = (AiChatDto) iContextBus.getTransmitInfo(AiChatDto.class.getName());

        aiChatDto.setVendor(VendorType.CHATGPT.getCode());
        aiChatDto.setModel("gpt-3.5-turbo");

        return chatGPTActuator.pursueSyc(aiChatDto, aiChatRequest.getResponder());
    }
}
