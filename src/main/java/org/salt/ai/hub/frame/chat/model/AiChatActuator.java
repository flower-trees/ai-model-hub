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

import org.salt.ai.hub.frame.chat.structs.dto.AiChatDto;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface AiChatActuator {
    void pursue(AiChatDto aiChatDto, Consumer<AiChatResponse> responder, BiConsumer<AiChatDto, AiChatResponse> callback);
    AiChatResponse pursueSyc(AiChatDto aiChatDto, Consumer<AiChatResponse> responder);
}
