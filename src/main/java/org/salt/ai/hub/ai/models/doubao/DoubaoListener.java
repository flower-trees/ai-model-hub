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

package org.salt.ai.hub.ai.models.doubao;

import org.jetbrains.annotations.NotNull;
import org.salt.ai.hub.ai.models.doubao.dto.DoubaoResponse;
import org.salt.ai.hub.ai.models.enums.VendorType;
import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.enums.MessageType;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.chat.model.DoListener;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DoubaoListener extends DoListener {

    public DoubaoListener(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback) {
        super(aiChatDto, responder, callback);
    }

    @Override
    protected AiChatResponse convertMsg(String msg) {
        DoubaoResponse response = JsonUtil.fromJson(msg, DoubaoResponse.class);
        if (response != null) {
            AiChatResponse aiChatResponse = new AiChatResponse();
            aiChatResponse.setVendor(VendorType.DOUBAO.getCode());
            aiChatResponse.setVendorId(response.getId());
            aiChatResponse.setVendorModel(response.getModel());
            List<AiChatResponse.Message> messages = getMessages(response);
            aiChatResponse.setMessages(messages);

            aiChatResponse.setCode(AiChatCode.MESSAGE.getCode());
            aiChatResponse.setMessage(AiChatCode.MESSAGE.getMessage());
            return aiChatResponse;
        }
        return null;
    }

    private static @NotNull List<AiChatResponse.Message> getMessages(DoubaoResponse response) {
        List<AiChatResponse.Message> messages = new ArrayList<>();
        if (!CollectionUtils.isEmpty(response.getChoices()) && response.getChoices().get(0).getDelta() != null) {
            AiChatResponse.Message message = new AiChatResponse.Message();
            DoubaoResponse.Choice.Delta delta = response.getChoices().get(0).getDelta();
            message.setRole(delta.getRole());
            message.setContent(delta.getContent());
            message.setType(MessageType.MARKDOWN.getCode());
            messages.add(message);
        }
        return messages;
    }
}
