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

package org.salt.ai.hub.chat.flow.nodes.common;

import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNode;
import org.salt.function.flow.node.register.NodeIdentity;

import java.util.ArrayList;

@NodeIdentity(nodeId = "paramBuilder")
public class ParamBuilder extends FlowNode {

    @Override
    public void process(IContextBus iContextBus) {

        //Init param
        AiChatRequest aiChatRequest = (AiChatRequest) iContextBus.getParam();

        AiChatDto aiChatDto = new AiChatDto();

        aiChatDto.setId(aiChatRequest.getId());
        aiChatDto.setSession(aiChatRequest.getSession());
        aiChatDto.setAgent(aiChatRequest.getAgent());

        if (aiChatDto.getMessages() == null) {
            aiChatDto.setMessages(new ArrayList<>());
        }

        iContextBus.putTransmitInfo(AiChatDto.class.getName(), aiChatDto);
    }
}
