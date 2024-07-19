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

import org.salt.ai.hub.data.service.AgentService;
import org.salt.ai.hub.data.service.ChatHisService;
import org.salt.ai.hub.data.service.ChatService;
import org.salt.ai.hub.data.service.SessionService;
import org.salt.ai.hub.data.vo.AgentVo;
import org.salt.ai.hub.data.vo.ChatHisVo;
import org.salt.ai.hub.data.vo.ChatVo;
import org.salt.ai.hub.data.vo.SessionVo;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.IdsUtil;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.salt.function.flow.context.IContextBus;
import org.salt.function.flow.node.FlowNodeWithReturn;
import org.salt.function.flow.node.register.NodeIdentity;
import org.springframework.beans.factory.annotation.Autowired;

@NodeIdentity(nodeId = "agentConfiger")
public class AgentConfiger extends FlowNodeWithReturn<AiChatDto> {

    @Autowired
    AgentService agentService;

    @Override
    public AiChatDto doProcess(IContextBus iContextBus) {

        //Init param
        AiChatRequest aiChatRequest = (AiChatRequest) iContextBus.getParam();
        AiChatDto aiChatDto = (AiChatDto) iContextBus.getTransmitInfo(AiChatDto.class.getName());

        AgentVo agentVo = agentService.load(aiChatRequest.getAgent());
        AgentVo.Configs configs = JsonUtil.fromJson(agentVo.getConfigs(), AgentVo.Configs.class);

        assert configs != null;
        aiChatDto.setVendor(configs.getVendor());
        aiChatDto.setModel(configs.getModel());

        return aiChatDto;
    }
}
