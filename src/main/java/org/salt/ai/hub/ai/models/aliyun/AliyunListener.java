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

package org.salt.ai.hub.ai.models.aliyun;

import org.jetbrains.annotations.NotNull;
import org.salt.ai.hub.ai.models.aliyun.dto.AliyunResponse;
import org.salt.ai.hub.ai.models.enums.VendorType;
import org.salt.ai.hub.frame.chat.model.DoListener;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.enums.MessageType;
import org.salt.ai.hub.frame.chat.structs.enums.RoleType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AliyunListener extends DoListener  {

    public AliyunListener(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {
        super(aiChatDto, responder, callback);
    }

    @Override
    protected AiChatResponse convertMsg(String msg) {
        AliyunResponse response = JsonUtil.fromJson(msg, AliyunResponse.class);
        if (response != null) {
            AiChatResponse aiChatResponse = new AiChatResponse();
            aiChatResponse.setVendor(VendorType.ALIYUN.getCode());
            aiChatResponse.setVendorId(response.getRequestId());
            //aiChatResponse.setVendorModel(response.getModel());
            List<AiChatResponse.Message> messages = getMessages(response);
            aiChatResponse.setMessages(messages);

            aiChatResponse.setCode(AiChatCode.MESSAGE.getCode());
            aiChatResponse.setMessage(AiChatCode.MESSAGE.getMessage());
            return aiChatResponse;
        }
        return null;
    }

    private static @NotNull List<AiChatResponse.Message> getMessages(AliyunResponse response) {
        List<AiChatResponse.Message> messages = new ArrayList<>();
        if (response.getOutput() != null) {
            AiChatResponse.Message message = new AiChatResponse.Message();
            message.setRole(RoleType.ASSISTANT.getCode());
            message.setContent(response.getOutput().getText());
            message.setType(MessageType.MARKDOWN.getCode());
            messages.add(message);
        }
        return messages;
    }
}
