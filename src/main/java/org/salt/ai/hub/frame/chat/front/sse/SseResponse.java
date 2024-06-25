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

package org.salt.ai.hub.frame.chat.front.sse;

import lombok.extern.slf4j.Slf4j;
import org.salt.ai.hub.frame.chat.front.sse.enums.EventName;
import org.salt.ai.hub.frame.chat.structs.enums.AiChatCode;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatResponse;
import org.salt.ai.hub.frame.utils.JsonUtil;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public class SseResponse {

    public static void responder(AiChatRequest aiChatRequest, AiChatResponse aiChatResponse, SseEmitter sseEmitter) {
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setSession(aiChatRequest.getSession());

        switch (AiChatCode.fromCode(aiChatResponse.getCode())) {
            case MESSAGE:
                SseResponse.responseMessage(aiChatResponse, sseEmitter);
                break;
            case ERROR:
                SseResponse.responseFail(aiChatRequest, sseEmitter, new Exception(aiChatResponse.getMessage()));
                break;
            case COMPLETE:
                SseResponse.responseComplete(aiChatRequest, sseEmitter);
                break;
            default:

        }
    }

    public static void responseMessage(AiChatResponse aiChatResponse, SseEmitter sseEmitter) {
        if (aiChatResponse != null) {
            aiChatResponse.setCode(AiChatCode.MESSAGE.getCode());
            aiChatResponse.setMessage(AiChatCode.MESSAGE.getMessage());
            String data = JsonUtil.toJson(aiChatResponse);
            if (data != null) {
                try {
                    sseEmitter.send(SseEmitter.event().name(EventName.MESSAGE.getCode()).data(data));
                } catch (Exception e) {
                    log.warn("event send fail:{} {}, e:", EventName.MESSAGE.getCode(), data, e);
                }
            }
        }
    }

    public static void responseFail(AiChatRequest aiChatRequest, SseEmitter sseEmitter, Exception e) {
        AiChatResponse aiChatResponse = new AiChatResponse();
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setCode(AiChatCode.ERROR.getCode());
        aiChatResponse.setMessage(AiChatCode.ERROR.getMessage());
        String data = JsonUtil.toJson(aiChatResponse);
        try {
            assert data != null;
            sseEmitter.send(SseEmitter.event().name(EventName.ERROR.getCode()).data(data));
            sseEmitter.completeWithError(e);
        } catch (Exception ee) {
            log.warn("event send error fail:{} {}, exception:{}", EventName.MESSAGE.getCode(), data, ee.getMessage());
        }
    }

    public static void responseComplete(AiChatRequest aiChatRequest, SseEmitter sseEmitter) {
        AiChatResponse aiChatResponse = new AiChatResponse();
        aiChatResponse.setId(aiChatRequest.getId());
        aiChatResponse.setCode(AiChatCode.COMPLETE.getCode());
        aiChatResponse.setMessage(AiChatCode.COMPLETE.getMessage());
        String data = JsonUtil.toJson(aiChatResponse);
        try {
            assert data != null;
            sseEmitter.send(SseEmitter.event().name(EventName.COMPLETE.getCode()).data(data));
            sseEmitter.complete();
        } catch (Exception ee) {
            log.warn("event send complete fail:{} {}, exception:{}", EventName.COMPLETE.getCode(), data, ee.getMessage());
        }
    }
}
