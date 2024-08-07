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
import org.salt.ai.hub.chat.service.ChatHubService;
import org.salt.ai.hub.frame.chat.front.stream.StreamResponse;
import org.salt.ai.hub.frame.chat.structs.vo.AiChatRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
@RequestMapping("/ai/stream")
@Slf4j
public class ChatStreamFlowController {

    @Autowired
    private ChatHubService chatHubService;

    @PostMapping(value = "/chat/flow", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseBodyEmitter> flow(@RequestBody AiChatRequest aiChatRequest) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter(300000L);

        try {
            chatHubService.flow(aiChatRequest, aiChatResponse -> {
                StreamResponse.responder(aiChatRequest, aiChatResponse, emitter);
            });
        } catch (Exception e) {
            log.error("ai models flow fail, e:", e);
            StreamResponse.responseFail(aiChatRequest, emitter, e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);

        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/chat/flow/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseBodyEmitter> flowById(@PathVariable String id, @RequestBody AiChatRequest aiChatRequest) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter(300000L);

        try {
            chatHubService.flowById(id, aiChatRequest, aiChatResponse -> {
                StreamResponse.responder(aiChatRequest, aiChatResponse, emitter);
            });
        } catch (Exception e) {
            log.error("ai models flow by id fail, e:", e);
            StreamResponse.responseFail(aiChatRequest, emitter, e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);

        return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }
}
