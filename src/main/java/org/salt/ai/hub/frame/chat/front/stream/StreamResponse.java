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

package org.salt.ai.hub.frame.chat.front.stream;

import lombok.extern.slf4j.Slf4j;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;

@Slf4j
public class StreamResponse {

    public static void responder(AiChatRequest aiChatRequest, AiChatResponse aiChatResponse, ResponseBodyEmitter emitter) {
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setSession(aiChatRequest.getSession());

        switch (AiChatCode.fromCode(aiChatResponse.getCode())) {
            case MESSAGE:
                StreamResponse.responseMessage(aiChatResponse, emitter);
                break;
            case ERROR:
                StreamResponse.responseFail(aiChatRequest, emitter, new Exception(aiChatResponse.getMessage()));
                break;
            case COMPLETE:
                StreamResponse.responseComplete(aiChatRequest, emitter);
                break;
            default:
        }
    }

    public static void responseMessage(AiChatResponse aiChatResponse, ResponseBodyEmitter emitter) {
        if (aiChatResponse != null) {
            aiChatResponse.setCode(AiChatCode.MESSAGE.getCode());
            aiChatResponse.setMessage(AiChatCode.MESSAGE.getMessage());
            String data = JsonUtil.toJson(aiChatResponse);
            if (data != null) {
                try {
                    emitter.send("data: " + data + "\n\n");
                } catch (IOException e) {
                    log.warn("Event send fail: {} {}, e:", AiChatCode.MESSAGE.getCode(), data, e);
                }
            }
        }
    }

    public static void responseFail(AiChatRequest aiChatRequest, ResponseBodyEmitter emitter, Exception e) {
        AiChatResponse aiChatResponse = new AiChatResponse();
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setCode(AiChatCode.ERROR.getCode());
        aiChatResponse.setMessage(AiChatCode.ERROR.getMessage());
        String data = JsonUtil.toJson(aiChatResponse);
        if (data != null) {
            try {
                emitter.send("data: " + data + "\n\n");
            } catch (IOException ee) {
                log.warn("Event send error fail: {} {}, exception: {}", AiChatCode.MESSAGE.getCode(), data, ee.getMessage());
            }
        }
    }

    public static void responseComplete(AiChatRequest aiChatRequest, ResponseBodyEmitter emitter) {
        AiChatResponse aiChatResponse = new AiChatResponse();
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setCode(AiChatCode.COMPLETE.getCode());
        aiChatResponse.setMessage(AiChatCode.COMPLETE.getMessage());
        String data = JsonUtil.toJson(aiChatResponse);
        if (data != null) {
            try {
                emitter.send("data: " + data + "\n\n");
                emitter.complete();
            } catch (IOException ee) {
                log.warn("Event send complete fail: {} {}, exception: {}", AiChatCode.COMPLETE.getCode(), data, ee.getMessage());
            }
        }
    }
}

