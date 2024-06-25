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

package org.salt.ai.hub.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.salt.ai.hub.chat.service.ChatService;
import org.salt.ai.hub.frame.chat.front.stream.StreamResponse;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
@RequestMapping("/ai/stream")
@Slf4j
public class ChatStreamController {

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseBodyEmitter> command(@RequestBody AiChatRequest aiChatRequest) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter(300000L);

        try {
            chatService.hub(aiChatRequest, aiChatResponse -> {
                StreamResponse.responder(aiChatRequest, aiChatResponse, emitter);
            });
        } catch (Exception e) {
            log.error("ai models hub fail, e:", e);
            StreamResponse.responseFail(aiChatRequest, emitter, e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);

        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }
}
