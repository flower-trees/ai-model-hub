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

package org.salt.ai.hub.chat.service;

import org.apache.commons.lang3.StringUtils;
import org.salt.ai.hub.ai.models.aliyun.AliyunActuator;
import org.salt.ai.hub.ai.models.chatgpt.ChatGPTActuator;
import org.salt.ai.hub.ai.models.doubao.DoubaoActuator;
import org.salt.ai.hub.ai.models.enums.VendorType;
import org.salt.ai.hub.ai.models.moonshot.MoonshotActuator;
import org.salt.ai.hub.ai.models.ollama.OllamaActuator;
import org.salt.ai.hub.chat.process.SimpleContextProcess;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.IdsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class ChatHubService {

    @Autowired
    ChatGPTActuator chatGPTActuator;

    @Autowired
    DoubaoActuator doubaoActuator;

    @Autowired
    AliyunActuator aliyunActuator;

    @Autowired
    MoonshotActuator moonshotActuator;

    @Autowired
    OllamaActuator ollamaActuator;

    @Autowired
    SimpleContextProcess simpleContextProcess;

    public void hub(AiChatRequest aiChatRequest, Consumer<AiChatResponse> responder) {

        if (StringUtils.isBlank(aiChatRequest.getSession())) aiChatRequest.setSession(IdsUtil.newSessionId());
        if (StringUtils.isBlank(aiChatRequest.getId())) aiChatRequest.setId(IdsUtil.newChatId());

        AiChatDto aiChatDto = simpleContextProcess.executeUp(aiChatRequest);

        if (aiChatDto.getVendor().equals(VendorType.CHATGPT.getCode())) {
            chatGPTActuator.pursue(aiChatDto, responder, simpleContextProcess::executeDown);
        } else if (aiChatDto.getVendor().equals(VendorType.DOUBAO.getCode())) {
            doubaoActuator.pursue(aiChatDto, responder, simpleContextProcess::executeDown);
        } else if (aiChatDto.getVendor().equals(VendorType.ALIYUN.getCode())) {
            aliyunActuator.pursue(aiChatDto, responder, simpleContextProcess::executeDown);
        } else if (aiChatDto.getVendor().equals(VendorType.MOONSHOT.getCode())) {
            moonshotActuator.pursue(aiChatDto, responder, simpleContextProcess::executeDown);
        } else if (aiChatDto.getVendor().equals(VendorType.OLLAMA.getCode())) {
            ollamaActuator.pursue(aiChatDto, responder, simpleContextProcess::executeDown);
        }
    }
}
